package org.antwhale.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.stream.Collectors;

/**
 * @Author: 何欢
 * @Date: 2022/10/3121:13
 * @Description:
 * 其实在spring-boot2.1.x版本是不用手动注入HttpMessageConverters的，
 * 因为可以自动配置的, 见HttpMessageConvertersAutoConfiguration。
 * 但是在spring-boot2.2.x版本HttpMessageConvertersAutoConfiguration有所改动，
 * 加了个@Conditional(NotReactiveWebApplicationCondition.class) ，
 * 因为gateway是ReactiveWeb，所以针对HttpMessageConverters的自动配置就不生效了，
 * 故需要手动注入HttpMessageConverters
 */
@Configuration
public class FeignConfig {
    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }
}
