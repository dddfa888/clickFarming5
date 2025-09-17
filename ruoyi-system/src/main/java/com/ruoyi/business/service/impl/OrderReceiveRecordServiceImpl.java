package com.ruoyi.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.ruoyi.business.domain.MRewardRecord;
import com.ruoyi.business.domain.MUserOrderSet;
import com.ruoyi.business.domain.ProductManage;
import com.ruoyi.business.mapper.MRewardRecordMapper;
import com.ruoyi.business.mapper.MUserOrderSetMapper;
import com.ruoyi.business.mapper.ProductManageMapper;
import com.ruoyi.click.domain.vo.OrderReceiveRecordVo;
import com.ruoyi.click.domain.vo.RankingVo;
import com.ruoyi.common.core.domain.entity.MUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DecimalUtil;
import com.ruoyi.click.domain.MAccountChangeRecords;
import com.ruoyi.click.domain.UserGrade;
import com.ruoyi.click.mapper.MAccountChangeRecordsMapper;
import com.ruoyi.click.mapper.MUserMapper;
import com.ruoyi.click.mapper.UserGradeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.ruoyi.business.mapper.OrderReceiveRecordMapper;
import com.ruoyi.business.domain.OrderReceiveRecord;
import com.ruoyi.business.service.IOrderReceiveRecordService;

import static com.ruoyi.common.utils.SecurityUtils.getUserId;

/**
 * 订单接收记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-06-17
 */
@Service
public class OrderReceiveRecordServiceImpl implements IOrderReceiveRecordService
{
    @Autowired
    private OrderReceiveRecordMapper orderReceiveRecordMapper;
    @Autowired
    private ProductManageMapper productManageMapper;
    @Autowired
    private MUserMapper mUserMapper;
    @Autowired
    private UserGradeMapper userGradeMapper;
    @Autowired
    private MAccountChangeRecordsMapper mAccountChangeRecordsMapper;
    @Autowired
    private MRewardRecordMapper mRewardRecordMapper;
    @Autowired
    private MUserOrderSetMapper mUserOrderSetMapper;

    /**
     * 查询订单接收记录
     *
     * @param id 订单接收记录主键
     * @return 订单接收记录
     */
    @Override
    public OrderReceiveRecord selectOrderReceiveRecordById(Long id)
    {
        return orderReceiveRecordMapper.selectOrderReceiveRecordById(id);
    }

    /**
     * 查询订单接收记录列表
     *
     * @param orderReceiveRecord 订单接收记录
     * @return 订单接收记录
     */
    @Override
    public List<OrderReceiveRecord> selectOrderReceiveRecordList(OrderReceiveRecord orderReceiveRecord)
    {
        return orderReceiveRecordMapper.selectOrderListOrderDescId(orderReceiveRecord);
    }

    /**
     * 倒序查询一个用户订单接收记录列表
     *
     * @param orderReceiveRecord 订单接收记录
     * @return 订单接收记录
     */
    @Override
    public List<OrderReceiveRecordVo> selectOrderListByUser(OrderReceiveRecord orderReceiveRecord)
    {
        orderReceiveRecord.setUserId(getUserId());
        return orderReceiveRecordMapper.selectListOrderDescVo(orderReceiveRecord);
    }

    /**
     * 新增订单接收记录
     *
     * @param orderReceiveRecord 订单接收记录
     * @return 结果
     */
    @Override
    public int insertOrderReceiveRecord(OrderReceiveRecord orderReceiveRecord)
    {
        orderReceiveRecord.setCreateTime(DateUtils.getNowDate());
        return orderReceiveRecordMapper.insertOrderReceiveRecord(orderReceiveRecord);
    }

    /**
     * 修改订单接收记录
     *
     * @param orderReceiveRecord 订单接收记录
     * @return 结果
     */
    @Override
    public int updateOrderReceiveRecord(OrderReceiveRecord orderReceiveRecord)
    {
        orderReceiveRecord.setUpdateTime(DateUtils.getNowDate());
        return orderReceiveRecordMapper.updateOrderReceiveRecord(orderReceiveRecord);
    }

    /**
     * 批量删除订单接收记录
     *
     * @param ids 需要删除的订单接收记录主键
     * @return 结果
     */
    @Override
    public int deleteOrderReceiveRecordByIds(Long[] ids)
    {
        return orderReceiveRecordMapper.deleteOrderReceiveRecordByIds(ids);
    }

