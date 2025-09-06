package com.ruoyi.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.business.domain.OrderReceiveRecord;
import com.ruoyi.click.domain.FundTransactionRecord;
import com.ruoyi.click.domain.YueBao;
import com.ruoyi.click.domain.vo.OrderReceiveRecordVo;
import com.ruoyi.click.domain.vo.RankingVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 余额宝记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-04
 */
@Mapper
public interface FundTransactionRecordMapper
{

    void insert(FundTransactionRecord record);
    List<FundTransactionRecord> listByUserId(@Param("userId") Long userId);

    // 新增方法：查用户指定类型的交易记录
    List<FundTransactionRecord> listByUserIdAndType(
            @Param("userId") Long userId,
            @Param("type") String type
    );
}
