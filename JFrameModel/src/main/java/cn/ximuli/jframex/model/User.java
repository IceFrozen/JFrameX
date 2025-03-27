package cn.ximuli.jframex.model;

import cn.ximuli.jframex.model.constants.Sex;
import cn.ximuli.jframex.model.constants.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class User {

    private int id;
    private String name;

    private Sex sex;
    private LocalDate birthday;

    private String phone;

    private String email;

    private UserType userType;


    private Department department;

    private Status status;

    private LocalDateTime createTime;

}
