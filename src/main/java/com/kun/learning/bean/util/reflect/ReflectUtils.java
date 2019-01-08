package com.kun.learning.bean.util.reflect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jrjiakun on 2019/1/4
 */
public class ReflectUtils {
    private final static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);
    // 需要设置值的field
    public static boolean isAccessMethod(Method method) {
        return method != null && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers());
    }

    public static Method getWriteMethod(Field field, Class clazz) {
        if (null != field) {
            String fieldName = field.getName();
            String methodSuf = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            String writeMethodStr = "set" + methodSuf;
            Method writeMethod = null;
            try {
                writeMethod = clazz.getMethod(writeMethodStr, field.getType());
            } catch (Exception e) {
                logger.info("clazz:{}中字段{}无对应的 set- 写方法", clazz.getName(), field.getName());
            }
            return writeMethod;
        }
        return null;
    }


    public static Method getReadMethod(Field field, Class clazz) {
        Method readMethod = null;
        if (null != field) {
            String fieldName = field.getName();
            String methodSuf = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            String readMethodStr = null;
            // boolean类型的，查看是否有is-方法
            if (boolean.class.equals(field.getType())) {
                readMethodStr = "is" + methodSuf;
                try {
                    readMethod = clazz.getMethod(readMethodStr);
                } catch (Exception e) {
                    logger.info("clazz:{}中字段{}无对应的 is- 读方法", clazz.getName(), field.getName());
                }
            }
            //get方法
            if (null == readMethod) {
                readMethodStr = "get" + methodSuf;
                try {
                    readMethod = clazz.getMethod(readMethodStr);
                } catch (Exception e) {
                    logger.info("clazz:{}中字段{}无对应的 get- 读方法", clazz.getName(), field.getName());
                }
            }
        }
        return readMethod;
    }

    public static boolean isSameParameterizedType(Field srcField, Field desField) {
        Type srcType = srcField.getGenericType();
        Type desType = desField.getGenericType();
        if (desType != null && desType instanceof ParameterizedType) {
            //判断原始类型是否一致
            if (srcType == null || !(srcType instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType srcParamType = ((ParameterizedType) srcType);
            ParameterizedType decParamType = ((ParameterizedType) desType);
            if (srcParamType.equals(decParamType)) {
//                Type[] srcActTypes =   srcParamType.getActualTypeArguments();
//                Type[] decActTypes =   decParamType.getActualTypeArguments();
//                for(int i=0;i<srcActTypes.length;i++){
//                   if(!srcActTypes[i].equals(decActTypes[i])){
//                       return false;
//                   }
//                }
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static List<Field>  getWholeDeclaredFields(Class clazz) {
        List<Field> fields = new ArrayList<Field>();
        while (clazz != Object.class) {
            //仅仅返回当前类（不包括父类）中的字段
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static Field getWholeDeclaredField(Class clazz, String fieldName) throws Exception {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (Exception e) {
            if (null == field && clazz != Object.class) {
                field = getWholeDeclaredField(clazz.getSuperclass(), fieldName);
            }
        }
        return field;
    }
}
