package com.rosevii.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rosevii.common.R;
import com.rosevii.domain.Category;
import com.rosevii.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author: rosevvi
 * @date: 2023/3/7 11:23
 * @version: 1.0
 * @description:
 */

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param category
     * @return
     */
    @PostMapping
    @CacheEvict(value = "category",allEntries = true)
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增成功");
    }

    /**
     * 分页查询菜品分类
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    @Cacheable(value = "category",key = "'category_page'+#page+':'+#pageSize+':'+#name")
    public R<Page> page(Integer page,Integer pageSize,String name){
        //分页构造器
        Page<Category> pageInfo=new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null, Category::getName,name);
        //添加排序条件  升序
        queryWrapper.orderByAsc(Category::getSort);
        //执行分页查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping()
    @Caching(evict = {
            @CacheEvict(value = "category",allEntries = true),
    })
    public R<String> delete(Long ids){
        log.info("删除功能");
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    /**
     * 修改菜品分类
     * @param category
     * @return
     */
    @PutMapping
    @CacheEvict(value = "category",allEntries = true)
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 获取菜品分类
     * @param category
     * @return
     */
    @RequestMapping("/list")
    @Cacheable(value = "category",key = "'category_list'+#category.id",unless = "#category == null ")
    public R<List<Category>> getCategoryList(Category category){
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}
