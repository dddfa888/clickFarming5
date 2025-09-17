package com.ruoyi.web.controller.click;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.business.domain.MRewardRecord;
import com.ruoyi.business.domain.OrderReceiveRecord;
import com.ruoyi.business.mapper.OrderReceiveRecordMapper;
import com.ruoyi.business.service.IMRewardRecordService;
import com.ruoyi.click.domain.MMoneyInvestWithdraw;
import com.ruoyi.click.domain.vo.PasswordUpdateVO;
import com.ruoyi.click.mapper.MMoneyInvestWithdrawMapper;
import com.ruoyi.click.service.IMMoneyInvestWithdrawService;
import com.ruoyi.common.core.domain.entity.MUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DecimalUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.click.domain.MAccountChangeRecords;
import com.ruoyi.click.domain.UserGrade;
import com.ruoyi.click.domain.vo.BalanceModel;
import com.ruoyi.click.service.IMAccountChangeRecordsService;
import com.ruoyi.click.service.IMUserService;
import com.ruoyi.click.service.IUserGradeService;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.web.controller.click.domain.TeamStatisticsVO;
import com.ruoyi.web.controller.click.domain.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户Controller
 *
 * @author ruoyi
 * @date 2025-06-15
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class MUserController extends BaseController
{
    @Autowired
    private IMUserService mUserService;

    @Autowired
    private IUserGradeService userGradeService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IMAccountChangeRecordsService accountChangeRecordsService;

    @Autowired
    private IMRewardRecordService mRewardRecordService;

    @Autowired
    private IMMoneyInvestWithdrawService mMoneyInvestWithdrawService;

    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private MMoneyInvestWithdrawMapper investWithdrawMapper;

    @Autowired
    private OrderReceiveRecordMapper orderRecordMapper;


    @GetMapping("/userInfo")
    public AjaxResult userInfo(HttpServletRequest request) {
        Long userId = tokenService.getLoginUser(request).getmUser().getUid();
        MUser mUser = mUserService.selectMUserByUid(userId);
        if (mUser.getSignature()==null){
            mUser.setSignature("这个人很懒什么也没留下");
        }
        mUser.setLevelName(userGradeService.getOne(new LambdaQueryWrapper<UserGrade>()
                .eq(UserGrade::getSortNum,mUser.getLevel())).getGradeName());
        mUser.setLoginPassword("***************");
        mUser.setFundPassword("***************");
        return success(mUser);
    }

    /**
     * 修改用户余额，前端输入增减的变化量，后端计算出余额新值
     * @param balanceModel
     * @return
     */
    @PostMapping("changeBalance")
    public AjaxResult changeBalance(HttpServletRequest request,@Validated @RequestBody BalanceModel balanceModel) {
        MUser mUser = mUserService.selectMUserByUid(balanceModel.getUid());
        BigDecimal accountForward = mUser.getAccountBalance();
        String userName = tokenService.getLoginUser(request).getUser().getUserName();
        // 修改余额
        HashMap<String, Object> map = mUserService.updateBalance(mUser, balanceModel);

        // 日志记录
        BigDecimal accountBack = (BigDecimal) map.get("accountBalance");
        Integer type = (Integer) map.get("type");
        MAccountChangeRecords changeRecords = new MAccountChangeRecords();
        changeRecords.setAmount(balanceModel.getBalance().abs());
        changeRecords.setType(type);
        changeRecords.setAccountForward(accountForward);
        changeRecords.setAccountBack(accountBack);
        changeRecords.setUid(String.valueOf(balanceModel.getUid()));
        changeRecords.setDescription(userName+"[后台增减余额] "+balanceModel.getReason());
        changeRecords.setTransactionType(1);
        accountChangeRecordsService.insertMAccountChangeRecords(changeRecords);

        //如果是增加余额，添加奖励记录
        if(balanceModel.getBalance().signum()>0){
            MRewardRecord mRewardRecord= new MRewardRecord();
            mRewardRecord.setUserId(mUser.getUid());
            mRewardRecord.setUserName(mUser.getLoginAccount());
            mRewardRecord.setRewardTime(DateUtils.getNowDate());
            mRewardRecord.setRewardAmount(balanceModel.getBalance());
            mRewardRecord.setBalanceBefore(accountForward);
            mRewardRecord.setBalanceAfter(accountBack);
            mRewardRecord.setDescription(balanceModel.getReason());
            mRewardRecord.setCreateTime(mRewardRecord.getRewardTime());
            mRewardRecordService.insertMRewardRecord(mRewardRecord);
        }

        // 升级等级
        //mUserService.upgrade(mUser.getUid());
        return success();
    }

    /**
     * 修改用户余额 前端直接输入余额新值
     * @param request, mUser
     * @return
     */
    @PostMapping("setBalance")
    public AjaxResult setBalance(HttpServletRequest request, @RequestBody MUser mUser) {
        try {
            log.info("开始设置用户余额，请求参数: {}", mUser);

            // 参数验证
            if (mUser.getUid() == null) {
                log.warn("用户ID不能为空");
                return AjaxResult.error("用户ID不能为空");
            }

            if (mUser.getAccountBalance() == null) {
                log.warn("账户余额不能为空");
                return AjaxResult.error("账户余额不能为空");
            }

            // 添加负数检查
            if (mUser.getAccountBalance().compareTo(BigDecimal.ZERO) < 0) {
                log.warn("账户余额不能为负数");
                return AjaxResult.error("账户余额不能为负数");
            }

            // 验证余额格式
            try {
                mUser.setAccountBalance(mUser.getAccountBalance().setScale(2, BigDecimal.ROUND_HALF_UP));
            } catch (Exception e) {
                log.error("账户余额格式不正确: {}", mUser.getAccountBalance());
                return AjaxResult.error("账户余额格式不正确");
            }

            // 获取原始用户信息
            MUser originMUser = mUserService.selectMUserByUid(mUser.getUid());
            if (originMUser == null) {
                log.warn("用户不存在，uid: {}", mUser.getUid());
                return AjaxResult.error("用户不存在");
            }
            log.info("原始用户信息: {}", originMUser);

            // 记录更新前的余额
            BigDecimal balanceBefore = originMUser.getAccountBalance() != null ?
                    originMUser.getAccountBalance() : BigDecimal.ZERO;
            BigDecimal balanceAfter = mUser.getAccountBalance();
            log.info("余额变更: {} -> {}", balanceBefore, balanceAfter);

            // 执行更新操作并检查结果
            int updateResult = mUserService.updateMUserSimple(mUser);
            log.info("更新操作结果: {}", updateResult);

            // 检查更新是否成功
            if (updateResult <= 0) {
                log.warn("更新失败，更新结果: {}", updateResult);
                return AjaxResult.error("更新失败");
            }

            String userName = "未知用户";
            try {
                userName = tokenService.getLoginUser(request).getUser().getUserName();
            } catch (Exception e) {
                log.warn("获取用户名失败，使用默认值");
            }

            // 计算变化类型和金额
            Integer type = null; // 0收入 1支出
            BigDecimal balanceChange = BigDecimal.ZERO;
            if(balanceBefore.compareTo(balanceAfter) < 0){
                type = 0;
                balanceChange = balanceAfter.subtract(balanceBefore);
                log.info("余额增加: {}", balanceChange);
            }else if(balanceBefore.compareTo(balanceAfter) > 0){
                type = 1;
                balanceChange = balanceBefore.subtract(balanceAfter);
                log.info("余额减少: {}", balanceChange);
            } else {
                // 余额没有变化
                log.info("余额没有变化");
                return AjaxResult.success("余额更新成功");
            }

            // 日志记录
            try {
                MAccountChangeRecords changeRecords = new MAccountChangeRecords();
                changeRecords.setAmount(balanceChange);
                changeRecords.setType(type);
                changeRecords.setAccountForward(balanceBefore);
                changeRecords.setAccountBack(balanceAfter);
                changeRecords.setUid(String.valueOf(mUser.getUid()));
                changeRecords.setDescription(userName+"[后台重新设置余额]");
                changeRecords.setTransactionType(1);
                accountChangeRecordsService.insertMAccountChangeRecords(changeRecords);
                log.info("账变记录已保存");
            } catch (Exception e) {
                log.error("保存账变记录时发生异常: ", e);
            }

            log.info("用户余额设置成功");
            return AjaxResult.success("余额更新成功");
        } catch (Exception e) {
            log.error("设置用户余额时发生异常: ", e);
            return AjaxResult.error("系统未知错误，请反馈给管理员");
        }
    }


    /**
     * 查询用户列表
     */
    @GetMapping("/list")
    public TableDataInfo list(MUser mUser)
    {
        startPage();
        List<MUser> list = mUserService.selectMUserList(mUser);
        TableDataInfo dataTable = getDataTable(list);
        List<MUser> rows = (List<MUser>) dataTable.getRows();
        rows.forEach(item -> {
            UserGrade userGrade = userGradeService.getOne(new LambdaQueryWrapper<UserGrade>()
                    .eq(UserGrade::getSortNum,item.getLevel()));
            if (userGrade != null) {
                item.setLevelName(userGrade.getGradeName());
            } else {
                item.setLevelName("暂无");
            }
        });
        dataTable.setRows(rows);
        return dataTable;
    }

    /**
     * 导出用户列表
     */
    @Log(title = "用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MUser mUser)
    {
        List<MUser> list = mUserService.selectMUserList(mUser);
        ExcelUtil<MUser> util = new ExcelUtil<MUser>(MUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * 获取用户详细信息
     */
    @GetMapping(value = "/{uid}")
    public AjaxResult getInfo(@PathVariable("uid") Long uid)
    {
        return success(mUserService.selectMUserByUid(uid));
    }

    /**
     * 获取前3级用户下级
     */
    @GetMapping("/getUpToFourLevelInviters")
    public AjaxResult getUpToFourLevelInviters(HttpServletRequest request) {
        // 1. 获取当前登录用户信息
        Long uid = tokenService.getLoginUser(request).getmUser().getUid();
        MUser currentUser = mUserService.selectMUserByUid(uid);
        if (currentUser == null) {
            return AjaxResult.error("当前用户不存在");
        }
        String invitationCode = currentUser.getInvitationCode();
        if (invitationCode == null) {
            // 构建空结果（各级数组为空）
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("level1", new ArrayList<>());
            emptyResult.put("level2", new ArrayList<>());
            emptyResult.put("level3", new ArrayList<>());
            emptyResult.put("totalAccountBalance", BigDecimal.ZERO);
            emptyResult.put("totalRecharge", BigDecimal.ZERO);
            emptyResult.put("teamTotal", 0);
            emptyResult.put("totalProfit", BigDecimal.ZERO);
            emptyResult.put("todayNewUsers", 0);
            return AjaxResult.success(emptyResult);
        }

        // 2. 初始化各级列表（分别存储1-3级数据）
        List<MUser> level1Users = new ArrayList<>();
        List<MUser> level2Users = new ArrayList<>();
        List<MUser> level3Users = new ArrayList<>();
        List<String> currentLevelCodes = Collections.singletonList(invitationCode);
        int hierarchy = 0;

        while (!currentLevelCodes.isEmpty() && hierarchy < 3) {
            hierarchy++;

            List<MUser> nextLevelUsers = mUserService.list(
                    new LambdaQueryWrapper<MUser>()
                            .in(MUser::getInviterCode, currentLevelCodes)
            );

            if (nextLevelUsers.isEmpty()) break;

            final int currentHierarchy = hierarchy;
            nextLevelUsers.forEach(user -> user.setHierarchy(currentHierarchy));

            // 按层级添加到对应列表
            switch (currentHierarchy) {
                case 1:
                    level1Users.addAll(nextLevelUsers);
                    break;
                case 2:
                    level2Users.addAll(nextLevelUsers);
                    break;
                case 3:
                    level3Users.addAll(nextLevelUsers);
                    break;
            }

            // 准备下一级查询的邀请码
            currentLevelCodes = nextLevelUsers.stream()
                    .map(MUser::getInvitationCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        // 合并所有层级数据用于统计
        List<MUser> allUsers = new ArrayList<>();
        allUsers.addAll(level1Users);
        allUsers.addAll(level2Users);
        allUsers.addAll(level3Users);

        // 3. 计算各项团队统计数据
        BigDecimal totalAccountBalance = calculateTotalBalance(allUsers);
        int teamTotal = allUsers.size();
        BigDecimal totalRecharge = calculateTotalRecharge(allUsers);
        BigDecimal totalProfit = calculateTotalProfit(allUsers);
        int todayNewUsers = countTodayNewUsers(allUsers);

        // 4. 封装各级数组和统计结果
        Map<String, Object> result = new HashMap<>();

        result.put("level1", level1Users);  // 1级数据数组
        result.put("level2", level2Users);  // 2级数据数组
        result.put("level3", level3Users);  // 3级数据数组
        result.put("totalAccountBalance", totalAccountBalance);
        result.put("totalRecharge", totalRecharge);
        result.put("teamTotal", teamTotal);
        result.put("totalProfit", totalProfit);
        result.put("todayNewUsers", todayNewUsers);

        return AjaxResult.success(result);
    }

    /**
     * 获取团队详细统计信息
     */
    @GetMapping("/teamStatistics")
    public AjaxResult getTeamStatistics(HttpServletRequest request) {
        try {
            // 1. 获取当前登录用户信息
            Long currentUserId = tokenService.getLoginUser(request).getmUser().getUid();
            MUser currentUser = mUserService.selectMUserByUid(currentUserId);

            if (currentUser == null) {
                return AjaxResult.error("当前用户不存在");
            }

            String invitationCode = currentUser.getInvitationCode();
            if (invitationCode == null) {
                return AjaxResult.success(new TeamStatisticsVO());
            }

            // 2. 初始化统计对象
            TeamStatisticsVO teamStats = new TeamStatisticsVO();

            // 3. 获取所有下级用户（1-3级）
            Map<String, Object> teamData = getTeamUsersWithDetails(invitationCode);

            @SuppressWarnings("unchecked")
            List<MUser> level1Users = (List<MUser>) teamData.get("level1");
            @SuppressWarnings("unchecked")
            List<MUser> level2Users = (List<MUser>) teamData.get("level2");
            @SuppressWarnings("unchecked")
            List<MUser> level3Users = (List<MUser>) teamData.get("level3");
            @SuppressWarnings("unchecked")
            List<MUser> allTeamUsers = (List<MUser>) teamData.get("allUsers");

            // 4. 计算各项统计数据
            // 团队总人数
            teamStats.setTeamTotalCount(allTeamUsers.size());

            // 直推人数（一级人数）
            teamStats.setDirectPushCount(level1Users.size());
            teamStats.setLevel1Count(level1Users.size());
            teamStats.setLevel2Count(level2Users.size());
            teamStats.setLevel3Count(level3Users.size());

            // 今日新增人数
            teamStats.setTodayNewCount(countTodayNewUsers(allTeamUsers));

            // 活跃人数（这里定义为有交易记录的用户，您可以根据实际需求调整）
            teamStats.setActiveCount(countActiveUsers(allTeamUsers));

            // 团队总充值
            teamStats.setTeamTotalRecharge(calculateTotalRecharge(allTeamUsers));

            // 团队总收益
            teamStats.setTeamTotalProfit(calculateTotalProfit(allTeamUsers));

            // 下级佣金（这里可以理解为下级用户产生的总收益）
            teamStats.setSubordinateCommission(teamStats.getTeamTotalProfit());

            // 总资产（当前用户自己的账户余额 + 下级佣金）
            BigDecimal selfBalance = currentUser.getAccountBalance() != null ?
                    currentUser.getAccountBalance() : BigDecimal.ZERO;
            BigDecimal subordinateCommission = teamStats.getSubordinateCommission() != null ?
                    teamStats.getSubordinateCommission() : BigDecimal.ZERO;

            teamStats.setTotalCommission(selfBalance.add(subordinateCommission));

            // 5. 转换用户信息为VO对象
            teamStats.setLevel1Users(convertToUserInfoVO(level1Users));
            teamStats.setLevel2Users(convertToUserInfoVO(level2Users));
            teamStats.setLevel3Users(convertToUserInfoVO(level3Users));

            return AjaxResult.success(teamStats);

        } catch (Exception e) {
            log.error("获取团队统计信息失败", e);
            return AjaxResult.error("获取团队统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取团队用户详细信息
     */
    private Map<String, Object> getTeamUsersWithDetails(String invitationCode) {
        Map<String, Object> result = new HashMap<>();

        // 初始化各级列表
        List<MUser> level1Users = new ArrayList<>();
        List<MUser> level2Users = new ArrayList<>();
        List<MUser> level3Users = new ArrayList<>();
        List<String> currentLevelCodes = Collections.singletonList(invitationCode);
        int hierarchy = 0;

        while (!currentLevelCodes.isEmpty() && hierarchy < 3) {
            hierarchy++;

            List<MUser> nextLevelUsers = mUserService.list(
                    new LambdaQueryWrapper<MUser>()
                            .in(MUser::getInviterCode, currentLevelCodes)
            );

            if (nextLevelUsers.isEmpty()) break;

            final int currentHierarchy = hierarchy;
            nextLevelUsers.forEach(user -> user.setHierarchy(currentHierarchy));

            // 按层级添加到对应列表
            switch (currentHierarchy) {
                case 1:
                    level1Users.addAll(nextLevelUsers);
                    break;
                case 2:
                    level2Users.addAll(nextLevelUsers);
                    break;
                case 3:
                    level3Users.addAll(nextLevelUsers);
                    break;
            }

            // 准备下一级查询的邀请码
            currentLevelCodes = nextLevelUsers.stream()
                    .map(MUser::getInvitationCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        // 合并所有层级数据
        List<MUser> allUsers = new ArrayList<>();
        allUsers.addAll(level1Users);
        allUsers.addAll(level2Users);
        allUsers.addAll(level3Users);

        result.put("level1", level1Users);
        result.put("level2", level2Users);
        result.put("level3", level3Users);
        result.put("allUsers", allUsers);

        return result;
    }

    /**
     * 统计活跃用户数（有交易记录的用户）
     */
    private int countActiveUsers(List<MUser> teamUsers) {
        if (teamUsers.isEmpty()) {
            return 0;
        }

        List<Long> userIds = teamUsers.stream()
                .map(MUser::getUid)
                .collect(Collectors.toList());

        // 查询有充值记录的用户数
        LambdaQueryWrapper<MMoneyInvestWithdraw> rechargeWrapper = new LambdaQueryWrapper<>();
        rechargeWrapper.in(MMoneyInvestWithdraw::getUserId, userIds);
        rechargeWrapper.eq(MMoneyInvestWithdraw::getType, "1"); // 充值记录

        long rechargeUserCount = mMoneyInvestWithdrawService.count(rechargeWrapper);

        // 查询有订单记录的用户数
        if (!userIds.isEmpty()) {
            LambdaQueryWrapper<OrderReceiveRecord> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.in(OrderReceiveRecord::getUserId, userIds);
            long orderUserCount = orderRecordMapper.selectCount(orderWrapper);

            // 合并去重（有充值或有订单的用户视为活跃用户）
            Set<Long> activeUserIds = new HashSet<>();

            // 获取有充值记录的用户ID
            List<MMoneyInvestWithdraw> rechargeRecords = mMoneyInvestWithdrawService.list(rechargeWrapper);
            rechargeRecords.forEach(record -> activeUserIds.add(record.getUserId()));

            // 获取有订单记录的用户ID
            List<OrderReceiveRecord> orderRecords = orderRecordMapper.selectList(orderWrapper);
            orderRecords.forEach(record -> activeUserIds.add(record.getUserId()));

            return activeUserIds.size();
        }

        return (int) rechargeUserCount;
    }

    /**
     * 转换用户信息为VO对象
     */
    private List<UserInfoVO> convertToUserInfoVO(List<MUser> users) {
        if (users == null || users.isEmpty()) {
            return new ArrayList<>();
        }

        return users.stream().map(user -> {
            UserInfoVO userInfo = new UserInfoVO();
            userInfo.setUid(user.getUid());
            userInfo.setUsername(user.getLoginAccount());
            userInfo.setPhoneNumber(user.getPhoneNumber());
            userInfo.setInvitationCode(user.getInvitationCode());
            userInfo.setAccountBalance(user.getAccountBalance() != null ? user.getAccountBalance() : BigDecimal.ZERO);
            userInfo.setCreateTime(user.getCreateTime() != null ? DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", user.getCreateTime()) : "");

            // 计算用户累计充值
            BigDecimal totalRecharge = calculateUserTotalRecharge(user.getUid());
            userInfo.setTotalRecharge(totalRecharge);

            // 计算用户累计收益
            BigDecimal totalProfit = calculateUserTotalProfit(user.getUid());
            userInfo.setTotalProfit(totalProfit);

            return userInfo;
        }).collect(Collectors.toList());
    }

    /**
     * 计算单个用户累计充值
     */
    private BigDecimal calculateUserTotalRecharge(Long userId) {
        LambdaQueryWrapper<MMoneyInvestWithdraw> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MMoneyInvestWithdraw::getUserId, userId);
        wrapper.eq(MMoneyInvestWithdraw::getType, "1");
        wrapper.eq(MMoneyInvestWithdraw::getStatus, 1); // 成功的充值

        List<MMoneyInvestWithdraw> records = mMoneyInvestWithdrawService.list(wrapper);
        return records.stream()
                .map(MMoneyInvestWithdraw::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算单个用户累计收益
     */
    private BigDecimal calculateUserTotalProfit(Long userId) {
        LambdaQueryWrapper<OrderReceiveRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderReceiveRecord::getUserId, userId);
        // wrapper.eq(OrderReceiveRecord::getProcessStatus, 1); // 错误：应该是字符串"Success"
        wrapper.eq(OrderReceiveRecord::getProcessStatus, "Success"); // 正确

        List<OrderReceiveRecord> records = orderRecordMapper.selectList(wrapper);
        return records.stream()
                .map(OrderReceiveRecord::getProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * 累加用户列表中所有用户的accountBalance（处理null值，避免空指针）
     */
    private BigDecimal calculateTotalBalance(List<MUser> userList) {
        return userList.stream()
                .map(user -> user.getAccountBalance() == null ? BigDecimal.ZERO : user.getAccountBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // 计算团队总充值（核心逻辑）
    private BigDecimal calculateTotalRecharge(List<MUser> teamUsers) {
        if (teamUsers.isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<Long> teamUserIds = teamUsers.stream()
                .map(MUser::getUid)
                .collect(Collectors.toList());

        LambdaQueryWrapper<MMoneyInvestWithdraw> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MMoneyInvestWithdraw::getUserId, teamUserIds);
        wrapper.eq(MMoneyInvestWithdraw::getType, "1");  // 充值类型为字符串"1"
        wrapper.eq(MMoneyInvestWithdraw::getStatus, 1);  // 成功状态为整数1

        List<MMoneyInvestWithdraw> rechargeRecords = mMoneyInvestWithdrawService.list(wrapper);
        return rechargeRecords.stream()
                .map(MMoneyInvestWithdraw::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // 计算团队总收益（核心逻辑）
    private BigDecimal calculateTotalProfit(List<MUser> teamUsers) {
        if (teamUsers.isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<Long> teamUserIds = teamUsers.stream()
                .map(MUser::getUid)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (teamUserIds.isEmpty()) {
            return BigDecimal.ZERO;
        }

        LambdaQueryWrapper<OrderReceiveRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrderReceiveRecord::getUserId, teamUserIds);
        wrapper.eq(OrderReceiveRecord::getProcessStatus, "Success");  // 成功状态为字符串"Success"

        List<OrderReceiveRecord> orderRecords = orderRecordMapper.selectList(wrapper);
        return orderRecords.stream()
                .map(OrderReceiveRecord::getProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 统计今日新增用户（今天注册的团队用户）
     */
    private int countTodayNewUsers(List<MUser> teamUsers) {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        Date todayStartDate = Date.from(todayStart.atZone(ZoneId.systemDefault()).toInstant());
        Date todayEndDate = Date.from(todayEnd.atZone(ZoneId.systemDefault()).toInstant());

        return (int) teamUsers.stream()
                .filter(user -> {
                    Date createTime = user.getCreateTime();
                    return createTime != null && createTime.after(todayStartDate) && createTime.before(todayEndDate);
                })
                .count();
    }

    // 重构 buildResult：整合所有团队统计数据
    private AjaxResult buildResult(List<MUser> teamUsers,
                                   BigDecimal totalBalance,
                                   BigDecimal totalRecharge,
                                   int teamTotal,
                                   BigDecimal totalProfit,
                                   int todayNewUsers) {
        Map<String, Object> data = new HashMap<>();
        data.put("invitedUsers", teamUsers);
        data.put("totalAccountBalance", totalBalance);
        data.put("totalRecharge", totalRecharge);
        data.put("teamTotal", teamTotal);
        data.put("totalProfit", totalProfit);
        data.put("todayNewUsers", todayNewUsers);
        data.put("levelCount", countUsersByLevel(teamUsers));
        return AjaxResult.success(data);
    }

    /**
     * 统计各层级的用户数量
     */
    private Map<Integer, Long> countUsersByLevel(List<MUser> userList) {
        return userList.stream()
                .collect(Collectors.groupingBy(MUser::getHierarchy, Collectors.counting()));
    }



    /**
     * 获取所有上级
     * @param inviterCode
     * @return
     */
    @GetMapping(value = "getAllSuperiorUids")
    public AjaxResult getAllSuperiorUids(String inviterCode) {
        List<MUser> uidList = new ArrayList<>();
        String currentInviterCode = inviterCode;
        while (currentInviterCode != null) {
            MUser inviter = mUserService.getOne(
                    new LambdaQueryWrapper<MUser>()
                            .eq(MUser::getInvitationCode, currentInviterCode)
            );
            if (inviter == null) {
                break;
            }


            UserGrade userGrade = userGradeService.getOne(new LambdaQueryWrapper<UserGrade>()
                    .eq(UserGrade::getSortNum,inviter.getLevel()));
            if (userGrade != null) {
                inviter.setLevelName(userGrade.getGradeName());
            } else {
                inviter.setLevelName("暂无");
            }
            uidList.add(inviter);

            // 向上查找下一个上级
            currentInviterCode = inviter.getInviterCode();
        }

        return success(uidList);
    }


    @GetMapping(value = "editStatus/{uid}")
    public AjaxResult editStatus(@PathVariable("uid") Long uid)
    {
        MUser byId = mUserService.selectMUserByUid(uid);
        byId.setStatus(byId.getStatus() == 1 ? 0 : 1);
        return success(mUserService.updateMUser(byId));
    }


    @GetMapping(value = "editRegisterType/{uid}")
    public AjaxResult editRegisterType(@PathVariable("uid") Long uid)
    {
        MUser byId = mUserService.selectMUserByUid(uid);
        byId.setRegisterType(byId.getRegisterType().equals("0") ? "1" : "0");

        //同步修改提现表中的“用户类型”
        Map<String,Object> param = new HashMap<>();
        param.put("userId", uid);
        param.put("userType", byId.getRegisterType());
        mMoneyInvestWithdrawService.updateUserInfoByUserId(param);

        return success(mUserService.updateMUser(byId));
    }

    /**
     * 新增用户
     */
    @Log(title = "用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MUser mUser)
    {

        return toAjax(mUserService.insertMUser(mUser));
    }

    /**
     * 修改用户
     */
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MUser mUser)
    {
        return toAjax(mUserService.updateMUser(mUser));
    }

    /**
     * 修改用户信息，不牵涉其他业务
     * @param mUser
     * @return
     */
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PostMapping("updateMUserSimple")
    public AjaxResult updateMUserSimple(MUser mUser) {
        return toAjax(mUserService.updateMUserSimple(mUser));
    }

    /**
     * 删除用户
     */
    @Log(title = "用户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{uids}")
    public AjaxResult remove(@PathVariable Long[] uids)
    {
        return toAjax(mUserService.deleteMUserByUids(uids));
    }

    /**
     * 前端修改用户信息，不传id
     */
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PostMapping("/updateUserBank")
    public AjaxResult updateUserFront(HttpServletRequest request, @RequestBody MUser mUser)
    {
        Long userId = tokenService.getLoginUser(request).getmUser().getUid();
        mUser.setUid(userId);
        return toAjax(mUserService.updateUserBank(mUser));
    }

    /**
     * 前端修改用户信息，不传id，不检查其他信息，直接保存
     */
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PostMapping("updateUserSimpleFront")
    public AjaxResult updateUserSimpleFront(HttpServletRequest request, @RequestBody MUser mUser)
    {
        Long userId = tokenService.getLoginUser(request).getmUser().getUid();
        mUser.setUid(userId);
        return toAjax(mUserService.updateMUserSimple(mUser));
    }

    /**
     * 修改用户连单数量
     */
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PutMapping("updateMultiOrderNum")
    public AjaxResult updateMultiOrderNum(@RequestBody MUser mUser)
    {
        return toAjax(mUserService.updateMultiOrderNum(mUser));
    }

    /**
     * 修改当前用户的等级
     */
    @Log(title = "修改当前用户的等级", businessType = BusinessType.UPDATE)
    @PostMapping("updateGrade")
    public AjaxResult updateGradeByUser(HttpServletRequest request, @RequestParam @NotNull Long gradeId)
    {
        Long userId = tokenService.getLoginUser(request).getmUser().getUid();
        return toAjax(mUserService.updateGrade(gradeId, userId));
    }

    /**
     * 单个上传(头像)
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file) throws Exception
    {
        try
        {
            //从ThreadLocal获取用户uid
            Long uId = SecurityUtils.getUserId();
            log.info("用户id:{}",uId);
            if (uId == null){
                return AjaxResult.error("用户未登录");
            }

            int result =  mUserService.updateUserAvatar(uId,file);

            if (result <= 0){
                return AjaxResult.error("头像更新失败");
            }

            SysConfig imageUrl = configMapper.checkConfigKeyUnique("image_url");
            String fileName = file.getOriginalFilename(); // 需要调整，实际文件名在Service中生成
            String url = imageUrl.getConfigValue() + fileName;

            AjaxResult ajax = AjaxResult.success("上传成功");
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("userId", uId);
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 修改用户信息
     * @param mUser
     * @return
     */
    @PutMapping("/updateUser")
    public AjaxResult updateUser(@RequestBody MUser mUser){
        mUser.setUid(getUserId());
        return toAjax(mUserService.updateUser(mUser));
    }

    /**
     * 修改用户登录密码
     * @param passwordUpdateVO
     * @return
     */
    @PutMapping("/updateLoginPassword")
    public AjaxResult updateLoginPassword(@RequestBody PasswordUpdateVO passwordUpdateVO){
        //通过Security获取用户uid
        Long uid = SecurityUtils.getUserId();
        //判断传递的数据是否为空
        if (passwordUpdateVO.getOldPassword()==""){
            throw new ServiceException("旧密码不能为空");
        }
        if (passwordUpdateVO.getNewPassword()==""){
            throw new ServiceException("新密码不能为空");
        }
        // 校验新密码和确认密码是否一致
        if (!passwordUpdateVO.getNewPassword().equals(passwordUpdateVO.getConfirmPassword())) {
            throw new ServiceException("俩次输入的新密码不一致");
        }
        // 调用服务层修改密码
        return toAjax(mUserService.updatePassword(uid, passwordUpdateVO.getOldPassword(), passwordUpdateVO.getNewPassword()));
    }

    /**
     * 修改资金密码
     * @param passwordUpdateVO
     * @return
     */
    @PutMapping("/updateFundPassword")
    public AjaxResult updateFundPassword(@RequestBody PasswordUpdateVO passwordUpdateVO){
        //通过Security获取用户uid
        Long uid = SecurityUtils.getUserId();
        // 调用服务层修改密码
        return toAjax(mUserService.updateFoundPassword(uid, passwordUpdateVO.getNewPassword()));
    }

    /**
     * 用户信息
     * @return
     */
    @GetMapping("/listInformation")
    public AjaxResult listInformation(){
        // 获取前50条用户数据
        List<MUser> users = mUserService.listInformain();

        // 初始化随机数生成器
        Random random = new Random();

        // 为每个用户添加1-30的随机数，用Map存储需要返回的字段
        List<Map<String, Object>> result = users.stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();

            // 1. 添加用户原有字段（根据需要选择，这里示例添加id和username）
            userMap.put("id", user.getUid());
            userMap.put("username", user.getLoginAccount());
            userMap.put("image",user.getHeadImg());
            // 可根据实际需求添加其他字段，如createTime等

            // 2. 生成1-30的随机数并添加
            int randomNum = random.nextInt(30) + 1; // nextInt(30)生成0-29，+1后变为1-30
            userMap.put("randomNum", randomNum);

            return userMap;
        }).collect(Collectors.toList());

        return AjaxResult.success(result);
    }
}
