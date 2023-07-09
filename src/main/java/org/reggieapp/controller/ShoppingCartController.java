package org.reggieapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.reggieapp.common.R;
import org.reggieapp.entity.ShoppingCart;
import org.reggieapp.service.ShoppingCartService;
import org.reggieapp.service.impl.ShoppingCartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.html.HTMLParagraphElement;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartServiceImpl shoppingCartService;

    //添加购物车
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        //设置user_id
        Long user_id = (Long)session.getAttribute("user");
        shoppingCart.setUserId(user_id);
        //如果是套餐
//        if(shoppingCart.getDishId()==null){
//            Long setmeal_id = shoppingCart.getSetmealId();
//            LambdaQueryWrapper<ShoppingCart>queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.eq(ShoppingCart::getSetmealId,setmeal_id).eq(ShoppingCart::getUserId,user_id);
//            ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
//            if(shoppingCart1!=null){
//                int num = shoppingCart1.getNumber();
//                double price = shoppingCart1.getAmount().doubleValue()/num;
//                shoppingCart1.setNumber(num+1);
//                double newprice = price*(num+1);
//                shoppingCart1.setAmount(new BigDecimal(newprice));
//                shoppingCartService.updateById(shoppingCart1);
//            }else {
//                shoppingCart.setCreateTime(LocalDateTime.now());
//            }
//
//
//        }
        //查找同ID的套餐记录
        //有 +1
        //没有 新建

        //如果是菜品
        //查找同ID的菜品记录

        //有
        //查找同口味的菜品记录
        //有 +1
        //没有 新建

        //没有 新建

        //检查数据库中是否有完全相同的记录 有的话 加1
        if(shoppingCart.getDishId()==null){
            //是套餐
            Long setmeal_id = shoppingCart.getSetmealId();
            LambdaQueryWrapper<ShoppingCart>queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getSetmealId,setmeal_id);
            ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
            if(shoppingCart1!=null) {
                int num =  shoppingCart1.getNumber();
                double price = shoppingCart1.getAmount().doubleValue()/num;
                shoppingCart1.setNumber(num+1);
//                double newprice = price*(num+1);
//                shoppingCart1.setAmount(new BigDecimal(newprice));
                shoppingCart1.setCreateTime(LocalDateTime.now());
                shoppingCartService.updateById(shoppingCart1);
                return R.success(shoppingCart1);
            }else{
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCartService.save(shoppingCart);
                return R.success(shoppingCart);
            }
        }else{
            //是菜品
            Long dish_id = shoppingCart.getDishId();
            LambdaQueryWrapper<ShoppingCart>queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(ShoppingCart::getDishId,dish_id);
            List<ShoppingCart> list = shoppingCartService.list(queryWrapper1);
            String dishflavor =  shoppingCart.getDishFlavor();
            log.info(dishflavor);
            for(ShoppingCart shop:list){
                //如果口味一样 加1
                if(shop.getDishFlavor().equals(dishflavor)){
                    int num = shop.getNumber();
                    double price = shop.getAmount().doubleValue()/num;
                    shop.setNumber(num+1);
//                    double newprice = price*(num+1);
//                    shop.setAmount(new BigDecimal(newprice));
                    shop.setCreateTime(LocalDateTime.now());
                    shoppingCartService.updateById(shop);
                    return R.success(shop);
                }
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            return R.success(shoppingCart);
        }
        //return R.success(shoppingCart);
    }




    //显示购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpSession session){
        //根据ID查询购物信息
        Long user_id = (Long) session.getAttribute("user");
        //查表
        LambdaQueryWrapper<ShoppingCart>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,user_id);
        List<ShoppingCart>list  = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    //清空购物车
    @DeleteMapping("/clean")
    public R<String> clean(HttpSession session){
        //根据ID查询购物信息
        Long user_id = (Long) session.getAttribute("user");
        //查表
        LambdaQueryWrapper<ShoppingCart>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,user_id);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功！");
    }
}
