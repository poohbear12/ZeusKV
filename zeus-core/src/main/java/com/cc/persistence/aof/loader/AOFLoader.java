package com.cc.persistence.aof.loader;

import com.cc.cmd.Command;
import com.cc.common.enmu.CMDTypeEnum;
import com.cc.common.utils.RespUtils;
import com.cc.database.core.RedisCore;
import com.cc.protocal.resp.RArrays;
import com.cc.protocal.resp.RBulkStrings;
import com.cc.protocal.resp.Resp;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @program: zeus-kv
 * @description: AOF文件恢复
 * @author: ccstar
 * @create: 2025-06-08  10:09
 **/
@Slf4j
public class AOFLoader {

    /**
     * 默认块大小为8MB
     */
    private static final int DEFALUT_CHUNK_SIZE = 8 * 1024 * 1024;

    /**
     * 指令判断
     */
    private static final byte[] REDIS_PREFIX = {'*', '$', '+', '-', ':'};


    public static void loaderAOF(FileChannel channel, RedisCore redisCore) throws IOException {
        log.info("加载AOF文件中");
        ByteBuf cmds = readFileContent(channel);
        int successCount = handlerCMDs(cmds, redisCore);
        log.info("加载AOF文件成功,成功加载{}条命令", successCount);
    }

    /**
     * todo 处理命令集
     * @param cmds
     * @param redisCore
     * @return
     */
    private static int handlerCMDs(ByteBuf cmds, RedisCore redisCore) {
        int successCount = 0;
        while (cmds.isReadable()) {
            int position = cmds.readerIndex();
            cmds.markReaderIndex();
            try {
                Resp cmd = RespUtils.decodeU(cmds);
                if (cmd == null) {
                    break;
                }
                if (excuteCmd(cmd, position, redisCore)) {
                    successCount++;
                    if (cmds.isReadable(2)) {
                        cmds.readBytes(2);
                    }
                }
            }catch (Exception e) {
                hanlerCmdError(cmds, position, e);
            }
        }
        return successCount;
    }

    /**
     * todo 待优化异常处理
     * @param cmds
     * @param position
     * @param e
     */
    private static void hanlerCmdError(ByteBuf cmds, int position, Exception e) {
        log.warn("命令执行错误,在{}", position, e.getMessage());
        cmds.resetReaderIndex();
        while(cmds.isReadable()) {
            byte b = cmds.readByte();
            if (isRespPrefix(b)) {
                cmds.readerIndex(cmds.readerIndex() - 1);
                break;
            }
        }
    }

    /**
     * todo
     * @param cmd
     * @param position
     * @param redisCore
     * @return
     */
    private static boolean excuteCmd(Resp cmd, int position, RedisCore redisCore) {
        if (!(cmd instanceof RArrays)) {
            log.warn("命令格式异常,在{}",position);
            return false;
        }
        RArrays command = (RArrays) cmd;
        if(!isValiedCmd(command)){
            log.warn("命令无效，在{}",position);
            return false;
        }
        return executeRedisCmd(command, position, redisCore);
    }

    /**
     * 执行指令
     * @param cmds
     * @param position
     * @param redisCore
     * @return
     */
    private static boolean executeRedisCmd(RArrays cmds, int position, RedisCore redisCore) {
        String commandName = new String(((RBulkStrings) cmds.getContent()[0]).getContent()).toUpperCase();
        try {
            CMDTypeEnum cmdTypeEnum = CMDTypeEnum.valueOf(commandName);
            Command cmd = cmdTypeEnum.getSupplier().apply(redisCore).setContext(cmds.getContent());
            cmd.handle();
            return true;
        } catch (IllegalArgumentException e) {
            log.error("命令执行失败,在{}", position,e);
        }
        return false;
    }

    /**
     * 指令是否正确
     * @param cmd
     * @return
     */
    private static boolean isValiedCmd(RArrays cmd) {
        Resp[] content = cmd.getContent();
        return content.length > 0 && content[0] instanceof RBulkStrings;
    }

    /**
     * 读取文件内容
     * @param channel
     * @return
     * @throws IOException
     */
    private static ByteBuf readFileContent(FileChannel channel) throws IOException {
        long fileSize = isActiveLen(channel);
        channel.position(0);
        if (fileSize <= Integer.MAX_VALUE) {
            return readSmallFile(channel, (int) fileSize);
        }
        return readLargeFile(channel, fileSize);
    }

    private static ByteBuf readSmallFile(FileChannel channel, int fileSize) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.directBuffer(fileSize);
        try {
            ByteBuffer byteBuffer = buffer.nioBuffer(0, fileSize);
            int bytesRead;
            while ((bytesRead = channel.read(byteBuffer)) != -1) {
                if (!byteBuffer.hasRemaining()) {
                    break;
                }
            }
            buffer.writerIndex(byteBuffer.position());
            return buffer;
        } catch (IOException e) {
            buffer.release();
            log.error("aof文件加载失败",e);
        }
        return Unpooled.EMPTY_BUFFER;
    }

    private static ByteBuf readLargeFile(FileChannel channel, long fileSize) {
        CompositeByteBuf composite = ByteBufAllocator.DEFAULT.compositeDirectBuffer();
            long position = 0;
            while (position < fileSize) {
                int currentChunkSize = (int) Math.min(DEFALUT_CHUNK_SIZE, fileSize - position);
                ByteBuf chunk = readChunk(channel, position, currentChunkSize);
                composite.addComponent(chunk);
                position += currentChunkSize;
            }
            return composite;
    }


    private static ByteBuf readChunk(FileChannel channel, long position, int size) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.directBuffer(size);
        try {
            ByteBuffer byteBuffer = buffer.nioBuffer(0, size);
            int bytesRead = 0;
            while (bytesRead < size) {
                int read = channel.read(byteBuffer, position + bytesRead);
                if (read == -1) break;
                bytesRead += read;
            }
            buffer.writerIndex(bytesRead);
            return buffer;
        } catch (IOException e) {
            buffer.release();
            log.error("aof文件加载失败",e);
        }
        return Unpooled.EMPTY_BUFFER;
    }

    private static boolean isRespPrefix(byte b){
        for(byte prefix: REDIS_PREFIX){
            if(prefix == b) return true;
        }
        return false;
    }

    private static long isActiveLen(FileChannel channel) throws IOException {
        long size = channel.size();

        // 如果文件为空，直接返回0
        if (size == 0) {
            return 0;
        }
        // 创建一个单字节缓冲区用于逐个字节检查
        ByteBuffer buffer = ByteBuffer.allocate(1);

        // 从文件末尾向前遍历
        for (long position = size - 1; position >= 0; position--) {
            // 将通道位置设置为当前检查位置
            channel.position(position);

            // 读取一个字节
            buffer.clear();
            int bytesRead = channel.read(buffer);

            if (bytesRead == 1) {
                // 检查字节是否为非空（非零值或非空白字符）
                buffer.flip();
                byte b = buffer.get();

                // 如果找到了非空字节，返回其位置+1（即有效长度）
                if (b != 0 && !Character.isWhitespace((char) b)) {
                    return (int) (position + 5);
                }
            }
        }

        // 如果整个文件都是空的，返回0
        return 0;
    }

}
