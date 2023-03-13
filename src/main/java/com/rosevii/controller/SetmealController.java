package com.rosevii.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rosevii.common.R;
import com.rosevii.domain.Category;
import com.rosevii.domain.Setmeal;
import com.rosevii.domain.SetmealDish;
import com.rosevii.dto.SetmealDto;
import com.rosevii.exception.CustomerException;
import com.rosevii.service.CategoryService;
import com.rosevii.service.SetmealDishService;
import com.rosevii.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: rosevvi
 * @date: 2023/3/9 10:44
 * @version: 1.0
 * @description:
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐,同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> saveWithDish(@RequestBody SetmealDto setmealDto){
        System.out.println(setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐管理分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper= new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行模糊查询
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo);
        //到这里虽然page里面有数据了但是前端显示会有问题 因为另一个套餐分类不在这个表没有查出来 所以要利用setmealDto来进行简单处理
        //进行对象的复制
        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        //现在setmealDtoPage里面和pageInfo里面基本一样 但是数据没有复制过来 利用pageInfo里面的records数据挨个遍历转换为SetmealDto对象
        List<Setmeal> pageInfoRecords = pageInfo.getRecords();
        List<SetmealDto> setmealDtoList= pageInfoRecords.stream().map(item->{
            //创建一个SetmealDto对象来将item的基本数据装到SetmealDto对象中
            SetmealDto setmealDto=new SetmealDto();
            //利用对象拷贝将基本数据拷贝过去
            BeanUtils.copyProperties(item,setmealDto);
            //利用每个item获取到categoryId
            Long categoryId = item.getCategoryId();
            //利用categoryId来对数据库进行查询 获取到category对象
            Category category = categoryService.getById(categoryId);
            //获取到套餐分类的名字
            String categoryName = category.getName();
            //现在setmealDto里面只有基本数据但是套餐分类的名字还没有设置
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(setmealDtoList);
        return R.success(setmealDtoPage);
    }

    /**
     * 套餐删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 修改套餐的套餐回显
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<SetmealDto> update(@PathVariable Long id){

        SetmealDto setmealDto = setmealService.update(id);
        return R.success(setmealDto);
    }


    /**
     * 修改套餐--提交
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        //因为这个修改需要操作两个表所以我们自己写一个新的方法
        setmealService.update(setmealDto);
        return R.success("修改成功");
    }

    /**
     * 套餐的停起售
     * @param id
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
        //套餐的停起售操作一张表就可以
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        setmealList = setmealList.stream().map(item->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(setmealList);

        return R.success("修改成功");
    }

}
