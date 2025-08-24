package com.ruoyi.click.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.click.domain.MMoneyInvestWithdraw;
import com.ruoyi.click.domain.vo.WithdrawalVO;
import com.ruoyi.click.mapper.MMoneyInvestWithdrawMapper;
import com.ruoyi.click.mapper.MUserMapper;
import com.ruoyi.click.service.IMMoneyInvestWithdrawService;
import com.ruoyi.common.core.domain.entity.MUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 存款取款记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-06-19
 */
@Service
public class MMoneyInvestWithdrawServiceImpl extends ServiceImpl<MMoneyInvestWithdrawMapper, MMoneyInvestWithdraw> implements IMMoneyInvestWithdrawService
{
    @Autowired
    private MMoneyInvestWithdrawMapper mMoneyInvestWithdrawMapper;

    @Autowired
    private MUserMapper mUserMapper;

    /**
     * 查询存款取款记录
     *
     * @param id 存款取款记录主键
     * @return 存款取款记录
     */
    @Override
    public MMoneyInvestWithdraw selectMMoneyInvestWithdrawById(Long id)
    {
        return mMoneyInvestWithdrawMapper.selectMMoneyInvestWithdrawById(id);
    }

    /**
     * 查询存款取款记录列表
     *
     * @param mMoneyInvestWithdraw 存款取款记录
     * @return 存款取款记录
     */
    @Override
    public List<MMoneyInvestWithdraw> selectMMoneyInvestWithdrawList(MMoneyInvestWithdraw mMoneyInvestWithdraw)
    {
        return mMoneyInvestWithdrawMapper.selectMMoneyInvestWithdrawList(mMoneyInvestWithdraw);
    }

    /**
     * 新增存款取款记录
     *
     * @param mMoneyInvestWithdraw 存款取款记录
     * @return 结果
     */
    @Override
    public int insertMMoneyInvestWithdraw(MMoneyInvestWithdraw mMoneyInvestWithdraw)
    {
        mMoneyInvestWithdraw.setCreateTime(DateUtils.getNowDate());
        return mMoneyInvestWithdrawMapper.insertMMoneyInvestWithdraw(mMoneyInvestWithdraw);
    }

    /**
     * 修改存款取款记录
     *
     * @param mMoneyInvestWithdraw 存款取款记录
     * @return 结果
     */
    @Override
    public int updateMMoneyInvestWithdraw(MMoneyInvestWithdraw mMoneyInvestWithdraw)
    {
        mMoneyInvestWithdraw.setUpdateTime(DateUtils.getNowDate());
        return mMoneyInvestWithdrawMapper.updateMMoneyInvestWithdraw(mMoneyInvestWithdraw);
    }

    /**
     * 批量删除存款取款记录
     *
     * @param ids 需要删除的存款取款记录主键
     * @return 结果
     */
    @Override
    public int deleteMMoneyInvestWithdrawByIds(Long[] ids)
    {
        return mMoneyInvestWithdrawMapper.deleteMMoneyInvestWithdrawByIds(ids);
    }

    /**
     * 删除存款取款记录信息
     *
     * @param id 存款取款记录主键
     * @return 结果
     */
    @Override
    public int deleteMMoneyInvestWithdrawById(Long id)
    {
        return mMoneyInvestWithdrawMapper.deleteMMoneyInvestWithdrawById(id);
    }


    /**
     * 一个用户一天内的取现或充值数额总计
     * @param param
     * @return 统计数量结果
     */
    @Override
    public BigDecimal sumAmountByUserDateType(Map<String,Object> param){
        return mMoneyInvestWithdrawMapper.sumAmountByUserDateType(param);
    }

    /**
     * 根据用户id修改用户信息
     * @param param
     * @return 修改数量
     */
    @Override
    public int updateUserInfoByUserId(Map<String,Object> param){
        return mMoneyInvestWithdrawMapper.updateUserInfoByUserId(param);
    }

    @Override
    public MMoneyInvestWithdraw getTodayWithdraw(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        return lambdaQuery()
                .eq(MMoneyInvestWithdraw::getUserId, userId)
                .eq(MMoneyInvestWithdraw::getStatus, 1)
                .eq(MMoneyInvestWithdraw::getType, 0)
                .between(MMoneyInvestWithdraw::getCreateTime, startOfDay, endOfDay)
                .last("LIMIT 1")
                .one();
    }

    @Override
    public int updateMMoneyInvestWithdrawWithdrawal(MMoneyInvestWithdraw withdraw) {
        withdraw.setUpdateTime(DateUtils.getNowDate());
        MUser mUser = mUserMapper.selectById(withdraw.getUserId());
        BigDecimal accountBalance = mUser.getAccountBalance();
        BigDecimal withdrawAmount = withdraw.getAmount();
        BigDecimal a = accountBalance.subtract(withdrawAmount);
        mUser.setAccountBalance(a);
        mUserMapper.updateById(mUser);
        return mMoneyInvestWithdrawMapper.updateMMoneyInvestWithdraw(withdraw);
    }

    @Override
    public int updateMMoneyInvestWithdrawRecharge(MMoneyInvestWithdraw withdraw) {
        withdraw.setUpdateTime(DateUtils.getNowDate());
        MUser mUser = mUserMapper.selectById(withdraw.getUserId());
        BigDecimal accountBalance = mUser.getAccountBalance();
        BigDecimal withdrawAmount = withdraw.getAmount();
        BigDecimal a = accountBalance.add(withdrawAmount);
        mUser.setAccountBalance(a);
        mUserMapper.updateById(mUser);
        return mMoneyInvestWithdrawMapper.updateMMoneyInvestWithdraw(withdraw);
    }

