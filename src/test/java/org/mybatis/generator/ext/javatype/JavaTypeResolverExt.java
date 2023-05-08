package org.mybatis.generator.ext.javatype;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;
import org.mybatis.generator.internal.util.StringUtility;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Properties;

/**
 * 数据库类型和 Java 类型的映射关系
 */
public class JavaTypeResolverExt extends JavaTypeResolverDefaultImpl {

    protected boolean forceLong = true;//无小数位时，优先使用Long
    protected boolean forceDouble = true;//无小数位时，优先使用double,而不是bigDecimal,比forceBigDecimals优先级高

    public JavaTypeResolverExt() {
        super();
        //把数据库的 TINYINT 映射成 Integer
        typeMap.put(Types.TINYINT, new JdbcTypeInformation("TINYINT", new FullyQualifiedJavaType(Integer.class.getName())));
        typeMap.put(Types.LONGVARBINARY, new JdbcTypeInformation("BLOB", new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.LONGVARCHAR, new JdbcTypeInformation("LONGTEXT", new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.LONGVARCHAR, new JdbcTypeInformation("TEXT", new FullyQualifiedJavaType(String.class.getName())));
    }

    public void addConfigurationProperties(Properties properties) {
        forceLong = StringUtility.isTrue(properties.getProperty("forceLong", "true"));
        forceDouble = StringUtility.isTrue(properties.getProperty("forceDouble", "true"));
        super.addConfigurationProperties(properties);
    }

    public FullyQualifiedJavaType calculateJavaType(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType answer = null;
        JdbcTypeInformation jdbcTypeInformation = typeMap.get(introspectedColumn.getJdbcType());

        if (jdbcTypeInformation == null) {
            switch (introspectedColumn.getJdbcType()) {
                case Types.DECIMAL:
                case Types.NUMERIC: {
                    int scale = introspectedColumn.getScale();
                    int length = introspectedColumn.getLength();
                    if (scale > 0) {//有小数位
                        if (forceDouble) {
                            answer = new FullyQualifiedJavaType(Double.class.getName());
                        } else {
                            answer = new FullyQualifiedJavaType(BigDecimal.class.getName());
                        }
                    } else {//无小数位
                        if (forceLong) {
                            answer = new FullyQualifiedJavaType(Long.class.getName());
                        } else if (forceBigDecimals) {
                            answer = new FullyQualifiedJavaType(BigDecimal.class.getName());
                        } else if (length > 18) {
                            new FullyQualifiedJavaType(BigDecimal.class.getName());
                        } else if (length < 4) {
                            answer = new FullyQualifiedJavaType(Short.class.getName());
                        } else if (length < 9) {
                            answer = new FullyQualifiedJavaType(Integer.class.getName());
                        } else {
                            answer = new FullyQualifiedJavaType(Long.class.getName());
                        }
                    }
                    break;
                }
            }
        } else {
            answer = jdbcTypeInformation.getFullyQualifiedJavaType();
        }

        return answer;
    }
}
