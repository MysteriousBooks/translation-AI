package com.translation.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.translation.common.enums.ResultCode;
import com.translation.common.exception.BusinessException;
import com.translation.common.utils.JwtUtil;
import com.translation.dto.admin.AdminLoginDTO;
import com.translation.entity.Admin;
import com.translation.mapper.AdminMapper;
import com.translation.service.AdminService;
import com.translation.vo.admin.AdminLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final AdminMapper adminMapper;
    private final JwtUtil jwtUtil;

    @Override
    public AdminLoginVO login(AdminLoginDTO dto) {
        Admin admin = lambdaQuery()
                .eq(Admin::getUsername, dto.getUsername())
                .one();
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (admin.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        if (!BCrypt.checkpw(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtil.generateAdminToken(admin.getId());

        AdminLoginVO vo = new AdminLoginVO();
        vo.setToken(token);
        vo.setAdminId(admin.getId());
        vo.setNickname(admin.getNickname());
        return vo;
    }
}