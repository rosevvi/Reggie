package com.rosevii.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rosevii.common.R;
import com.rosevii.domain.Category;
import com.rosevii.domain.Dish;
import com.rosevii.dto.DishDto;
import com.rosevii.service.CategoryService;
import com.rosevii.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author: rosevvi
 * @date: 2023/3/8 10:10
 * @version: 1.0
 * @description:
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品功能
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success(" 新增菜品成功");
    }

    /**
     * 菜品分页功能
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page>  page(Integer page,Integer pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper =new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //虽然查询到了内容但里面没有页面要显示的菜品分类名称  所以要简单处理下 pageInfo查询到的数据里面有category的id 可以利用这个id 在查询category的name
        //利用类拷贝
        Page<DishDto> dishDtoPage =new Page<>();
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //除了records全部拷贝过来 因为records里面的数据需要简单处理
        List<Dish> list = pageInfo.getRecords();
        List<DishDto> dishDtoList = list.stream().map(item->{
            //因为DishDto继承了Dish 所以Dish有的属性 Dto都有 所以可以进行对象的拷贝 将每个item数据 拷贝到dishdto对象里
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());
        //将完整的Records设置到dishDtoPage中
        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
     public R<DishDto> get(@PathVariable("id") Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }


    /**
     * 根据条件查询对应菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //查询状态为1的  1：起售状态
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByDesc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }


    /**
     * 菜品删除(批量)
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> stop(@RequestParam List<Long> ids){
        //要删除两张表内容所以我们自己创建了一个方法
        dishService.deleteWithFlavor(ids);
        return R.success("删除成功");
    }

    /**
     * 菜品停起售状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
        //停起售状态只需要操作一个表 dish
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId,ids);
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
        list = list.stream().map(item->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());

        dishService.updateBatchById(list);
        return R.success("修改成功");
    }
}
