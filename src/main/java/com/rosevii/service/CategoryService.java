package com.rosevii.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rosevii.domain.Category;

/**
 * @author: rosevvi
 * @date: 2023/3/7 11:19
 * @version: 1.0
 * @description:
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
