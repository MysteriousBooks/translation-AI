package com.translation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.translation.entity.WalletRecord;
import com.translation.vo.app.WalletRecordVO;
import com.translation.vo.app.WalletVO;

public interface WalletService extends IService<WalletRecord> {

    WalletVO getWalletInfo(Long userId);

    Page<WalletRecordVO> getWalletRecords(Long userId, Integer type, int page, int size);
}