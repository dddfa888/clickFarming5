package com.ruoyi.click.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.click.domain.MMoneyInvestWithdraw;
import com.ruoyi.click.domain.UserGrade;
import com.ruoyi.click.domain.vo.WithdrawalVO;
import org.apache.ibatis.annotations.Param;

/**
 * 存款取款记录Service接口
 *
 * @author ruoyi
 * @date 2025-06-19
 */
public interface IMMoneyInvestWithdrawService extends IService<MMoneyInvestWithdraw>
{
    /**
     * 查询存款取款记录
     *
     * @param id 存款取款记录主键
     * @return 存款取款记录
     */
    MMoneyInvestWithdraw selectMMoneyInvestWithdrawById(Long id);

    /**
     * 查询存款取款记录列表
     *
     * @param mMoneyInvestWithdraw 存款取款记录
     * @return 存款取款记录集合
     */
    List<MMoneyInvestWithdraw> selectMMoneyInvestWithdrawList(MMoneyInvestWithdraw mMoneyInvestWithdraw);

    /**
     * 新增存款取款记录
     *
     * @param mMoneyInvestWithdraw 存款取款记录
     * @return 结果
     */
    int insertMMoneyInvestWithdraw(MMoneyInvestWithdraw mMoneyInvestWithdraw);

    /**
     * 修改存款取款记录
     *
     * @param mMoneyInvestWithdraw 存款取款记录
     * @return 结果
     */
    int updateMMoneyInvestWithdraw(MMoneyInvestWithdraw mMoneyInvestWithdraw);

    /**
     * 批量删除存款取款记录
     *
     * @param ids 需要删除的存款取款记录主键集合
     * @return 结果
     */
    int deleteMMoneyInvestWithdrawByIds(Long[] ids);

    /**
     * 删除存款取款记录信息
     *
     * @param id 存款取款记录主键
     * @return 结果
     */
    int deleteMMoneyInvestWithdrawById(Long id);

    /**
     * 一个用户一天内的取现或充值数额总计
     * @param param
     * @return 统计数量结果
     */
    BigDecimal sumAmountByUserDateType(Map<String,Object> param);

    /**
     * 根据用户id修改用户信息
     * @param param
     * @return 修改数量
     */
    int updateUserInfoByUserId(Map<String,Object> param);

    /**
     * 获取今天的提现记录
     */
    MMoneyInvestWithdraw getTodayWithdraw(Long userId);

    /**
     * 同意提款
     * @param withdraw
     */
    int updateMMoneyInvestWithdrawWithdrawal(MMoneyInvestWithdraw withdraw);

    /**
     * 同意充值
     * @param withdraw
     */
    int updateMMoneyInvestWithdrawRecharge(MMoneyInvestWithdraw withdraw);


    /**
     * 充值功能
     *
     * @param userId
     * @param amount
     */
    int insertRecharge(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 提现功能
     * @param withdrawalVO
     */
    int insertDraw(WithdrawalVO withdrawalVO);

    /**
     * 个人咨询详情
     * @param withdraw
     * @param startTime
     * @param endTime
     * @return
     */
    List<MMoneyInvestWithdraw> selectWithdrawByTimeRange(MMoneyInvestWithdraw withdraw, LocalDateTime startTime, LocalDateTime endTime);

}
