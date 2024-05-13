package com.kieran.mallchat.common.user.controller;


import com.kieran.mallchat.common.common.domain.vo.resp.ApiResult;
import com.kieran.mallchat.common.common.utils.RequestHolder;
import com.kieran.mallchat.common.user.domain.vo.req.ModifyNameReq;
import com.kieran.mallchat.common.user.domain.vo.req.WearBadgeReq;
import com.kieran.mallchat.common.user.domain.vo.resp.BadgesResp;
import com.kieran.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.kieran.mallchat.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2024-04-23
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("/capi/user")
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation("获取用户信息")
    @GetMapping("/userInfo")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @ApiOperation("修改用户名")
    @PutMapping("/name")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(), req.getName());
        return ApiResult.success();
    }

    @ApiOperation("获取用户徽章")
    @GetMapping("/badges")
    public ApiResult<List<BadgesResp>> getBadges() {
        return ApiResult.success(userService.getBadges(RequestHolder.get().getUid()));
    }

    @ApiOperation("撇带徽章")
    @PutMapping("/badge")
    public ApiResult<Void> wearBadge(@Valid @RequestBody WearBadgeReq req) {
        userService.wearBadge(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }
}

