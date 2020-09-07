package com.xiaoy.hashid.demo.hashid;

import com.xiaoy.hashid.demo.annotation.HashIdAnnotated;
import com.xiaoy.hashid.demo.utils.HashIdsUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

/**
 * @Author: yuqiang
 * @Description: 处理 {@link HashIdAnnotated}标注的属性，进行 HashId处理
 * @Date: Created in 2020/7/10 20:20
 * @Modified By:
 */
public class HashIdMethodParameterProcess {

    public static final String ENCODE = "ENCODE";

    public static final String DECODE = "DECODE";

    /**
     * 是否支持方法参数
     * @param methodParameter
     * @return
     */
    public boolean supportsMethodParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(HashIdAnnotated.class);
    }

    /**
     * 是否支持方法返回值参数
     * @param methodParameter
     * @return
     */
    public boolean supportsMethodReturn(MethodParameter methodParameter) {
        return methodParameter.hasMethodAnnotation(HashIdAnnotated.class);
    }

    /**
     * 开始处理方法参数
     * @param body
     * @param parameter
     * @param type
     * @return
     */
    public Object processMethodParameter(Object body, MethodParameter parameter, String type) {
        if(body == null){
            return null;
        }
        ResolvableType resolvableType = ResolvableType.forMethodParameter(parameter);
        if(resolvableType.resolve() == String.class){
            return hashIdProcess(type, body);
        }else{
            return doProcessMethodParameter(resolvableType, body, body, type);
        }
    }

    /**
     * hashId处理
     * @param type
     * @param value
     * @return
     */
    private String hashIdProcess(String type, Object value){
        if(ENCODE.equals(type)){
            String encodeValue = HashIdsUtil.encode(Long.valueOf(value.toString()));
            return encodeValue == null ? null : encodeValue;
        }else{
            Long decodeValue = HashIdsUtil.decode(value.toString());
            return decodeValue == null ? String.valueOf(0L) : String.valueOf(decodeValue);
        }
    }

    /**
     * 处理本地字段以及父类
     * @param resolvableType
     * @param obj
     * @param type
     */
    private void doProcessLocalFields(ResolvableType resolvableType, Object obj, String type){
        Class<?> targetClass = resolvableType.resolve();
        do {
            if(obj == null){
                return;
            }
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                if (Modifier.isStatic(field.getModifiers())) {
                    return;
                }
                Object value = getFieldValue(field, obj);
                if(value == null){
                    return;
                }
                ResolvableType filedType = ResolvableType.forField(field);
                if(filedType.resolve() == null){
                    filedType = ResolvableType.forInstance(value);
                }
                if(filedType.resolve() == String.class){
                    if(!isHashIdAnnotated(field)){
                        return;
                    }
                    setFieldValue(field, obj, value, type);
                }else{
                    doProcessMethodParameter(filedType, obj, value, type);
                }
            });
            targetClass = targetClass.getSuperclass();
        }while (targetClass != null && targetClass != Object.class);
    }

    /**
     * 处理集合、数组
     * @param resolvableType
     * @param obj
     * @param value
     * @param type
     * @return
     */
    public Object doProcessMethodParameter(ResolvableType resolvableType, Object obj, Object value, String type) {
        ResolvableType componentType;
        //处理 Collection
        if(ResolvableType.forClass(Collection.class).isAssignableFrom(resolvableType)){
            Collection<?> collection = (Collection<?>) value;
            if(collection.isEmpty()){
                return obj;
            }
            ResolvableType genericType = resolvableType.getGeneric(0);
            if(genericType == null || genericType.resolve() == null){
                Object next = collection.iterator().next();
                genericType = ResolvableType.forClass(next.getClass());
            }
            //处理 Collection<String> && 存在 HashIdAnnotated
            if(genericType.resolve() == String.class
                    && isHashIdAnnotated(resolvableType.getSource())){
                Collection newCollection = BeanUtils.instantiateClass(collection.getClass());
                for (Object item : collection) {
                    newCollection.add(hashIdProcess(type, item.toString()));
                }
                if(resolvableType.getSource() instanceof Field){
                    Field field = (Field)resolvableType.getSource();
                    ReflectionUtils.makeAccessible(field);
                    ReflectionUtils.setField(field, obj, newCollection);
                    return newCollection;
                }
                return newCollection;
            }else{
                //处理 Collection<自定义>
                for (Object item : collection){
                    doProcessLocalFields(genericType, item, type);
                }
            }
        //处理数组
        }else if((componentType = resolvableType.getComponentType()).resolve() != null){
            if(componentType.resolve() == String.class
                    && isHashIdAnnotated(resolvableType.getSource())){
                String[] array = (String[]) value;
                if(array.length == 0){
                    return obj;
                }
                String[] newInstance = (String[])Array.newInstance(String.class, array.length);
                for (int i = 0; i < array.length; i++) {
                    newInstance[i] = hashIdProcess(type, array[i]);
                }
                if(resolvableType.getSource() instanceof Field){
                    Field field = (Field)resolvableType.getSource();
                    ReflectionUtils.makeAccessible(field);
                    ReflectionUtils.setField(field, obj, newInstance);
                    return newInstance;
                }
                return newInstance;
            }
            //处理非原生数据类型
        }else if(!isPrimitiveOrWrapper(resolvableType.resolve())){
            doProcessLocalFields(resolvableType, value, type);
        }
        return obj;
    }

    /**
     * 反射获取字段值
     * @param field
     * @param obj
     * @return
     */
    private Object getFieldValue(Field field, Object obj){
        if(obj == null){
            return null;
        }
        ReflectionUtils.makeAccessible(field);
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射修改字段值
     * @param field
     * @param obj
     * @param value
     * @param type
     */
    private void setFieldValue(Field field, Object obj, Object value, String type){
        if(obj == null || value == null){
            return;
        }
        ReflectionUtils.makeAccessible(field);
        try {
            String hashId = hashIdProcess(type, value);
            field.set(obj, hashId);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否标记 {@link HashIdAnnotated}
     * @param source
     * @return
     */
    private boolean isHashIdAnnotated(Object source){
        if(source instanceof AnnotatedElement){
            AnnotatedElement annotatedElement = (AnnotatedElement)source;
            return annotatedElement.isAnnotationPresent(HashIdAnnotated.class);
        }
        if(source instanceof MethodParameter){
            MethodParameter methodParameter = (MethodParameter)source;
            return supportsMethodParameter(methodParameter);
        }
        return false;
    }

    /**
     * 是否原生类型、包装类型、java开头的类
     * @param clazz
     * @return
     */
    private boolean isPrimitiveOrWrapper(Class<?> clazz){
        return ClassUtils.isPrimitiveOrWrapper(clazz)
                || clazz.getName().startsWith("java");
    }
}
