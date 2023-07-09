package org.reggieapp.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.reggieapp.common.BaseContext;
import org.reggieapp.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//注解WebFilter
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器 支持通配符
    public static  final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //转型
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        //具体的处理逻辑
        //获取本次请求的URI
        String requestURI=request.getRequestURI();
        //日志
        log.info("拦截到请求：{}",requestURI);
        //判断本次请求是否需要处理
        //如果访问的是某些特定路径 不需要处理
        String[] urls = new String[]{
               "/employee/login",//登录按钮
               "/employee/logout",//退出按钮
                "/backend/**",//静态
                "/front/**",   //静态
                "/user/login",
                "/user/sendMsg"
        };
        boolean check = check(requestURI,urls);
        //如果不需要处理直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //需要处理：判断登录状态 如果已经登录 放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("employee"));
            //设置当前用户ID到线程中
            Long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("user"));
            //设置当前用户ID到线程中
            Long UserId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(UserId);
            filterChain.doFilter(request,response);
            return;
        }
        //需要处理：没登录 则返回未登录结果 通过输出流方式向页面返回对应数据
        // 前端有响应拦截器 会返回登录界面
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString((R.error("NOTLOGIN"))));

        //放行
        //filterChain.doFilter(request,response);
    }

    //路径匹配方法
    public boolean check(String requestURI,String[]urls){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match) return true;
        }
        return false;
    }
}
