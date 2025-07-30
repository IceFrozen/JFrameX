package cn.ximuli.jframex.service.impl;

import cn.ximuli.jframex.model.Department;
import cn.ximuli.jframex.service.DepartmentService;
import cn.ximuli.jframex.service.mock.MockClass;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Override
    public List<Department> queryAllDepartments() {
        return MockClass.getDepartmentList();
    }

    @Override
    public Department getRootDepartment() {
        List<Department> departments = this.queryAllDepartments();
        // 使用 findFirst 并检查 Optional 是否包含值
        Optional<Department> rootDepartment = departments.stream()
                .filter(d -> d.getParentId() == null)
                .findFirst();

        // 如果存在则返回，否则抛出自定义异常或提供默认值
        return rootDepartment.orElseThrow(() -> new RuntimeException("not exist"));
    }
}
