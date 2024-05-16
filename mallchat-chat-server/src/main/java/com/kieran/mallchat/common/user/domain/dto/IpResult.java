package com.kieran.mallchat.common.user.domain.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class IpResult<T> {

    private Integer code;
    private String msg;
    private T data;

    public boolean isSuccess() {
        return Objects.nonNull(this.code) && this.code == 0;
    }
}
