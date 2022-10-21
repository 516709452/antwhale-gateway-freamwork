package org.antwhale.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 何欢
 * @Date: 2022/10/1922:22
 * @Description:
 */
@Configuration
public class FilterConfig {
    @Bean
    public GlobalFilter customFilter(){
        return new CustomGlobaFilter();
    }
}
