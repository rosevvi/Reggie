package com.rosevii.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosevii.domain.DishFlavor;
import com.rosevii.mapper.DishFlavorMapper;
import com.rosevii.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author: rosevvi
 * @date: 2023/3/8 10:09
 * @version: 1.0
 * @description:
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
