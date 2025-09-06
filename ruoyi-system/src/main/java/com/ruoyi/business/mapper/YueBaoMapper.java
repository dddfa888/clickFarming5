package com.ruoyi.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.business.domain.OrderReceiveRecord;
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
 * 余额宝信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
@Mapper
public interface YueBaoMapper
{

    YueBao selectByUserId(@Param("userId") Long userId);

    void insert(YueBao yueBao);
    void updateById(YueBao yueBao);
    List<YueBao> list();
}
