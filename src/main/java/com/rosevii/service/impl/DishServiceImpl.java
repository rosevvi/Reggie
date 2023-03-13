package com.rosevii.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosevii.domain.Dish;
import com.rosevii.domain.DishFlavor;
import com.rosevii.dto.DishDto;
import com.rosevii.exception.CustomerException;
import com.rosevii.mapper.DishMapper;
import com.rosevii.service.DishFlavorService;
import com.rosevii.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: rosevvi
 * @date: 2023/3/7 15:41
 * @version: 1.0
 * @description:
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时插入菜品对应的口味信息，需要操作两张表：dish、dish_flavor
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        //如果直接保存到dish_flavor中会发现缺少dish_id因为dishDto中的flavor集合中只有dish_flavor的value和name 而id在dishDto中并没有添加进去
        //所以需要对里面的dish_id进行赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(flavor->{
            flavor.setDishId(dishDto.getId());
         });

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 根据id查询对应的口味信息
     * @param id
     * @return
     */
    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查
        Dish dish = this.getById(id);
        //查询到菜品基本信息后拷贝到DishDto中  因为DishDto继承自Dish 所以Dish有的属性它都有
        DishDto dishDto=new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        //查询口味信息，从dish_flavor表查
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavorList);
        return dishDto;
    }


    /**
     * 更新菜品信息以及菜品口味信息
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //首先更新dish表信息
        this.updateById(dishDto);
        //清理当前菜品对应口味数据--dish_flavor表的delete操作 因为网页端的口味数据是直接删除没有向后端发起请求 所以要先删除再添加
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //因为dishDto里面的flavors里面的数据中没有dishId数据 所以要遍历一遍并给他赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(item->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //添加当前提交过来的数据--dish_flavor表的insert操作
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品信息同时删除菜品口味信息
     * @param ids
     */
    @Override
    @Transactional
    public void deleteWithFlavor(List<Long> ids) {
        //要删除菜品信息首先要查看是否为停售状态
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        dishLambdaQueryWrapper.in(Dish::getId,ids);
        //如果查询到数据表示为在售状态 不能删除
        long count = this.count(dishLambdaQueryWrapper);
        if (count > 0){
            //不能删除 抛出一个业务异常  会显示到前端页面
            throw new CustomerException("在售状态，不能删除");
        }

        //可以删除 就先删除dish表数据
        this.removeBatchByIds(ids);
        //再删除 dish_flavor表
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
    }
}
