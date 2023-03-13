package com.rosevii.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosevii.domain.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: rosevvi
 * @date: 2023/3/7 11:19
 * @version: 1.0
 * @description:
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
