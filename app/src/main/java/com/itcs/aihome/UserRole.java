package com.itcs.aihome;

import java.util.function.Function;

public class UserRole {
    String role, iduser;

    public UserRole(String role, String iduser) {
        this.role = role;
        this.iduser = iduser;
    }

    public String userRole() {
        switch (role) {
            case "admin":
                break;
            case "user":
                break;
            case "":
                break;
        }
        return "";
    }
}
