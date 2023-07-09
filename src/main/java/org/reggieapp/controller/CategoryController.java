package org.reggieapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.reggieapp.common.R;
import org.reggieapp.entity.Category;
import org.reggieapp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }


    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category>queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //分页查询执行
        categoryService.page(pageInfo,queryWrapper);
        //返回查询结果
        return R.success(pageInfo);
    }

    //根据ID删除分类
    @DeleteMapping()
    public R<String> delete(Long ids){
        log.info("删除分类的id:{}",ids);
        categoryService.remove(ids);
        return R.success("删除分类成功");
    }


    //根据ID修改分类信息
    @PutMapping
    public R<String>update (@RequestBody Category category){
        log.info("要修改的分类：{}",category.toString());
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    //获取菜品分类
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //传过来的是type 直接封装成一个category对象
        //条件构造器
        LambdaQueryWrapper<Category>queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        //执行查询
        List<Category> list = categoryService.list(queryWrapper);
        //返回查询结果
        return R.success(list);
    }
}
