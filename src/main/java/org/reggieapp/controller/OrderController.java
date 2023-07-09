package org.reggieapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.ibatis.annotations.Options;
import org.apache.tomcat.jni.Time;
import org.reggieapp.common.R;
import org.reggieapp.dto.OrdersDto;
import org.reggieapp.entity.*;
import org.reggieapp.service.AddressBookService;
import org.reggieapp.service.OrderDetailService;
import org.reggieapp.service.OrdersService;
import org.reggieapp.service.impl.OrderDetailServiceImpl;
import org.reggieapp.service.impl.OrdersServiceImpl;
import org.reggieapp.service.impl.ShoppingCartServiceImpl;
import org.reggieapp.service.impl.UserServiceImpl;
import org.reggieapp.utils.ValidateCodeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.lang.invoke.LambdaMetafactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrdersServiceImpl ordersService;

    @Autowired
    OrderDetailServiceImpl orderDetailService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    ShoppingCartServiceImpl shoppingCartService;

    @Autowired
    AddressBookService addressBookService;

    //提交订单
    @PostMapping("/submit")
    public R<String> sumbit(@RequestBody Orders orders, HttpSession session){
         //获取当前用户ID
         Long user_id = (Long) session.getAttribute("user");
         //根据ID查找用户
         User user = userService.getById(user_id);
         //写Orders表的订单对象
        orders.setUserId(user_id);
        orders.setUserName(user.getName());
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        //根据地址ID获取接受人的信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
         //根据用户ID获得用户的购物车
        LambdaQueryWrapper<ShoppingCart>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,user_id);
        List<ShoppingCart>shoppingCartList = shoppingCartService.list(queryWrapper);
        //计算金额
        double price = 0;
        for(ShoppingCart shoppingCart:shoppingCartList) {
            price += shoppingCart.getNumber() * shoppingCart.getAmount().doubleValue();
        }
        BigDecimal bigDecimal= new BigDecimal(price);
        orders.setAmount(bigDecimal);
        //随机生成一个orders_id
        Long orders_id = Long.valueOf(ValidateCodeUtils.generateValidateCode(6).toString());
        orders.setId(orders_id);
        ordersService.save(orders);
        //得到order_id?
//        LambdaQueryWrapper<Orders>queryWrapper1 = new LambdaQueryWrapper<>();
//        queryWrapper1.eq(Orders::getUserId,user_id).eq(Orders::getOrderTime,order_time);
//        List<Orders>list = ordersService.list(queryWrapper1);
        for(ShoppingCart shoppingCart:shoppingCartList){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail,"id");
            orderDetail.setOrderId(orders_id);
            orderDetailService.save(orderDetail);
        }

        //清空购物车
        //根据ID查询购物信息
        //Long user_id = (Long) session.getAttribute("user");
        //查表
        LambdaQueryWrapper<ShoppingCart>queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,user_id);
        shoppingCartService.remove(queryWrapper1);


        return R.success("下单成功！");
    }


    //历史订单
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize,HttpSession session){
        //得到用户ID
        Long user_id = (Long) session.getAttribute("user");
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,user_id);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //得到订单列表 对于每一个 订单Orders 封装成 OrdersDto
        List<Orders> ordersList = ordersService.list(queryWrapper);
        List<OrdersDto> ordersDtoList = new ArrayList<>();
        for(Orders orders:ordersList){
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders,ordersDto);
            //对于每个orders 找到所以的order detail
            Long orders_id = orders.getId();
            LambdaQueryWrapper<OrderDetail>queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(OrderDetail::getOrderId,orders_id);
            List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper1);
            ordersDto.setOrderDetails(orderDetailList);
            //得到 姓名 电话 地址 收件人 这四个字段
            ordersDto.setNumber(orders.getNumber());
            ordersDto.setUserName(orders.getUserName());
            ordersDto.setPhone(orders.getPhone());
            ordersDto.setConsignee(orders.getConsignee());
            ordersDtoList.add(ordersDto);
        }
        pageInfo.setRecords(ordersDtoList);
        return R.success(pageInfo);
    }


    //管理后台返回订单列表
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        if(beginTime!=null)log.info(beginTime.toString());
        if(endTime!=null)log.info(endTime.toString());
        //分页构造器
        Page<Orders> pageInfo = new Page(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //排序 相似 时间 条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Orders::getOrderTime);
        if (number!=null)
        {
        queryWrapper.like(!StringUtils.isEmpty(number), Orders::getId, number);
        }
        if(beginTime!=null&&endTime!=null) {
            queryWrapper.ge(Orders::getOrderTime,beginTime).lt(Orders::getOrderTime,endTime);
            //queryWrapper.between(Orders::getOrderTime, beginTime, endTime);
        }
        //执行查询
        pageInfo = ordersService.page(pageInfo,queryWrapper);
        //拷贝
        BeanUtils.copyProperties(pageInfo,ordersDtoPage);
        List<Orders>ordersList = pageInfo.getRecords();
        List<OrdersDto>ordersDtoList = new ArrayList<>();
        for(Orders orders:ordersList){
            //拷贝
             OrdersDto ordersDto = new OrdersDto();
             BeanUtils.copyProperties(orders,ordersDto);
            //对于每一个订单 查到user_id
             Long user_id = orders.getUserId();
             User user = userService.getById(user_id);
             //设置dto的属性
            ordersDto.setUserName(user.getName());
            ordersDto.setPhone(orders.getPhone());
            ordersDto.setAddress(orders.getAddress());
            ordersDto.setConsignee(orders.getConsignee());
            //add
            ordersDtoList.add(ordersDto);
        }
        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }

    //修改订单状态
    @PutMapping
    public R<String> setStatus(@RequestBody Orders orders){
        log.info(orders.getStatus().toString());
        if(orders.getStatus()==2){
            //完成
            orders.setStatus(3);
            ordersService.updateById(orders);
            return R.success("订单已派送！");
        }else if(orders.getStatus()==3){
            //待派送
            orders.setStatus(4);
            ordersService.updateById(orders);
            return R.success("订单已完成！！");
        }
        return R.success("修改订单状态成功！");
    }
}
