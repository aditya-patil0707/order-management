package com.example.ordermanagement.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse {

    private Boolean status;
    private String message;
    private Object data;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private LocalDateTime timestamp;
}