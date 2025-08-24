package com.ruoyi.web.controller.click;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.business.domain.MRewardRecord;
import com.ruoyi.business.service.IMRewardRecordService;
import com.ruoyi.click.domain.vo.PasswordUpdateVO;
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


    @GetMapping("/userInfo")
    public AjaxResult userInfo(HttpServletRequest request) {
        Long userId = tokenService.getLoginUser(request).getmUser().getUid();
        MUser mUser = mUserService.selectMUserByUid(userId);
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
    public AjaxResult setBalance(HttpServletRequest request, MUser mUser) {
        MUser originMUser = mUserService.selectMUserByUid(mUser.getUid());
        mUserService.updateMUserSimple(mUser);

        BigDecimal balanceBefore = originMUser.getAccountBalance();
        BigDecimal balanceAfter = mUser.getAccountBalance();
        String userName = tokenService.getLoginUser(request).getUser().getUserName();

        Integer type = null; // 0收入 1支出
        BigDecimal balanceChange = null;
        if(balanceBefore.compareTo(balanceAfter) < 0){
            type = 0;
            balanceChange = DecimalUtil.subtract(balanceAfter, balanceBefore);
        }else{
            type = 1;
            balanceChange = DecimalUtil.subtract(balanceBefore, balanceAfter);
        }

        // 日志记录
        MAccountChangeRecords changeRecords = new MAccountChangeRecords();
        changeRecords.setAmount(balanceChange);
        changeRecords.setType(type);
        changeRecords.setAccountForward(balanceBefore);
        changeRecords.setAccountBack(balanceAfter);
        changeRecords.setUid(String.valueOf(mUser.getUid()));
        changeRecords.setDescription(userName+"[后台重新设置余额]");
        changeRecords.setTransactionType(1);
        accountChangeRecordsService.insertMAccountChangeRecords(changeRecords);
        // 升级等级
        //mUserService.upgrade(mUser.getUid());
        return success();
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
     * 获取前4级用户下级
     */
    @GetMapping("/getUpToFourLevelInviters")
    public AjaxResult getUpToFourLevelInviters(HttpServletRequest request) {
        Long uid = tokenService.getLoginUser(request).getmUser().getUid();
        MUser currentUser = mUserService.selectMUserByUid(uid);
        String invitationCode = currentUser.getInvitationCode();

        List<MUser> result = new ArrayList<>();
        List<String> currentLevelCodes = Collections.singletonList(invitationCode);

        int hierarchy = 0;
        while (!currentLevelCodes.isEmpty() && hierarchy < 4) {
            hierarchy++;

            List<MUser> nextLevelUsers = mUserService.list(
                    new LambdaQueryWrapper<MUser>()
                            .in(MUser::getInviterCode, currentLevelCodes)
            );

            if (nextLevelUsers.isEmpty()) break;

            final int currentHierarchy = hierarchy; // 用 final 变量供 lambda 使用
            nextLevelUsers.forEach(user -> user.setHierarchy(currentHierarchy));

            result.addAll(nextLevelUsers);

            currentLevelCodes = nextLevelUsers.stream()
                    .map(MUser::getInvitationCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return AjaxResult.success(result);
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
    @PostMapping("updateUserBank")
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
     * 修改信用分
     * @param mUser
     * @return
     */
    @PostMapping("/updateCreditScore")
    public AjaxResult updateCreditScore(@RequestBody MUser mUser){
        return toAjax(mUserService.updateScore(mUser));
    }

    /**
     * 修改账户名
     * @param mUser
     * @return
     */
    @PutMapping("/updateLoginAccount")
    public AjaxResult updateLoginAccount(@RequestBody MUser mUser){
        return toAjax(mUserService.updateLoginAccount(mUser));
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
        return toAjax(mUserService.updateFoundPassword(uid, passwordUpdateVO.getOldPassword(), passwordUpdateVO.getNewPassword()));
    }

}
