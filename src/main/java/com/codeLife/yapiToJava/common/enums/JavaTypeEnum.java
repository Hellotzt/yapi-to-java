package com.codeLife.yapiToJava.common.enums;

/**
 * 类型枚举映射
 *
 * @author Hellotzt
 */
public enum JavaTypeEnum {
    String("string","String","@NotBlank"),
    Number("number","Integer","@NotNull"),
    Array("array","List","@NotEmpty"),
    Object("object","Object","@Valid"),
    Boolean("boolean","boolean","@NotNull"),
    Integer("integer","Integer","@NotNull"),
    Null("null","String","@NotBlank"),
    ;
    /**
     * yapi类型
     */
    private final String yapi;
    /**
     * java类型
     */
    private final String java;
    /**
     * 必填校验注解
     */
    private final String verify;

    JavaTypeEnum(String yapi, String java, String verify) {
        this.yapi = yapi;
        this.java = java;
        this.verify = verify;
    }

    public java.lang.String getYapi() {
        return yapi;
    }

    public java.lang.String getJava() {
        return java;
    }

    public java.lang.String getVerify() {
        return verify;
    }

    /**
     * 获取java类型
     * @param yapi yapi类型
     * @return java类型
     */
    public static String typeToJava(String yapi){
        for (JavaTypeEnum javaTypeEnum : JavaTypeEnum.values()) {
            if(javaTypeEnum.getYapi().equals(yapi)){
                return javaTypeEnum.getJava();
            }
        }
        return Object.getJava();
    }

    /**
     * 获取必填校验注解
     * @param yapi yapi类型
     * @return 必填校验注解
     */
    public static String typeToVerify(String yapi){
        for (JavaTypeEnum javaTypeEnum : JavaTypeEnum.values()) {
            if(javaTypeEnum.getYapi().equals(yapi)){
                return javaTypeEnum.getVerify();
            }
        }
        return "";
    }
}
