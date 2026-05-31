package com.translation.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.translation.common.constant.CommonConstant;
import com.translation.common.result.PageResult;
import com.translation.common.result.Result;
import com.translation.common.utils.RedisUtil;
import com.translation.entity.User;
import com.translation.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "后台-用户管理")
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final RedisUtil redisUtil;

    @ApiOperation("用户列表")
    @GetMapping("/list")
    public Result<PageResult<User>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> result = userService.lambdaQuery()
                .like(StrUtil.isNotBlank(keyword), User::getEmail, keyword)
                .or()
                .like(StrUtil.isNotBlank(keyword), User::getNickname, keyword)
                .eq(status != null, User::getStatus, status)
                .orderByDesc(User::getCreateTime)
                .page(new Page<>(page, size));
        return Result.success(new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @ApiOperation("启用/停用用户")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = userService.getById(id);
        if (user != null) {
            user.setStatus(status);
            userService.updateById(user);
            /* 停用用户时踢下线 */
            if (status == 0) {
                redisUtil.delete(CommonConstant.APP_ACTIVE_TOKEN_PREFIX + id);
            }
        }
        return Result.success();
    }

    @ApiOperation("用户详情")
    @GetMapping("/{id}")
    public Result<User> detail(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }
}