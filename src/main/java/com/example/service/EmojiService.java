package com.example.service;

import com.example.dao.PersonMapper;
import com.example.entity.Person;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 发现更多精彩  关注公众号：木子的昼夜编程
 * 一个生活在互联网底层，做着增删改查的码农,不谙世事的造作
 */
@Service
public class EmojiService {

    @Autowired
    PersonMapper mapper;

    // 新增数据
    public Person save(Person p) {
        // 转换为别名
        if (StringUtils.isNoneBlank(p.getNickname())){
            System.out.println("昵称转换前："+p.getNickname());
            p.setNickname(EmojiParser.parseToAliases(p.getNickname()));
            System.out.println("昵称转换前："+p.getNickname());
        }
        mapper.save(p);
        return p;
    }

    // 根据ID更新昵称数据
    public void updateNickName(Person p) {
        if (StringUtils.isNoneBlank(p.getNickname())){
            System.out.println("昵称转换前："+p.getNickname());
            p.setNickname(EmojiParser.parseToAliases(p.getNickname()));
            System.out.println("昵称转换前："+p.getNickname());
        }
        mapper.updateNickName(p.getNickname(), p.getId());
    }

    // 获取Person对象
    public Person get(Long id) {
        Person p = mapper.get(id);
        if (StringUtils.isNoneBlank(p.getNickname())) {
            System.out.println("昵称转换前："+p.getNickname());
            p.setNickname(EmojiParser.parseToUnicode(p.getNickname()));
            System.out.println("昵称转换后："+p.getNickname());
        }
        return p;
    }
}
