package com.rosevii.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rosevii.domain.Setmeal;
import com.rosevii.dto.SetmealDto;

import java.util.List;

/**
 * @author: rosevvi
 * @date: 2023/3/7 15:41
 * @version: 1.0
 * @description:
 */
public interface SetmealService extends IService<Setmeal> {

    //新增套餐
    public void saveWithDish(SetmealDto setmealDto);

    //删除套餐
    public void removeWithDish(List<Long> ids);

    //修改套餐（回显）
    public SetmealDto update(Long id);

    //修改套餐--提交
    public void update(SetmealDto setmealDto);
}
