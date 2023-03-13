package com.rosevii.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosevii.domain.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: rosevvi
 * @date: 2023/3/6 9:46
 * @version: 1.0
 * @description:
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
