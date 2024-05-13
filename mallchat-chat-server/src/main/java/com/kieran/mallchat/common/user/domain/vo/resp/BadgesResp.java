package com.kieran.mallchat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("徽章")
@Data
public class BadgesResp {
    @ApiModelProperty("徽章ID")
    private Long id;

    @ApiModelProperty("徽章图标")
    private String img;

    @ApiModelProperty("描述")
    private String describe;

    @ApiModelProperty("是否拥有 0-否 1-是")
    private Integer obtain;

    @ApiModelProperty("是否佩戴 0-否 1-是")
    private Integer wearing;
}
