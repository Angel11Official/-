package org.reggieapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.reggieapp.common.R;
import org.reggieapp.dto.DishDto;
import org.reggieapp.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品 同时新增口味数据
    //同时操作两张表的方法 dish dish_flavor
    public void savewithFlavor(DishDto dishDto);

    //获取菜品信息和口味信息
    //同时操作两张表 dish dish_flavor
    public DishDto getByIdwithFlavor(Long id);

    //修改菜品 同时更新口味数据
    //同时操作两张表
    public void updatewithFlavor(DishDto dishDto);


    //根据分类ID获得该类所有菜品
    //public List<Dish> getListbyCategory(Long id);
    //客户端 获得菜品＋口味
    public  List<DishDto> getListbyCategory(Long id);

    //根据ID删除菜品
    public List<String> deletebyId(String ids);

    //根据ID开启销售
    public void open(String ids);

    //根据ID关闭销售
    public void close(String ids);

}
