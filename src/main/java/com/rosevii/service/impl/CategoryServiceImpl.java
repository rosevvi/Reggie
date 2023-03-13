package com.rosevii.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosevii.domain.Category;
import com.rosevii.domain.Dish;
import com.rosevii.domain.Setmeal;
import com.rosevii.exception.CustomerException;
import com.rosevii.mapper.CategoryMapper;
import com.rosevii.service.CategoryService;
import com.rosevii.service.DishService;
import com.rosevii.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: rosevvi
 * @date: 2023/3/7 11:22
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        long count = dishService.count(dishLambdaQueryWrapper);
        log.info("+++++++++++++++"+count);
        if (count >0){
            throw new CustomerException("当前分类已经关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        long count1 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1 > 0){
            throw new CustomerException("当前分类已经关联了套餐，不能删除");
        }
        //正常删除分类
        super.removeById(id);
    }
}
