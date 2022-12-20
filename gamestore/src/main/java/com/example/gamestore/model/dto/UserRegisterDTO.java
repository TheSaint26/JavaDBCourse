package com.example.gamestore.model.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

public class UserRegisterDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;

    public UserRegisterDTO() {
    }

    public UserRegisterDTO(String email, String password, String confirmPassword, String fullName) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.fullName = fullName;
    }

    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Pattern(regexp = "[A-z\\d]{6,}", message = "Invalid password!")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
