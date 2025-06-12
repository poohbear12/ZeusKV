package com.cc.config.loader;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.util.HashMap;
import java.util.Map;

public class KebabCaseConstructor extends Constructor {
    public KebabCaseConstructor(Class<?> theRoot) {
        super(theRoot);
        // 设置自定义PropertyUtils
        setPropertyUtils(new KebabCasePropertyUtils());
    }

    private static class KebabCasePropertyUtils extends PropertyUtils {
        private final Map<String, String> kebabToCamelCache = new HashMap<>();

        @Override
        public Property getProperty(Class<?> type, String name) {
            try {
                // 尝试直接获取属性
                return super.getProperty(type, name);
            } catch (Exception e) {
                // 尝试转换为camelCase
                String camelCaseName = kebabToCamel(name);
                try {
                    return super.getProperty(type, camelCaseName);
                } catch (Exception ex) {
                    // 仍然找不到，抛出原始异常
                    throw new org.yaml.snakeyaml.error.YAMLException("Unable to find property '" + name + "' on class: " + type.getName());
                }
            }
        }

        private String kebabToCamel(String kebab) {
            return kebabToCamelCache.computeIfAbsent(kebab, k -> {
                StringBuilder result = new StringBuilder();
                boolean capitalizeNext = false;
                for (char c : k.toCharArray()) {
                    if (c == '-') {
                        capitalizeNext = true;
                    } else {
                        result.append(capitalizeNext ? Character.toUpperCase(c) : c);
                        capitalizeNext = false;
                    }
                }
                return result.toString();
            });
        }
    }
}