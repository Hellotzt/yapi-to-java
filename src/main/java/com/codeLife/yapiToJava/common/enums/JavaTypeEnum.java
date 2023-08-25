package com.codeLife.yapiToJava.common.enums;

/**
 * 类型枚举映射
 *
 * @author Hellotzt
 */
public enum JavaTypeEnum {
    String("string","String","@NotBlank","import javax.validation.constraints.NotBlank;\n"),
    Number("number","int","@NotNull","import javax.validation.constraints.NotNull;\n"),
    Array("array","List","@NotEmpty","import javax.validation.constraints.NotEmpty;\n"),
    Object("object","Object","@Valid","import javax.validation.Valid;\n"),
    Boolean("boolean","boolean","@NotNull","import javax.validation.constraints.NotNull;\n"),
    Integer("integer","Integer","@NotNull","import javax.validation.constraints.NotNull;\n"),
    Null("null","String","@NotBlank","import javax.validation.constraints.NotBlank;\n"),
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
    /**
     * 必填校验导包语句
     */
    private final String importStr;

    JavaTypeEnum(String yapi, String java, String verify,String importStr) {
        this.yapi = yapi;
        this.java = java;
        this.verify = verify;
        this.importStr = importStr;
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

    public java.lang.String getImportStr() {
        return importStr;
    }

    /**
     * 获取yapi类型
     * @param javaType javaType类型
     * @return yapi类型
     */
    public static String typeToYapi(String javaType){
        for (JavaTypeEnum javaTypeEnum : JavaTypeEnum.values()) {
            if(javaTypeEnum.getJava().equals(javaType)){
                return javaTypeEnum.getYapi();
            }
        }
        return Object.getYapi();
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

    /**
     * 获取必填校验导包语句
     * @param yapi yapi类型
     * @return 必填校验导包语句
     */
    public static String typeToImportStr(String yapi){
        for (JavaTypeEnum javaTypeEnum : JavaTypeEnum.values()) {
            if(javaTypeEnum.getYapi().equals(yapi)){
                return javaTypeEnum.getImportStr();
            }
        }
        return "";
    }
}
