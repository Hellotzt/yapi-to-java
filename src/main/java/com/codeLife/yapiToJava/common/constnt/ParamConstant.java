package com.codeLife.yapiToJava.common.constnt;

import java.util.Arrays;
import java.util.List;

/**
 * 常量
 *
 * @author Hellotzt
 */
public class ParamConstant {
    public static final String OPTIONS = "OPTIONS";
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
    public static final String BASE_CLASS = "public class ";
    /**
     * 注释
     */
    public static final String DESCRIPTION = "description";
    public static final String PRIVATE = "private";
    /**
     * 类的私有属性前缀
     */
    public static final String STATS_PREFIX = "\tprivate ";
    /**
     * List类导包所需
     */
    public static final String LIST_IMPORT = "import java.util.List;\n";
    /**
     * 特殊符号集合
     */
    public static List<String> SPECIAL_SYMBOL_LIST = Arrays.asList(" ", "：",":");
    /**
     * yapi模式
     */
    public static final String BASE_SCHEMA = "http://json-schema.org/draft-04/schema#";
    /**
     * 类型
     */
    public static final String TYPE = "type";
    public static final String ITEMS = "items";
    public static final String PROPERTIES = "properties";
    /**
     * Yapi接口返回体参数
     */
    public static final String RES_BODY = "res_body";
    /**
     * Yapi接口请求体参数
     */
    public static final String REQ_BODY_OTHER = "req_body_other";
    /**
     * 必填
     */
    public static final String REQUIRED = "required";
    /**
     * 导入@data注解语句
     */
    public static final String IMPORT_DATA = "import lombok.Data;\n";
}
