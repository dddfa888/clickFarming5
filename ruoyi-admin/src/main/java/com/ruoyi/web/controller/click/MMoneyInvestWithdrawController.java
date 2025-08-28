package com.ruoyi.web.controller.click;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.ruoyi.business.domain.OrderReceiveRecord;
import com.ruoyi.business.mapper.OrderReceiveRecordMapper;
import com.ruoyi.click.domain.MAccountChangeRecords;
import com.ruoyi.click.domain.MMoneyInvestWithdraw;
import com.ruoyi.click.domain.UserGrade;
import com.ruoyi.click.domain.vo.AccountQueryVO;
import com.ruoyi.click.domain.vo.BackOperateVo;
import com.ruoyi.click.domain.vo.WithdrawVo;
import com.ruoyi.click.domain.vo.WithdrawalVO;
import com.ruoyi.click.service.IMAccountChangeRecordsService;
import com.ruoyi.click.service.IMMoneyInvestWithdrawService;
import com.ruoyi.click.service.IMUserService;
import com.ruoyi.click.service.IUserGradeService;
import com.ruoyi.common.core.domain.entity.MUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DecimalUtil;
import com.ruoyi.common.utils.EncoderUtil;
import com.ruoyi.common.utils.RandomUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

import static com.ruoyi.common.utils.SecurityUtils.getUserId;

/**
 * 存款取款记录Controller
 *
 * @author ruoyi
 * @date 2025-06-19
 */
@RestController
@RequestMapping("/api/withdraw")
public class MMoneyInvestWithdrawController extends BaseController
{
    @Autowired
    private IMMoneyInvestWithdrawService mMoneyInvestWithdrawService;


    @Autowired
    private IMUserService mUserService;

    @Autowired
    private TokenService tokenService;


    @Autowired
    private IMAccountChangeRecordsService mAccountChangeRecordsService;

    @Autowired
    private OrderReceiveRecordMapper orderReceiveRecordMapper;
    @Autowired
    private IUserGradeService userGradeService;
    @Autowired
    private SysConfigMapper configMapper;

    /**
     * 获取个人的提现记录
     * @param request
     * @return
     */
    @GetMapping("/userList")
    public TableDataInfo userList(HttpServletRequest request) {
        startPage();
        Long userId = tokenService.getLoginUser(request).getmUser().getUid();
        MMoneyInvestWithdraw withdraw = new MMoneyInvestWithdraw();
        withdraw.setUserId(userId);
        List<MMoneyInvestWithdraw> list = mMoneyInvestWithdrawService.selectMMoneyInvestWithdrawList(withdraw);
        return getDataTable(list);
    }

    /**
     * 根据类型查询充值或提款记录
     * @param mMoneyInvestWithdraw
     * @return
     */
    @GetMapping("/userList2")
    public TableDataInfo userList2(MMoneyInvestWithdraw mMoneyInvestWithdraw) {
        startPage();
        MMoneyInvestWithdraw withdraw = new MMoneyInvestWithdraw();
        withdraw.setUserId(mMoneyInvestWithdraw.getUserId());
        withdraw.setType(mMoneyInvestWithdraw.getType());
        List<MMoneyInvestWithdraw> list = mMoneyInvestWithdrawService.selectMMoneyInvestWithdrawList(withdraw);
        return getDataTable(list);
    }


    /**
     * 个人详细咨询记录
     * @param accountQueryVO
     * @return
     */
    @GetMapping("/userList3")
    public TableDataInfo userList3(AccountQueryVO accountQueryVO) {
        startPage();
        MMoneyInvestWithdraw withdraw = new MMoneyInvestWithdraw();
        withdraw.setUserId(accountQueryVO.getUserId());
        withdraw.setType(accountQueryVO.getType());
        // 获取时间范围参数
        LocalDateTime startTime = accountQueryVO.getStartTime();
        LocalDateTime endTime = accountQueryVO.getEndTime();
        // 调用带时间条件的查询方法
        List<MMoneyInvestWithdraw> list = mMoneyInvestWithdrawService.selectWithdrawByTimeRange(withdraw, startTime, endTime);
        return getDataTable(list);
    }


