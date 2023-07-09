package org.reggieapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.LongValue;
import org.reggieapp.common.R;
import org.reggieapp.dto.SetmealDto;
import org.reggieapp.entity.Setmeal;
import org.reggieapp.service.CategoryService;
import org.reggieapp.service.impl.CategoryServiceImpl;
import org.reggieapp.service.impl.SetmealServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.util.SimpleElementVisitor6;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {


    @Autowired
    private SetmealServiceImpl setmealService;
    @Autowired
    private CategoryServiceImpl categoryService;
    //套餐分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal>pageInfo = new Page(page,pageSize);
        Page<SetmealDto>setmealDtoPageInfo = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Setmeal>queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        if(name != null){
            queryWrapper.like(!StringUtils.isEmpty(name),Setmeal::getName,name);
        }
        //添加排序条件
        queryWrapper.orderByAsc(Setmeal::getPrice).orderByAsc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo,queryWrapper);
        //拷贝除了records之外的属性
        BeanUtils.copyProperties(pageInfo,setmealDtoPageInfo,"records");
        //填充每个setmealdto中的categoryName
        List<SetmealDto> list = new ArrayList<>();
        pageInfo.getRecords().forEach(setmeal -> {
            //新建一个dto对象
            SetmealDto setmealDto = new SetmealDto();
            //将setmeal中信息拷贝到dto中
            BeanUtils.copyProperties(setmeal,setmealDto);
            //对于每一个套餐 得到分类ID
            Long c_id = setmeal.getCategoryId();
            //对于每一个分类ID 得到对应的分类名
            String c_name = categoryService.getById(c_id).getName();
            //填入dto对象
            setmealDto.setCategoryName(c_name);
            //加入列表
            list.add(setmealDto);
        });
        //将list写入pageinfo
        setmealDtoPageInfo.setRecords(list);
        //返回查询结果
        return R.success(setmealDtoPageInfo);
    }


    //新增套餐
    @PostMapping
    public R<String> add(@RequestBody SetmealDto setmealDto){
         setmealService.addSetmeal(setmealDto);
         return R.success("添加套餐成功！");
    }


    //根据ID来查询套餐信息
    //回显
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        return R.success(setmealService.getSetMealDto(id));
    }


    //修改套餐
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateSetmealDto(setmealDto);
        return R.success("修改套餐信息成功!");
    }

    //删除套餐
    @DeleteMapping
    public R<String> delete(String ids){
        setmealService.deleteSetmeal(ids);
        return R.success("删除套餐成功！");
    }


    //起售套餐
    @PostMapping("/status/1")
    public R<String> open(String ids){
        setmealService.open(ids);
        return R.success("启售套餐成功！");
    }


    //停售套餐
    @PostMapping("/status/0")
    public R<String> close(String ids){
        setmealService.close(ids);
        return R.success("停售套餐成功！");
    }

    //客户端返回套餐分类对应的套餐
    @GetMapping("/list")
    public R<List<Setmeal>> list(String categoryId,String status){
        //获取要查询的分类ID和状态
        Integer s = new Integer(status).intValue();
        Long c_Id = new LongValue(categoryId).getValue();
        //条件构造器
        LambdaQueryWrapper<Setmeal>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,c_Id);
        queryWrapper.eq(Setmeal::getStatus,s);
        List<Setmeal>retlist = setmealService.list(queryWrapper);
        //返回查询结果
        return R.success(retlist);
    }
}
