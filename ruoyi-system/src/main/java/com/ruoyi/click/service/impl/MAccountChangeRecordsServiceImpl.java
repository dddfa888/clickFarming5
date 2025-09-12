package com.ruoyi.click.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.ruoyi.business.domain.OrderReceiveRecord;
import com.ruoyi.business.mapper.OrderReceiveRecordMapper;
import com.ruoyi.click.domain.UserGrade;
import com.ruoyi.click.mapper.MMoneyInvestWithdrawMapper;
import com.ruoyi.click.mapper.MUserMapper;
import com.ruoyi.click.mapper.UserGradeMapper;
import com.ruoyi.common.core.domain.entity.MUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.click.domain.MAccountChangeRecords;
import com.ruoyi.click.mapper.MAccountChangeRecordsMapper;
import com.ruoyi.click.service.IMAccountChangeRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ruoyi.common.utils.SecurityUtils.getUserId;

/**
 * 账变记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-06-16
 */
@Service
public class MAccountChangeRecordsServiceImpl implements IMAccountChangeRecordsService
{
    @Autowired
    private MAccountChangeRecordsMapper mAccountChangeRecordsMapper;
    @Autowired
    private MUserMapper mUserMapper;
    @Autowired
    private UserGradeMapper userGradeMapper;
    @Autowired
    private OrderReceiveRecordMapper orderReceiveRecordMapper;
    @Autowired
    private MMoneyInvestWithdrawMapper mMoneyInvestWithdrawMapper;

    /**
     * 查询账变记录
     *
     * @param uuid 账变记录主键
     * @return 账变记录
     */
    @Override
    public MAccountChangeRecords selectMAccountChangeRecordsByUuid(String uuid)
    {
        return mAccountChangeRecordsMapper.selectMAccountChangeRecordsByUuid(uuid);
    }

    /**
     * 查询账变记录列表
     *
     * @param mAccountChangeRecords 账变记录
     * @return 账变记录
     */
    @Override
    public List<MAccountChangeRecords> selectMAccountChangeRecordsList(MAccountChangeRecords mAccountChangeRecords)
    {
        return mAccountChangeRecordsMapper.selectMAccountChangeRecordsList(mAccountChangeRecords);
    }

    /**
     * 查询账变记录列表-个人的奖励历史记录
     *
     * @param
     * @return 账变记录
     */
    @Override
    public List<Map<String,Object>> selectMAccountChangeForeByUser()
    {
        MAccountChangeRecords mAccountChangeRecords = new MAccountChangeRecords();
        mAccountChangeRecords.setUid(getUserId().toString());
        mAccountChangeRecords.setTransactionType(3); // 3:专用于标记订单利润
        return mAccountChangeRecordsMapper.selectMAccountChangeForeByUser(mAccountChangeRecords);
    }

