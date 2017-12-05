package com.huawei.cloud.util;

import ch.qos.logback.classic.gaffer.PropertyUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhouyibin on 2017/12/6.
 */
@Component
public class PropertityUtil {

    private static Properties props;

    static {
        try {
            Resource resource = new ClassPathResource("application.properties");
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key){
        return props == null ? null :  props.getProperty(key);
    }

    public static String getProperty(String key,String defaultValue){
        return props == null ? null : props.getProperty(key, defaultValue);
    }
}
