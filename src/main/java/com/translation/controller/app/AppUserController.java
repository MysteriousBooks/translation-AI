package com.translation.controller.app;

import com.translation.common.result.Result;
import com.translation.common.utils.UserContext;
import com.translation.dto.app.ChangePasswordDTO;
import com.translation.dto.app.UpdateUserDTO;
import com.translation.service.UserService;
import com.translation.vo.app.UserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "APP端-用户")
@RestController
@RequestMapping("/api/app/user")
@RequiredArgsConstructor
public class AppUserController {

    private final UserService userService;

    @ApiOperation("获取个人信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        return Result.success(userService.getUserInfo(UserContext.getRequiredUserId()));
    }

    @ApiOperation("修改个人信息")
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@Validated @RequestBody UpdateUserDTO dto) {
        userService.updateUserInfo(UserContext.getRequiredUserId(), dto);
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Validated @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(UserContext.getRequiredUserId(), dto);
        return Result.success();
    }
}