    /**
     * 个人信息与近两日利润 前台《订单详细信息》页
     *
     * @param
     * @return 用户信息
     */
    @Override
    public Map<String,Object> getUserProfitInfo()
    {
        //用户、等级信息
        MUser mUser = mUserMapper.selectMUserByUid(getUserId());
        //根据用户id去查询等级信息
        UserGrade userGrade = userGradeMapper.selectUserGradeBySortNum(mUser.getLevel());
        //差额金额
        OrderReceiveRecord orderReceiveRecord = orderReceiveRecordMapper.selectOrderReceiveRecordById(getUserId()
        );


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        String strYesterday = formatter.format(localDate.minusDays(1));
        String strToday = formatter.format(localDate);
        String strTomorrow = formatter.format(localDate.plusDays(1));

        //今日已付款订单数量
        Map<String,Object> param = new HashMap<>();
        param.put("userId", getUserId());
        param.put("processStatus", OrderReceiveRecord.PROCESS_STATUS_SUCCESS);
        param.put("date1", strToday);
        param.put("date2", strTomorrow);
        long finishNum = orderReceiveRecordMapper.countNumByUserDate(param);

        //获得所有佣金
        Map<String,Object> param1 = new HashMap<>();
        param1.put("userId", getUserId());
        param1.put("processStatus", OrderReceiveRecord.PROCESS_STATUS_SUCCESS);
        BigDecimal totalProfit  = orderReceiveRecordMapper.sumProfit(param1);

        //未支付订单总和
        Map<String,Object> param2 = new HashMap<>();
        param2.put("userId", getUserId());
        param2.put("processStatus", OrderReceiveRecord.PROCESS_STATUS_WAIT);
        param2.put("date1", strToday);
        param2.put("date2", strTomorrow);
        long cancel = orderReceiveRecordMapper.countNumByUserDate(param2);


        //近2日订单利润 （前端的“昨天折扣”和“今天折扣”）
        param1 = new HashMap<>();
        param1.put("userId", getUserId());
        param1.put("processStatus", OrderReceiveRecord.PROCESS_STATUS_SUCCESS);
        //param.put("transactionType", 3); // 3:专用于标记订单利润，用于查账变表
        param1.put("date1", strYesterday);
        param1.put("date2", strToday);
        BigDecimal profitYesterday = orderReceiveRecordMapper.sumAmountByUserDate(param1);
        param1.put("date1", strToday);
        param1.put("date2", strTomorrow);
        BigDecimal profitToday = orderReceiveRecordMapper.sumAmountByUserDate(param1);
        //近2日提现数额 —— 无用
        /*param = new HashMap<>();
        param.put("userId", getUserId());
        param.put("processStatus", OrderReceiveRecord.PROCESS_STATUS_SUCCESS);
        param.put("date1", strYesterday);
        param.put("date2", strToday);
        BigDecimal withdrawYesterday = mMoneyInvestWithdrawMapper.sumAmountByUserDateType(param);
        param.put("date1", strToday);
        param.put("date2", strTomorrow);
        BigDecimal withdrawToday = mMoneyInvestWithdrawMapper.sumAmountByUserDateType(param);*/

        int remainingNum = userGrade.getBuyProdNum() - (int) finishNum;// 计算剩余可购买数量
        Map<String, Object> res = new HashMap<>();
        res.put("userBalance", mUser.getAccountBalance().setScale(2, RoundingMode.HALF_UP)); //用户余额
        res.put("userLevel", userGrade.getGradeName()); //用户等级
        res.put("orderNum", finishNum); //完整的订单数量
        res.put("remainingNum",remainingNum); //剩余可购买订单
        res.put("balanceAmount",orderReceiveRecord.getBalanceAmount()); //差额金额
        res.put("profit",totalProfit);  //利润总和
        res.put("cancel",cancel);   //未支付订单
        res.put("numYesterday", profitYesterday.setScale(2, RoundingMode.HALF_UP).toString()); //昨天折扣
        res.put("numToday", profitToday.setScale(2, RoundingMode.HALF_UP).toString()); //今天折扣
        res.put("withdrawalAddress", mUser.getWithdrawalAddress()); //提现地址  用于判断，页面不显示
        return res;
    }


    /**
     * 新增账变记录
     *
     * @param mAccountChangeRecords 账变记录
     * @return 结果
     */
    @Override
    public int insertMAccountChangeRecords(MAccountChangeRecords mAccountChangeRecords)
    {
        mAccountChangeRecords.setCreateTime(DateUtils.getNowDate());
        return mAccountChangeRecordsMapper.insertMAccountChangeRecords(mAccountChangeRecords);
    }

    /**
     * 修改账变记录
     *
     * @param mAccountChangeRecords 账变记录
     * @return 结果
     */
    @Override
    public int updateMAccountChangeRecords(MAccountChangeRecords mAccountChangeRecords)
    {
        mAccountChangeRecords.setUpdateTime(DateUtils.getNowDate());
        return mAccountChangeRecordsMapper.updateMAccountChangeRecords(mAccountChangeRecords);
    }

    /**
     * 批量删除账变记录
     *
     * @param uuids 需要删除的账变记录主键
     * @return 结果
     */
    @Override
    public int deleteMAccountChangeRecordsByUuids(String[] uuids)
    {
        return mAccountChangeRecordsMapper.deleteMAccountChangeRecordsByUuids(uuids);
    }

    /**
     * 删除账变记录信息
     *
     * @param uuid 账变记录主键
     * @return 结果
     */
    @Override
    public int deleteMAccountChangeRecordsByUuid(String uuid)
    {
        return mAccountChangeRecordsMapper.deleteMAccountChangeRecordsByUuid(uuid);
    }