    /**
     * 删除订单接收记录信息
     *
     * @param id 订单接收记录主键
     * @return 结果
     */
    @Override
    public int deleteOrderReceiveRecordById(Long id)
    {
        return orderReceiveRecordMapper.deleteOrderReceiveRecordById(id);
    }

    /**
     * 统计一个用户当日的订单数量
     *
     * @param
     * @return 结果
     */
    @Override
    public int countNumByUserDate()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        Map<String, Object> param = new HashMap<>();
        param.put("userId", getUserId());
        param.put("date1", formatter.format(today));
        param.put("date2", formatter.format(today.plusDays(1)));
        return orderReceiveRecordMapper.countNumByUserDate(param);
    }

    /**
     * 前台用户点击后添加订单
     * 为了数据入库后返回id，orderReceiveRecord由Controller传过来而不是本方法内新建
     *
     * @return 新增订单数量
     */
    @Override
    public Map<String, Object> insertOrderByUser(OrderReceiveRecord orderReceiveRecord) {
        Map<String, Object> map = new HashMap<>();
        MUser mUser = mUserMapper.selectMUserByUid(getUserId());
        OrderReceiveRecord orderParam = new OrderReceiveRecord();
        orderParam.setUserId(mUser.getUid());
        orderParam.setProcessStatus(OrderReceiveRecord.PROCESS_STATUS_WAIT);

        long unfinishedCount = orderReceiveRecordMapper.countNum(orderParam);
        if (unfinishedCount > 0) {
            throw new ServiceException("有订单未完成，请先付款");//user
        }

        OrderReceiveRecord order = orderReceiveRecordMapper.selectLastOrder(mUser.getUid());
        if(order != null) {
            Date createTime = order.getCreateTime();
            Date nowDate = DateUtils.getNowDate();
            // 计算时间差（秒）
            long diff = (nowDate.getTime() - createTime.getTime()) / 1000;

            if (diff < 8) {
                throw new ServiceException("正在分发");//user
            }
        }
        UserGrade userGrade = userGradeMapper.selectUserGradeBySortNum(mUser.getLevel());
        if (userGrade == null)
            throw new ServiceException("用户等级不存在");//user
        //若余额小于等级内设置的最低余额，则给出提示，下单失败
        if (mUser.getAccountBalance().compareTo(userGrade.getMinBalance()) < 0) {
            String nameCn = "";
            //会员等级名称越南语转为中文
            Integer gradeName = userGrade.getId().intValue();
            switch (gradeName) {
                case 1:
                    nameCn = "白银";
                    break;
                case 2:
                    nameCn = "黄金";
                    break;
                case 3:
                    nameCn = "铂金";
                    break;
                case 4:
                    nameCn = "钻石";
                    break;
                default:
                    nameCn = "白银";
            }
            map.put("name", "等级配置");
            map.put("level", nameCn);
            map.put("value", userGrade.getMinBalance());
            return map;
        }

        //int todayCount = countNumByUserDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        int numTarget = userGrade.getBuyProdNum();
        String strToday = formatter.format(localDate);
        String strTomorrow = formatter.format(localDate.plusDays(1));
        Map<String, Object> param = new HashMap<>();
        param.put("userId", getUserId());
        param.put("date1", strToday);
        param.put("date2", strTomorrow);
        long finishNum = orderReceiveRecordMapper.countNumByUserDate(param);
        int todayCount = (int) finishNum;
        if (finishNum >= numTarget)
            throw new ServiceException("您已完成今天的申请");//user
        //原系统中当日下单次数达到设置值时提示如下：
        //  您已完成今天的申请
        //  You have completed your order today
        //  Bạn đã hoàn thành hết đơn hôm nay

        orderReceiveRecord.setUserId(mUser.getUid());
        orderReceiveRecord.setUserName(mUser.getLoginAccount());

        //无论是否连单，至少需保存一个订单
        setValueSaveProdList(orderReceiveRecord, mUser, userGrade, numTarget, ++todayCount);
        int saveOrderNum = 1;

        //检查用户表设置的值，判断是否连单，若multiOrderNum，说明需要生成多个订单
        Integer multiOrderNum = mUser.getMultiOrderNum();
        Long firstOrderId = null;
        if (multiOrderNum != null && multiOrderNum > 1) {
            firstOrderId = orderReceiveRecord.getId();
            for (int i = 1; i < multiOrderNum; i++) { //上面已经保存1单，所以此处i初始值为1，而不是0
                setValueSaveProdList(orderReceiveRecord, mUser, userGrade, numTarget, ++todayCount);
            }
            saveOrderNum = multiOrderNum;
            //第1个订单的id返回到前端
            orderReceiveRecord.setId(firstOrderId);
        }
        map.put("orderId", firstOrderId);
        mUserMapper.increaseBrushNumber(mUser.getUid(), saveOrderNum);
        return map;
    }

    /**
     * 设置一个订单的数据并保存入数据库
     */
    public void setValueSaveProdList(OrderReceiveRecord orderReceiveRecord, MUser mUser, UserGrade userGrade, int numTarget, int todayCount) {
        //查《订单设置》表，如果提前设置了某一单的限制，就根据限制条件查询产品，没设置就按默认情况查询
        MUserOrderSet paramOrderSet = new MUserOrderSet();
        paramOrderSet.setUserId(mUser.getUid());
        paramOrderSet.setOrderNum(todayCount);
        List<MUserOrderSet> orderSetList = mUserOrderSetMapper.selectMUserOrderSetList(paramOrderSet);

        ProductManage product = null;
        if (orderSetList != null && orderSetList.size() > 0) {
            product = setOrderProdLimit(orderReceiveRecord, orderSetList.get(0));
        } else {
            product = setOrderProdNormal(orderReceiveRecord, mUser);
        }

        orderReceiveRecord.setProductId(product.getId());
        orderReceiveRecord.setProductName(product.getProductName());
        orderReceiveRecord.setProductImageUrl(product.getImageUrl());
        orderReceiveRecord.setUnitPrice(product.getPrice());

        // 随机生成hide值
        Random random = new Random();
        int hideValue = random.nextInt(2); // 随机生成0或1
        orderReceiveRecord.setHide(hideValue);

        orderReceiveRecord.setTotalAmount(DecimalUtil.multiple(product.getPrice(), orderReceiveRecord.getNumber()));

        // 计算利润
        BigDecimal profit = calcProfit(userGrade, orderReceiveRecord.getTotalAmount(), hideValue);

        // 验证隐藏商品条件：如果hide=1，三倍利润不能大于总金额
        if (hideValue == 1) {
            BigDecimal tripleProfit = profit.multiply(new BigDecimal(3));
            // 如果三倍利润大于总金额，则不设置为隐藏商品
            if (tripleProfit.compareTo(orderReceiveRecord.getTotalAmount()) > 0) {
                orderReceiveRecord.setHide(0); // 设置为非隐藏商品
                // 重新计算利润（非隐藏商品的利润）
                profit = calcProfit(userGrade, orderReceiveRecord.getTotalAmount(), 0);
            }
        }

        orderReceiveRecord.setProfit(profit);
        orderReceiveRecord.setRefundAmount(DecimalUtil.add(orderReceiveRecord.getTotalAmount(), profit));
        orderReceiveRecord.setProcessStatus(OrderReceiveRecord.PROCESS_STATUS_WAIT);
        orderReceiveRecord.setNumTarget(numTarget);
        orderReceiveRecord.setNumSeq(todayCount);
        orderReceiveRecord.setMultiType(OrderReceiveRecord.MULTI_TYPE_NO);
        orderReceiveRecord.setFreezeStatus(OrderReceiveRecord.FREEZE_STATUS_NO);
        orderReceiveRecord.setCreateTime(DateUtils.getNowDate());
        orderReceiveRecordMapper.insertOrderReceiveRecord(orderReceiveRecord);
    }



    /**
     * 计算用户可支付范围内的产品数量
     */
    public ProductManage setOrderProdNormal(OrderReceiveRecord orderReceiveRecord, MUser mUser) {
        Map<String, Object> paramIds = new HashMap<>();
        paramIds.put("price_Le", mUser.getAccountBalance());
        List<Long> idList = productManageMapper.getIdList(paramIds);
        if (idList == null || idList.isEmpty())
            throw new ServiceException("未查到产品信息");//user
        //前面查出符合条件的产品id，然后随机挑选一个产品id，查出产品
        int prodIndex = (int) Math.floor(Math.random() * idList.size());
        ProductManage product = productManageMapper.selectProductManageById(idList.get(prodIndex));

        // 计算产品数量，先计算用户余额整除产品价格的商，即用户可支付范围内的最大值（最大产品数量）
        int prodNum = mUser.getAccountBalance().divide(product.getPrice(), 0, RoundingMode.DOWN).intValue();
        // 如果上面计算的prodNum是1，产品数量直接设为1。否则，假设prodNum（用户可支付范围内的最大数量）是10，生成随机数取5-10之间的整数作为本次订单实际产品数量。
        if (prodNum > 1) {
            Double min = prodNum * (0.7);
            prodNum = randomMinMax(min.intValue(), prodNum);
        }
        orderReceiveRecord.setNumber(prodNum);
        return product;
    }

    //    public static void setOrderProdNormal2(BigDecimal price, BigDecimal money){
