package com.ruoyi.business.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.business.domain.OrderReceiveRecord;
import com.ruoyi.click.domain.vo.OrderReceiveRecordVo;
import com.ruoyi.click.domain.vo.RankingVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 订单接收记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-17
 */
@Mapper
public interface OrderReceiveRecordMapper extends BaseMapper<OrderReceiveRecord>
{
    /**
     * 查询订单接收记录
     * 
     * @param id 订单接收记录主键
     * @return 订单接收记录
     */
    OrderReceiveRecord selectOrderReceiveRecordById(Long id);

    /**
     * 查询订单接收记录列表
     * 
     * @param orderReceiveRecord 订单接收记录
     * @return 订单接收记录集合
     */
    List<OrderReceiveRecord> selectOrderReceiveRecordList(OrderReceiveRecord orderReceiveRecord);

    /**
     * 查询订单接收记录列表，根据id倒序查询
     *
     * @param orderReceiveRecord 订单接收记录
     * @return 订单接收记录集合
     */
    List<OrderReceiveRecord> selectOrderListOrderDescId(OrderReceiveRecord orderReceiveRecord);

    /**
     * 倒序查询订单接收记录列表
     *
     * @param orderReceiveRecord 订单接收记录
     * @return 订单接收记录集合
     */
    List<OrderReceiveRecord> selectListOrderDesc(OrderReceiveRecord orderReceiveRecord);

    /**
     * 新增订单接收记录
     * 
     * @param orderReceiveRecord 订单接收记录
     * @return 结果
     */
    int insertOrderReceiveRecord(OrderReceiveRecord orderReceiveRecord);

    /**
     * 修改订单接收记录
     * 
     * @param orderReceiveRecord 订单接收记录
     * @return 结果
     */
    int updateOrderReceiveRecord(OrderReceiveRecord orderReceiveRecord);

    /**
     * 删除订单接收记录
     * 
     * @param id 订单接收记录主键
     * @return 结果
     */
    int deleteOrderReceiveRecordById(Long id);

    /**
     * 批量删除订单接收记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteOrderReceiveRecordByIds(Long[] ids);

    /**
     * 根据条件统计数量，也可用于判断符合条件的数据是否存在
     * @param orderReceiveRecord
     * @return 统计数量结果
     */
    long countNum(OrderReceiveRecord orderReceiveRecord);

    /**
     * 统计一个用户当日的订单数量
     * @param param
     * @return 统计数量结果
     */
    int countNumByUserDate(Map<String,Object> param);

    /**
     * 一个用户一天内的利润总计
     * @param param
     * @return 统计数量结果
     */
    BigDecimal sumAmountByUserDate(Map<String,Object> param);

    List<OrderReceiveRecordVo> selectListOrderDescVo(OrderReceiveRecord orderReceiveRecord);

    /**
     * 查询最近的订单
     */
    @Select("select * FROM m_order_receive_record where user_id=#{uid} order by create_time desc limit 1")
    OrderReceiveRecord selectLastOrder(Long uid);

    /**
     * 利润总和
     * @param param1
     * @return
     */
    BigDecimal sumProfit(Map<String, Object> param1);

    /**
     * 一个月订单利润总和
     * @return
     */
    List<RankingVo> selectCustomerConsumeStat();

    /**
     * 查询个人当天全部订单记录
     * @param userId
     * @return
     */
    List<OrderReceiveRecord> selectByUid(Long userId);

    /**
     * 查询个人昨天全部订单记录
     * @param userId
     * @return
     */
    List<OrderReceiveRecord> selectYesterdayByUid(Long userId);


    /**
     * 根据用户ID列表查询订单记录
     * @param userIds 用户ID列表
     * @return 符合条件的订单记录列表
     */
    @Select("SELECT * FROM m_order_receive_record WHERE user_id IN (#{userIds})")
    List<OrderReceiveRecord> listByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 统计订单数量
     * @param userId
     * @return
     */
    int selectTotal(Long userId);
}
