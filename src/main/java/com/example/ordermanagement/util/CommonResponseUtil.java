package com.example.ordermanagement.util;

import org.springframework.stereotype.Component;

@Component
public class CommonResponseUtil {

    public CommonResponse success(String message, Object data) {
        return CommonResponse.builder()
                .status(true)
                .message(message)
                .data(data)
                .build();
    }

    public CommonResponse success(String message, Object data, Integer pageNumber, Integer pageSize, Long totalElements) {
        return CommonResponse.builder()
                .status(true)
                .message(message)
                .data(data)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .build();
    }

    public CommonResponse error(String message) {
        return CommonResponse.builder()
                .status(false)
                .message(message)
                .build();
    }
}