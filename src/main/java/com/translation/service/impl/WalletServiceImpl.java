package com.translation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.translation.common.enums.ResultCode;
import com.translation.common.exception.BusinessException;
import com.translation.entity.User;
import com.translation.entity.WalletRecord;
import com.translation.mapper.UserMapper;
import com.translation.mapper.WalletRecordMapper;
import com.translation.service.WalletService;
import com.translation.vo.app.WalletRecordVO;
import com.translation.vo.app.WalletVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl extends ServiceImpl<WalletRecordMapper, WalletRecord> implements WalletService {

    private final WalletRecordMapper walletRecordMapper;
    private final UserMapper userMapper;

    @Override
    public WalletVO getWalletInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        WalletVO vo = new WalletVO();
        vo.setBalance(user.getBalance());
        vo.setTotalConsume(user.getTotalConsume());
        return vo;
    }

    @Override
    public Page<WalletRecordVO> getWalletRecords(Long userId, Integer type, int page, int size) {
        LambdaQueryWrapper<WalletRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WalletRecord::getUserId, userId);
        if (type != null) {
            wrapper.eq(WalletRecord::getType, type);
        }
        wrapper.orderByDesc(WalletRecord::getCreateTime);

        Page<WalletRecord> pageParam = new Page<>(page, size);
        Page<WalletRecord> result = walletRecordMapper.selectPage(pageParam, wrapper);

        Page<WalletRecordVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toVO).collect(java.util.stream.Collectors.toList()));
        return voPage;
    }

    private WalletRecordVO toVO(WalletRecord record) {
        WalletRecordVO vo = new WalletRecordVO();
        BeanUtil.copyProperties(record, vo);
        return vo;
    }
}