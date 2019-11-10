package com.qingcheng.timer;

import com.qingcheng.util.DateUtil;
import org.junit.Test;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Author: Feiyue
 * Date: 2019/9/5 22:40
 * Desc:
 */
@Component //Spring注解，Bean注册
public class SeckillGoodsPushTask {



    /**
     * 定时将符合参与秒杀的商品查询出来，再存入到Redis缓存
     * 每30秒执行一次
     */
    //@Test
    @Scheduled(cron = "0/30 * * * * ?")
    public void loadGoodsPushRedis() {
        // 计算每天秒杀活动的各时间段
        List<Date> dateMenus = DateUtil.getDateMenus();

        // 查询各时间区间内的秒杀商品
        /*
        条件：
            1）商家提交想要参与秒杀活动的商品，平台审核通过
            2）时间限制
                每个活动时间区间，有开始时间与结束时间。
                    商品参与的秒杀活动的开始时间 > 当前循环区间的开始时间
                    商品参与的秒杀活动的结束时间 < 当前循环区间的结束时间
            3）库存限制
                库存 > 0
         */


        // 将所有秒杀商品压入Redis缓存

    }
}
