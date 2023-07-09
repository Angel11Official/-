package org.reggieapp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggieapp.entity.DishFlavor;
import org.reggieapp.mapper.DishFalvorMapper;
import org.reggieapp.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFalvorMapper, DishFlavor> implements DishFlavorService {
}
