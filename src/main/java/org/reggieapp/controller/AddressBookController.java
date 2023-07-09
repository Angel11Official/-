package org.reggieapp.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.reggieapp.common.R;
import org.reggieapp.entity.AddressBook;
import org.reggieapp.service.impl.AddressBookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {


    @Autowired
    AddressBookServiceImpl addressBookService;

    //用户添加地址
    //传过来的数据 用 addressBook对象接受
    @PostMapping
    public R<String> addAddress(@RequestBody AddressBook addressBook, HttpSession session){
        //当前对象缺少的属性 user_id
        //怎么获取？ session
        Long user_id = (Long)session.getAttribute("user");
        addressBook.setUserId(user_id);
        //查询是否有地址
        LambdaQueryWrapper<AddressBook>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,user_id);
        List<AddressBook>list = addressBookService.list(queryWrapper);
        if(list.size()==0){
            addressBook.setIsDefault(1);
        }
        //写入数据库
        addressBookService.save(addressBook);
        return R.success("添加地址成功");
    }

    //显示用户的地址
    @GetMapping("/list")
    public R<List<AddressBook>> getAddress(HttpSession session){
        Long user_id = (Long)session.getAttribute("user");
        //条件构造器
        LambdaQueryWrapper<AddressBook>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,user_id);
        //执行查询
        return R.success(addressBookService.list(queryWrapper));
    }

    //设置为默认地址
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook,HttpSession session){
        Long address_id = addressBook.getId();

        //检查用户是否已经有默认地址
        Long user_id = (Long)session.getAttribute("user");
        LambdaQueryWrapper<AddressBook>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,user_id);
        boolean check = true;
        List<AddressBook> list = addressBookService.list(queryWrapper);
        Long default_address_id = Long.valueOf(0);
        AddressBook default_address = new AddressBook();
        for(AddressBook address:list){
            if(address.getIsDefault()==1){
                default_address_id = address.getId();
                break;
            }
        }
        if(default_address_id.equals(addressBook.getId())){
            return R.success("该地址已经是默认地址");
        }else {
            addressBook.setIsDefault(1);
            //修改原默认
            AddressBook addressBook1 = addressBookService.getById(default_address_id);
            addressBook1.setIsDefault(0);
            addressBookService.updateById(addressBook1);
            //修改新默认
            addressBookService.updateById(addressBook);
            return R.success("修改默认地址成功！");
        }
    }

    //返回默认地址
    @GetMapping("/getdefault")
    public R<AddressBook> getdefault(HttpSession session){
        Long user_id = (Long) session.getAttribute("user");
        LambdaQueryWrapper<AddressBook>queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,user_id);
        List<AddressBook> list =addressBookService.list(queryWrapper);
        for(AddressBook addressBook:list){
            if(addressBook.getIsDefault()==1){
                return R.success(addressBook);
            }
        }
        return R.error(null);
    }


    //回显地址信息
    @GetMapping("/{id}")
    public R<AddressBook> returnAddress(@PathVariable Long id){
        log.info(id.toString());
        //根据id回显
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    //修改地址信息
    @PutMapping()
    public R<String> setAddress(@RequestBody AddressBook addressBook){
        //得到ID
        Long address_id = addressBook.getId();
        //修改
        addressBookService.updateById(addressBook);
        return R.success("修改地址信息成功!");
    }
}