//
//        int prodNum = money.divide(price, 0, RoundingMode.DOWN).intValue();
//        // 如果上面计算的prodNum是1，产品数量直接设为1。否则，假设prodNum（用户可支付范围内的最大数量）是10，生成随机数取5-10之间的整数作为本次订单实际产品数量。
//        if(prodNum>1){
//            Double min=prodNum*(0.8);
//            prodNum = randomMinMax(min.intValue(),prodNum);
//        }
//        System.out.println(prodNum*price.doubleValue());
//    }
//
//    public static void main(String[] args) {
//        setOrderProdNormal2(new BigDecimal(123),new BigDecimal(500));
//    }
    // 生成包含 min 和 max 的随机数
    int randomMinMax(int min, int max) {
        Random rand = new Random();
        if (min < 1) {
            min = 1;
        }
        if (max < 2) {
            max = 2;
        }
        if (min > max) {
            return 1;
        }
        return rand.nextInt(max - min + 1) + min;
    }

    /**
     * 从数据库中随机查询一个产品，默认只查询价格小于或等于用户余额的
     */
    public ProductManage setOrderProdLimit(OrderReceiveRecord orderReceiveRecord, MUserOrderSet orderSet) {
        BigDecimal minNum = orderSet.getMinNum();
        BigDecimal maxNum = orderSet.getMaxNum();
        BigDecimal maxHalf = maxNum.divide(new BigDecimal(2));
        Map<String, Object> paramIds = new HashMap<>();
        paramIds.put("min", minNum);
        paramIds.put("max", maxNum);
        paramIds.put("max_half", maxHalf);
        List<Long> idList = productManageMapper.getIdListByOrderSet(paramIds);
        if (idList == null || idList.isEmpty())
            throw new ServiceException("未查到产品信息");//user

        int prodIndex = (int) Math.floor(Math.random() * idList.size());
        ProductManage product = productManageMapper.selectProductManageById(idList.get(prodIndex));
        BigDecimal price = product.getPrice();

        // 计算合适的产品数量，使总额在min到max之间
        int prodNum = 1; //默认数量1，适合产品单价 > half的情况

        if (price.compareTo(maxHalf) <= 0) {
            long min = Math.round(Math.ceil(minNum.divide(price, 2, RoundingMode.HALF_UP).doubleValue()));
            long max = Math.round(Math.floor(maxNum.divide(price, 2, RoundingMode.HALF_UP).doubleValue()));
            prodNum = (int) (Math.round(Math.floor(Math.random() * (max - min))) + min);
        }
        orderReceiveRecord.setNumber(prodNum);
        return product;
    }

    /**
     * 从数据库中随机查询一个产品，只查询价格小于或等于用户余额的
     * @return
     *//*
    public ProductManage getProductRand(MUser mUser){
        Map<String,Object> paramIds = new HashMap<>();
        paramIds.put("price_Le", mUser.getAccountBalance());
        List<Long> idList = productManageMapper.getIdList(paramIds);
        if(idList==null || idList.isEmpty())
            throw new ServiceException("未查到产品信息");//user

        int prodIndex = (int) Math.floor(Math.random() * idList.size());
        return productManageMapper.selectProductManageById(idList.get(prodIndex));
    }*/

    /**
     * 计算利润
     *
     * @param userGrade 用户等级信息
     * @param totalAmount 订单总金额
     * @param hideValue 隐藏标识值（0-不隐藏，1-隐藏）
     * @return 利润金额
     */
    public BigDecimal calcProfit(UserGrade userGrade, BigDecimal totalAmount, int hideValue) {
        BigDecimal rate = userGrade.getCommissionRate();
        BigDecimal profit = DecimalUtil.multiply(rate, totalAmount).setScale(2, RoundingMode.HALF_UP);

        // 如果是隐藏商品（hideValue=1），利润乘以3
        if (hideValue == 1) {
            profit = profit.multiply(new BigDecimal(3));
        }

        return profit;
    }



    /**
     * 支付订单
     *
     * @param orderId
     */
    @Override
    public int payOrder(Long orderId) {
        OrderReceiveRecord orderReceiveRecord = orderReceiveRecordMapper.selectOrderReceiveRecordById(orderId);
        if (orderReceiveRecord == null)
            throw new ServiceException("订单不存在");//user
        if (OrderReceiveRecord.PROCESS_STATUS_SUCCESS.equals(orderReceiveRecord.getProcessStatus()))
            throw new ServiceException("订单已支付，不可重复支付");//user

        MUser mUser = mUserMapper.selectMUserByUid(orderReceiveRecord.getUserId());

        //用户余额小于订单总金额时，不可支付，需要先充值。
        if (mUser.getAccountBalance().compareTo(orderReceiveRecord.getTotalAmount()) < 0)
            throw new ServiceException("您的帐户不足。请继续充值！");//user

        BigDecimal balanceBefore = mUser.getAccountBalance(); //记录变化前余额
        BigDecimal balanceChange = orderReceiveRecord.getProfit(); //新增余额（已根据是否隐藏商品计算了相应利润）

        BigDecimal balanceAfter = DecimalUtil.add(balanceBefore, balanceChange);

        Date nowDate = DateUtils.getNowDate();
        //更新用户余额
        mUser.setAccountBalance(balanceAfter);
        mUser.setUpdateTime(nowDate);
        mUserMapper.updateMUser(mUser);

        //记录余额变化详情
        MAccountChangeRecords changeRecords = new MAccountChangeRecords();
        changeRecords.setAmount(balanceChange);
        changeRecords.setType(0); //0收入 1支出
        changeRecords.setAccountForward(balanceBefore);
        changeRecords.setAccountBack(balanceAfter);
        changeRecords.setUid(String.valueOf(mUser.getUid()));
        changeRecords.setDescription(mUser.getLoginAccount() + "订单奖励");
        changeRecords.setTransactionType(3); // 3:专用于标记订单利润
        changeRecords.setCreateTime(nowDate);
        changeRecords.setRelatedId(orderId.toString());
        mAccountChangeRecordsMapper.insertMAccountChangeRecords(changeRecords);

        //添加奖励记录
        MRewardRecord mRewardRecord = new MRewardRecord();
        mRewardRecord.setUserId(mUser.getUid());
        mRewardRecord.setUserName(mUser.getLoginAccount());
        mRewardRecord.setRewardTime(nowDate);
        mRewardRecord.setRewardAmount(balanceChange);
        mRewardRecord.setBalanceBefore(balanceBefore);
        mRewardRecord.setBalanceAfter(balanceAfter);
        mRewardRecord.setDescription("订单奖励");
        mRewardRecord.setCreateTime(nowDate);
        mRewardRecordMapper.insertMRewardRecord(mRewardRecord);

        //更新值  支付状态：完成
        orderReceiveRecord.setProcessStatus(OrderReceiveRecord.PROCESS_STATUS_SUCCESS);
        orderReceiveRecord.setUpdateTime(nowDate);
        return orderReceiveRecordMapper.updateOrderReceiveRecord(orderReceiveRecord);
    }

    @Override
    public List<RankingVo> getRanking() {
        // 1. 调用 Mapper 查询数据（已包含头像）
        List<RankingVo> profitStatList = orderReceiveRecordMapper.selectCustomerConsumeStat();

        // 2. 处理空数据
        if (profitStatList == null || profitStatList.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 计算排名（同分同排名逻辑）
        int currentRank = 1;
        BigDecimal prevProfit = null;

        for (int i = 0; i < profitStatList.size(); i++) {
            RankingVo rankingVo = profitStatList.get(i);
            BigDecimal currentProfit = rankingVo.getTotalAmount();

            if (i == 0) {
                rankingVo.setRank(currentRank);
                prevProfit = currentProfit;
                continue;
            }

            if (currentProfit != null && currentProfit.compareTo(prevProfit) == 0) {
                rankingVo.setRank(currentRank);
            } else {
                currentRank = i + 1;
                rankingVo.setRank(currentRank);
                prevProfit = currentProfit;
            }
        }

        // 4. 返回结果（包含头像、排名、利润等）
        return profitStatList;
    }

    /**
     * 每天凌晨0点执行检查当天刷单任务的用户
     */
    @Scheduled(cron = "0 0 0 * * ?")
// @Scheduled(cron = "0 * * * * ?")  // 每小时执行（原来）
    public void checkUnfinishedOrders() {
        System.out.println("=== 定时任务开始执行 === 时间: " + new Date());
        try {
            // 获取今天的时间范围（今天00:00:00到今天23:59:59）
            LocalDate today = LocalDate.now();
            LocalDateTime startTime = LocalDateTime.of(today, LocalTime.MIN);      // 今天00:00:00
            LocalDateTime endTime = LocalDateTime.of(today, LocalTime.MAX);       // 今天23:59:59

            System.out.println("检查时间范围: " + startTime + " 到 " + endTime);

            // 查询今天有刷单记录的所有用户ID（去重）
            Map<String, Object> timeParams = new HashMap<>();
            timeParams.put("startTime", DateUtils.toDate(startTime));
            timeParams.put("endTime", DateUtils.toDate(endTime));
            System.out.println("查询参数: " + timeParams);

            List<Long> userIds = orderReceiveRecordMapper.selectUserIdsWithOrdersToday(timeParams);
            System.out.println("查询到的用户数量: " + (userIds != null ? userIds.size() : 0));
            if (userIds != null) {
                System.out.println("用户ID列表: " + userIds);
            }

            // 检查每个有刷单记录的用户是否完成了对应等级的刷单任务
            if (userIds != null && !userIds.isEmpty()) {
                for (Long userId : userIds) {
                    System.out.println("开始检查用户ID: " + userId);
                    MUser user = mUserMapper.selectMUserByUid(userId);
                    if (user == null) {
                        System.out.println("用户不存在，跳过: " + userId);
                        continue; // 跳过不存在用户
                    }
                    if (user.getStatus() == null || user.getStatus() != 1) {
                        System.out.println("用户状态不是活跃状态，跳过: " + userId + ", 状态: " + user.getStatus());
                        continue; // 跳过非活跃用户
                    }

                    // 根据用户等级获取应完成的刷单数量
                    int requiredBrushNumber = getRequiredBrushNumberByLevel(user.getLevel());
                    System.out.println("用户等级: " + user.getLevel() + ", 要求完成订单数: " + requiredBrushNumber);

                    // 统计用户今天完成的订单数
                    Map<String, Object> params = new HashMap<>();
                    params.put("userId", userId);
                    params.put("startTime", DateUtils.toDate(startTime));
                    params.put("endTime", DateUtils.toDate(endTime));
                    params.put("processStatus", OrderReceiveRecord.PROCESS_STATUS_SUCCESS); // 已完成状态
                    System.out.println("统计订单参数: " + params);

                    int finishedOrders = orderReceiveRecordMapper.countFinishedOrdersByUserAndDate(params);
                    System.out.println("用户 " + userId + " 今天已完成订单数: " + finishedOrders);

                    // 如果未达到要求，则冻结用户账户
                    if (finishedOrders < requiredBrushNumber) {
                        System.out.println("用户 " + userId + " 未完成任务要求，将被冻结");
                        freezeUserAccount(userId);
                    } else {
                        System.out.println("用户 " + userId + " 已完成任务要求");
                    }
                }
            } else {
                System.out.println("今天没有用户有刷单记录");
            }
            System.out.println("=== 定时任务执行完成 ===");
        } catch (Exception e) {
            System.err.println("定时任务执行出错: " + e.getMessage());
            e.printStackTrace();
        }
    }




    /**
     * 冻结用户账户资金
     * @param userId 用户ID
     */
    private void freezeUserAccount(Long userId) {
        try {
            MUser user = mUserMapper.selectMUserByUid(userId);
            if (user != null && user.getStatus() != null && user.getStatus() == 1) {
                // 设置用户状态为冻结（0表示冻结状态，1表示正常状态）
                user.setStatus(0);
                user.setUpdateTime(DateUtils.getNowDate());
                mUserMapper.updateMUser(user);

                // 可以添加日志记录或发送通知
                System.out.println("用户ID " + userId + " 因未完成刷单任务已被冻结，请联系客服解冻");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 可以添加日志记录
        }
    }



    /**
     * 根据用户等级获取应完成的刷单数量
     * @param level 用户等级
     * @return 应完成的刷单数量
     */
    private int getRequiredBrushNumberByLevel(Integer level) {
        if (level == null) return 30; // 默认白银等级

        switch (level) {
            case 1: // 白银会员
                return 30;
            case 2: // 黄金会员
                return 70;
            case 3: // 铂金会员
                return 120;
            case 4: // 钻石会员
                return 200;
            default:
                return 30; // 默认要求
        }
    }
}
