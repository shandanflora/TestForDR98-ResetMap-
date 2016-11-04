package com.ecovacs.ecosphere.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2015/10/27.
 */
public class PropertyData {

    private static Properties props =  null;

    public static void setFile(String strFile){
        try {
            props =  new Properties();
            InputStream inputstream = PropertyData.class.getClassLoader().getResourceAsStream(strFile);
            props.load(inputstream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setProps(Properties props) {
        PropertyData.props = props;
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }


    /*static Properties props=new Properties();

    static{
        try {
            props.load(PropertyData.class.getClassLoader().getResourceAsStream("commondata.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PropertyData(){};

    public static String getProperty(String key){
        return props.getProperty(key);
    }*/

}
