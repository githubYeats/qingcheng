package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.pojo.order.OrderItemOrder;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.util.IdWorker;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Order> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Order> orders = (Page<Order>) orderMapper.selectAll();
        return new PageResult<Order>(orders.getTotal(), orders.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        return new PageResult<Order>(orders.getTotal(), orders.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param order
     */
    public void add(Order order) {
        orderMapper.insert(order);
    }

    /**
     * 修改
     *
     * @param order
     */
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 订单id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 订单创建时间 createTime    指定日期
            if (searchMap.get("createTime") != null && !"".equals(searchMap.get("createTime"))) {
                criteria.andEqualTo("createTime", searchMap.get("createTime"));
            }
            // 支付类型，1、在线支付、0 货到付款
            if (searchMap.get("payType") != null && !"".equals(searchMap.get("payType"))) {
                criteria.andLike("payType", "%" + searchMap.get("payType") + "%");
            }
            // 物流名称
            if (searchMap.get("shippingName") != null && !"".equals(searchMap.get("shippingName"))) {
                criteria.andLike("shippingName", "%" + searchMap.get("shippingName") + "%");
            }
            // 物流单号
            if (searchMap.get("shippingCode") != null && !"".equals(searchMap.get("shippingCode"))) {
                criteria.andLike("shippingCode", "%" + searchMap.get("shippingCode") + "%");
            }
            // 用户名称
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                criteria.andLike("username", "%" + searchMap.get("username") + "%");
            }
            // 买家留言
            if (searchMap.get("buyerMessage") != null && !"".equals(searchMap.get("buyerMessage"))) {
                criteria.andLike("buyerMessage", "%" + searchMap.get("buyerMessage") + "%");
            }
            // 是否评价
            if (searchMap.get("buyerRate") != null && !"".equals(searchMap.get("buyerRate"))) {
                criteria.andLike("buyerRate", "%" + searchMap.get("buyerRate") + "%");
            }
            // 收货人
            if (searchMap.get("receiverContact") != null && !"".equals(searchMap.get("receiverContact"))) {
                criteria.andLike("receiverContact", "%" + searchMap.get("receiverContact") + "%");
            }
            // 收货人手机
            if (searchMap.get("receiverMobile") != null && !"".equals(searchMap.get("receiverMobile"))) {
                criteria.andLike("receiverMobile", "%" + searchMap.get("receiverMobile") + "%");
            }
            // 收货人地址
            if (searchMap.get("receiverAddress") != null && !"".equals(searchMap.get("receiverAddress"))) {
                criteria.andLike("receiverAddress", "%" + searchMap.get("receiverAddress") + "%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (searchMap.get("sourceType") != null && !"".equals(searchMap.get("sourceType"))) {
                criteria.andLike("sourceType", "%" + searchMap.get("sourceType") + "%");
            }
            // 交易流水号
            if (searchMap.get("transactionId") != null && !"".equals(searchMap.get("transactionId"))) {
                criteria.andLike("transactionId", "%" + searchMap.get("transactionId") + "%");
            }
            // 订单状态
            if (searchMap.get("orderStatus") != null && !"".equals(searchMap.get("orderStatus"))) {
                criteria.andLike("orderStatus", "%" + searchMap.get("orderStatus") + "%");
            }
            // 支付状态
            if (searchMap.get("payStatus") != null && !"".equals(searchMap.get("payStatus"))) {
                criteria.andLike("payStatus", "%" + searchMap.get("payStatus") + "%");
            }
            // 发货状态
            if (searchMap.get("consignStatus") != null && !"".equals(searchMap.get("consignStatus"))) {
                criteria.andLike("consignStatus", "%" + searchMap.get("consignStatus") + "%");
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andLike("isDelete", "%" + searchMap.get("isDelete") + "%");
            }
            // 数量合计
            if (searchMap.get("totalNum") != null) {
                criteria.andEqualTo("totalNum", searchMap.get("totalNum"));
            }
            // 金额合计
            if (searchMap.get("totalMoney") != null) {
                criteria.andEqualTo("totalMoney", searchMap.get("totalMoney"));
            }
            // 优惠金额
            if (searchMap.get("preMoney") != null) {
                criteria.andEqualTo("preMoney", searchMap.get("preMoney"));
            }
            // 邮费
            if (searchMap.get("postFee") != null) {
                criteria.andEqualTo("postFee", searchMap.get("postFee"));
            }
            // 实付金额
            if (searchMap.get("payMoney") != null) {
                criteria.andEqualTo("payMoney", searchMap.get("payMoney"));
            }

            // 批量发货的条件
            if (searchMap.get("ids") != null) {
                //public Criteria andIn(String property, Iterable values)   第2个参数，得是一个List
                criteria.andIn("id", Arrays.asList((String[]) searchMap.get("ids")));
            }
        }
        return example;
    }

    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 通过订单id查询订单及订单项
     *
     * @param id 订单id，分布式id
     * @return
     */
    @Override
    public OrderItemOrder findOrderByOrderId(String id) {
        // 查询Order对象
        Order order = orderMapper.selectByPrimaryKey(id);

        // 查询OrderItem列表
        /*
        查询tb_order_item表，查询条件是order_id字段值
         */
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", id);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(example);

        //封装成实体
        OrderItemOrder orderItemOrder = new OrderItemOrder();
        orderItemOrder.setOrder(order);
        orderItemOrder.setOrderItemList(orderItemList);

        //返回
        return orderItemOrder;
    }

    /**
     * 批量发货
     *
     * @param ids 订单编号数组
     */
    @Override
    public List<String> batchConsign(String[] ids) {
        List<String> orderIdList = new ArrayList<>();

        // 遍历订单，发货
        for (String id : ids) {
            Order order = orderMapper.selectByPrimaryKey(id);

            // 判断：订单状态为“待发货”才能进行发货
            /*
            订单状态：order_status
                0 待付款;1 待发货;2 已发货;3 已完成;4	已关闭
             */
            if (order.getOrderStatus().equals("1")) {
                //--------发货---------
                //看tb_order表中有哪些字段要修改
                //修改订单状态    order_status字段
                order.setOrderStatus("2");

                // 设置发货时间   consign_time字段
                order.setConsignTime(new Date());

                // 修改发货状态   consign_status字段    0：未发货；1：已发货
                order.setOrderStatus("1");

                // 更新数据库信息，才是真正地进行了发货
                orderMapper.updateByPrimaryKeySelective(order);

                orderIdList.add(order.getId());
            }
        }
        // 记录发货日志

        // 返回成功发货的订单编号数组
        return orderIdList;
    }

    /**
     * 批量发货
     *
     * @param orderList 经过筛选的"待发货"状态的订单集合
     */
    @Override
    public void batchSend(List<Order> orderList) {
        // 判断物流信息是否填写：物流公司 + 物流单号（目前先将其固定）
        for (Order order : orderList) {
            if (order.getShippingName() == null || order.getShippingCode() == null) {
                throw new RuntimeException("物流不完整，请重新填写！");
            }
        }

        // 遍历订单，发货
        for (Order order : orderList) {
            //--------发货---------
            //看tb_order表中有哪些字段要修改
            //修改订单状态    order_status字段
            order.setOrderStatus("2");

            // 设置发货时间   consign_time字段
            order.setConsignTime(new Date());

            // 修改发货状态   consign_status字段    0：未发货；1：已发货
            order.setOrderStatus("1");

            // 更新数据库信息，才是真正地进行了发货
            orderMapper.updateByPrimaryKeySelective(order);
        }
    }

    /**
     * 合并订单
     *
     * @param orderId1 主订单号
     * @param orderId2 从订单号，将要被合并到主订单的订单。
     */
    @Override
    public void merge(String orderId1, String orderId2) {

    }

    /**
     * 拆分订单
     *
     * @param maps Map<String, Integer>   Map<订单项id, 拆分数量>
     */
    @Override
    public void split(List<Map<String, Integer>> maps) {
        // 一个订单中多个订单项进行拆分，生成一个新的订单，新订单中包含拆分出来的多个订单项
        // 创建新订单
        Order newOrder = new Order();
        String newOrderId = idWorker.nextId() + "";
        newOrder.setId(newOrderId);

        // 拆分
        for (Map<String, Integer> map : maps) {
            Set<String> orderItemIds = map.keySet();//待拆分的各订单项id
            for (String orderItemId : orderItemIds) {
                /*
                ------------------------------前置条件判断-------------------------------------
                前置条件，即可以进行拆单的条件。   前置条件很多，如：
                    1. 所选订单，其is_delete字段，是否为0，即未逻辑删除
                    2. 是否是待发货订单 & 是否是未关闭的订单 & 。。。
                这里目前仅作最简单的判断，即进行拆分数量的判断！！
                 */
                // 订单项拆分数量要合法：大于0，小于原数量     =0 | =原数量，不用拆单
                int num = map.get(orderItemId);//拆分数量
                OrderItem orderItem = orderItemMapper.selectByPrimaryKey(orderItemId);//原订单项
                int totalNum = orderItem.getNum();//原来的总数量
                if (num < 0 || num > totalNum) {
                    continue;
                }

                /*
                -----------------------------数据库中，添加新的记录------------------------------------
                1. 生成新订单项，添加到订单明细表（tb_order_item）中，并设置其订单归属（即order_id字段值）
                2. 生成的新订单，添加到订单表中（tb_order）
                 */
                //新订单项 = 原订单项，然后再设置新订单项中需要修改的地方
                OrderItem newOrderItem = orderItem;
                // id字段     新订单项的id     分布式id   新订单项，新的id
                String newOrderItemId = idWorker.nextId() + "";
                newOrderItem.setOrderId(newOrderItemId);
                // num字段       新订单项的数量
                newOrderItem.setNum(num);
                // money字段      新订单项总金额
                newOrderItem.setMoney(orderItem.getMoney() * num);
                // sku_id字段
                // order_id字段     新订单项属于哪个新的订单
                newOrderItem.setOrderId(newOrderId);
                // pay_money字段      付金额
                // post_fee字段       运费
                // is_return字段      是否退货
                // 添加到数据库
                orderItemMapper.updateByPrimaryKey(newOrderItem);
                orderMapper.updateByPrimaryKey(newOrder);

                /*
                -----------------------------修改关联表的相关信息-----------------------------
                 */
                // 这是业务要求，得靠业务经验的积累，才能知道具体要修改哪些表，哪些字段
            }
        }
    }
}
