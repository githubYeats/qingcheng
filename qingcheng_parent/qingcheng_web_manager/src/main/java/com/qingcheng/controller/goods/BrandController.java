package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.goods.Brand;
import com.qingcheng.service.goods.BrandService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

//@Controller //SpringMVC注解
//@ResponseBody //SpringMVC注解，表示该类下所有方法均是以json格式回写数据
@RestController // = @ResponseBody + @Controller
@RequestMapping("/brand") //SpringMVC注解，接口文档中规定了请求URL格式，如: /brand/findAll.do
public class BrandController {

    @Reference // Dubbo注解
    private BrandService brandService;

    /**
     * 品牌查询
     *
     * @return
     */
    @GetMapping("/findAll") //SpringMVC注解
    public List<Brand> findAll() {
        return brandService.findAll();
    }

    /**
     * 品牌查询，分页展示
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/findPage")
    public PageResult<Brand> findPage(int page, int size){
        return brandService.findPage(page, size);
    }

    /**
     * 品牌条件查询（不加分页效果）
     * 注解@RequestBody不能少，指定请求参数为json类型
     *
     * @param searchMap 查询条件
     * @return
     */
    @PostMapping("/findList") //SpringMVC注解
    public List<Brand> findList(@RequestBody Map<String, Object> searchMap) {
        List<Brand> brandList = brandService.findList(searchMap);
        return brandList;
    }

    /**
     * 品牌条件查询，分页展示
     *
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/findPage")
    public PageResult<Brand> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  brandService.findPage(searchMap,page,size);
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Brand findById(int id){
        Brand brand = brandService.findById(id);
        return brand;
    }

    /**
     * 新增品牌
     * @param brand
     */
    @PostMapping("/add")
    public Result add(@RequestBody Brand brand){
        brandService.add(brand);
        return new Result();
    }

    /**
     * 修改品牌信息
     * @param brand
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Brand brand){
        int i = brandService.update(brand);
        Result info = new Result();
        if (i<=0){
            info.setCode(1);//执行有误
            info.setMessage("修改失败！");
        }
        return info;
    }

    /**
     * 根据id删除品牌
     * @param id
     * @return
     */
    @GetMapping("/delete")
    public Result delete(int id){
        int i = brandService.delete(id);
        Result info = new Result();
        if(i<=0){
            info.setCode(1);//执行有误
            info.setMessage("删除失败！");
        }
        return info;
    }

}
