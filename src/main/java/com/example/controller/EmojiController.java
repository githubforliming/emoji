package com.example.controller;

import com.example.entity.Person;
import com.example.service.EmojiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 发现更多精彩  关注公众号：木子的昼夜编程
 * 一个生活在互联网底层，做着增删改查的码农,不谙世事的造作
 */
@RestController
public class EmojiController {

    @Autowired
    EmojiService service;

    // 新增
    @PostMapping(value = "/person/save")
    Person Person(@RequestBody Person p){
        return service.save(p);
    }

    // 更新昵称
    @PostMapping(value = "/person/updateNickName")
    void updateNickName(@RequestBody Person p){
        service.updateNickName(p);
    }

    // 获取Person对象
    @GetMapping(value = "/person/{id}" ,produces = "application/json; charset=utf-8")
    Person get(@PathVariable(value = "id") Long id){
        return service.get(id);
    }

    // 测试没有白名单的请求 如果有表情 这里输出后已经是过滤掉的了
    @PostMapping(value = "/person/test")
    void PersonTest(@RequestBody Person p){
        System.out.println(p.getNickname());
    }

}
