package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.SpuService;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/spu")
public class SpuController {

    @Reference
    private SpuService spuService;

    @GetMapping("/findAll")
    public List<Spu> findAll() {
        return spuService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Spu> findPage(int page, int size) {
        return spuService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Spu> findList(@RequestBody Map<String, Object> searchMap) {
        return spuService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Spu> findPage(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return spuService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public Spu findById(String id) {
        return spuService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Spu spu) {
        spuService.add(spu);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Spu spu) {
        spuService.update(spu);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id) {
        spuService.delete(id);
        return new Result();
    }

    @PostMapping("/saveGoods")
    public Result saveGoods(@RequestBody Goods goods) {
        spuService.saveGoods(goods);
        return new Result();
    }

    /**
     * 通过ID查询商品
     *
     * @param id spuId
     * @return
     */
    @GetMapping("/findGoodsById")
    public Goods findGoodsById(String id) {
        Goods good = spuService.findGoodsById(id);
        return good;
    }

    /**
     * 商品审核
     *
     * @param map
     * @return
     */
    @PostMapping("/audit")
    public Result audit(@RequestBody Map<String, String> map) {
        spuService.audit(map.get("id"), map.get("statue"), map.get("message"));
        return new Result();
    }

    /**
     * 商品下架
     *
     * @param id spuId
     * @return
     */
    @GetMapping("/pull")
    public Result pull(String id) {
        spuService.pull(id);
        return new Result();
    }

    /**
     * 商品上架
     *
     * @param id
     * @return
     */
    @GetMapping("/put")
    public Result put(String id) {
        spuService.put(id);
        return new Result();
    }

    @GetMapping("/putMany")
    public Result putMany(String[] ids) {
        int count = spuService.putMany(ids);

        // 设置响应信息
        Result result = new Result();
        result.setCode(0);//执行成功
        result.setMessage("成功上架了" + count + "款商品！");

        // 返回消息
        return result;
    }

    /**
     * 商品批量下架
     * @param ids
     * @return
     */
    @GetMapping("/pullMany")
    public Result pullMany(String[] ids) {
        int count = spuService.pullMany(ids);

        // 设置响应信息
        Result result = new Result();
        result.setCode(0);//执行成功
        result.setMessage("成功下架了" + count + "款商品！");

        // 返回消息
        return result;
    }


}
