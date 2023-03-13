package com.rosevii.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosevii.domain.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: rosevvi
 * @date: 2023/3/7 15:42
 * @version: 1.0
 * @description:
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
