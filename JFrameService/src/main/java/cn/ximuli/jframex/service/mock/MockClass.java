package cn.ximuli.jframex.service.mock;

import cn.ximuli.jframex.common.utils.RandomUtil;
import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.model.Department;
import cn.ximuli.jframex.model.Page;
import cn.ximuli.jframex.model.User;
import cn.ximuli.jframex.model.UserType;
import cn.ximuli.jframex.model.constants.Sex;
import cn.ximuli.jframex.model.constants.Status;
import cn.ximuli.jframex.service.util.SpringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MockClass {

    public static final List<UserType> userTypes = getUserTypes();

    public static final List<Department> departmentList = getDepartmentList();


    public static final List<User> allUser = getAllUser();

    public static List<User> getAllUser() {
        return UserGenerator.generateUsers(1000);
    }


    public static Page<User> getUserByPage(int pageNumber, int pageSize) {
        return getUserByPage(allUser, pageNumber, pageSize);
    }

    public static Page<User> getUserByPage(List<User> userList, int pageNumber, int pageSize) {
        int totalUsers = userList.size();
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        int startIndex = (pageNumber - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalUsers);

        List<User> pageUsers = new ArrayList<>(userList.subList(startIndex, endIndex));

        Page<User> page = new Page<>();
        page.setPage(pageNumber);
        page.setPageSize(pageSize);
        page.setTotal(totalPages);
        page.setData(pageUsers);
        return page;
    }

    public static List<UserType> getUserTypes() {
        if (SpringUtils.getDefaultLocale().getLanguage().contains("en")) {
            return List.of(
                    new UserType(1, "Admin"),
                    new UserType(2, "User")
            );
        }

        return List.of(
                new UserType(1, "管理员"),
                new UserType(2, "普通用户")
        );
    }

    public static List<Department> getDepartmentList() {
        // 根据语言动态获取部门数据
        String[][] departmentData = getDepartmentData(SpringUtils.getDefaultLocale().getLanguage().contains("en"));

        // 创建部门列表
        List<Department> departments = new ArrayList<>();
        for (String[] data : departmentData) {
            departments.add(createDepartment(data[0], data[1], data[2]));
        }
        return departments;
    }

    private static String[][] getDepartmentData(boolean isEnglish) {
        if (isEnglish) {
            return new String[][]{
                    {"1", "Human Resources Department", null},
                    {"2", "Recruitment Group", "1"},
                    {"3", "Training Group", "1"},
                    {"4", "Technology Department", null},
                    {"5", "Frontend Group", "4"},
                    {"6", "Backend Group", "4"},
                    {"7", "Sales Department", null},
                    {"8", "Domestic Sales Group", "7"},
                    {"9", "International Sales Group", "7"}
            };
        } else {
            return new String[][]{
                    {"1", "人力资源部", null},
                    {"2", "招聘组", "1"},
                    {"3", "培训组", "1"},
                    {"4", "技术部", null},
                    {"5", "前端组", "4"},
                    {"6", "后端组", "4"},
                    {"7", "销售部", null},
                    {"8", "国内销售组", "7"},
                    {"9", "国际销售组", "7"}
            };
        }
    }

    private static Department createDepartment(String id, String name, String parentId) {
        Department dept = new Department();
        dept.setId(id);
        dept.setName(name);
        dept.setParentId(parentId);
        return dept;
    }

    public static Page<User> searchUserByPage(String departmentId, String searchInput, int page, int pageSize) {
        List<User> filteredUser = allUser;
        if (StringUtil.isNoneBlank(departmentId)) {
            filteredUser = filteredUser.stream().filter(user -> departmentId.equals(user.getDepartment().getId())).toList();
        }
        if (StringUtil.isNoneBlank(searchInput)) {
            filteredUser = filteredUser.stream().filter(user ->
                    searchInput.equals(user.getId() + "") ||
                    user.getName().contains(searchInput) ||
                    user.getEmail().contains(searchInput)).toList();
        }
        return getUserByPage(filteredUser, page, pageSize);
    }

    public class UserGenerator {

        private static final Random RANDOM = new Random();


        public static List<User> generateUsers(int count) {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                User user = new User();
                user.setId(i + 1);
                user.setName("User" + (i + 1));
                user.setSex(randomSex());
                user.setBirthday(randomBirthday());
                user.setPhone(randomPhone());
                user.setEmail(user.getName() + "@example.com");
                user.setUserType(randomUserType());
                user.setDepartment(randomDepartment());
                user.setStatus(randomStatus());
                user.setCreateTime(LocalDateTime.now());
                users.add(user);
            }
            return users;
        }

        private static Sex randomSex() {
            return RANDOM.nextBoolean() ? Sex.MALE : Sex.FEMALE;
        }

        private static LocalDate randomBirthday() {
            int year = 1950 + RANDOM.nextInt(70); // 1950 到 2020 之间的随机年份
            int month = 1 + RANDOM.nextInt(12); // 1 到 12 之间的随机月份
            int day = 1 + RANDOM.nextInt(28); // 1 到 28 之间的随机日期
            return LocalDate.of(year, month, day);
        }

        private static String randomPhone() {
            return "1" + String.format("%010d", RANDOM.nextInt(1000000000));
        }

        private static UserType randomUserType() {
            return userTypes.get(RandomUtil.randomInt(0, userTypes.size()));
        }

        public static Department randomDepartment() {
            List<Department> leafDepartments = getLeafDepartments(departmentList);
            if (leafDepartments.isEmpty()) {
                return null; // 或者抛出异常，根据需求决定
            }
            return leafDepartments.get(RandomUtil.randomInt(0, leafDepartments.size()));
        }

        private static List<Department> getLeafDepartments(List<Department> departments) {
            Map<String, Department> departmentMap = new HashMap<>();
            for (Department dept : departments) {
                departmentMap.put(dept.getId(), dept);
            }

            List<Department> leafDepartments = new ArrayList<>();
            for (Department dept : departments) {
                if (!departmentMap.containsKey(dept.getParentId())) {
                    continue;
                }
                boolean isLeaf = true;
                for (Department otherDept : departments) {
                    if (otherDept.getParentId() != null && otherDept.getParentId().equals(dept.getId())) {
                        isLeaf = false;
                        break;
                    }
                }
                if (isLeaf) {
                    leafDepartments.add(dept);
                }
            }
            return leafDepartments;
        }

        private static Status randomStatus() {
            Status[] statuses = Status.values();
            return statuses[RANDOM.nextInt(statuses.length)];
        }
    }


}
