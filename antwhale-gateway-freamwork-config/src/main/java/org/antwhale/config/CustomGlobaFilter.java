package org.antwhale.config;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import org.antwhale.feign.auth.CheckApiAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 何欢
 * @Date: 2022/10/1922:18
 * @Description:
 */
public class CustomGlobaFilter implements GlobalFilter, Ordered {
    @Autowired
    private CheckApiAuthClient checkApiAuthClient;
    /**
     * @author 何欢
     * @Date 20:31 2022/10/20
     * @Description return chain.filter(exchange)： 表示继续向后执行。
     * return exchange.getResponse().setComplete()： 表示拦截停止执行立即请求返回。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        noteServerWebExchange(exchange);
        // 获取ServerHttpRequest对象
        ServerHttpRequest request = exchange.getRequest();
        //获取ServerHttpResponse对象
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst("Authorization");
        //token为空时，对请求进行拦截
        if (null == token || "".equals(token)) {
            //如果是登录请求则放行
            if (isLoginRequest(request)) {
                return chain.filter(exchange);
            }
            return interceptRequest(response);
        }
        //不为空访问认证中心校验token是否符合规则：
        Map<String,String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        checkApiAuthClient.cehckTokenController(tokenMap);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * @author 何欢
     * @Date 20:48 2022/10/20
     * @Description 关于ServerWebExchange的笔记
     **/
    private void noteServerWebExchange(ServerWebExchange exchange) {
        // 获取ServerHttpRequest对象
        ServerHttpRequest request = exchange.getRequest();
        //获取ServerHttpResponse对象
        ServerHttpResponse response = exchange.getResponse();
        //返回当前exchange的请求属性，返回结果是一个可变的Map
        Map<String, Object> attributes = exchange.getAttributes();
        //返回当前请求的认证用户，如果存在的话
        Mono<Principal> principal = exchange.getPrincipal();
        // 返回请求的表单数据或者一个空的Map，只有Content-Type为application/x-www-form-urlencoded的时候这个方法才会返回一个非空的Map -- 这个一般是表单数据提交用到
        Mono<MultiValueMap<String, String>> formData = exchange.getFormData();
        // 返回multipart请求的part数据或者一个空的Map，只有Content-Type为multipart/form-data的时候这个方法才会返回一个非空的Map  -- 这个一般是文件上传用到
        Mono<MultiValueMap<String, Part>> multipartData = exchange.getMultipartData();
        // 返回Spring的上下文
        ApplicationContext applicationContext = exchange.getApplicationContext();
    }

    /**
     * @author 何欢
     * @Date 20:49 2022/10/20
     * @Description 判断是否为登录请求
     **/
    private Boolean isLoginRequest(ServerHttpRequest request) {
        String path = request.getPath().toString();
        return "/antwhale-auth-dowork/login/goLogin,/antwhale-auth-dowork/captchaImage,/antwhale-auth-dowork/checkCaptcha".contains(path) ? true : false;
    }

    /**
     * @author 何欢
     * @Date 20:54 2022/10/20
     * @Description 拦截token为空的请求
     **/
    private Mono interceptRequest(ServerHttpResponse response) {
        Map bodyMap = new HashMap();
        DataBuffer buffer = null;
        try {
            bodyMap.put("code", "401");
            bodyMap.put("message", "请先进行登录");

            Gson gson = new Gson();
            byte[] bytes = gson.toJson(bodyMap).getBytes("utf-8");
            buffer = response.bufferFactory().wrap(bytes);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.writeWith(Mono.just(buffer));
    }
}
