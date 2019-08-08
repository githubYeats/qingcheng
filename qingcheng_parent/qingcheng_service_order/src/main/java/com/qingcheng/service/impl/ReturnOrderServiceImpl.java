package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.ReturnOrderItemMapper;
import com.qingcheng.dao.ReturnOrderMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.pojo.order.ReturnOrder;
import com.qingcheng.pojo.order.ReturnOrderItem;
import com.qingcheng.service.order.ReturnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = ReturnOrderService.class )
public class ReturnOrderServiceImpl implements ReturnOrderService {

    @Autowired
    private ReturnOrderMapper returnOrderMapper;

    @Autowired
    private ReturnOrderItemMapper returnOrderItemMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<ReturnOrder> findAll() {
        return returnOrderMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<ReturnOrder> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<ReturnOrder> returnOrders = (Page<ReturnOrder>) returnOrderMapper.selectAll();
        return new PageResult<ReturnOrder>(returnOrders.getTotal(), returnOrders.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<ReturnOrder> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return returnOrderMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<ReturnOrder> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<ReturnOrder> returnOrders = (Page<ReturnOrder>) returnOrderMapper.selectByExample(example);
        return new PageResult<ReturnOrder>(returnOrders.getTotal(), returnOrders.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public ReturnOrder findById(Long id) {
        return returnOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param returnOrder
     */
    public void add(ReturnOrder returnOrder) {
        returnOrderMapper.insert(returnOrder);
    }

    /**
     * 修改
     *
     * @param returnOrder
     */
    public void update(ReturnOrder returnOrder) {
        returnOrderMapper.updateByPrimaryKeySelective(returnOrder);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(Long id) {
        returnOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(ReturnOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 用户账号
            if (searchMap.get("userAccount") != null && !"".equals(searchMap.get("userAccount"))) {
                criteria.andLike("userAccount", "%" + searchMap.get("userAccount") + "%");
            }
            // 联系人
            if (searchMap.get("linkman") != null && !"".equals(searchMap.get("linkman"))) {
                criteria.andLike("linkman", "%" + searchMap.get("linkman") + "%");
            }
            // 联系人手机
            if (searchMap.get("linkmanMobile") != null && !"".equals(searchMap.get("linkmanMobile"))) {
                criteria.andLike("linkmanMobile", "%" + searchMap.get("linkmanMobile") + "%");
            }
            // 类型
            if (searchMap.get("type") != null && !"".equals(searchMap.get("type"))) {
                criteria.andLike("type", "%" + searchMap.get("type") + "%");
            }
            // 是否退运费
            if (searchMap.get("isReturnFreight") != null && !"".equals(searchMap.get("isReturnFreight"))) {
                criteria.andLike("isReturnFreight", "%" + searchMap.get("isReturnFreight") + "%");
            }
            // 申请状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andLike("status", "%" + searchMap.get("status") + "%");
            }
            // 凭证图片
            if (searchMap.get("evidence") != null && !"".equals(searchMap.get("evidence"))) {
                criteria.andLike("evidence", "%" + searchMap.get("evidence") + "%");
            }
            // 问题描述
            if (searchMap.get("description") != null && !"".equals(searchMap.get("description"))) {
                criteria.andLike("description", "%" + searchMap.get("description") + "%");
            }
            // 处理备注
            if (searchMap.get("remark") != null && !"".equals(searchMap.get("remark"))) {
                criteria.andLike("remark", "%" + searchMap.get("remark") + "%");
            }

            // 退款金额
            if (searchMap.get("returnMoney") != null) {
                criteria.andEqualTo("returnMoney", searchMap.get("returnMoney"));
            }
            // 退货退款原因
            if (searchMap.get("returnCause") != null) {
                criteria.andEqualTo("returnCause", searchMap.get("returnCause"));
            }
            // 管理员id
            if (searchMap.get("adminId") != null) {
                criteria.andEqualTo("adminId", searchMap.get("adminId"));
            }

        }
        return example;
    }

    /**
     * 处理退款申请
     *
     * @param id     退货退款服务单号  分布式id
     * @param money  商家确认的退款金额
     * @param remark 处理备注
     * @param code 标记是同意退款，还是拒绝退款     1 同意    2 拒绝
     */
    @Override
    @Transactional
    public void dealRefund(String id, Integer money, String remark, int code) {
        // 1.获取ReturnOrder对象
        ReturnOrder returnOrder = returnOrderMapper.selectByPrimaryKey(id);

        //--------------2.前置条件判断--------------------------------
        //订单存在与否
        if (null == returnOrder) {
            throw new RuntimeException("退款订单不存在！");
        }
        // "退款"订单，才能进行退款    type字段：1 退货；2 退款
        if (!returnOrder.getType().equals("2")) {
            throw new RuntimeException("非退款订单，不能退款！");
        }
        // 退款金额合法与否
        if (money > returnOrder.getReturnMoney() || money <= 0) {
            throw new RuntimeException("退款金额不合法");
        }

        //------------3.前端传递参数的处理------------------------------
        returnOrder.setReturnMoney(money);
        returnOrder.setRemark(remark);

        //------------4.returnOrder对象其他属性信息设置------------------
        //处理时间  dispose_time
        //管理员信息 admin_id    标识处理者
        //申请状态  status: 0 未处理；1 同意；2 驳回
        returnOrder.setDisposeTime(new Date());
        returnOrder.setAdminId(1);//此处先写死。  应该是动态获取
        if(code == 1){//同意退款
            returnOrder.setStatus("1");
        }else {//驳回退款申请
            returnOrder.setStatus("2");
        }

        // 更新数据库数据
        returnOrderMapper.updateByPrimaryKeySelective(returnOrder);

        //------------5.关联表的信息设置（【业务要求】）------------------
        // 这是业务要求，从单张数据库表，是看不出来的。它是表字段间的关联性，这需要业务经验的积累！！！！
        // （1）设置tb_order_item表的is_return字段
        /*
        它标识一个具体的商品是否退货退款
            0 未申请
            1 已申请
            2 已退货 | 已退款

         【如何关联到tb_order_item表？】这是关键
            在退货退款模块，直接涉及到3种表：
                退货退款申请表：tb_return_order
                退货退款申请明细表：tb_return_order_item
                退货退货原因表：tb_return_cause
            细数三张表的各字段，能链接到tb_order_item表的字段是：tb_return_order_item表order_item_id字段。
         */
        Example example = new Example(ReturnOrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("returnOrderId", id);
        List<ReturnOrderItem> returnOrderItemList = returnOrderItemMapper.selectByExample(example);
        for (ReturnOrderItem returnOrderItem : returnOrderItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(returnOrderItem.getOrderItemId());
            if(code == 1){//同意退款
                orderItem.setIsReturn("2");
            }else {//驳回退款申请
                orderItem.setIsReturn("0");//设置成未申请，以便用户可以重新提起申请！
            }
            //更新数据库
            orderItemMapper.updateByPrimaryKeySelective(orderItem);
        }
    }
}
