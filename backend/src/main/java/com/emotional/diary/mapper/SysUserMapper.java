package com.emotional.diary.mapper;

import com.emotional.diary.entity.SysUser;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SysUserMapper {

    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    SysUser findById(Long id);

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser findByUsername(String username);

    @Select("SELECT * FROM sys_user WHERE username LIKE CONCAT('%', #{keyword}, '%') OR real_name LIKE CONCAT('%', #{keyword}, '%') LIMIT #{offset}, #{pageSize}")
    List<SysUser> findByKeyword(@Param("keyword") String keyword, 
                                 @Param("offset") int offset, 
                                 @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM sys_user WHERE username LIKE CONCAT('%', #{keyword}, '%') OR real_name LIKE CONCAT('%', #{keyword}, '%')")
    int countByKeyword(@Param("keyword") String keyword);

    @Insert("INSERT INTO sys_user (username, password, real_name, nickname, email, phone, role, status, student_id, department, major, grade) " +
            "VALUES (#{username}, #{password}, #{realName}, #{nickname}, #{email}, #{phone}, #{role}, #{status}, #{studentId}, #{department}, #{major}, #{grade})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    @Update("UPDATE sys_user SET real_name=#{realName}, nickname=#{nickname}, avatar=#{avatar}, email=#{email}, phone=#{phone}, " +
            "student_id=#{studentId}, department=#{department}, major=#{major}, grade=#{grade} WHERE id=#{id}")
    int update(SysUser user);

    @Update("UPDATE sys_user SET password=#{password} WHERE id=#{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE sys_user SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteById(Long id);
}
