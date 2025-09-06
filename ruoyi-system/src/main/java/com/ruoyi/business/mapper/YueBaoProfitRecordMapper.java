package com.ruoyi.business.mapper;

import com.ruoyi.click.domain.YuebaoProfitRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 余额宝信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
@Mapper
public interface YueBaoProfitRecordMapper
{
    void insert(YuebaoProfitRecord record);
    YuebaoProfitRecord getByUserIdAndDate(@Param("userId") Long userId, @Param("profitDate") Date profitDate);
}