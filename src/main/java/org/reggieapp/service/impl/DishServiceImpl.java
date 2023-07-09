package org.reggieapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.LongValue;
import org.reggieapp.common.R;
import org.reggieapp.dto.DishDto;
import org.reggieapp.entity.Dish;
import org.reggieapp.entity.DishFlavor;
import org.reggieapp.entity.SetmealDish;
import org.reggieapp.mapper.DishMapper;
import org.reggieapp.service.DishFlavorService;
import org.reggieapp.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private SetmealDishServiceImpl setmealDishService;

    //新增菜品 同时新增口味数据
    //同时操作两张表的方法 dish dish_flavor
    @Transactional
    public void savewithFlavor(DishDto dishDto) {
        //先保存菜品信息
        this.save(dishDto);

        //获得口味列表
        List<DishFlavor> list =dishDto.getFlavors();
        //每个口味都对应同一个菜品ID 要填入这个ID
        Long dishId = dishDto.getId();
        list.forEach(l->{
            l.setDishId(dishId);
        });
        //再保存菜品口味数据
        dishFlavorService.saveBatch(list);

    }


    //获取菜品信息和口味信息
    //同时操作两张表 dish dish_flavor
    @Override
    public DishDto getByIdwithFlavor(Long id) {
        //要返回的对象
        DishDto dishDto = new DishDto();
        //直接根据ID查找dish对象
        Dish dish = this.getById(id);
        //拷贝到dishDto中
        BeanUtils.copyProperties(dish,dishDto);
        //再根据查找dish_flavor对象
        List<DishFlavor>list = new ArrayList<>();
        LambdaQueryWrapper<DishFlavor>queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(DishFlavor::getDishId,id);
        //执行查询 得到一个List 填充到dto中
        list = dishFlavorService.list(queryWrapper);
        log.info(list.toString());
        dishDto.setFlavors(list);
        //返回dto
        return dishDto;
    }

    @Override
    @Transactional
    public void updatewithFlavor(DishDto dishDto) {
        //先修改dish表
        this.updateById(dishDto);
        //再修改dish_flavor
        //先清理对应的口味
        Long dish_id = dishDto.getId();
        LambdaQueryWrapper<DishFlavor>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish_id);
        dishFlavorService.remove(queryWrapper);
        //再插入对应的口味
        List<DishFlavor>list = dishDto.getFlavors();
        list.forEach(l->{
            l.setDishId(dish_id);
            dishFlavorService.save(l);
        });
    }


    //根据分类ID获得该类所有菜品
//    @Override
//    public List<Dish> getListbyCategory(Long id) {
//        //构造条件
//        LambdaQueryWrapper<Dish>queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dish::getCategoryId,id);
//        //执行查询
//        return this.list(queryWrapper);
//    }

    //客户端 根据ID获得菜品+口味
    @Override
    public List<DishDto> getListbyCategory(Long id) {
        //构造条件
        LambdaQueryWrapper<Dish>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        List<Dish>dishlist = this.list(queryWrapper);
        List<DishDto>dtolist = new ArrayList<>();
        //对于每个dish 找到 对应的口味信息
        for(Dish dish : dishlist){
            //找到dish的口味列表
            List<DishFlavor> flavorList= new ArrayList<>();
            //得到dish的ID
            Long dish_id = dish.getId();
            LambdaQueryWrapper<DishFlavor>queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dish_id);
            flavorList = dishFlavorService.list(queryWrapper1);
            //拷贝dish 到 dishdto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            dishDto.setFlavors(flavorList);
            dtolist.add(dishDto);
        }
        //返回结果
        return dtolist;
    }


    //根据ID删除菜品
    @Override
    public List<String> deletebyId(String ids) {
        String[] strarr = ids.split(",");
        List<String> list = new ArrayList<>();
        for (String str: strarr) {
            //log.info(str);
            LongValue longValue = new LongValue(str);
            Long id = longValue.getValue();
            //删除菜品？！
            //先检查该菜品有没有和某个套餐相关联 如果有 那么 不能删除
            //根据菜品ID 查 setmeal_dish表
            //条件构造器
            LambdaQueryWrapper<SetmealDish>queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getDishId,id);
            //执行查询
            if(setmealDishService.list(queryWrapper).size()!=0){
                //存在关联 不能删除
                list.add(this.getById(id).getName());
            }else {
                //先删除菜品
                this.removeById(id);
                //再删除菜品对应的口味
                LambdaQueryWrapper<DishFlavor>queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(DishFlavor::getDishId,id);
                dishFlavorService.remove(queryWrapper1);
            }
        }
        return list;
    }

    //根据ID开启菜品
    @Override
    public void open(String ids) {
        String[] strarr = ids.split(",");
        for(String str:strarr){
            //对于每一个str 转换为id
            LongValue longValue = new LongValue(str);
            Long id = longValue.getValue();
            //找出该ID对应的对象 并 将状态改为1
            Dish dish = this.getById(id);
            dish.setStatus(1);
            this.updateById(dish);
        }
    }

    //根据ID关闭菜品
    @Override
    public void close(String ids) {
        String[] strarr = ids.split(",");
        for(String str:strarr){
            //对于每一个str 转换为id
            LongValue longValue = new LongValue(str);
            Long id = longValue.getValue();
            //找出该ID对应的对象 并 将状态改为0
            Dish dish = this.getById(id);
            dish.setStatus(0);
            this.updateById(dish);
        }
    }

}
