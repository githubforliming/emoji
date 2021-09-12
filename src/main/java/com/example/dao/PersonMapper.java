package com.example.dao;

import com.example.entity.Person;
import org.apache.ibatis.annotations.*;

/**
 * @author 发现更多精彩  关注公众号：木子的昼夜编程
 * 一个生活在互联网底层，做着增删改查的码农,不谙世事的造作
 */
@Mapper
public interface PersonMapper {

    // 插入数据
    @Insert(value = "INSERT INTO person " +
            "(name,nickname,age,gender) " +
            "VALUES (#{name},#{nickname},#{age},#{gender} )")
    @SelectKey(statement = "select last_insert_id()", keyProperty = "id", before = false, resultType = Long.class)
    void save(Person p) ;

    // 根据ID更新昵称
    @Update(value = "update person set nickname=#{nickname}  where id = #{id}")
    void updateNickName(@Param("nickname") String nickname, @Param("id") Long id);

    @Select("select * from person where id =#{id}")
    Person get(Long id);
}
