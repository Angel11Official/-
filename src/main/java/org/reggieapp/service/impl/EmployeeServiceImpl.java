package org.reggieapp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggieapp.entity.Employee;
import org.reggieapp.mapper.EmployeeMapper;
import org.reggieapp.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
