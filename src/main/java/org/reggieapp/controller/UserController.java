package org.reggieapp.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.reggieapp.common.R;
import org.reggieapp.entity.User;
import org.reggieapp.service.impl.UserServiceImpl;
import org.reggieapp.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserServiceImpl userService;


    //发送验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
         String phone = user.getPhone();
         log.info(phone);
         if(phone!=null){
             //调用工具类 随机生成一个验证码
             String code = ValidateCodeUtils.generateValidateCode(4).toString();
             log.info(code);
             //调用阿里云提供的短信服务发送短信
             //SMSUtils.sendMessage("瑞吉外卖"," ",phone,code);

             //存储验证码到Session用于验证
             session.setAttribute("phone",code);
             return R.success("短信发送成功");
         }

        return R.error("短信发送失败");
    }

    //移动端用户登录
    @PostMapping("/login")
    public R<User>login(@RequestBody Map map, HttpServletRequest request){
        //获取手机号
        String phone = (String) map.get("phone");
        //获取验证码
        String code = (String) map.get("code");
        if(request.getSession().getAttribute("phone")==null)return R.error("手机号错误或未获取验证码");
        //比较验证码
        log.info(request.getSession().getAttribute("phone").toString());
        log.info(code);
         if(request.getSession().getAttribute("phone").toString().equals(code)){
             //判断当前手机号是否是新用户 如果是的话 在用户表中记录
             //查表
             LambdaQueryWrapper<User>queryWrapper = new LambdaQueryWrapper<>();
             queryWrapper.eq(User::getPhone,phone);
             User user =  userService.getOne(queryWrapper);
             if(user==null){
                 user = new User();
                 user.setName(phone);
                 user.setPhone(phone);
                 user.setStatus(1);
                 userService.save(user);
             }
             //登录成功 把用户ID放到session里 因为之后访问页面 过滤器会进行检查
             request.getSession().setAttribute("user",user.getId());
             // 前端会自动跳转 返回user对象给前端
             return R.success(user);
         }
         return R.error("登录失败");
    }


    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出登录成功！");
    }
}
