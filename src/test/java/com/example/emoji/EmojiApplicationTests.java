package com.example.emoji;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmojiApplicationTests {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    void insert() throws Exception {
        JSONObject params = new JSONObject();
        params.put("name", "刘强国");
        params.put("nickname", "小锅锅");
        params.put("age", "18");
        params.put("gender", "男");
        // 插入数据
        String res = mockMvc.perform(post("/person/save")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(params.toString()))
                .andReturn().getResponse().getContentAsString();        // 获取方法的返回值
        JSONObject parse = JSONObject.parseObject(res);
        System.out.println("插入数据自增主键ID=" + parse.get("id"));

    }

    @Test
    void updateNickName() throws Exception {
        JSONObject params = new JSONObject();
        params.put("id", "15"); // isnert方法输出了自增主键ID
        // emoji表情 会报错
        params.put("nickname","\uD83D\uDE01\uD83D\uDE0A\uD83D\uDE42\uD83D\uDE42\uD83D\uDE42\uD83D\uDC66\uD83C\uDFFF");
        // 插入数据
        mockMvc.perform(post("/person/updateNickName")
                .servletPath("/person/updateNickName") // 这里如果不设置servletPath Filter获取不到
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(params.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPerson() throws Exception {
        // 插入数据
        String contentAsString = mockMvc.perform(
                get("/person/15")
        ).andReturn().getResponse().getContentAsString();
        Person p = JSONObject.parseObject(contentAsString, Person.class);

        System.out.println("昵称："+p.getNickname());
    }

    // 这个测试 会看到 emoji表情被过滤掉了
    @Test
    void saveTest() throws Exception {
        JSONObject params = new JSONObject();
        params.put("id", "15"); // isnert方法输出了自增主键ID
        // emoji表情 会报错
        params.put("nickname","后边是表情，但是会被过滤掉：\uD83D\uDE01\uD83D\uDE0A\uD83D\uDE42\uD83D\uDE42\uD83D\uDE42\uD83D\uDC66\uD83C\uDFFF");
        // 插入数据
        mockMvc.perform(post("/person/test")
                .servletPath("/person/test") // 这里如果不设置servletPath Filter获取不到
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(params.toString()))
                .andExpect(status().isOk());
    }

}
