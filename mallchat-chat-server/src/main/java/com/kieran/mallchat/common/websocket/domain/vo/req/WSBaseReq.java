package com.kieran.mallchat.common.websocket.domain.vo.req;

import lombok.Data;

@Data
public class WSBaseReq {

    /**
     * 请求类型
     */
    private Integer type;

    /**
     * 请求数据
     */
    private String data;
}
