package com.ruoyi.click.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.click.domain.MMoneyInvestWithdraw;
import com.ruoyi.click.mapper.MAccountChangeRecordsMapper;
import com.ruoyi.click.mapper.UserGradeMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.core.domain.entity.MUser;
import com.ruoyi.click.domain.MAccountChangeRecords;
import com.ruoyi.click.domain.UserGrade;
import com.ruoyi.click.domain.vo.UserRegisterModel;
import com.ruoyi.click.domain.vo.BalanceModel;
import com.ruoyi.click.mapper.MUserMapper;
import com.ruoyi.click.service.IMAccountChangeRecordsService;
import com.ruoyi.click.service.IMUserService;
import com.ruoyi.click.service.IUserGradeService;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户Service业务层处理
 *
 * @author ruoyi
 * @date 2025-06-15
 */
@Service
public class MUserServiceImpl extends ServiceImpl<MUserMapper, MUser>  implements IMUserService {
    @Autowired
    private MUserMapper mUserMapper;

    @Autowired
    private IUserGradeService userGradeService;
    @Autowired
    private UserGradeMapper userGradeMapper;
    @Autowired
    private IMAccountChangeRecordsService  accountChangeRecordsService;
    @Autowired
    private MAccountChangeRecordsMapper mAccountChangeRecordsMapper;
    @Autowired
    private SysConfigMapper configMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    /**
     * 查询用户
     *
     * @param uid 用户主键
     * @return 用户
     */
    @Override
    public MUser selectMUserByUid(Long uid)
    {
        return mUserMapper.selectMUserByUid(uid);
    }

    /**
     * 查询用户列表
     *
     * @param mUser 用户
     * @return 用户
     */
    @Override
    public List<MUser> selectMUserList(MUser mUser)
    {
        return mUserMapper.selectMUserList(mUser);
    }
    /**
     * 新增用户
     *
     * @param mUser 用户
     * @return 结果
     */
    @Override
    public int insertMUser(MUser mUser) {
        if (StringUtils.isNotBlank(mUser.getInviterCode())) {
            MUser inviterUser = this.getOne(
                    new LambdaQueryWrapper<MUser>()
                            .eq(MUser::getInvitationCode, mUser.getInviterCode())
            );

            if (inviterUser == null) {
                throw new ServiceException("邀请码无效");//user
            }

            mUser.setInviter(String.valueOf(inviterUser.getUid()));
            mUser.setInviterName(inviterUser.getLoginAccount());
            mUser.setInviterCode(inviterUser.getInvitationCode());


        }

        // 校验账号是否重复
        String loginAccount = mUser.getLoginAccount();
        if (StringUtils.isNotBlank(loginAccount) &&
                this.getOne(new LambdaQueryWrapper<MUser>().eq(MUser::getLoginAccount, loginAccount)) != null) {
            throw new ServiceException("账号已存在");//user
        }

        mUser.setBrushNumber(0);
        mUser.setAccountBalance(DecimalUtil.toBigDecimal(0));
        mUser.setLoginPassword(EncoderUtil.encoder(mUser.getLoginPassword()));
        mUser.setFundPassword(EncoderUtil.encoder(mUser.getFundPassword()));
        mUser.setInvitationCode(generateCode());
        mUser.setCreateTime(DateUtils.getNowDate());
        mUser.setStatus(1);
        return mUserMapper.insertMUser(mUser);
    }


