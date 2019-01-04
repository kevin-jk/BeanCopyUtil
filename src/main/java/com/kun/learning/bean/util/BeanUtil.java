package com.kun.learning.bean.util;



import com.kun.learning.bean.util.config.BeanTypeConfigHolder;
import com.kun.learning.bean.util.convert.BeanCopyConvert;
import com.kun.learning.bean.util.em.BeanCopyConfigEm;
import com.kun.learning.bean.util.exception.BeanCopyException;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Created by jrjiakun on 2018/12/27
 *
 * Bean Copy 工具
 *
 * 介于目前开源的Bean Copy工具都是基于具有相同类型的字段才能进行copy，否则会抛出异常，本copy工具可以根据
 * 自己的实际情况，提供1. 自定义的转化函数，然后进行转转换
 *
 *属性copy规则满足：
 * 1. 相同属性名的字段才会进行复制  （必要条件）
 * 2. 对应的属性必须有提供相应的读写方法，对于源，需要有public类型的get类读方法，对于目标来说需要又public类型的set写方法
 * 3. 如果源属性和目标属性类型不一样，会抛出异常 @see BeanCopyException
 * 4. 对于泛型类型的属性，只有泛型类型一样，才会copy
 *
 *
 * //todo
 * 1. 默认类型转换器
 * 2. 针对某个具体字段转换器
 * 3. 忽略属性拦截器
 * 4. 泛型的完整属性copy？
 * 5. class属性的缓存，弱引用
 *
 * version 1.0
 */
public class BeanUtil {
    private final static Logger logger = LoggerFactory.getLogger(BeanUtil.class);
    //针对类型进行自定义转换
    private Map<BeanTypeConfigHolder, BeanCopyConvert> typeConfigMap;

    //todo 针对某个具体的字段进行特殊化处理
    private Map<String, BeanCopyConvert> feildConfigMap;

    //todo 针对某些字段不进行copy
    List<String> excludeFieldsList = new ArrayList<String>();

    public BeanUtil() {
        typeConfigMap = new HashMap<BeanTypeConfigHolder, BeanCopyConvert>();
        feildConfigMap = new HashMap<String, BeanCopyConvert>();
    }

    public BeanUtil(Map<BeanTypeConfigHolder, BeanCopyConvert> typeConfigMap) {
        this.typeConfigMap = typeConfigMap;
    }

    public void copyProperties(Object src, Object des){
        Class srcClazz = src.getClass();
        Class desClazz = des.getClass();
        List<Field> desFields =  getWholeDeclaredFields(desClazz);
        for (Field desField : desFields) {
            // 获取目标对象对应字段的set方法
            Method desWriteMethod = getWriteMethod(desField, desClazz);
            //目标对象对应字段无set方法，或者set方法不是public，或者是静态方法都不进行copy
            if (isAccessMethod(desWriteMethod)) {
                // 获取源对象对应的字段
                Field srcField = null;
                try {
                    srcField = getWholeDeclaredField(srcClazz,desField.getName());
                } catch (Exception e) {
                    logger.debug("源对象无{}字段", desField.getName());
                    continue;
                }
                //获取源目标的对应的读方法
                Method srcReadMethod = getReadMethod(srcField, srcClazz);
                // 检查是否为是public方法
                if (isAccessMethod(srcReadMethod)) {
                    // 如果是map，list之类的，如何解决？
                    // 如果是泛型类型的field, 需要判断是否一致，如果一致则直接copy，否则不做操作
                    if (isSameParameterizedType(srcField, desField)) {
                        //获取源目标对应的字段值
                        Object srcValue = null;
                        try {
                            //为了解决非Public类可能带来的问题
                            srcReadMethod.setAccessible(true);
                            srcValue = srcReadMethod.invoke(src);
                        } catch (Exception e) {
                            logger.info("源对象{}字段值获取失败，e:{}", srcField.getName(), e);
                            continue;
                        }
                        Class srcFieldTypeClazz = srcField.getType();
                        Class desFieldTypeClazz = desField.getType();
                        if (!srcFieldTypeClazz.equals(desFieldTypeClazz)) {
                            BeanTypeConfigHolder beanTypeConfigHolder= new BeanTypeConfigHolder(srcFieldTypeClazz,desFieldTypeClazz);
                            BeanCopyConvert convertFunc = typeConfigMap.get(beanTypeConfigHolder);
                            if (null != convertFunc) {
                                srcValue = convertFunc.convert(srcValue);
                            } else {
                                logger.info("源对象和目标对象中{}字段类型不匹配，且没有配置对应的转换方法，请检查", srcField.getName());
                                throw new BeanCopyException("The filed of " + srcField.getName() + " type is not same, but there is no convert method, please check!");
                            }
                        }
                        try {
                            //为了解决非Public类可能带来的问题
                            desWriteMethod.setAccessible(true);
                            desWriteMethod.invoke(des, srcValue);
                        } catch (Exception e) {
                            logger.info("目标对象{}字段值设置失败，e:{}", desField.getName(), e);
                        }
                    }
                }
            }
        }
    }
    // 需要设置值的feild
    private boolean isAccessMethod(Method method) {
        return method != null && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers());
    }


    private Method getWriteMethod(Field field, Class clazz) {
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


    private Method getReadMethod(Field field, Class clazz) {
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
            if(null==readMethod){
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

    private boolean isSameParameterizedType(Field srcField, Field desField) {
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

    private List<Field> getWholeDeclaredFields(Class clazz) {
        List<Field> fields = new ArrayList<Field>();
        while (clazz != Object.class) {
            //仅仅返回当前类（不包括父类）中的字段
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private Field getWholeDeclaredField(Class clazz,String fieldName) throws Exception{
        Field  field = null;
        try{
            field  =  clazz.getDeclaredField(fieldName);
        }catch (Exception e){
            if(null==field&&clazz!=Object.class){
                field = getWholeDeclaredField(clazz.getSuperclass(),fieldName);
            }
        }
        return field ;
    }
}
