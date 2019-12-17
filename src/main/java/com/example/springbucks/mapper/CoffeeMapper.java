package com.example.springbucks.mapper;

import com.example.springbucks.model.Coffee;
import org.apache.ibatis.annotations.*;
@Mapper
public interface CoffeeMapper {
    @Insert("insert into t_coffee (name, price, create_time,update_time)" + "values (#{name},#{price},now(),now())")
    /*需要加上回填的字段名称才能回填*/
    @Options(useGeneratedKeys = true,keyProperty="id")
    int save(Coffee coffee);

    @Select("select * from t_coffee where id = #{id}")
    @Results({
            @Result(id = true,column = "id",property = "id"),
            @Result(column = "create_time",property = "createTime"),
    })
    Coffee findById(@Param("id") Long id);
}
