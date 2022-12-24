package com.iyoungman.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iyoungman.entity.Employee;
import com.iyoungman.mapper.EmployeeMapper;
import com.iyoungman.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author Blockbuster
 * @date 2022/4/13 16:46:07 星期三
 */

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
