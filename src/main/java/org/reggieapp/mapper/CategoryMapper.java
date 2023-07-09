package org.reggieapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.reggieapp.entity.Category;


@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
