package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String email;
    private String senha;
    private String tipo;
}
