package com.ruoyi.business.service;

import com.ruoyi.click.domain.YueBao;

import java.util.List;

/**
 * 余额宝Service接口
 *
 * @author ruoyi
 * @date 2025-09-04
 */
public interface YueBaoService {

    YueBao selectByUserId(Long userId);
    void insert(YueBao yueBao);
    void updateById(YueBao yueBao);
    List<YueBao> list();
}
