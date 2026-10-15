package com.example.talentbridge.user.domain;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;

import java.time.Instant;
import java.time.LocalDate;

/** A person with an account. Company and role are referenced by id. */
public class User {

    private final Long id;
    private final String email;
    private String name;
    private String passwordHash;
    private LocalDate dob;
    private String address;
    private Gender gender;
    private String logoUrl;
    private Long companyId;
    private Long roleId;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(Long id, String email, String name, String passwordHash, LocalDate dob, String address, Gender gender,
                 String logoUrl, Long companyId, Long roleId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = requireText(email, "Email người dùng không được để trống");
        this.name = name;
        this.passwordHash = passwordHash;
        this.dob = dob;
        this.address = address;
        this.gender = gender;
        this.logoUrl = logoUrl;
        this.companyId = companyId;
        this.roleId = roleId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(String email, String name, String passwordHash, LocalDate dob, String address, Gender gender) {
        return new User(null, email, name, passwordHash, dob, address, gender, null, null, null, null, null);
    }

    public static User restore(Long id, String email, String name, String passwordHash, LocalDate dob, String address,
                               Gender gender, String logoUrl, Long companyId, Long roleId,
                               Instant createdAt, Instant updatedAt) {
        return new User(id, email, name, passwordHash, dob, address, gender, logoUrl, companyId, roleId, createdAt, updatedAt);
    }

    public void updateProfile(String name, LocalDate dob, String address, Gender gender) {
        this.name = name;
        this.dob = dob;
        this.address = address;
        this.gender = gender;
    }

    public void changePasswordHash(String newHash) {
        this.passwordHash = requireText(newHash, "Mật khẩu người dùng không được để trống");
    }

    public void changeAvatar(String url) {
        this.logoUrl = url;
    }

    public void joinCompany(Long companyId) {
        this.companyId = companyId;
    }

    public void leaveCompany() {
        this.companyId = null;
    }

    public void assignRole(Long roleId) {
        this.roleId = roleId;
    }

    public void clearRole() {
        this.roleId = null;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank())
            throw new InvalidRequestException(message);
        return value;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDate getDob() { return dob; }
    public String getAddress() { return address; }
    public Gender getGender() { return gender; }
    public String getLogoUrl() { return logoUrl; }
    public Long getCompanyId() { return companyId; }
    public Long getRoleId() { return roleId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
