package com.ruoyi.web.controller.business;

import com.ruoyi.business.mapper.OrderReceiveRecordMapper;
import com.ruoyi.business.service.FundTransactionRecordService;
import com.ruoyi.business.service.YueBaoProfitService;
import com.ruoyi.business.service.YueBaoService;
import com.ruoyi.click.domain.FundTransactionRecord;
import com.ruoyi.click.domain.UserGrade;
import com.ruoyi.click.domain.YueBao;
import com.ruoyi.click.mapper.MUserMapper;
import com.ruoyi.click.mapper.UserGradeMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.MUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 余额宝Controller
 *
 * @author ruoyi
 * @date 2025-09-04
 */
@RestController
@RequestMapping("/api/yuebao")
public class YueBaoController {
    @Autowired
    private YueBaoService yueBaoService;
    @Autowired
    private FundTransactionRecordService fundTransactionRecordService;
    @Autowired
    private YueBaoProfitService yueBaoProfitService;
     @Autowired
     private MUserMapper mUserMapper;
     @Autowired
     private UserGradeMapper userGradeMapper;
     @Autowired
     private OrderReceiveRecordMapper orderReceiveRecordMapper;

    /**
     * 个人余额宝详情
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId) {
        // 先查询用户的余额宝记录
        YueBao yueBao = yueBaoService.selectByUserId(userId);

        if (yueBao == null) {
            // 如果不存在，创建新的余额宝记录
            yueBao = new YueBao();
            yueBao.setUserId(userId);
            yueBao.setTotalAssets(BigDecimal.ZERO);          // 初始总资产为0
            yueBao.setYesterdayProfit(BigDecimal.ZERO);      // 初始昨日收益为0
            yueBao.setCumulativeProfit(BigDecimal.ZERO);     // 初始累计收益为0
            yueBao.setAnnualizedRate(new BigDecimal("5.00"));// 设定初始年化收益率
            yueBao.setCreateTime(new Date());                // 设置创建时间（如果有该字段）
            yueBao.setUpdateTime(new Date());                // 设置更新时间（如果有该字段）

            // 调用Service层插入方法，保存到数据库
            yueBaoService.insert(yueBao);
        }

        return AjaxResult.success(yueBao);
    }

    /**
     * 转入余额宝
     * @param userId
     * @return
     */
    @GetMapping("/deposit/{userId}")
    public AjaxResult deposit(
            @PathVariable("userId") Long userId,
            @RequestParam("amount") BigDecimal amount) {
        try {
            // 1. 参数合法性校验
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return AjaxResult.error("转入金额必须大于0");
            }

            // 2. 用户及主账户校验
            MUser mUser = mUserMapper.selectMUserByUid1(userId);
            if (mUser == null) {
                return AjaxResult.error("用户不存在");
            }

            // 3. 等级与拼单数量校验
            UserGrade userGrade = userGradeMapper.selectUserGradeById1(mUser.getLevel());
            int finishOrderCount = orderReceiveRecordMapper.selectTotal(userId);
            int requiredOrderCount = userGrade.getBuyProdNum();
            if (finishOrderCount < requiredOrderCount) {
                return AjaxResult.error("未完成今日拼单数（需完成" + requiredOrderCount + "单，当前已完成" + finishOrderCount + "单），无法转入");
            }

            // 4. 主账户余额充足性校验
            BigDecimal accountBalance = mUser.getAccountBalance() == null ? BigDecimal.ZERO : mUser.getAccountBalance();
            if (accountBalance.compareTo(amount) < 0) {
                return AjaxResult.error("主账户余额不足（当前余额：" + accountBalance + "，需转入：" + amount + "）");
            }

            // 5. 扣减主账户对应金额
            BigDecimal newAccountBalance = accountBalance.subtract(amount);
            mUser.setAccountBalance(newAccountBalance);
            mUserMapper.updateById(mUser);

            // 6. 记录转入交易明细
            FundTransactionRecord transRecord = new FundTransactionRecord();
            transRecord.setUserId(userId);
            transRecord.setAmount(amount);
            transRecord.setType("1"); // 1=转入
            transRecord.setTransactionTime(new Date());
            transRecord.setBalanceBefore(accountBalance); // 交易前余额
            transRecord.setBalanceAfter(newAccountBalance); // 交易后余额
            transRecord.setRemark("主账户转入余额宝（自定义金额）");
            fundTransactionRecordService.insert(transRecord);

            // 7. 更新/创建余额宝账户
            YueBao yueBao = yueBaoService.selectByUserId(userId);
            if (yueBao == null) {
                // 新建余额宝记录
                yueBao = new YueBao();
                yueBao.setUserId(userId);
                yueBao.setTotalAssets(amount);
                yueBao.setAnnualizedRate(new BigDecimal("5.00"));
                yueBao.setYesterdayProfit(BigDecimal.ZERO);
                yueBao.setCumulativeProfit(BigDecimal.ZERO);
                yueBao.setCreateTime(new Date());
                yueBao.setUpdateTime(new Date());
                yueBaoService.insert(yueBao);
            } else {
                // 更新已有余额宝记录（累加金额）
                yueBao.setTotalAssets(yueBao.getTotalAssets().add(amount));
                yueBao.setUpdateTime(new Date());
                yueBaoService.updateById(yueBao);
            }

            // 8. 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("depositAmount", amount); // 实际转入金额
            result.put("yueBaoTotal", yueBao.getTotalAssets()); // 余额宝当前总额
            result.put("mainAccountBalance", newAccountBalance); // 主账户剩余余额
            result.put("transactionTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.put("annualizedRate", yueBao.getAnnualizedRate());
            return AjaxResult.success("转入成功", result);
        } catch (Exception e) {
            return AjaxResult.error("转入失败：" + e.getMessage());
        }
    }

