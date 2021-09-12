package com.example.filter;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author 发现更多精彩  关注公众号：木子的昼夜编程
 * 一个生活在互联网底层，做着增删改查的码农,不谙世事的造作
 * @create 2021-09-12 11:26
 */
@Component
@Slf4j
public class EmojiFilter implements Filter {

    // 白名单 不需要过滤的路径
    List<String> urls = Arrays.asList(
            "/person/save",
            "/person/updateNickName"
    );

    // 需要过滤的请求类型 有些类型不需要过滤 比如文件上传 —— 此话来自亮哥
    List<String> mediaTypeList = Arrays.asList(
            "application/x-www-form-urlencoded",
            "application/json",
            "application/xml"
            );

    @Override
    public void init(FilterConfig filterConfig)   {
        log.info("init...");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 1. 获取请求方式、请求类型
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String method = request.getMethod();
        String contentType = request.getContentType();
        // 2. 只过滤非get请求 并且 contentType类型在mediaTypeList中包含的请求
        boolean flag = !HttpMethod.GET.name().equals(method.toUpperCase())
                && mediaTypeList.contains(contentType == null ? null : contentType.toLowerCase());
        //
        if (!flag) {
            // 有些请求无需处理 如 上传文件
            log.info("无需处理 method={} contentType={}", method, contentType);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 3. 处理白名单
        String srvPath = request.getServletPath();
        boolean count = urls.stream().filter(e -> srvPath.startsWith(e)).findAny().isPresent();
        if (count) {
            // 在白名单中 直接放行
            log.info("在白名单中 直接放行 srvPath={}", srvPath);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 4. 处理表情 咦？怎么就一句话 对喽大部分的Filter只需要这里一句话
        // 重要的代码是怎么包装Request 和 Response  我觉得可以叫他装饰模式也可以叫他代理模式
        // 这里的UTF-8编码模式 也需要写到配置文件或者配置中心 我这里还是为了方便大家看 所以写死在代码
        filterChain.doFilter(new ReqWrapper(request, "UTF-8"), servletResponse);
    }


    @Override
    public void destroy() {
        log.info("destroy...");
    }

    // 包装request 常用套路
    public static class ReqWrapper extends HttpServletRequestWrapper {
        // 编码格式
        private final String charset;
        // 构造
        public ReqWrapper(HttpServletRequest request, String charset) {
            super(request);
            this.charset = charset;
        }

        // 这个方法最重要 大部分的项目都是用@RequestBody接收参数 那时候就会用getInputStream 就是在这个时候进行clean
        @Override
        public ServletInputStream getInputStream() throws IOException {
            ServletInputStream inputStream = super.getInputStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset))) {
                String line = null;
                StringBuilder result = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    result.append(clean(line));
                }
                return new ServletInputStreamWrapper(new ByteArrayInputStream(result.toString()
                        .getBytes(Charset.forName(charset))));
            }
        }

        // 下边这仨在springboot项目中 很少用 但是必须写 不然容易出问题
        @Override
        public String getParameter(String name) {
            return clean(super.getParameter(name));
        }

        @Override
        public String[] getParameterValues(String name) {
            return clean(super.getParameterValues(name));
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return clean(super.getParameterMap());
        }

        // 下边三个clean才是真正的受苦受累的打工人 所有emoji的过滤 都是他们来完成
        // 像极了我们打工人
        private Map<String, String[]> clean(Map<String, String[]> map) {
            if (map == null) {
                return map;
            }
            // req 中的map不可直接修改需要新创建一个map
            // 而且要用LinkedHashMap 因为进来的参数都是有序的 不能随意改顺序 （虽然一般情况可能没啥问题）
            Map<String, String[]> result = new LinkedHashMap<>();
            for (Map.Entry<String, String[]> me : map.entrySet()) {
                result.put(me.getKey(), clean(me.getValue()));
            }
            // 人家本身不可修改 咱也给搞成一个不可修改的map其实很简单 就是在修改的方法里边啥也不干抛异常
            return Collections.unmodifiableMap(result);
        }

        private String[] clean(String[] arr) {
            if (arr != null) {
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = clean(arr[i]);
                }
            }
            return arr;
        }

        // 这个是最主要 最后 的一个处理方法 就是他兢兢业业的在处理emoji问题
        private String clean(String val) {
            if (StringUtils.isEmpty(val)) {
                return val;
            }
            // 国外大佬的jar包 包含的方法 删除emoji
            return EmojiParser.removeAllEmojis(val);
        }
    }

    public static class ServletInputStreamWrapper extends ServletInputStream {

        public ServletInputStreamWrapper(ByteArrayInputStream stream) {
            this.stream = stream;
        }

        private InputStream stream;

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }
    }
}
