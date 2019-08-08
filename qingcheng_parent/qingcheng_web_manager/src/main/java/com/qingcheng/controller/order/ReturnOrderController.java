package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.order.ReturnOrder;
import com.qingcheng.service.order.ReturnOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/returnOrder")
public class ReturnOrderController {

    @Reference
    private ReturnOrderService returnOrderService;

    @GetMapping("/findAll")
    public List<ReturnOrder> findAll() {
        return returnOrderService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<ReturnOrder> findPage(int page, int size) {
        return returnOrderService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<ReturnOrder> findList(@RequestBody Map<String, Object> searchMap) {
        return returnOrderService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<ReturnOrder> findPage(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return returnOrderService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public ReturnOrder findById(Long id) {
        return returnOrderService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody ReturnOrder returnOrder) {
        returnOrderService.add(returnOrder);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody ReturnOrder returnOrder) {
        returnOrderService.update(returnOrder);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id) {
        returnOrderService.delete(id);
        return new Result();
    }

    /**
     * 同意退款
     *
     * @param id     退货退款服务单号  分布式id
     * @param money  商家确认的退款金额
     * @param remark 处理备注
     * @param code 标记是同意退款，还是拒绝退款     1 同意    2 拒绝
     */
    @GetMapping("/dealRefund")
    public Result dealRefund(String id, Integer money, String remark, int code) {
        returnOrderService.dealRefund(id, money, remark, code);
        return new Result();
    }
}
