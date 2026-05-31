package com.translation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.translation.dto.admin.AdminLoginDTO;
import com.translation.entity.Admin;
import com.translation.vo.admin.AdminLoginVO;

public interface AdminService extends IService<Admin> {

    AdminLoginVO login(AdminLoginDTO dto);
}