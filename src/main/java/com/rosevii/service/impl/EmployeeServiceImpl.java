package com.rosevii.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosevii.domain.Employee;
import com.rosevii.mapper.EmployeeMapper;
import com.rosevii.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: rosevvi
 * @date: 2023/3/6 9:48
 * @version: 1.0
 * @description:
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService{

}
