package com.example.entity;

import lombok.Data;

/**
 * @author 发现更多精彩  关注公众号：木子的昼夜编程
 * 一个生活在互联网底层，做着增删改查的码农,不谙世事的造作
 */
@Data
public class Person {
    private Long id;
    private String name;
    private String nickname;
    private Integer age;
    private String gender;
}
