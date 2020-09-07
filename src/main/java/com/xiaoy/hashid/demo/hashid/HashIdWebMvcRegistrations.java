package com.xiaoy.hashid.demo.hashid;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.Collections;

/**
 * @Author: yuqiang
 * @Description: 定制 RequestMappingHandlerAdapter
 * @Date: Created in 2020/7/11 13:15
 * @Modified By:
 */
@Configuration
public class HashIdWebMvcRegistrations implements WebMvcRegistrations {

    @Override
    public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
        HashIdRequestMappingAdapter hashIdRequestMappingAdapter = new HashIdRequestMappingAdapter();
        hashIdRequestMappingAdapter.setResponseBodyAdvice(Collections.singletonList(new HashIdResponseBodyAdvice()));
        return hashIdRequestMappingAdapter;
    }
}
