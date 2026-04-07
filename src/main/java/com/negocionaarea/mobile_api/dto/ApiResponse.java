package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter

public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private T data;

    public ApiResponse(int status, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.data = data;
    }

}
