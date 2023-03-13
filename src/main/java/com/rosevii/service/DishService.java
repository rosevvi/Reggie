package com.rosevii.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rosevii.domain.Dish;
import com.rosevii.dto.DishDto;

import java.util.List;

/**
 * @author: rosevvi
 * @date: 2023/3/7 15:40
 * @version: 1.0
 * @description:
 */
public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味信息，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时还要更新口味信息
    public void  updateWithFlavor(DishDto dishDto);

    //删除菜品信息，同时还要删除菜品口味信息，需要操作两张表：dish、dish_flavor
    public void deleteWithFlavor(List<Long> ids);
}
