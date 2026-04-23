package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginCredentialsRequest {
    private String email;
    private String senha;
}

