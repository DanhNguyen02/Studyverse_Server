package com.studyverse.server.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Date;

public class User {
    private int id;
    private String email;

    @JsonIgnore
    private String password;
    private String firstName;
    private String lastName;
    private Date dob;
    private String phone;
    private String avatar;
    private String userStatus;
    private boolean accountStatus;
    private LocalDateTime lastLogin;
    private int familyId;
    private String nickName;
    private String role;
    private boolean isFamilyHost;

    public User() {

    }

    public User(int id, String email, String firstName, String lastName, String phone, String avatar, String nickName, String role, String userStatus, boolean accountStatus, Date dob) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.avatar = avatar;
        this.nickName = nickName;
        this.role = role;
        this.userStatus = userStatus;
        this.accountStatus = accountStatus;
        this.dob = dob;
    }

    public User(int id, String email, String firstName, String lastName, String phone, String avatar, String nickName, String role, String userStatus, boolean accountStatus, Date dob, Integer familyId, Boolean isFamilyHost) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.avatar = avatar;
        this.nickName = nickName;
        this.role = role;
        this.userStatus = userStatus;
        this.accountStatus = accountStatus;
        this.dob = dob;
        this.familyId = familyId;
        this.isFamilyHost = isFamilyHost;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public boolean isAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(boolean accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isFamilyHost() {
        return isFamilyHost;
    }

    public void setFamilyHost(boolean familyHost) {
        isFamilyHost = familyHost;
    }
}
