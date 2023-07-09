package org.reggieapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.reggieapp.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
