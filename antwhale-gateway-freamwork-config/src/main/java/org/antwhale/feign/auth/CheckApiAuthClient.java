package org.antwhale.feign.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author: 何欢
 * @Date: 2022/10/3120:45
 * @Description:
 */
@Component
@FeignClient("${feignclient.authdowork.servername}")
public interface CheckApiAuthClient {
    @PostMapping("/check/checkToken")
    void cehckTokenController(Map<String,String> tokenMap);
}
