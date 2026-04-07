package com.negocionaarea.mobile_api.dto;

public enum Role {
    ENTERPRISE("enterprise"),
    CUSTOMER("customer");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
