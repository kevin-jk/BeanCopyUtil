package com.kun.learning.bean.util;


import com.kun.learning.bean.util.config.BeanTypeConfigHolder;
import com.kun.learning.bean.util.config.CopyConfig;
import com.kun.learning.bean.util.config.Features;
import com.kun.learning.bean.util.convert.BeanCopyConvert;
import com.kun.learning.bean.util.exception.BeanCopyException;
import com.kun.learning.bean.util.reflect.ReflectUtils;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Created by jrjiakun on 2018/12/27
 * <p>
 * Bean Copy 工具
 * <p>
 * 介于目前开源的Bean Copy工具都是基于具有相同类型的字段才能进行copy，否则会抛出异常，本copy工具可以根据
 * 自己的实际情况，提供1. 自定义的转化函数，然后进行转转换
 * <p>
 * 属性copy规则满足：
 * 1. 相同属性名的字段才会进行复制  （必要条件）
 * 2. 对应的属性必须有提供相应的读写方法，对于源，需要有public类型的get类读方法，对于目标来说需要又public类型的set写方法
 * 3. 如果源属性和目标属性类型不一样，会抛出异常 @see BeanCopyException
 * 4. 对于泛型类型的属性，只有泛型类型一样，才会copy
 * <p>
 * <p>
 * //todo
 * class属性的缓存，弱引用
 * <p>
 * version 1.0
 */
public class BeanUtil {

    private final static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    private static void copyProperties0(Object src, Object des, Map<BeanTypeConfigHolder, BeanCopyConvert> typeConfigMap, Map<String, BeanCopyConvert> fieldConfigMap, List<String> excludeFieldsList) {
        if(null==src){
            throw new BeanCopyException("IllegalArgument, the src is null");
        }
        if(null==des){
            throw new BeanCopyException("IllegalArgument, the des is null");
        }
        Class srcClazz = src.getClass();
        Class desClazz = des.getClass();
        List<Field> desFields = ReflectUtils.getWholeDeclaredFields(desClazz);
        for (Field desField : desFields) {
            // 如果需要排除某些字段，则直接进行下一个字段属性的copy
            if (excludeFieldsList.contains(desField.getName())) {
                continue;
            }
            // 获取目标对象对应字段的set方法
            Method desWriteMethod = ReflectUtils.getWriteMethod(desField, desClazz);
            //目标对象对应字段无set方法，或者set方法不是public，或者是静态方法都不进行copy
            if (ReflectUtils.isAccessMethod(desWriteMethod)) {
                // 获取源对象对应的字段
                Field srcField = null;
                try {
                    srcField = ReflectUtils.getWholeDeclaredField(srcClazz, desField.getName());
                } catch (Exception e) {
                    logger.debug("源对象无{}字段", desField.getName());
                    continue;
                }
                //获取源目标的对应的读方法
                Method srcReadMethod = ReflectUtils.getReadMethod(srcField, srcClazz);
                // 检查是否为是public方法
                if (ReflectUtils.isAccessMethod(srcReadMethod)) {
                    // 如果是map，list之类的，如何解决？
                    // 如果是泛型类型的field, 需要判断是否一致，如果一致则直接copy，否则不做操作
                    if (ReflectUtils.isSameParameterizedType(srcField, desField)) {
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
                            // 特殊字段的处理
                            BeanCopyConvert convertFunc = null;
                            if (fieldConfigMap.size() > 0) {
                                convertFunc = fieldConfigMap.get(srcField.getName());
                            }
                            if (null == convertFunc) {
                                BeanTypeConfigHolder beanTypeConfigHolder = new BeanTypeConfigHolder(srcFieldTypeClazz, desFieldTypeClazz);
                                convertFunc = typeConfigMap.get(beanTypeConfigHolder);
                            }
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

    public static void copyProperties(Object src, Object des){
        copyProperties(src,des,null,null);
    }

    public static void copyProperties(Object src, Object des, Features... features) {
        CopyConfig[] copyConfigs = null;
        if (null != features) {
            copyConfigs = new CopyConfig[features.length];
            for (int i=0;i<features.length;i++) {
                if(features[i]!=null){
                    CopyConfig copyConfig = new CopyConfig();
                    copyConfig.setKey(Features.getHolder(features[i]));
                    copyConfig.setValue( Features.getConvert(features[i]));
                    copyConfigs[i]=copyConfig;
                }
            }
        }
        copyProperties(src, des,copyConfigs);
    }

    public static void copyProperties(Object src, Object des, CopyConfig...copyConfigs) {
        copyProperties(src, des,copyConfigs,null);
    }

    public static void copyProperties(Object src, Object des, CopyConfig[] copyConfigs, String[] ignoreFields) {
        Map<BeanTypeConfigHolder, BeanCopyConvert> typeConfigMap = new HashMap<BeanTypeConfigHolder, BeanCopyConvert>();
        Map<String, BeanCopyConvert> fieldConfigMap = new HashMap<String, BeanCopyConvert>();
        List<String> excludeFieldsList = new ArrayList<String>();
        if (null != copyConfigs) {
            for (CopyConfig copyConfig : copyConfigs) {
                if(null!=copyConfig){
                    if (copyConfig.getKey() instanceof BeanTypeConfigHolder && copyConfig.getValue() instanceof BeanCopyConvert) {
                        typeConfigMap.put((BeanTypeConfigHolder) copyConfig.getKey(), copyConfig.getValue());
                    } else if (copyConfig.getKey() instanceof String && copyConfig.getValue() instanceof BeanCopyConvert) {
                        fieldConfigMap.put((String) copyConfig.getKey(), copyConfig.getValue());
                    } else {
                        throw new BeanCopyException("Unknown typeConfig, please check!");
                    }
                }
            }
        }
        if (null != ignoreFields) {
            for(String ignoreField:ignoreFields){
                if(null!=ignoreField&&!"".equals(ignoreField)){
                    excludeFieldsList.add(ignoreField);
                }
            }
        }
        copyProperties0(src, des,typeConfigMap,fieldConfigMap,excludeFieldsList);
    }

    public static void copyProperties(Object src, Object des, String... ignoreFields) {
        copyProperties(src, des,null,ignoreFields);
    }

}
