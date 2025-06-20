package com.cc.database.datastructure;

public class Node {
    private String name;
    private Node next;

    public Node() {}

    public Node(String name) {
      this.name = name;
    }

    public String getName() { return name; }
    public Node getNext() { return next; }
    public void setNext(Node next) { this.next = next; }
  }