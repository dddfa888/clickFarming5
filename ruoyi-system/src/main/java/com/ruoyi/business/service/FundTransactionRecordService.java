package com.ruoyi.business.service;


import com.ruoyi.click.domain.FundTransactionRecord;
import com.ruoyi.click.domain.YueBao;

import java.util.List;

/**
 * 余额宝记录Service接口
 *
 * @author ruoyi
 * @date 2025-09-04
 */
public interface FundTransactionRecordService {

    void insert(FundTransactionRecord record);
    List<FundTransactionRecord> listByUserId(Long userId);

    // 新增方法：查用户指定类型的记录（1=转入，2=转出）
    List<FundTransactionRecord> listByUserIdAndType(Long userId, String type);

}