    /**
     * 转出余额宝
     * @param userId
     * @param amount
     * @return
     */
    @GetMapping("/withdraw/{userId}")
    @Transactional
    public AjaxResult withdraw(
            @PathVariable("userId") Long userId,
            @RequestParam("amount") BigDecimal amount) {
        try {
            // 1. 参数合法性校验
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return AjaxResult.error("转出金额必须大于0");
            }

            // 2. 用户及账户校验
            MUser mUser = mUserMapper.selectMUserByUid1(userId);
            if (mUser == null) {
                return AjaxResult.error("用户不存在");
            }

            YueBao yueBao = yueBaoService.selectByUserId(userId);
            if (yueBao == null) {
                return AjaxResult.error("未开通余额宝账户，无法转出");
            }

            // 3. 余额宝余额充足性校验
            BigDecimal yueBaoBalance = yueBao.getTotalAssets() == null ? BigDecimal.ZERO : yueBao.getTotalAssets();
            if (amount.compareTo(yueBaoBalance) > 0) {
                return AjaxResult.error("转出金额超过余额宝可用余额（当前余额：" + yueBaoBalance + "元）");
            }

            // 4. 扣减余额宝金额，增加主账户金额
            BigDecimal newYueBaoBalance = yueBaoBalance.subtract(amount);
            yueBao.setTotalAssets(newYueBaoBalance);
            yueBao.setUpdateTime(new Date());
            yueBaoService.updateById(yueBao);

            // 处理主账户可能为null的情况
            BigDecimal mainAccountBalance = mUser.getAccountBalance() == null ? BigDecimal.ZERO : mUser.getAccountBalance();
            BigDecimal newMainAccountBalance = mainAccountBalance.add(amount);
            mUser.setAccountBalance(newMainAccountBalance);
            mUserMapper.updateById(mUser);

            // 5. 记录转出交易明细
            FundTransactionRecord transRecord = new FundTransactionRecord();
            transRecord.setUserId(userId);
            transRecord.setAmount(amount);
            transRecord.setType("2"); // 2=转出
            transRecord.setTransactionTime(new Date());
            transRecord.setBalanceBefore(yueBaoBalance); // 交易前余额宝余额
            transRecord.setBalanceAfter(newYueBaoBalance); // 交易后余额宝余额
            transRecord.setRemark("余额宝转出至主账户（自定义金额）");
            fundTransactionRecordService.insert(transRecord);

            // 6. 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("withdrawAmount", amount); // 实际转出金额
            result.put("yueBaoRemaining", newYueBaoBalance); // 余额宝剩余金额
            result.put("mainAccountBalance", newMainAccountBalance); // 主账户当前余额
            result.put("transactionTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.put("annualizedRate", yueBao.getAnnualizedRate());
            return AjaxResult.success("转出成功", result);
        } catch (Exception e) {
            // 事务回滚会自动处理
            return AjaxResult.error("转出失败：" + e.getMessage());
        }
    }

    @GetMapping("/transaction/history/{userId}")
    public AjaxResult getTransactionHistory(
            @PathVariable Long userId,  // 从URL路径中获取用户ID
            @RequestParam(required = false) String type  // 从请求参数中获取类型（可选）
    ) {
        List<FundTransactionRecord> records;
        // 根据是否传递type参数，执行不同查询逻辑
        if (type != null && ("1".equals(type) || "2".equals(type))) {
            // 传递了合法的type：查指定类型的记录（1=转入，2=转出）
            records = fundTransactionRecordService.listByUserIdAndType(userId, type);
        } else {
            // 未传递type，或type不合法：查该用户所有交易记录
            records = fundTransactionRecordService.listByUserId(userId);
        }
        return AjaxResult.success(records);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void calculateDailyProfit() {
        yueBaoProfitService.calculateDailyProfit();
    }
}


