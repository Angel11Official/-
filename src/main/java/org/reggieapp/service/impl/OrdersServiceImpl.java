package org.reggieapp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggieapp.entity.Orders;
import org.reggieapp.mapper.OrdersMapper;
import org.reggieapp.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}
