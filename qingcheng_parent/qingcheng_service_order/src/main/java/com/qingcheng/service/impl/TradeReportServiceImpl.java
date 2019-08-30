package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.dao.TradeReportMapper;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.TradeReport;
import com.qingcheng.service.order.CategoryReportService;
import com.qingcheng.service.order.OrderItemService;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.order.TradeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * Author: Feiyue
 * Date: 2019/8/27 19:00
 * Desc:
 */
@Service(interfaceClass = TradeReportService.class) // dubbo注解，将该类注册为服务
public class TradeReportServiceImpl implements TradeReportService {
    @Autowired
    private TradeReportMapper tradeReportMapper;

    @Autowired
    private OrderService orderService;

    /**
     * 定时任务，在凌晨1点执行统计，统计前一日销售情况，并将结果插入到交易统计表
     */
    @Transactional //Spring注解，事务管理
    public void countPreDay() {
        // 设置统计日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        Map searchMap = new HashMap();
        searchMap.put("countDate", date);

        // 查询前一日所有订单
        List<Order> orderList = orderService.findList(searchMap);
        // 1.统计数据（构建TradeReport对象）
        TradeReport tradeReport = new TradeReport();
        tradeReport.setCountDate(date);// count_date   统计日期
        tradeReport.setUv(100);//	uv	浏览人数 ，暂时固定
        if (orderList != null && orderList.size() > 0) {
            int orderdPersons = 0; //orderd_persons	下单人数
            int skus = 0; //skus	下单件数
            int ordersValid = 0; //orders_valid	有效订单数

            tradeReport.setOrders(orderList.size()); //	orders	订单数

            int orderMoney = 0; //order_money	下单金额
            int returnMoney = 0; //return_money	退款金额
            int payPersons = 0; //pay_persons	付款人数
            int payOrders = 0; //pay_orders	付款订单数
            int paySkus = 0; //pay_skus	付款件数
            int payMoney = 0; //pay_money	付款金额
            int pct = 0; //pct	客单价

            Set set = new HashSet();//用于统计下单人数
            Set set1 = new HashSet();//用于统计支付人数
            for (Order order : orderList) {
                // 下单人数
                if (!"".equals(order.getUsername()) && order.getUsername() != null) {
                    set.add(order.getUsername());
                    System.out.println(set.size());
                }
                // 下单件数
                if (order.getTotalNum() != null) {
                    skus += order.getTotalNum();
                }
                // 有效订单数
                if ("1".equals(order.getOrderStatus())) {
                    ordersValid++;
                }
                // 下单金额
                if (order.getPayMoney() != null) {
                    orderMoney += order.getPayMoney();
                }
                // 退款金额
                if ("2".equals(order.getPayStatus())) {//已退款订单
                    returnMoney += order.getPayMoney();
                }

                if ("1".equals(order.getPayStatus())) {// 已支付订单
                    //pay_persons	付款人数
                    set1.add(order.getUsername());
                    //pay_orders	付款订单数
                    payOrders++;
                    //pay_skus	付款件数
                    if (order.getTotalNum() != null) {
                        paySkus += order.getTotalNum();
                    }
                    //pay_money	付款金额
                    if (order.getPayMoney() != null) {
                        payMoney += order.getPayMoney();
                    }
                }
            }
            // 下单人数
            orderdPersons = set.size();
            // 付款人数
            payPersons = set1.size();

            // 设置tradeReport对象
            tradeReport.setOrderdPersons(orderdPersons);
            tradeReport.setSkus(skus);
            tradeReport.setOrdersValid(ordersValid);
            tradeReport.setOrderMoney(orderMoney);
            tradeReport.setReturnMoney(returnMoney);
            tradeReport.setPayPersons(payPersons);
            tradeReport.setPayOrders(payOrders);
            tradeReport.setPaySkus(paySkus);
            tradeReport.setPayMoney(payMoney);

            //pct	客单价
            if (tradeReport.getPayMoney() != null && tradeReport.getPayPersons() != null && tradeReport.getPayPersons() != 0) {
                pct = tradeReport.getPayMoney() / tradeReport.getPayPersons();
            }
            tradeReport.setPct(pct);
        }
        // 2.添加统计结果到数据库
        tradeReportMapper.insert(tradeReport);
    }

    public List<TradeReport> findAll(){
        return tradeReportMapper.selectAll();
    }

    /**
     * 按日期段查询交易统计数据
     *
     * @param dateBegin 开始日期
     * @param dateEnd   结束日期
     * @return
     */
    @Override
    public List<Map> findByDateRange(String dateBegin, String dateEnd) {
        List<Map> mapList = tradeReportMapper.findByDateRange(dateBegin, dateEnd);
        return mapList;
    }
}
