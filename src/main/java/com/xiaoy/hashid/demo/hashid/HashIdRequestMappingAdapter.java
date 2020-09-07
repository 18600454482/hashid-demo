package com.xiaoy.hashid.demo.hashid;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

/**
 * @Author: yuqiang
 * @Description: spring mvc 方法入参 定制处理
 * @Date: Created in 2020/7/11 12:52
 * @Modified By:
 */
public class HashIdRequestMappingAdapter extends RequestMappingHandlerAdapter {

    @Override
    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
        return new HashIdServletInvocableHandlerMethod(handlerMethod);
    }

    class HashIdServletInvocableHandlerMethod extends ServletInvocableHandlerMethod {

        private final HashIdMethodParameterProcess hashIdMethodParameterProcess = new HashIdMethodParameterProcess();

        public HashIdServletInvocableHandlerMethod(HandlerMethod handlerMethod) {
            super(handlerMethod);
        }

        @Override
        protected Object[] getMethodArgumentValues(NativeWebRequest request, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
            Object[] methodArgumentValues = super.getMethodArgumentValues(request, mavContainer, providedArgs);
            MethodParameter[] methodParameters = getMethodParameters();
            for (int i = 0; i < methodParameters.length; i++) {
                MethodParameter methodParameter = methodParameters[i];
                if(hashIdMethodParameterProcess.supportsMethodParameter(methodParameter)){
                    Object afterProcessValue = hashIdMethodParameterProcess.processMethodParameter(methodArgumentValues[i],
                            methodParameter, HashIdMethodParameterProcess.DECODE);
                    methodArgumentValues[i] = afterProcessValue;
                }
            }
            return methodArgumentValues;
        }
    }
}
