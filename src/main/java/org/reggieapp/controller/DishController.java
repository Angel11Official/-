package org.reggieapp.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.reggieapp.common.R;
import org.reggieapp.dto.DishDto;
import org.reggieapp.entity.Category;
import org.reggieapp.entity.Dish;
import org.reggieapp.entity.Employee;
import org.reggieapp.service.impl.CategoryServiceImpl;
import org.reggieapp.service.impl.DishFlavorServiceImpl;
import org.reggieapp.service.impl.DishServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    DishServiceImpl dishService;

    @Autowired
    DishFlavorServiceImpl dishFlavorService;

    @Autowired
    CategoryServiceImpl categoryService;

    //分类查询
    @RequestMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Dish>pageInfo = new Page(page,pageSize);
        Page<DishDto>dishDtoPageInfo = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish>queryWrapper = new LambdaQueryWrapper<Dish>();
        //添加过滤条件
        if(name!=null){
            queryWrapper.like(!StringUtils.isEmpty(name), Dish::getName,name);
        }
        //排序条件
        queryWrapper.orderByAsc(Dish::getPrice);
        //分页查询执行
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝 除了records之外都拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPageInfo,"records");
        //将ID映射为name
        List<Dish> dishlist = pageInfo.getRecords();
        List<DishDto> dishDtoList = new ArrayList<>();
        dishlist.forEach(dish->{
            DishDto dishDto = new DishDto();
            //拷贝其他属性
            BeanUtils.copyProperties(dish,dishDto);
            //得到id
            long id = dish.getCategoryId();
            LambdaQueryWrapper<Category>lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Category::getId,id);
            //通过id 得到分类对象 再得到name
            String c_name = categoryService.getOne(lambdaQueryWrapper).getName();
            dishDto.setCategoryName(c_name);
            dishDtoList.add(dishDto);
        });
        dishDtoPageInfo.setRecords(dishDtoList);
        //返回查询结果
        return R.success(dishDtoPageInfo);
    }

    //新增菜品
    //用DishDto获取数据
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.savewithFlavor(dishDto);
       return R.success("添加菜品成功");
    }


    //根据id来查询菜品信息和口味信息
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
         return R.success(dishService.getByIdwithFlavor(id));
    }

    //修改菜品信息
    //用DishDto获取数据
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updatewithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    //获取同类菜品列表
//    @GetMapping("/list")
////    public R<List<Dish>> getlist(Long categoryId){
////        log.info(categoryId.toString());
////        return R.success(dishService.getListbyCategory(categoryId));
////    }

    //客户端获取同类菜品列表
    @GetMapping("/list")
    public R<List<DishDto>> getlist(Long categoryId){
        log.info(categoryId.toString());
        return R.success(dishService.getListbyCategory(categoryId));
    }

    @DeleteMapping()
    public R<String> delete(String ids){
        log.info(ids.toString());
        List<String>list = dishService.deletebyId(ids);
        if(list.size()!=0){
            String str = "";
            for(String l:list){
                str += l;
                str += " ";
            }
            return R.error("以下菜品与现存套餐存在关联，无法删除："+str);
        }
        return R.success("删除菜品成功!");
    }

   @PostMapping("/status/1")
    public R<String> open(String ids){
        dishService.open(ids);
        return R.success("启售菜品成功！");
   }

   @PostMapping("/status/0")
    public R<String> close(String ids){
        dishService.close(ids);
        return R.success("停售菜品成功！");
   }


}
