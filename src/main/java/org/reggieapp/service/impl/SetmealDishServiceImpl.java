package org.reggieapp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggieapp.entity.SetmealDish;
import org.reggieapp.mapper.SetmealDishMapper;
import org.reggieapp.service.SetmealDishService;
import org.reggieapp.service.SetmealService;
import org.springframework.stereotype.Service;


@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish>implements SetmealDishService {
}
