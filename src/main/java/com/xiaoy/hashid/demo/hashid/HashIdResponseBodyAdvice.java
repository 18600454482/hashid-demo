package com.xiaoy.hashid.demo.hashid;

import com.xiaoy.hashid.demo.annotation.HashIdAnnotated;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Author: yuqiang
 * @Description: 只支持 {@link org.springframework.web.bind.annotation.ResponseBody}注解标准的返回类型
 * @Date: Created in 2020/7/11 14:21
 * @Modified By:
 */
public class HashIdResponseBodyAdvice implements ResponseBodyAdvice {

    private final HashIdMethodParameterProcess hashIdMethodParameterProcess = new HashIdMethodParameterProcess();

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), HashIdAnnotated.class) ||
                hashIdMethodParameterProcess.supportsMethodReturn(returnType));
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return hashIdMethodParameterProcess.processMethodParameter(body,returnType, HashIdMethodParameterProcess.ENCODE);
    }
}
