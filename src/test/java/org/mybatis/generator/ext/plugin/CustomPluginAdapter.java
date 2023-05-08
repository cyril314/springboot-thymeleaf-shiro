package org.mybatis.generator.ext.plugin;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.internal.JDBCConnectionFactory;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @AUTO 自定义代码生成器
 * @Author AIM
 * @DATE 2018/5/2
 */
public class CustomPluginAdapter extends PluginAdapter {

    private final static String patternTime = "yyyy-MM-dd HH:mm:ss";
    private final static String COMMON_PATH = "com.fit.common.base";
    public static SimpleDateFormat df = new SimpleDateFormat(patternTime);
    private final String[] ignoreTable = {"createTime"};
    private String targetBusine = "";

    public static void main(String[] args) {
        String config = CustomPluginAdapter.class.getClassLoader().getResource("generatorConfig.xml").getFile();
        String[] arg = {"-configfile", config, "-overwrite"};
        ShellRunner.main(arg);
    }

    /**
     * 生成Service层
     */
    public CompilationUnit getServiceClazz(IntrospectedTable introspectedTable) {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        String tableComment = getTableComment(table);
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();// 获取bean
        String destPackage = domainObjectName + "Service";
        String clazzType = introspectedTable.getBaseRecordType();// 获取bean路径
        String clazzName = clazzType.substring(clazzType.lastIndexOf(".") + 1);
        String daoInterfaceType = introspectedTable.getDAOInterfaceType().replace("DAO", "Dao");//获取DAO路径
        String daoName = daoInterfaceType.substring(daoInterfaceType.lastIndexOf(".") + 1);
        targetBusine = clazzType.substring(0, clazzType.lastIndexOf(".")).replace("model", "service").replace("bean", "service");
        FullyQualifiedJavaType superClassType = new FullyQualifiedJavaType("BaseCrudService<" + daoName + "," + clazzName + ">");
        FullyQualifiedJavaType impClassType = new FullyQualifiedJavaType(COMMON_PATH + ".BaseCrudService");
        FullyQualifiedJavaType impServiceType = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
        FullyQualifiedJavaType beanType = new FullyQualifiedJavaType(clazzType);
        FullyQualifiedJavaType daoType = new FullyQualifiedJavaType(daoInterfaceType);
        TopLevelClass dto = new TopLevelClass(destPackage);
        dto.addFileCommentLine("package " + targetBusine + ";\n");
        dto.addImportedType(impServiceType);
        dto.setSuperClass(superClassType);
        dto.addImportedType(impClassType);
        dto.addImportedType(beanType);
        dto.addImportedType(daoType);
        dto.setVisibility(JavaVisibility.PUBLIC);
        dto.addAnnotation("@Service");
        dto.addJavaDocLine("/**\n" +
                " * @AUTO " + tableComment + "服务实现类\n" +
                " * @Author AIM\n" +
                " * @DATE " + df.format(new Date()) +
                "\n */");
        return dto;
    }

