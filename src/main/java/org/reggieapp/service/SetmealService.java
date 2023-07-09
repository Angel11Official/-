package org.reggieapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.reggieapp.common.R;
import org.reggieapp.dto.SetmealDto;
import org.reggieapp.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {

    //添加套餐 以及 套餐与菜品之间 关系
    public void addSetmeal(SetmealDto setmealDto);

    //获取套餐信息
    public SetmealDto getSetMealDto(Long id);

    //修改套餐
    void updateSetmealDto(SetmealDto setmealDto);


    //删除套餐
    void deleteSetmeal(String ids);

    void open(String ids);


    void close(String ids);
}
