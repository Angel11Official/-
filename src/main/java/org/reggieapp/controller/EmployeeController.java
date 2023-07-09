package org.reggieapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.reggieapp.common.R;
import org.reggieapp.entity.Employee;
import org.reggieapp.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //登录 postMappering 前端是post方法
    //@Requestbody 将json数据封装成实体类对象
    //HttpServletRequest 将登录信息放到session中 方便以后获取
    @PostMapping(value = "/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        //处理逻辑：
        //密码先MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //用户名比对数据库
        LambdaQueryWrapper<Employee>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //判断是否查询到
        if(emp==null){
            return R.error("登录失败!");
        }
        //密码比对数据库
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败!");
        }
        //查询员工状态 禁用？
        if(emp.getStatus()==0){
            //禁用
            return R.error("账号已禁用！");
        }
        //登录成功 返回成功信息 将账号密码放入session
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    //注销/退出功能
    //要清理 ID 需要参数httpServletRequest
    @PostMapping(value = "/logout")
    public R<String> logout(HttpServletRequest request){
        //清理登录ID
        request.getSession().removeAttribute("employee");
        //返回结果 前端会重定向
        return R.success("退出成功！");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息:{}",employee.toString());
        //设置初始密码123456 md5加密
        employee.setPassword((DigestUtils.md5DigestAsHex("123456".getBytes())));
        //手动设置其他信息
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setCreateUser((Long)request.getSession().getAttribute("employee"));
        //employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));
        //这个save方法 是MybatisPlus替我们实现的
        employeeService.save(employee);
        return R.success("新增员工成功！");
    }


    //员工信息分页查询
    //注意这个泛型 Page
    //参数 page pagesize name
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //传过来Name 构造条件构造器
        LambdaQueryWrapper<Employee>queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        if(name!=null) {
            queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        }
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        //结果封装到pageInfo里
        return R.success(pageInfo);
    }


    //根据ID修改员工信息
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.updateById(employee);
        return R.success("员工信息更改成功");
    }

    //根据ID查询员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee =  employeeService.getById(id);
        if(employee!=null)return R.success(employee);
        return R.error("查询失败");
    }

}
