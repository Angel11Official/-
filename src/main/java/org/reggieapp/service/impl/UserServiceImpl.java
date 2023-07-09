package org.reggieapp.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggieapp.entity.User;
import org.reggieapp.mapper.UserMapper;
import org.reggieapp.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
