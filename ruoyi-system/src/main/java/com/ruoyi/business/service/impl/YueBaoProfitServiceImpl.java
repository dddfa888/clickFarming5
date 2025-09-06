package com.ruoyi.business.service.impl;
import com.ruoyi.business.mapper.YueBaoProfitRecordMapper;
import com.ruoyi.business.service.YueBaoProfitService;
import com.ruoyi.business.service.YueBaoService;
import com.ruoyi.click.domain.YueBao;
import com.ruoyi.click.domain.YuebaoProfitRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


/**
 * 余额宝利息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-04
 */
@Service
public class YueBaoProfitServiceImpl implements YueBaoProfitService {
    @Autowired
    private YueBaoProfitRecordMapper profitRecordMapper;
    @Autowired
    private YueBaoService yueBaoService;

    @Override
    public void calculateDailyProfit() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Date profitDate = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<YueBao> allYueBao = yueBaoService.list();
        for (YueBao yueBao : allYueBao) {
            Long userId = yueBao.getUserId();
            BigDecimal totalAssets = yueBao.getTotalAssets();
            BigDecimal annualizedRate = yueBao.getAnnualizedRate();

            YuebaoProfitRecord existRecord = profitRecordMapper.getByUserIdAndDate(userId, profitDate);
            if (existRecord != null) {
                continue;
            }

            BigDecimal dailyProfit = totalAssets.multiply(annualizedRate)
                    .divide(new BigDecimal("36500"), 2, RoundingMode.HALF_UP);

            yueBao.setYesterdayProfit(dailyProfit);
            yueBao.setCumulativeProfit(yueBao.getCumulativeProfit().add(dailyProfit));
            yueBao.setTotalAssets(totalAssets.add(dailyProfit));
            yueBao.setUpdateTime(new Date());
            yueBaoService.updateById(yueBao);

            YuebaoProfitRecord record = new YuebaoProfitRecord();
            record.setUserId(userId);
            record.setProfitDate(profitDate);
            record.setDailyProfit(dailyProfit);
            record.setTotalAssets(totalAssets);
            record.setAnnualizedRate(annualizedRate);
            record.setCreateTime(new Date());
            profitRecordMapper.insert(record);
        }
    }
}
