package com.ruoyi.business.service.impl;

import com.ruoyi.business.mapper.YueBaoMapper;
import com.ruoyi.business.service.YueBaoService;
import com.ruoyi.click.domain.YueBao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 余额宝Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-04
 */
@Service
public class YueBaoServiceImpl implements YueBaoService {

    @Autowired
    private YueBaoMapper yueBaoMapper;

    @Override
    public YueBao selectByUserId(Long userId) {
        return yueBaoMapper.selectByUserId(userId);
    }

    @Override
    public void insert(YueBao yueBao) {
        yueBaoMapper.insert(yueBao);
    }

    @Override
    public void updateById(YueBao yueBao) {
        yueBaoMapper.updateById(yueBao);
    }

    @Override
    public List<YueBao> list() {
        return yueBaoMapper.list();
    }
}
