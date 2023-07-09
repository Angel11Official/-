package org.reggieapp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggieapp.entity.ShoppingCart;
import org.reggieapp.mapper.ShoppingCartMapper;
import org.reggieapp.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
