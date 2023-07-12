package com.codeLife.yapiToJava.constnt;

import java.util.Arrays;
import java.util.List;

/**
 * 常量
 *
 * @author Hellotzt
 */
public class ParamConstant {
    public static final String DTO = "Dto";
    public static final String VO = "Vo";
    public static final String COOKIE = "Cookie";
    public static final String DATA = "data";
    /**
     * 接口标题名称
     */
    public static final String TITLE = "title";
    /**
     * 类前缀,使用lombook的@Data注解
     */
    public static final String CLASS = "@Data\npublic class ";
    /**
     * 注释
     */
    public static final String DESCRIPTION = "description";
    /**
     * 类的私有属性前缀
     */
    public static final String STATS_PREFIX = "\tprivate ";
    /**
     * 特殊符号集合
     */
    public static List<String> SPECIAL_SYMBOL_LIST = Arrays.asList(" ", "：",":");
}
