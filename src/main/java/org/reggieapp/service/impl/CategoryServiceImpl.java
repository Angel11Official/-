package org.reggieapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggieapp.common.CustomException;
import org.reggieapp.entity.Category;
import org.reggieapp.entity.Dish;
import org.reggieapp.entity.Setmeal;
import org.reggieapp.mapper.CategoryMapper;
import org.reggieapp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    DishServiceImpl dishService;

    @Autowired
    SetmealServiceImpl setmealService;

    //根据ID删除分类 在删除前查询是否与菜品/套餐关联
    //如果已经关联 抛出一个异常
    @Override
    public void remove(Long id) {

        //检查与菜品关联
        //select * from dish where category_id==?
        //添加查询条件
        LambdaQueryWrapper<Dish>dishQueryWrapper = new LambdaQueryWrapper<Dish>();
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishQueryWrapper);
        if(count1>0){
            //有关联 不能删除 抛出一个业务异常
            throw new CustomException("要删除的分类关联了菜品，无法删除");
        }
        //检查与套餐关联
        //同上
        LambdaQueryWrapper<Setmeal>setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2>0){
            //有关联 不能删除 抛出一个业务异常
            throw new CustomException("要删除的分类关联了套餐，无法删除");
        }

        //正常删除
        super.removeById(id);
    }
}
