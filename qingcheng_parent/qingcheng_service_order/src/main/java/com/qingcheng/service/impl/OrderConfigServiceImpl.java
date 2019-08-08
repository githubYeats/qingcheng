package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.OrderConfigMapper;
import com.qingcheng.dao.OrderLogMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderConfig;
import com.qingcheng.pojo.order.OrderLog;
import com.qingcheng.service.order.OrderConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderConfigServiceImpl implements OrderConfigService {

    @Autowired
    private OrderConfigMapper orderConfigMapper;



    /**
     * 返回全部记录
     * @return
     */
    public List<OrderConfig> findAll() {
        return orderConfigMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<OrderConfig> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<OrderConfig> orderConfigs = (Page<OrderConfig>) orderConfigMapper.selectAll();
        return new PageResult<OrderConfig>(orderConfigs.getTotal(),orderConfigs.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<OrderConfig> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderConfigMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<OrderConfig> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<OrderConfig> orderConfigs = (Page<OrderConfig>) orderConfigMapper.selectByExample(example);
        return new PageResult<OrderConfig>(orderConfigs.getTotal(),orderConfigs.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public OrderConfig findById(Integer id) {
        return orderConfigMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param orderConfig
     */
    public void add(OrderConfig orderConfig) {
        orderConfigMapper.insert(orderConfig);
    }

    /**
     * 修改
     * @param orderConfig
     */
    public void update(OrderConfig orderConfig) {
        orderConfigMapper.updateByPrimaryKeySelective(orderConfig);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        orderConfigMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(OrderConfig.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){

            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 正常订单超时时间（分）
            if(searchMap.get("orderTimeout")!=null ){
                criteria.andEqualTo("orderTimeout",searchMap.get("orderTimeout"));
            }
            // 秒杀订单超时时间（分）
            if(searchMap.get("seckillTimeout")!=null ){
                criteria.andEqualTo("seckillTimeout",searchMap.get("seckillTimeout"));
            }
            // 自动收货（天）
            if(searchMap.get("takeTimeout")!=null ){
                criteria.andEqualTo("takeTimeout",searchMap.get("takeTimeout"));
            }
            // 售后期限
            if(searchMap.get("serviceTimeout")!=null ){
                criteria.andEqualTo("serviceTimeout",searchMap.get("serviceTimeout"));
            }
            // 自动五星好评
            if(searchMap.get("commentTimeout")!=null ){
                criteria.andEqualTo("commentTimeout",searchMap.get("commentTimeout"));
            }

        }
        return example;
    }

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    /**
     * 订单超时处理逻辑
     */
    @Override
    public void orderTimeoutLogic() {
        // 获取订单配置对象
        //1：tb_order_config的id字段值，表示使用表中第1条记录的配置进行订单超时的处理
        OrderConfig orderConfig = orderConfigMapper.selectByPrimaryKey(1);

        //查询超时时间        tb_order_config表字段值
        Integer normalOrderTimeout = orderConfig.getOrderTimeout();//正常订单超时时间（分）

        //得到超时时间点
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(normalOrderTimeout);

        //设置查询条件
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLessThan("createTime",localDateTime);//订单创建时间小于超时时间
        criteria.andEqualTo("orderStatus","0");//未付款的
        criteria.andEqualTo("isDelete","0");//未删除的

        //查询超时订单
        List<Order> orderList = orderMapper.selectByExample(example);
        for (Order order : orderList) {
            //-------------记录订单变动日志     tb_order_log表-------------
            // id   订单日志id号     DB自动生成
            // operater     操作人员
            // operater_time    操作时间
            // order_id     订单id号
            // order_status     订单状态：0待付款；1待发货；2已发货；3已完成；4已关闭
            // pay_status   支付状态：0未支付；1已支付；2已退款
            // consign_status   发货状态：0未发货；1已发货
            // remark   备注
            OrderLog orderLog = new OrderLog();
            //先默认写成system。做了Spring Security框架后，可动态获取当前操作用户
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderId(order.getId());
            orderLog.setOrderStatus("4");//超时，将其关闭
            orderLog.setPayStatus(order.getPayStatus());
            orderLog.setConsignStatus(order.getConsignStatus());
            orderLog.setRemarks("订单超时未支付，系统自动将其关闭！");

            //更新数据库
            orderLogMapper.insert(orderLog);

            //更改订单状态
            order.setOrderStatus("4");
            order.setCloseTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }
    }

}
