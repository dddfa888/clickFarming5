package com.ruoyi.business.service.impl;

import com.ruoyi.business.mapper.FundTransactionRecordMapper;
import com.ruoyi.business.service.FundTransactionRecordService;
import com.ruoyi.click.domain.FundTransactionRecord;
import com.ruoyi.click.domain.YueBao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * 余额宝记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-04
 */
@Service
public class FundTransactionRecordServiceImpl implements FundTransactionRecordService {

    @Autowired
    private FundTransactionRecordMapper fundTransactionRecordMapper;

    @Override
    public void insert(FundTransactionRecord record) {
        fundTransactionRecordMapper.insert(record);
    }

    @Override
    public List<FundTransactionRecord> listByUserId(Long userId) {
        return fundTransactionRecordMapper.listByUserId(userId);
    }

    // 新增实现：查指定类型的记录
    @Override
    public List<FundTransactionRecord> listByUserIdAndType(Long userId, String type) {
        return fundTransactionRecordMapper.listByUserIdAndType(userId, type);
    }
}