    /**
     * 查询存款取款记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(MMoneyInvestWithdraw mMoneyInvestWithdraw)
    {
        startPage();
        List<MMoneyInvestWithdraw> list = mMoneyInvestWithdrawService.selectMMoneyInvestWithdrawList(mMoneyInvestWithdraw);
        /*TableDataInfo dataTable = getDataTable(list);
        List<MMoneyInvestWithdraw> rows = (List<MMoneyInvestWithdraw>) dataTable.getRows();
        rows.forEach(item -> {
            MUser mUser = mUserService.selectMUserByUid(item.getUserId());
            if (mUser != null) {
                item.setRegisterType(mUser.getRegisterType());
            }
        });
        dataTable.setRows(rows);*/
        return getDataTable(list);
    }

    /**
     * 导出存款取款记录列表
     */
    @Log(title = "存款取款记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MMoneyInvestWithdraw mMoneyInvestWithdraw)
    {
        List<MMoneyInvestWithdraw> list = mMoneyInvestWithdrawService.selectMMoneyInvestWithdrawList(mMoneyInvestWithdraw);
        ExcelUtil<MMoneyInvestWithdraw> util = new ExcelUtil<MMoneyInvestWithdraw>(MMoneyInvestWithdraw.class);
        util.exportExcel(response, list, "存款取款记录数据");
    }

    /**
     * 获取存款取款记录详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(mMoneyInvestWithdrawService.selectMMoneyInvestWithdrawById(id));
    }

    /**
     * 充值
     * @param userId
     * @param amount
     * @return
     */
    @PostMapping("/recharge/{userId}/{amount}")
    public AjaxResult recharge(@PathVariable("userId") Long userId, @PathVariable("amount") BigDecimal amount){
        if (amount == null) {
            return AjaxResult.error("充值金额不能为空");
        }
        // 拦截 0 和负数（前端传 0、-2 等非法值）
        // BigDecimal 不能用 >/< 直接比较，必须用 compareTo：返回 1=大于，0=等于，-1=小于
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return AjaxResult.error("充值金额必须大于0，请重新输入");
        }
        // 正确：判断金额是否超过100万（>=1000000）
        if (amount.compareTo(new BigDecimal("1000000")) >= 0) {
            return AjaxResult.error("充值金额最大为1000000，请重新输入");
        }
        mMoneyInvestWithdrawService.insertRecharge(userId,amount);

        return success();
    }

    /**
     * 提款
     * @param withdrawalVO
     * @return
     */
    @PostMapping("/draw")
    public AjaxResult draw(@RequestBody WithdrawalVO withdrawalVO){
        MUser mUser = mUserService.selectMUserByUid(withdrawalVO.getUserId());
        // 1. 账户余额校验（BigDecimal必须用compareTo比较）
        BigDecimal balance = mUser.getAccountBalance();

        // 2. 先判断金额是否为null
        if (withdrawalVO.getAmount() == null) {
            return AjaxResult.error("提款金额不能为空");
        }

        // 新增：判断金额是否为整数（小数部分必须为0）
        if (withdrawalVO.getAmount().stripTrailingZeros().scale() > 0) {
            return AjaxResult.error("提款金额必须为整数，不能包含小数");
        }

        // 3. 再判断金额是否大于0（提款场景下的正确提示）
        if (withdrawalVO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return AjaxResult.error("提款金额必须大于0，请重新输入");
        }

        // 4. 资金密码非空校验（VO中存储的是前端传递的明文密码）
        String plainFundPassword = withdrawalVO.getFundPassword();
        if (StringUtils.isBlank(plainFundPassword)) {
            return AjaxResult.error("请输入资金密码");
        }

        // 5. 核心验证：使用BCrypt比对明文与密文（自动处理盐值）
        if (!BCrypt.checkpw(plainFundPassword, mUser.getFundPassword())) {
            return AjaxResult.error("资金密码错误，请重新输入");
        }

        // 6. 比较：如果余额 < 提款金额
        if (balance.compareTo(withdrawalVO.getAmount()) < 0) {
            return AjaxResult.error("账户余额不足，无法提款");
        }

        mMoneyInvestWithdrawService.insertDraw(withdrawalVO);

        return success();
    }

    /**
     * 同意提现或充值
     * @param backOperateVo
     * @return
     */
    @PostMapping(value = "/agree")
    public AjaxResult agreeRecharge(@Validated @RequestBody BackOperateVo  backOperateVo) {

        MMoneyInvestWithdraw withdraw = mMoneyInvestWithdrawService.selectMMoneyInvestWithdrawById(backOperateVo.getId());
        if (withdraw == null) {
            return error("提现订单不存在");
        }
        //提款
        if (withdraw.getType() != null && withdraw.getType().equals("0")){
            // 3.2 校验：提款金额不能大于账户余额
            if (withdraw.getAmount().compareTo(withdraw.getAccountForward()) > 0) {
                return error("提款金额超过账户余额，无法操作");
            }
            withdraw.setStatus(1);
            mMoneyInvestWithdrawService.updateMMoneyInvestWithdrawWithdrawal( withdraw);
        }
        //充值
        if (withdraw.getType() != null && withdraw.getType().equals("1")){
            withdraw.setStatus(1);
            mMoneyInvestWithdrawService.updateMMoneyInvestWithdrawRecharge( withdraw);
        }
        return success();
    }

    /**
     * 一键同意所有员工提现
     * @return
     */
    @GetMapping(value = "/oneClickAgree")
    public AjaxResult oneClickAgree(@RequestParam String type) {
        MUser mUser = new MUser();
        mUser.setRegisterType(type);
        List<Long> userIdList = mUserService.selectMUserList(mUser).stream().map(MUser::getUid).collect(Collectors.toList());
        if(userIdList.isEmpty()){
            return error("暂无员工");
        }
        LambdaQueryWrapper<MMoneyInvestWithdraw> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MMoneyInvestWithdraw::getUserId,userIdList);
        wrapper.eq(MMoneyInvestWithdraw::getStatus,0);
        List<MMoneyInvestWithdraw> list = mMoneyInvestWithdrawService.list(wrapper);
        if(list.isEmpty()){
            return error("暂无员工提现");
        }
        list.forEach(item -> {
            item.setStatus(1);
        });
        mMoneyInvestWithdrawService.updateBatchById(list);
        return success(list);
    }

    /**
     * 驳回
     * @param backOperateVo
     * @return
     */
    @PostMapping(value = "/reject")
    public AjaxResult reject(@Validated @RequestBody BackOperateVo  backOperateVo) {
        MMoneyInvestWithdraw withdraw = mMoneyInvestWithdrawService.selectMMoneyInvestWithdrawById(backOperateVo.getId());
        if (withdraw == null) {
            return error("提现订单不存在");
        }
        withdraw.setReasonForRejection(backOperateVo.getNotes());
        withdraw.setStatus(2);
        mMoneyInvestWithdrawService.updateMMoneyInvestWithdraw( withdraw);

        // 驳回金额返还
        MUser mUser = mUserService.selectMUserByUid(withdraw.getUserId());
        BigDecimal accountForward = mUser.getAccountBalance();
        BigDecimal accountBack = DecimalUtil.add(accountForward, withdraw.getAmount());
        mUser.setAccountBalance(accountBack);
        mUserService.updateMUser(mUser);

        // 日志
        MAccountChangeRecords mAccountChangeRecords = new MAccountChangeRecords();
        mAccountChangeRecords.setLoginAccount(mUser.getLoginAccount());
        mAccountChangeRecords.setUid(String.valueOf(mUser.getUid()));
        mAccountChangeRecords.setAmount(withdraw.getAmount());
        mAccountChangeRecords.setAccountBack(accountBack);
        mAccountChangeRecords.setAccountForward(accountForward);
        mAccountChangeRecords.setType(0);
        mAccountChangeRecords.setTransactionType(4);
        mAccountChangeRecords.setDescription("提现订单["+withdraw.getOrderId()+"] 驳回");
        mAccountChangeRecordsService.insertMAccountChangeRecords(mAccountChangeRecords);
        return success();
    }

    /**
     * 新增存款取款记录
     */
    @Log(title = "提现", businessType = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("add")
    public synchronized AjaxResult add(HttpServletRequest request,@Validated @RequestBody WithdrawVo withdrawVo) {
        String amountStr = withdrawVo.getAmount();
        Assert.notEmpty(amountStr, "请填写提现数额");//user
        BigDecimal withdrawAmount = null;
        try{
            withdrawAmount = DecimalUtil.parseNumberBothCommaPoint(amountStr);
        }catch (Exception e){
            throw new ServiceException("取款数额格式错误，无法解析");//user
        }
        SysConfig minWithdrawConfig = configMapper.checkConfigKeyUnique("minWithdrawAmount");
        if(withdrawAmount.doubleValue() < Double.parseDouble(minWithdrawConfig.getConfigValue())){
            throw new ServiceException("最低提款额为50美元");//user
        }
        Long userId = tokenService.getLoginUser(request).getmUser().getUid();

        MUser mUser = mUserService.selectMUserByUid(userId);
        if(mUser.getStatus() != 1){
            throw new ServiceException("用户被禁用");//user
        }
        boolean matches = EncoderUtil.matches(withdrawVo.getFundPassword(), mUser.getFundPassword());
        if(!matches){
            throw new ServiceException("资金密码错误");//user
        }
        BigDecimal accountForward = mUser.getAccountBalance();
        if (accountForward.compareTo(withdrawAmount) < 0) {
            return AjaxResult.error("余额不足");//user
        }
        //未处理
        MMoneyInvestWithdraw withdraw1 = mMoneyInvestWithdrawService
                .lambdaQuery()
                .eq(MMoneyInvestWithdraw::getUserId, userId)
                .eq(MMoneyInvestWithdraw::getStatus, 0)
                .eq(MMoneyInvestWithdraw::getType, 0)
                .last("LIMIT 1")
                .one();
        if (withdraw1!=null){
            throw new ServiceException("已有提现订单,请勿重复申请");//user
        }

        //今天的提现
        MMoneyInvestWithdraw withdraw2=mMoneyInvestWithdrawService.getTodayWithdraw(userId);
        if(withdraw2!=null){
            throw new ServiceException("今日提现已达最大次数");//user
        }
        OrderReceiveRecord orderParam = new OrderReceiveRecord();
        orderParam.setUserId(mUser.getUid());
        orderParam.setProcessStatus(OrderReceiveRecord.PROCESS_STATUS_WAIT);
        long unfinishedCount = orderReceiveRecordMapper.countNum(orderParam);
        if(unfinishedCount>0){
            throw new ServiceException("有订单未完成，不可提现");//user
        }
        // 用户当日刷单数达到等级规定的“每天购买的产品数量”，才可以提现
        UserGrade userGrade = userGradeService.getOne(new LambdaQueryWrapper<UserGrade>().eq(UserGrade::getSortNum,mUser.getLevel()));
        Assert.notNull(userGrade, "用户等级不存在");//user
        //获取今天完成的订单
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        String strToday = formatter.format(localDate);
        String strTomorrow = formatter.format(localDate.plusDays(1));
        Map<String,Object> param = new HashMap<>();
        param.put("userId", getUserId());
        param.put("date1", strToday);
        param.put("date2", strTomorrow);
        long finishNum = orderReceiveRecordMapper.countNumByUserDate(param);
        if( finishNum!= userGrade.getBuyProdNum()){
            throw new ServiceException("您尚未完成当天的申请数量");//user
        }

        checkBank(mUser);

        BigDecimal accountBack = DecimalUtil.subtract(accountForward, withdrawAmount);
        mUser.setAccountBalance(accountBack);
        mUserService.updateMUser(mUser);


        MMoneyInvestWithdraw mMoneyInvestWithdraw = new MMoneyInvestWithdraw();
        mMoneyInvestWithdraw.setAmount(withdrawAmount);
        mMoneyInvestWithdraw.setUserId(mUser.getUid());
        mMoneyInvestWithdraw.setUserName(mUser.getLoginAccount());
        mMoneyInvestWithdraw.setUserType(mUser.getRegisterType());
        mMoneyInvestWithdraw.setBankName(mUser.getBankName());
        mMoneyInvestWithdraw.setBankAccountName(mUser.getBankAccountName());
        mMoneyInvestWithdraw.setBankAccountNumber(mUser.getBankAccountNumber());
        mMoneyInvestWithdraw.setOrderId(RandomUtil.generateRandomNumber(16));
        mMoneyInvestWithdraw.setType("0");
        mMoneyInvestWithdraw.setStatus(0);
        mMoneyInvestWithdraw.setAccountForward(accountForward);
        mMoneyInvestWithdraw.setAccountBack(accountBack);
        mMoneyInvestWithdrawService.insertMMoneyInvestWithdraw(mMoneyInvestWithdraw);

        MAccountChangeRecords mAccountChangeRecords = new MAccountChangeRecords();
        mAccountChangeRecords.setUid(String.valueOf(mUser.getUid()));
        mAccountChangeRecords.setAmount(withdrawAmount);
        mAccountChangeRecords.setAccountBack(accountBack);
        mAccountChangeRecords.setAccountForward(accountForward);
        mAccountChangeRecords.setType(1);
        mAccountChangeRecords.setTransactionType(2);
        mAccountChangeRecords.setDescription("发起提现订单["+mMoneyInvestWithdraw.getOrderId()+"]");
        mAccountChangeRecordsService.insertMAccountChangeRecords(mAccountChangeRecords);
        return success();
    }

    private void checkBank(MUser mUser) {
        if (StringUtils.isAnyBlank(
                mUser.getBankName(),
                mUser.getBankAccountName(),
                mUser.getBankAccountNumber())) {
            throw new ServiceException("银行信息不完整，请填写银行名称、账户名称和账号");//user
        }
    }

    /**
     * 修改存款取款记录
     */
    @Log(title = "存款取款记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MMoneyInvestWithdraw mMoneyInvestWithdraw)
    {
        return toAjax(mMoneyInvestWithdrawService.updateMMoneyInvestWithdraw(mMoneyInvestWithdraw));
    }

    /**
     * 删除存款取款记录
     */
    @Log(title = "存款取款记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(mMoneyInvestWithdrawService.deleteMMoneyInvestWithdrawByIds(ids));
    }

    /**
     * 查询存款取款记录列表
     */
    @GetMapping("/sumAmountByUserDateType")
    public AjaxResult sumAmountByUserDateType(Map<String,Object> param)
    {
        return success(mMoneyInvestWithdrawService.sumAmountByUserDateType(param));
    }


    /**
     * 根据用户id修改用户信息
     */
    @Log(title = "根据用户id修改用户信息", businessType = BusinessType.UPDATE)
    @PutMapping(value="updateUserInfoByUserId")
    public AjaxResult updateUserInfoByUserId(Map<String,Object> param)
    {
        return toAjax(mMoneyInvestWithdrawService.updateUserInfoByUserId(param));
    }
}
