package cn.ximuli.jframex.service;

import cn.ximuli.jframex.model.Department;

import java.util.List;

public interface DepartmentService {

    List<Department> queryAllDepartments();

    Department getRootDepartment();
}
