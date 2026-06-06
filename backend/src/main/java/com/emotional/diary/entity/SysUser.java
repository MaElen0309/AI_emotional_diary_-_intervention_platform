package com.emotional.diary.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysUser {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Integer role;
    private Integer status;
    private String studentId;
    private String department;
    private String major;
    private String grade;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static final int ROLE_STUDENT = 0;
    public static final int ROLE_TEACHER = 1;
    public static final int ROLE_ADMIN = 2;

    public boolean isAdmin() {
        return this.role != null && this.role == ROLE_ADMIN;
    }

    public boolean isTeacher() {
        return this.role != null && this.role == ROLE_TEACHER;
    }

    public boolean isStudent() {
        return this.role != null && this.role == ROLE_STUDENT;
    }
}
