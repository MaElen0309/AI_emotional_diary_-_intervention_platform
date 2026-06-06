package com.emotional.diary.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface SystemConfigMapper {

    @Select("SELECT * FROM system_config WHERE config_key = #{key}")
    Map<String, Object> findByKey(String key);

    @Select("SELECT * FROM system_config")
    List<Map<String, Object>> findAll();

    @Insert("INSERT INTO system_config (config_key, config_value, config_type, description) " +
            "VALUES (#{configKey}, #{configValue}, #{configType}, #{description})")
    int insert(Map<String, Object> config);

    @Update("UPDATE system_config SET config_value=#{configValue}, description=#{description} WHERE config_key=#{configKey}")
    int update(@Param("configKey") String key, @Param("configValue") String value, @Param("description") String description);

    @Delete("DELETE FROM system_config WHERE config_key = #{key}")
    int deleteByKey(String key);
}
