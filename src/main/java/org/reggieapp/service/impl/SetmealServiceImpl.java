package org.reggieapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.sf.jsqlparser.expression.LongValue;
import org.reggieapp.common.R;
import org.reggieapp.dto.SetmealDto;
import org.reggieapp.entity.Setmeal;
import org.reggieapp.entity.SetmealDish;
import org.reggieapp.mapper.SetmealMapper;
import org.reggieapp.service.SetmealDishService;
import org.reggieapp.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealDishServiceImpl setmealDishService;

    @Autowired
    CategoryServiceImpl categoryService;

    //添加套餐
    @Override
    public void addSetmeal(SetmealDto setmealDto) {
        //首先添加新套餐
        this.save(setmealDto);
        //套餐id
        Long setmeal_id = setmealDto.getId();
        //再添加 套餐 与 菜品的关系
        setmealDto.getSetmealDishes().forEach(setmealDish -> {
            //对于每一个setmealdish都要新增一条于套餐的关系
            setmealDish.setSetmealId(setmeal_id);
            setmealDishService.save(setmealDish);
        });

    }

    //回显套餐信息
    @Override
    public SetmealDto getSetMealDto(Long id) {
        //要返回的对象
        SetmealDto setmealDto = new SetmealDto();
        //直接获取套餐对象
        Setmeal setmeal = this.getById(id);
        //拷贝到setmealDTO中
        BeanUtils.copyProperties(setmeal,setmealDto);
        //还需要List<SetmealDish>这个属性
        //怎么获取？
        //根据id 查 SetmealDish表
        //条件构造器
        LambdaQueryWrapper<SetmealDish>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        //执行查询
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        //根据套餐ID 得到分类ID 再得到分类名
        setmealDto.setCategoryName(categoryService.getById(setmeal.getCategoryId()).getName());
        //返回结果
        return setmealDto;
    }


    //修改套餐信息
    @Override
    public void updateSetmealDto(SetmealDto setmealDto) {
        //操作两个表
        //setmeal setmealdish
        //先修改Setmeal表
        this.updateById(setmealDto);
        //先删除原来的套餐-菜品对应信息
        //根据setmeal_id来删除
        long setmeal_id = setmealDto.getId();
        //条件构造器
        LambdaQueryWrapper<SetmealDish>queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal_id);
        //执行删除
        setmealDishService.remove(queryWrapper);
        //再添加新的套餐-菜品对应信息
        //插入的是什么？一条条的setmeal_dish对象
        setmealDto.getSetmealDishes().forEach(setmealDish -> {
            //填入setmeal_id
            setmealDish.setSetmealId(setmeal_id);
            setmealDishService.save(setmealDish);
        });
        return ;

    }

    //根据ID删除套餐
    @Override
    public void deleteSetmeal(String ids) {
        String[] strarr = ids.split(",");
        for(String str:strarr){
            LongValue longValue = new LongValue(str);
            Long id = longValue.getValue();
            //首先删除套餐
            //条件构造器
            this.removeById(id);
            //再删除该套餐对应的菜品
            //条件构造器
            LambdaQueryWrapper<SetmealDish>queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,id);
            setmealDishService.remove(queryWrapper);
        }
    }

    //开启套餐
    @Override
    public void open(String ids) {
        String[] strarr = ids.split(",");
        for(String str:strarr){
            LongValue longValue = new LongValue(str);
            Long id = longValue.getValue();
            Setmeal setmeal = this.getById(id);
            setmeal.setStatus(1);
            this.updateById(setmeal);
        }
    }

    //关闭套餐
    @Override
    public void close(String ids) {
        String[] strarr = ids.split(",");
        for(String str:strarr){
            LongValue longValue = new LongValue(str);
            Long id = longValue.getValue();
            Setmeal setmeal = this.getById(id);
            setmeal.setStatus(0);
            this.updateById(setmeal);
        }
    }
}