    public  String generateCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            char letter = (char) ('A' + random.nextInt(26));
            sb.append(letter);
        }
        long count = this.count(new LambdaQueryWrapper<MUser>().eq(MUser::getInvitationCode, sb.toString()));
        if(count>0){
            generateCode();
        }
        return sb.toString();
    }

    /**
     * 修改用户
     *
     * @param mUser 用户
     * @return 结果
     */
    @Override
    public int updateMUser(MUser mUser)
    {
        mUser.setUpdateTime(DateUtils.getNowDate());
        MUser user = mUserMapper.selectMUserByUid(mUser.getUid());
        String loginAccount = mUser.getLoginAccount();

        if (!user.getLoginAccount().equals(loginAccount)) {
            MUser one1 = this.getByLoginAccount(mUser.getLoginAccount());
            if (one1 != null) {
                throw new ServiceException("账号已存在");
            }
        }

        // 修改点1：检查前端传递的登录密码是否为null
        if (mUser.getLoginPassword() != null) {
            // 只有前端传递了新密码时才加密
            if (!mUser.getLoginPassword().equals(user.getLoginPassword())) {
                mUser.setLoginPassword(EncoderUtil.encoder(mUser.getLoginPassword()));
            } else {
                // 密码相同，设置为null避免更新
                mUser.setLoginPassword(null);
            }
        } else {
            // 前端传递的密码为null，设置为null避免更新
            mUser.setLoginPassword(null);
        }

        // 修改点2：检查前端传递的资金密码是否为null
        if (mUser.getFundPassword() != null) {
            if (!mUser.getFundPassword().equals(user.getFundPassword())) {
                mUser.setFundPassword(EncoderUtil.encoder(mUser.getFundPassword()));
            } else {
                mUser.setFundPassword(null);
            }
        } else {
            mUser.setFundPassword(null);
        }

        if (mUser.getInviterCode() != null) {
            if (!user.getInviterCode().equals(mUser.getInviterCode())) {
                MUser one = this.getOne(new LambdaQueryWrapper<MUser>()
                        .eq(MUser::getInvitationCode, mUser.getInviterCode()));
                mUser.setInviter(String.valueOf(one.getUid()));
                mUser.setInviterName(one.getLoginAccount());
                mUser.setInviterCode(one.getInvitationCode());
            }
        }

        return mUserMapper.updateMUser(mUser);
    }

    /**
     * 修改用户，用于前端修改银行信息
     *
     * @param mUser 用户
     * @return 结果
     */
    @Override
    public int updateUserBank(MUser mUser)
    {
        mUser.setUpdateTime(DateUtils.getNowDate());
        MUser user = mUserMapper.selectMUserByUid(mUser.getUid());

        //银行卡号唯一
        String bank = mUser.getBankAccountNumber();
        if(StringUtils.isEmpty(bank)){
            throw new ServiceException("银行卡号不能为空");//user
        }
        if(StringUtils.isEmpty(mUser.getBankAccountName())){
            throw new ServiceException("银行名不能为空");//user
        }
        if (this.getOne(new LambdaQueryWrapper<MUser>().eq(MUser::getBankAccountNumber, bank)) != null) {
            throw new ServiceException("银行卡已被绑定");//user
        }
        return mUserMapper.updateMUser(mUser);
    }

    /**
     * 批量删除用户
     *
     * @param uids 需要删除的用户主键
     * @return 结果
     */
    @Override
    public int deleteMUserByUids(Long[] uids)
    {
        return mUserMapper.deleteMUserByUids(uids);
    }

    /**
     * 删除用户信息
     *
     * @param uid 用户主键
     * @return 结果
     */
    @Override
    public int deleteMUserByUid(Long uid)
    {
        return mUserMapper.deleteMUserByUid(uid);
    }

    @Override
    public void userRegister(UserRegisterModel model) {
        MUser inviterUser = this.getOne(
                new LambdaQueryWrapper<MUser>()
                        .eq(MUser::getInvitationCode, model.getInvitationCode())
        );

        if (inviterUser == null) {
            throw new ServiceException("邀请码无效");//user
        }

        MUser mUser = new MUser();
        mUser.setInviter(String.valueOf(inviterUser.getUid()));
        mUser.setInviterName(inviterUser.getLoginAccount());
        mUser.setInviterCode(inviterUser.getInvitationCode());

        // 校验账号是否重复
        String loginAccount = model.getLoginAccount();
        if (StringUtils.isNotBlank(loginAccount) &&
                this.getOne(new LambdaQueryWrapper<MUser>().eq(MUser::getLoginAccount, loginAccount)) != null) {
            throw new ServiceException("已存在的用户名或电话号码");//user
        }
        //手机唯一
        String loginPhone = model.getPhone();
        if (this.getOne(new LambdaQueryWrapper<MUser>().eq(MUser::getPhoneNumber, loginPhone)) != null) {
            throw new ServiceException("已存在的用户名或电话号码");//user
        }
        mUser.setLoginAccount(loginAccount);
        mUser.setBrushNumber(0);
        mUser.setAccountBalance(DecimalUtil.toBigDecimal(0));
        mUser.setLoginPassword(EncoderUtil.encoder(model.getLoginPassword()));
        mUser.setFundPassword(EncoderUtil.encoder(model.getFundPassword()));
        mUser.setInvitationCode(generateCode());
        mUser.setCreateTime(DateUtils.getNowDate());
        mUser.setRegisterType("1");
        mUser.setPhoneNumber(model.getPhone());
        mUser.setStatus(1);
        // 添加头像处理
        mUser.setHeadImg(model.getHeadImg());
        // TODO 此处需要思考
        mUser.setLevel(1);
        mUserMapper.insertMUser(mUser);
    }


    @Override
    public MUser getUserOne(String loginAccount, String loginPassword) {
        LambdaQueryWrapper<MUser> wrapper = new LambdaQueryWrapper<>();
        // 关键：将数据库中的login_account字段转为小写，与处理后的小写账号匹配
        // 无论数据库中是大写（如ADMIN）还是小写（如admin），都会被转为小写比对
        wrapper.apply("LOWER(login_account) = {0}", loginAccount);
        MUser user = this.getOne(wrapper);
        if (user == null) {
            throw new ServiceException("没有该用户");//user
        }

        boolean matches = EncoderUtil.matches(loginPassword, user.getLoginPassword());
        if (!matches) {
            throw new ServiceException("密码错误");//user
        }
        return user;
    }

    @Override
    public HashMap<String, Object> updateBalance(MUser mUser, BalanceModel balanceModel) {
        if(mUser==null){
            throw new ServiceException("用户已删除");//user
        }
        BigDecimal accountBalance = mUser.getAccountBalance();
        BigDecimal changeBalance = balanceModel.getBalance();
        Integer type = 0;
        if(changeBalance.signum()>=0){ //changeBalance大于或等于0
            type = 0; // 增加
        }else {
            // 修复这里的逻辑，应该比较绝对值
            if (accountBalance.compareTo(changeBalance.abs()) < 0) {
                throw new ServiceException("当前账户金额小于减少金额");//user
            }
            type = 1; // 减少
        }
        accountBalance = DecimalUtil.add(accountBalance, changeBalance);

        // 确保余额不会变为负数
        if (accountBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("操作后账户余额不能为负数");//user
        }

        mUser.setAccountBalance(accountBalance);
        this.updateMUser(mUser);

        HashMap<String, Object> map = new HashMap<>();
        map.put("accountBalance",accountBalance);
        map.put("type",type);
        return map;
    }


    @Override
    public void upgrade(Long uid) {
        MUser mUser = this.selectMUserByUid(uid);
        if (mUser == null) return;

        // 获取用户充值总额
        MAccountChangeRecords changeRecords = new MAccountChangeRecords();
        changeRecords.setUid(String.valueOf(uid));
        changeRecords.setType(0); // 增加
        changeRecords.setTransactionType(1); // 充值

        List<MAccountChangeRecords> selected = accountChangeRecordsService.selectMAccountChangeRecordsList(changeRecords);
        BigDecimal totalAmount = selected.stream()
                .map(MAccountChangeRecords::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 查询所有等级中 join_cost <= totalAmount 的，按 sort_num 降序取第一个
        List<UserGrade> matchedGrades = userGradeService.list(
                new LambdaQueryWrapper<UserGrade>()
                        .apply("CAST(join_cost AS DECIMAL(20,6)) <= {0}", totalAmount)
                        .orderByDesc(UserGrade::getSortNum)
        );

        if (!matchedGrades.isEmpty()) {
            UserGrade targetGrade = matchedGrades.get(0);
            int currentLevel = mUser.getLevel();
            if (targetGrade.getSortNum() > currentLevel) {
                // 升级
                mUser.setLevel(targetGrade.getSortNum().intValue());
                this.updateMUser(mUser);
            }
        }
    }



    /**
     * 用户当天刷单数量清零
     */
    @Override
    public void clearBrushNumber(){
        mUserMapper.clearBrushNumber();
    }

    /**
     * 用户当日订单数量加1
     * @param uid 用户主键
     * @return
     */
    @Override
    public int increaseBrushNumber(Long uid, Integer num){
        return mUserMapper.increaseBrushNumber(uid, num);
    }



    /**
     * 直接修改用户信息，不涉及其他业务
     *
     * @param mUser 用户
     * @return 结果
     */
    @Override
    public int updateMUserSimple(MUser mUser) {
        try {
            mUser.setUpdateTime(DateUtils.getNowDate());
            return mUserMapper.updateMUser(mUser);
        } catch (Exception e) {
            throw new RuntimeException("更新用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 修改用户连单数量
     *
     * @param mUser 用户
     * @return 结果
     */
    @Override
    public int updateMultiOrderNum(MUser mUser)
    {
        mUser.setUpdateTime(DateUtils.getNowDate());
        return mUserMapper.updateMUser(mUser);
    }


    /**
     * 修改当前用户的等级
     *
     * @param gradeId 用户等级id
     * @param userId 用户id
     * @return 结果
     */
    @Override
    public int updateGrade(Long gradeId, Long userId)
    {
        UserGrade userGrade = userGradeMapper.selectUserGradeById(gradeId);
        BigDecimal minBalance = userGrade.getMinBalance();
        BigDecimal joinCost = new BigDecimal(userGrade.getJoinCost());
        MUser user = mUserMapper.selectMUserByUid(userId);
        if(userGrade.getSortNum() < user.getLevel())
            throw new ServiceException("您无法升级到低于当前级别的级别");//user
        if(user.getAccountBalance().compareTo(minBalance) < 0||user.getAccountBalance().compareTo(joinCost) < 0)
            throw new ServiceException("余额不足无法升级");//user
        //扣除金额
        BigDecimal accountBalance = user.getAccountBalance();
        user.setAccountBalance(user.getAccountBalance().subtract(joinCost).setScale(2, BigDecimal.ROUND_HALF_UP));
        //记录账变
        MAccountChangeRecords changeRecords = new MAccountChangeRecords();
        changeRecords.setAmount(joinCost);
        changeRecords.setType(1); //0收入 1支出
        changeRecords.setAccountForward(accountBalance);
        changeRecords.setAccountBack(user.getAccountBalance());
        changeRecords.setUid(String.valueOf(user.getUid()));
        changeRecords.setDescription(user.getLoginAccount()+"升级扣除 等级名称"+userGrade.getGradeName());
        changeRecords.setTransactionType(1); // 3:专用于标记订单利润
        changeRecords.setCreateTime(DateUtils.getNowDate());
        changeRecords.setRelatedId("-");
        mAccountChangeRecordsMapper.insertMAccountChangeRecords(changeRecords);
        user.setLevel(userGrade.getSortNum());
        user.setUpdateTime(DateUtils.getNowDate());
        return mUserMapper.updateMUser(user);
    }

    @Override
    public MUser getByLoginAccount(String loginAccount) {
        return this.lambdaQuery()
                .eq(MUser::getLoginAccount, loginAccount)
                .last("LIMIT 1")
                .one();
    }

    @Override
    public int updateUserAvatar(Long uId, MultipartFile file) {
        try {
            // 1. 上传文件
            String filePath = RuoYiConfig.getUploadPath();
            String fileName = FileUploadUtils.upload(filePath, file);

            // 2. 构建完整URL
            SysConfig imageUrl = configMapper.checkConfigKeyUnique("image_url");
            String avatarUrl = imageUrl.getConfigValue() + fileName;

            // 3. 调用Mapper更新数据库（传URL字符串）
            return mUserMapper.updateUserAvatar(uId, avatarUrl);

        } catch (Exception e) {
            throw new ServiceException("头像更新失败: " + e.getMessage());
        }
    }

    @Override
    public int updatePassword(Long uid, String oldPassword, String newPassword) {
        // 查询用户信息
        MUser user = mUserMapper.selectById(uid);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        // 验证原始密码是否正确
        if (!passwordEncoder.matches(oldPassword, user.getLoginPassword())) {
            throw new ServiceException("原始密码错误");
        }

        // 检查新密码是否与原始密码相同
        if (passwordEncoder.matches(newPassword, user.getLoginPassword())) {
            throw new ServiceException("新密码不能与原始密码相同");
        }

        // 加密新密码并更新
        MUser updateUser = new MUser();
        updateUser.setUid(uid);
        updateUser.setLoginPassword(passwordEncoder.encode(newPassword));
        updateUser.setUpdateTime(new Date());

        return mUserMapper.updateById(updateUser);
    }

    @Override
    public int updateFoundPassword(Long uid, String newPassword) {
        // 查询用户信息
        MUser user = mUserMapper.selectById(uid);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        // 加密新密码并更新
        MUser updateUser = new MUser();
        updateUser.setUid(uid);
        updateUser.setFundPassword(passwordEncoder.encode(newPassword));
        updateUser.setUpdateTime(new Date());

        return mUserMapper.updateById(updateUser);
    }

    @Override
    public int updateUser(MUser mUser) {
        MUser user = mUserMapper.selectMUserByUid(mUser.getUid());
        String loginAccount = mUser.getLoginAccount();
        if(!user.getLoginAccount().equals(loginAccount)){
            MUser one1 = this.getByLoginAccount(mUser.getLoginAccount());
            if(one1!=null){
                throw new ServiceException("账号已存在");
            }
        }
        return mUserMapper.updateMUser(mUser);
    }

    @Override
    public List<MUser> listInformain() {
        List<MUser> mUsers =  mUserMapper.selectInformation();
        return mUsers;
    }
}
