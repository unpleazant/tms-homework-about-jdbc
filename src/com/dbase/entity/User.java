package com.dbase.entity;

import java.util.List;

public class User {
    private int id;
    private String name;
    private String username;
    private String password;
    private List<Address> address;
    private List<PhoneNumber> phoneNumber;

    public User(int id, String name, String username, String password, List<Address> address, List<PhoneNumber> phoneNumber) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
		this.address = address;
		this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

    public List<PhoneNumber> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<PhoneNumber> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "\nUser {" +
                "\nid: " + id +
                ",\nname: '" + name + '\'' +
                ",\nusername: '" + username + '\'' +
                ",\npassword: '" + password + '\'' +
                ",\n" + address +
                ",\n" + phoneNumber + '}';
    }
}