    @Override
    public int insertRecharge(Long userId, BigDecimal amount) {
        // 1. 生成16位唯一订单号（时间戳+随机数组合，保证唯一性）
        String orderId = generateOrderId();
        MUser mUser = mUserMapper.selectById(userId);
        if (mUser == null) {
            // Service 层通过抛出异常传递错误，而非返回 AjaxResult
            throw new ServiceException("用户不存在，无法充值");
        }
        if (mUser.getStatus() != 1) { // 假设 1 为正常状态
            throw new ServiceException("用户已被禁用，无法充值");
        }
        // 正确的 BigDecimal 加法运算
        BigDecimal a = mUser.getAccountBalance().add(amount);
        MMoneyInvestWithdraw mMoneyInvestWithdraw = new MMoneyInvestWithdraw();
        mMoneyInvestWithdraw.setUserId(userId);
        mMoneyInvestWithdraw.setUserName(mUser.getLoginAccount());
        mMoneyInvestWithdraw.setUserType("1");
        mMoneyInvestWithdraw.setAmount(amount);
        mMoneyInvestWithdraw.setBankName(mUser.getBankName());
        mMoneyInvestWithdraw.setBankAccountName(mUser.getBankAccountName());
        mMoneyInvestWithdraw.setBankAccountNumber(mUser.getBankAccountNumber());
        mMoneyInvestWithdraw.setType("1");
        mMoneyInvestWithdraw.setStatus(0);
        mMoneyInvestWithdraw.setOrderId(orderId);
        mMoneyInvestWithdraw.setAccountForward(mUser.getAccountBalance());
        mMoneyInvestWithdraw.setAccountBack(a);
        mMoneyInvestWithdraw.setCreateTime(DateUtils.getNowDate());

        return mMoneyInvestWithdrawMapper.insertMMoneyInvestWithdraw(mMoneyInvestWithdraw);
    }

    @Override
    public int insertDraw(WithdrawalVO withdrawalVO) {
        // 1. 生成16位唯一订单号（时间戳+随机数组合，保证唯一性）
        String orderId = generateOrderId();
        // 2. 查询用户数据
        MUser mUser = mUserMapper.selectById(withdrawalVO.getUserId());
        if (mUser == null) {
            // Service 层通过抛出异常传递错误，而非返回 AjaxResult
            throw new ServiceException("用户不存在，无法提现");
        }
        if (mUser.getStatus() != 1) { // 假设 1 为正常状态
            throw new ServiceException("用户已被禁用，无法提现");
        }
        // BigDecimal 减法运算（余额 - 提款金额）
        BigDecimal b = mUser.getAccountBalance().subtract(withdrawalVO.getAmount());
        MMoneyInvestWithdraw mMoneyInvestWithdraw = new MMoneyInvestWithdraw();
        mMoneyInvestWithdraw.setUserId(withdrawalVO.getUserId());
        mMoneyInvestWithdraw.setUserName(mUser.getLoginAccount());
        mMoneyInvestWithdraw.setUserType("1");
        mMoneyInvestWithdraw.setAmount(withdrawalVO.getAmount());
        mMoneyInvestWithdraw.setBankName(mUser.getBankName());
        mMoneyInvestWithdraw.setBankAccountName(mUser.getBankAccountName());
        mMoneyInvestWithdraw.setBankAccountNumber(mUser.getBankAccountNumber());
        mMoneyInvestWithdraw.setType("0");
        mMoneyInvestWithdraw.setStatus(0);
        mMoneyInvestWithdraw.setWithdrawalMethod(withdrawalVO.getWithdrawalMethod());
        mMoneyInvestWithdraw.setOrderId(orderId);
        mMoneyInvestWithdraw.setAccountForward(mUser.getAccountBalance());
        mMoneyInvestWithdraw.setAccountBack(b);
        mMoneyInvestWithdraw.setCreateTime(DateUtils.getNowDate());

        return mMoneyInvestWithdrawMapper.insertMMoneyInvestWithdraw(mMoneyInvestWithdraw);
    }

    @Override
    public List<MMoneyInvestWithdraw> selectWithdrawByTimeRange(MMoneyInvestWithdraw withdraw, LocalDateTime startTime, LocalDateTime endTime) {
        return mMoneyInvestWithdrawMapper.selectWithdrawByTimeRange1(withdraw, startTime, endTime);
    }

    /**
     * 生成16位唯一订单号：时间戳偏移+4位随机数
     * 格式示例：4367778709968925（与业务系统常见订单ID格式一致）
     */
    private String generateOrderId() {
        // 基准时间戳（项目上线时间，用于缩短时间戳位数）
        long baseTimestamp = 1710000000000L; // 可替换为项目实际上线时间的毫秒数
        // 当前时间戳减去基准时间，得到偏移量（约10位数字）
        long timeOffset = System.currentTimeMillis() - baseTimestamp;
        // 生成4位随机数（0000-9999）
        int randomNum = new Random().nextInt(9999);
        // 拼接为16位订单号（时间偏移量+4位随机数）
        return String.format("%d%04d", timeOffset, randomNum);
    }
}
