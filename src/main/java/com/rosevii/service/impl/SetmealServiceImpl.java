package com.rosevii.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosevii.domain.Setmeal;
import com.rosevii.domain.SetmealDish;
import com.rosevii.dto.SetmealDto;
import com.rosevii.exception.CustomerException;
import com.rosevii.mapper.SetmealMapper;
import com.rosevii.service.SetmealDishService;
import com.rosevii.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: rosevvi
 * @date: 2023/3/7 15:44
 * @version: 1.0
 * @description:
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐,同时需要保存套餐和菜品的管理关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息操作setmeal，执行inset操作
        this.save(setmealDto);
        //因为setmealDto继承了Setmeal所以Setmeal有的数据他都有  但是setmealDto中的setmeal_dish里面的setmealId没有  所以要设置一下
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息，操作setmeal_Dish,执行inset操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品关联的数据
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态查询是否能删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        //起售状态才可以删除
        queryWrapper.eq(Setmeal::getStatus,1);
        //我们只查询他有没有不可以删除的  大于0表示有在售状态不能删除
        long count = this.count(queryWrapper);
        if (count >0 ){
            //如果不能删除抛出业务异常
            throw new CustomerException("套餐正在售卖，不能删除");
        }
        //如果可以删除 先删除套餐表---setmeal
        this.removeByIds(ids);
        //在删除套餐表关联的菜品信息---setmeal_dish
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishLambdaQueryWrapper);
    }

    /**
     * 修改套餐  内容回显
     * @param id
     * @return
     */
    @Transactional
    @Override
    public SetmealDto update(Long id) {
        //因为前端页面显示的内容中有两个表的数据 所以要查询两个表
        //且SetmealDto实体类可以保存这两个表的数据所以我们返回值选择setmealDto
        SetmealDto setmealDto=new SetmealDto();
        Setmeal setmeal = this.getById(id);
        //通过类拷贝将数据拷贝给setmealDto
        BeanUtils.copyProperties(setmeal,setmealDto);
        //此时dto中还有套餐菜品信息没有拷贝 所以先利用setmeal中的setmealId查询setmeal_dish表将数据拿到后拷贝给dto
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        //然后给setmealdto给它的属性setmeal_dishes赋值
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    /**
     * 修改套餐的提交功能
     * @param setmealDto
     */
    @Override
    @Transactional
    public void update(SetmealDto setmealDto) {
        //先对setmeal表进行更新
        this.updateById(setmealDto);
        //在对setmeal_dish更新   因为前端删除套餐菜品时没有发送请求  所以要把整个的setmeal_dish删除后再新插入一条完成更新
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        //然后新插入一条数据
        //插入前发现 在setmeal中的setmealDishes集合里面的数据中并没有setmeal的id  所以要先遍历setmealDishes给他的setmealid赋值
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //现在setmeal的属性setmealDishes集合中的每个数据都有了setmeal的id
        setmealDishService.saveBatch(setmealDishes);
    }
}
