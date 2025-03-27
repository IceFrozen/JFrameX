package cn.ximuli.jframex.service.impl;

import cn.ximuli.jframex.model.Department;
import cn.ximuli.jframex.service.DepartmentService;
import cn.ximuli.jframex.service.mock.MockClass;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Override
    public List<Department> queryAllDepartments() {
        return MockClass.departmentList;
    }
}
