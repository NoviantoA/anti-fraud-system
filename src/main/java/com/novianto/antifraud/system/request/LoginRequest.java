package com.novianto.antifraud.system.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginRequest {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
