package com.cc.database.datastructure;

public class User {
    private String name;
    private int age;
    private Address address;

    public User() {}

    public User(String name, int age) {
      this.name = name;
      this.age = age;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
  }