package com.qingcheng.service.order;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.ReturnOrder;

import java.util.*;

/**
 * returnOrder业务逻辑层
 */
public interface ReturnOrderService {


    public List<ReturnOrder> findAll();


    public PageResult<ReturnOrder> findPage(int page, int size);


    public List<ReturnOrder> findList(Map<String,Object> searchMap);


    public PageResult<ReturnOrder> findPage(Map<String,Object> searchMap,int page, int size);


    public ReturnOrder findById(Long id);

    public void add(ReturnOrder returnOrder);


    public void update(ReturnOrder returnOrder);


    public void delete(Long id);

    /**
     * 处理退款申请
     * @param id  退货退款服务单号  分布式id
     * @param money  商家确认的退款金额
     * @param remark  处理备注  同意退款时，可不填写处理备注；驳回退款申请时，务必填写。
     * @param code 标记是同意退款，还是拒绝退款   1 同意    2 拒绝
     */
    public void dealRefund(String id, Integer money, String remark, int code);
}