    /**
     * 生成额外java文件
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> list = new ArrayList<GeneratedJavaFile>();
        CompilationUnit addServiceClazz = getServiceClazz(introspectedTable);
        String targetProject = "src/main/java/" + targetBusine.replace(".", "/");
        File file = new File(targetProject);
        // 文件不存在
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        GeneratedJavaFile gjfService = new GeneratedJavaFile(addServiceClazz, targetProject, this.context.getProperty("javaFileEncoding"), this.context.getJavaFormatter());

        list.add(gjfService);

        return list;
    }

    /**
     * 生成dao
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        String tableComment = getTableComment(table);
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("BaseCrudDao<" + introspectedTable.getBaseRecordType() + ">");
        FullyQualifiedJavaType imp = new FullyQualifiedJavaType(COMMON_PATH + ".BaseCrudDao");
        FullyQualifiedJavaType mapimp = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper");
        interfaze.addSuperInterface(fqjt);// 添加 extends BaseDao<User>
        interfaze.addImportedType(imp);// 添加import common.BaseDao;
        interfaze.addImportedType(mapimp);// 添加import common.BaseDao;
        interfaze.addAnnotation("@Mapper");
        interfaze.addJavaDocLine("/**\n" +
                " * @AUTO " + tableComment + "接口\n" +
                " * @Author AIM\n" +
                " * @DATE " + df.format(new Date()) +
                "\n */");
        interfaze.getMethods().clear();
        return true;
    }

    /**
     * 获取表说明备注
     */
    private String getTableComment(FullyQualifiedTable table) {
        String tableComment = "";
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            JDBCConnectionFactory jdbc = new JDBCConnectionFactory(context.getJdbcConnectionConfiguration());
            connection = jdbc.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery("SHOW CREATE TABLE " + table.getIntrospectedTableName());

            if (rs != null && rs.next()) {
                String createDDL = rs.getString(2);
                int index = createDDL.indexOf("COMMENT='");
                if (index > 0) {
                    tableComment = createDDL.substring(index + 9);
                    tableComment = tableComment.substring(0, tableComment.length() - 1);
                }
            }
        } catch (SQLException e) {
        } finally {
            try {
                if (null != rs)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (statement != null)
                        statement.close();
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    try {
                        if (connection != null)
                            connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return tableComment;
    }

    /**
     * 制定序列化
     */
    private void makeSerializable(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Field field = new Field();
        field.setFinal(true);
        field.setInitializationString("1L");
        field.setName("serialVersionUID");
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("long"));
        field.setVisibility(JavaVisibility.PRIVATE);

        List<Field> fields = topLevelClass.getFields();
        fields.add(0, field);
    }

    /**
     * 生成实体Entity
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        String tableComment = getTableComment(table);
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("BaseEntity<" + introspectedTable.getBaseRecordType() + ">");
        topLevelClass.setSuperClass(fqjt);
        FullyQualifiedJavaType imp = new FullyQualifiedJavaType(COMMON_PATH + ".BaseEntity");
        topLevelClass.addImportedType(imp);
        FullyQualifiedJavaType impDate = new FullyQualifiedJavaType("java.util.Date");
        topLevelClass.addImportedType(impDate);
        makeSerializable(topLevelClass, introspectedTable);
        topLevelClass.addJavaDocLine("/**\n" +
                " * @AUTO " + tableComment + "\n" +
                " * @Author AIM\n" +
                " * @DATE " + df.format(new Date()) +
                "\n */");
        return true;
    }

    /**
     * 实体类字段
     */
    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        // 追加日期格式化注解
        if (introspectedColumn.getJdbcTypeName() == "TIMESTAMP") {
            field.addAnnotation("@DateTimeFormat(pattern = \"" + patternTime + "\")");//set
            field.addAnnotation("@JsonFormat(pattern = \"" + patternTime + "\",timezone=\"GMT+8\")");//get
            topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.format.annotation.DateTimeFormat"));
            topLevelClass.addImportedType(new FullyQualifiedJavaType("com.fasterxml.jackson.annotation.JsonFormat"));
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    /**
     * 生成mapping
     */
    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        try {
            //使用反射在运行时把'isMergeable'强制改成false
            java.lang.reflect.Field field = sqlMap.getClass().getDeclaredField("isMergeable");
            field.setAccessible(true);
            field.setBoolean(sqlMap, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }

    /**
     * 生成mapping 添加自定义sql
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        //添加自定义sql
        sqlXMLGenerated(document, introspectedTable);//Base_Where_List
        //添加自定义方法
        methodXMLGenerated_one(document, introspectedTable); //findList
        methodXMLGenerated_two(document, introspectedTable); //findCount
        methodXMLGenerated_three(document, introspectedTable); //get
        methodXMLGenerated_four(document, introspectedTable); //batchDelete
        methodXMLGenerated_five(document, introspectedTable); //deleteTable
        methodXMLGenerated_six(document, introspectedTable); //selectList

        return true;
    }

    //生成mapping 添加自定义sql Base_Where_List
    public boolean sqlXMLGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement parentElement = document.getRootElement();
        XmlElement sql = new XmlElement("sql");
        sql.addAttribute(new Attribute("id", "Base_Where_List"));
        XmlElement where = new XmlElement("where");
        // 加入 逻辑删除 del_flag标识 根据选择是否添加
        // where.addElement(new TextElement(" DEL_FLAG != 1 "));
        StringBuilder sb = new StringBuilder();
        List<String> list = Arrays.asList(ignoreTable);
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement isNotNullElement = new XmlElement("if");
            String javaProperty = introspectedColumn.getJavaProperty();// java字段名
            if (list.contains(javaProperty)) {
                continue;
            }
            switch (introspectedColumn.getJdbcType()) {
                case Types.DATE:
                    handleDate(sb, introspectedColumn, where, isNotNullElement);
                    break;
                case Types.TIMESTAMP:
                    handleDate(sb, introspectedColumn, where, isNotNullElement);
                    break;
                default:
                    sb.setLength(0);
                    sb.append(javaProperty + " != null and ").append(javaProperty + " != ''");
                    isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
                    where.addElement(isNotNullElement);

                    sb.setLength(0);
                    sb.append(" and ");
                    sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                    sb.append(" = ");
                    sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                    isNotNullElement.addElement(new TextElement(sb.toString()));
            }
        }
        sql.addElement(where);
        parentElement.addElement(sql);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    /**
     * 处理Base_Where_List中有时间的查询条件
     */
    private void handleDate(StringBuilder sb, IntrospectedColumn introspectedColumn, XmlElement where, XmlElement isNotNullElement) {
        // 大于开始时间
        isNotNullElement.addAttribute(new Attribute("test", "startDate != null "));
        where.addElement(isNotNullElement);
        sb.setLength(0);
        sb.append(" and ").append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
        sb.append(" &gt;= #{startDate }");
        isNotNullElement.addElement(new TextElement(sb.toString()));
        // 小于结束时间
        isNotNullElement = new XmlElement("if");
        isNotNullElement.addAttribute(new Attribute("test", "stopDate != null "));
        where.addElement(isNotNullElement);
        sb.setLength(0);
        sb.append(" and ").append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
        sb.append(" &lt;= #{stopDate } ");
        isNotNullElement.addElement(new TextElement(sb.toString()));
    }

    //生成mapping 添加自定义方法(一) 条件查询列表记录   findList
    public boolean methodXMLGenerated_one(Document document, IntrospectedTable introspectedTable) {
        XmlElement parentElement = document.getRootElement();
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", "findList"));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        select.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        select.addElement(new TextElement(" select "));
        XmlElement include1 = new XmlElement("include");
        include1.addAttribute(new Attribute("refid", "Base_Column_List"));
        select.addElement(include1);
        select.addElement(new TextElement(" from " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        XmlElement include2 = new XmlElement("include");
        include2.addAttribute(new Attribute("refid", "Base_Where_List"));
        select.addElement(include2);
        String introspectedKeyColumn = introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName();
        select.addElement(new TextElement("order by " + introspectedKeyColumn + " desc "));
        //select.addElement(new TextElement("<if test=\"pageNum != null and pageSize != null\">\n\tlimit ${pageNum}, ${pageSize}\n\t</if>"));
        parentElement.addElement(select);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    //生成mapping 添加自定义方法(二)条件查询列表记录总数  findCount
    public boolean methodXMLGenerated_two(Document document, IntrospectedTable introspectedTable) {
        XmlElement parentElement = document.getRootElement();
        XmlElement selectCount = new XmlElement("select");
        selectCount.addAttribute(new Attribute("id", "findCount"));
        selectCount.addAttribute(new Attribute("resultType", "java.lang.Long"));
        selectCount.addElement(new TextElement("select count(1) from " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid", "Base_Where_List"));
        selectCount.addElement(include);
        parentElement.addElement(selectCount);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    //生成mapping 添加自定义方法(三) 条件查询列表记录   get
    public boolean methodXMLGenerated_three(Document document, IntrospectedTable introspectedTable) {
        XmlElement parentElement = document.getRootElement();
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", "get"));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
//        select.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        select.addAttribute(new Attribute("parameterType", "java.util.Map"));
        select.addElement(new TextElement(" select "));
        XmlElement include1 = new XmlElement("include");
        include1.addAttribute(new Attribute("refid", "Base_Column_List"));
        select.addElement(include1);
        select.addElement(new TextElement(" from " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        select.addElement(new TextElement(" <include refid=\"Base_Where_List\" />"));

        parentElement.addElement(select);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    //生成mapping 添加自定义方法(四) 条件批量伤处
    public boolean methodXMLGenerated_four(Document document, IntrospectedTable introspectedTable) {
        String introspectedKeyColumn = introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName();
        XmlElement parentElement = document.getRootElement();
        XmlElement select = new XmlElement("delete");
        select.addAttribute(new Attribute("id", "batchDelete"));
        select.addElement(new TextElement("delete from " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        select.addElement(new TextElement(" where " + introspectedKeyColumn + " in ("));
        select.addElement(new TextElement(" <foreach collection=\"array\" item=\"id\" separator=\",\">"));
        select.addElement(new TextElement(" #{id}"));
        select.addElement(new TextElement(" </foreach>"));
        select.addElement(new TextElement(")"));

        parentElement.addElement(select);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    public boolean methodXMLGenerated_five(Document document, IntrospectedTable introspectedTable) {
        XmlElement parentElement = document.getRootElement();
        XmlElement select = new XmlElement("update");
        select.addAttribute(new Attribute("id", "deleteTable"));
        select.addElement(new TextElement("truncate table " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        parentElement.addElement(select);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    public boolean methodXMLGenerated_six(Document document, IntrospectedTable introspectedTable) {
        XmlElement parentElement = document.getRootElement();
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", "selectList"));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        select.addAttribute(new Attribute("parameterType", "java.lang.String"));
        select.addElement(new TextElement("${value}"));
        parentElement.addElement(select);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    /**
     * 自定义插入方法
     */
    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Attribute> attributes = element.getAttributes();
        for (Attribute a : attributes) {
            if (a.getName().toString().trim().equals("id")) {
                attributes.remove(a);
            }
        }
        element.addAttribute(new Attribute("id", "save"));
        Collections.reverse(attributes);
        return true;
    }

    /**
     * 自定义更新方法
     */
    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Attribute> attributes = element.getAttributes();
        for (Attribute a : attributes) {
            if (a.getName().toString().trim().equals("id")) {
                attributes.remove(a);
            }
        }
        element.addAttribute(new Attribute("id", "update"));
        Collections.reverse(attributes);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    /**
     * 自定义删除方法
     */
    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Attribute> attributes = element.getAttributes();
        for (Attribute a : attributes) {
            if (a.getName().toString().trim().equals("id")) {
                attributes.remove(a);
            }
        }
        element.addAttribute(new Attribute("id", "delete"));
        Collections.reverse(attributes);

        return true;
    }

    /**
     * 自定义查询方法
     */
    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Attribute> attributes = element.getAttributes();
        for (Iterator<Attribute> it = attributes.iterator(); it.hasNext(); ) {
            Attribute a = it.next();
            if (a.getName().toString().trim().equals("id")) {
                it.remove();  // ok
            }
        }
        element.addAttribute(new Attribute("id", "getById"));
        Collections.reverse(attributes);
        return true;
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        // LIMIT5,10; // 检索记录行 6-15
        //      XmlElement isNotNullElement = new XmlElement("if");//$NON-NLS-1$
        //      isNotNullElement.addAttribute(new Attribute("test", "limitStart != null and limitStart >=0"));//$NON-NLS-1$ //$NON-NLS-2$
        // isNotNullElement.addElement(new
        // TextElement("limit ${limitStart} , ${limitEnd}"));
        // element.addElement(isNotNullElement);
        // LIMIT 5;//检索前 5个记录行
        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    public boolean validate(List<String> arg0) {
        return true;
    }
}
