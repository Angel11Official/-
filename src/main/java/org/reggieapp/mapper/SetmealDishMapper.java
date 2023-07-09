package org.reggieapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.apache.ibatis.annotations.Mapper;
import org.reggieapp.entity.SetmealDish;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
}
