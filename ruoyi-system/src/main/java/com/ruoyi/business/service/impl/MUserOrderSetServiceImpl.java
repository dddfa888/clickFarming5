package com.ruoyi.business.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.ruoyi.click.domain.vo.UserOrderSetSaveVO;
import com.ruoyi.click.mapper.MUserMapper;
import com.ruoyi.common.core.domain.entity.MUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.business.mapper.MUserOrderSetMapper;
import com.ruoyi.business.domain.MUserOrderSet;
import com.ruoyi.business.service.IMUserOrderSetService;

/**
 * 订单设置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-30
 */
@Service
public class MUserOrderSetServiceImpl implements IMUserOrderSetService 
{
    @Autowired
    private MUserOrderSetMapper mUserOrderSetMapper;
    @Autowired
    private MUserMapper mUserMapper; // 添加这行

    /**
     * 查询订单设置
     * 
     * @param id 订单设置主键
     * @return 订单设置
     */
    @Override
    public MUserOrderSet selectMUserOrderSetById(Long id)
    {
        return mUserOrderSetMapper.selectMUserOrderSetById(id);
    }

    /**
     * 查询订单设置列表
     * 
     * @param mUserOrderSet 订单设置
     * @return 订单设置
     */
    @Override
    public List<MUserOrderSet> selectMUserOrderSetList(MUserOrderSet mUserOrderSet)
    {
        return mUserOrderSetMapper.selectMUserOrderSetList(mUserOrderSet);
    }

    /**
     * 根据用户id查询订单设置列表
     *
     * @param userId
     * @return 订单设置
     */
    @Override
    public List<MUserOrderSet> selectByUserId(Long userId)
    {
        return mUserOrderSetMapper.selectByUserId(userId);
    }

    /**
     * 新增订单设置
     * 
     * @param mUserOrderSet 订单设置
     * @return 结果
     */
    @Override
    public int insertMUserOrderSet(MUserOrderSet mUserOrderSet)
    {
        mUserOrderSet.setCreateTime(DateUtils.getNowDate());
        return mUserOrderSetMapper.insertMUserOrderSet(mUserOrderSet);
    }

    /**
     * 修改订单设置
     * 
     * @param mUserOrderSet 订单设置
     * @return 结果
     */
    @Override
    public int updateMUserOrderSet(MUserOrderSet mUserOrderSet)
    {
        return mUserOrderSetMapper.updateMUserOrderSet(mUserOrderSet);
    }

    /**
     * 批量删除订单设置
     * 
     * @param ids 需要删除的订单设置主键
     * @return 结果
     */
    @Override
    public int deleteMUserOrderSetByIds(Long[] ids)
    {
        return mUserOrderSetMapper.deleteMUserOrderSetByIds(ids);
    }

    /**
     * 删除订单设置信息
     * 
     * @param id 订单设置主键
     * @return 结果
     */
    @Override
    public int deleteMUserOrderSetById(Long id)
    {
        return mUserOrderSetMapper.deleteMUserOrderSetById(id);
    }


    /**
     * 批量更新一个用户的设置
     *
     * @param vo 订单设置
     * @return 结果
     */
    @Override
    public int saveOrderSetByUser(UserOrderSetSaveVO vo) {
        // 获取用户信息
        MUser user = mUserMapper.selectMUserByUid(vo.getUserId());
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        mUserOrderSetMapper.deleteByUserId(vo.getUserId());

        String dataStr = vo.getOrderSetData();
        if(StringUtils.isEmpty(dataStr))
            return 1; //1表示成功，没有报错，返回0表示操作失败

        Date date = DateUtils.getNowDate();
        String[] dataArray = dataStr.split("#");
        List<MUserOrderSet> list = new LinkedList<>();
        MUserOrderSet orderSetset = null;
        for(String rowStr : dataArray){
            String[] rowArray = rowStr.split("_");
            if(rowArray.length<3){
                continue;
            }
            orderSetset = new MUserOrderSet();
            orderSetset.setUserId(vo.getUserId());
            orderSetset.setOrderNum(Integer.valueOf(rowArray[0]));
            orderSetset.setMinNum(new BigDecimal(rowArray[1]));
            orderSetset.setMaxNum(new BigDecimal(rowArray[2]));

            // 验证最小值必须大于用户余额
            if (orderSetset.getMinNum().compareTo(user.getAccountBalance()) <= 0) {
                throw new ServiceException("订单序号 " + orderSetset.getOrderNum() + " 的最小值必须大于用户当前余额 " + user.getAccountBalance());
            }

            orderSetset.setCreateTime(date);
            list.add(orderSetset);
        }
        return mUserOrderSetMapper.insertBatch(list);
    }


}
