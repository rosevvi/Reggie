package com.rosevii.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rosevii.common.R;
import com.rosevii.domain.Employee;
import com.rosevii.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author: rosevvi
 * @date: 2023/3/6 9:52
 * @version: 1.0
 * @description:
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        System.out.println("----------------------");
        //1、md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println("====================="+employee.getUsername());
        //2、根据用户名查询数据库
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);
        //3、如果没有查询到用户返回登录失败结果
        if (emp ==null){
            return R.error("登录失败");
        }
        //4、密码对比，如果不一致返回登陆失败
        if (!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }

        //5、查看员工状态如果已禁用返回已禁用
        if (emp.getStatus()==0){
            return R.error("账号已禁用");
        }
        //6、登录成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //1、清理session中保存的当前员工的对象id
        request.getSession().removeAttribute("employee");
        //2、
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工 员工信息：{}",employee.toString());
        //1、统一设置默认密码123456 需要进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("12345".getBytes()));
        //2、status不需要设置会有默认值
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //获取当前用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){
        //构造分页构造器
        Page pageInfo =new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        log.info("page="+page+",pagesize="+pageSize+",name="+name);
        return R.success(pageInfo);
    }

    /**
     * 根据用户id修改状态
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<Employee> updateEmployee(@PathVariable("id") Long id){
        Employee employee = employeeService.getById(id);
        if (employee!= null){
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }

}