    /**
     * 展示数据
     * @return
     */
    @Override
    public Map<String, Object> getInformation() {
        // 查询用户信息
        MUser mUser = mUserMapper.selectMUserByUid(getUserId());

        // 添加用户空值检查
        if (mUser == null) {
            throw new RuntimeException("用户信息不存在");
        }

        // 添加账户余额空值检查
        if (mUser.getAccountBalance() == null) {
            throw new RuntimeException("用户账户余额信息异常");
        }

        // 根据用户id去查询等级信息
        UserGrade userGrade = userGradeMapper.selectUserGradeBySortNum(mUser.getLevel());

        // 添加用户等级空值检查
        if (userGrade == null) {
            throw new RuntimeException("用户等级信息不存在");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        String strYesterday = formatter.format(localDate.minusDays(1));
        String strToday = formatter.format(localDate);
        String strTomorrow = formatter.format(localDate.plusDays(1));

        // 今日已付款订单数量
        Map<String, Object> param = new HashMap<>();
        param.put("userId", getUserId());
        param.put("processStatus", OrderReceiveRecord.PROCESS_STATUS_SUCCESS);
        param.put("date1", strToday);
        param.put("date2", strTomorrow);
        long finishNum = orderReceiveRecordMapper.countNumByUserDate(param);

        // 查询该用户今天的所有订单
        List<OrderReceiveRecord> orderReceiveRecord = orderReceiveRecordMapper.selectByUid(getUserId());

        // 找到未支付订单的数据（安全处理）
        List<OrderReceiveRecord> waitingRecords = Optional.ofNullable(orderReceiveRecord)
                .orElse(Collections.emptyList())
                .stream()
                .filter(record -> "Waiting".equals(record.getProcessStatus()))
                .collect(Collectors.toList());

        OrderReceiveRecord firstRecord = null;
        BigDecimal frozenAmount = BigDecimal.ZERO;
        if (!waitingRecords.isEmpty()) {
            firstRecord = waitingRecords.get(0);
            // 添加对退款金额的空值检查
            if (firstRecord.getRefundAmount() != null) {
                frozenAmount = firstRecord.getRefundAmount();
            }
        }

        // 余额 = 总金额-未支付订单金额
        BigDecimal totalAccount = mUser.getAccountBalance().setScale(2, RoundingMode.HALF_UP)
                .subtract(frozenAmount.setScale(2, RoundingMode.HALF_UP));

        // 今日佣金
        BigDecimal totalProfit = Optional.ofNullable(orderReceiveRecord)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderReceiveRecord::getProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profit = totalProfit.subtract(firstRecord != null && firstRecord.getProfit() != null ?
                firstRecord.getProfit().setScale(2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO);

        // 将要展示的数据存到Map集合返回给前端
        Map<String, Object> res = new HashMap<>();
        res.put("userBalance", totalAccount); // 用户余额
        res.put("userLevel", userGrade.getGradeName()); // 等级
        res.put("rate", userGrade.getCommissionRate()); // 佣金比例
        res.put("profit", profit); // 利润
        res.put("orderNum", finishNum); // 完整的订单数量
        res.put("frozenAmount", frozenAmount); // 冻结金额
        res.put("withdrawalAddress", mUser.getWithdrawalAddress()); // 提现地址
        return res;
    }


    @Override
    public Map<String, Object> getTotalAssets() {
        // 查询用户信息
        MUser mUser = mUserMapper.selectMUserByUid(getUserId());

        // 添加空值检查
        if (mUser == null) {
            throw new RuntimeException("用户信息不存在");
        }

        // 检查账户余额是否为空
        if (mUser.getAccountBalance() == null) {
            throw new RuntimeException("用户账户余额信息异常");
        }

        // 查询该用户今天的所有订单
        List<OrderReceiveRecord> orderReceiveRecord = orderReceiveRecordMapper.selectByUid(getUserId());

        // 找到未支付订单的数据（安全处理）
        List<OrderReceiveRecord> waitingRecords = Optional.ofNullable(orderReceiveRecord)
                .orElse(Collections.emptyList())
                .stream()
                .filter(record -> "Waiting".equals(record.getProcessStatus()))
                .collect(Collectors.toList());

        OrderReceiveRecord firstRecord = null;
        BigDecimal frozenAmount = BigDecimal.ZERO;
        if (!waitingRecords.isEmpty()) {
            firstRecord = waitingRecords.get(0);
            if (firstRecord.getRefundAmount() != null) {
                frozenAmount = firstRecord.getRefundAmount();
            }
        }

        // 余额 = 总金额-未支付订单金额
        BigDecimal totalAccount = mUser.getAccountBalance().setScale(2, RoundingMode.HALF_UP)
                .subtract(frozenAmount.setScale(2, RoundingMode.HALF_UP));

        // 今日佣金
        BigDecimal totalProfit = Optional.ofNullable(orderReceiveRecord)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderReceiveRecord::getProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profit = totalProfit.subtract(firstRecord != null && firstRecord.getProfit() != null ?
                firstRecord.getProfit().setScale(2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO);

        // 查询该用户昨天的所有订单
        List<OrderReceiveRecord> orderReceiveRecord1 = orderReceiveRecordMapper.selectYesterdayByUid(getUserId());

        // 昨天佣金
        BigDecimal totalProfit1 = Optional.ofNullable(orderReceiveRecord1)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderReceiveRecord::getProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profit1 = totalProfit1.subtract(firstRecord != null && firstRecord.getProfit() != null ?
                firstRecord.getProfit().setScale(2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO);

        // 总收益
        BigDecimal total = profit.add(profit1);

        Map<String, Object> res = new HashMap<>();
        res.put("userBalance", totalAccount); // 余额
        res.put("profit", profit);            // 今天佣金
        res.put("profit1", profit1);          // 昨天佣金
        res.put("total", total);              // 总收益
        return res;
    }


}
