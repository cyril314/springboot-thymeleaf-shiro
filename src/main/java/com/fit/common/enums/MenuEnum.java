package com.fit.common.enums;

/**
 * @AUTO 菜单类型的枚举
 * @Author AIM
 * @DATE 2019/3/5
 */
public enum MenuEnum {

    M(1, "栏目"), C(2, "菜单"), A(3, "按钮");

    private int i;
    private String typeName;

    private MenuEnum(int i, String typeName) {
        this.i = i;
        this.typeName = typeName;
    }

    // 普通方法
    public static String getName(int index) {
        for (MenuEnum c : MenuEnum.values()) {
            if (c.getI() == index) {
                return c.typeName;
            }
        }
        return null;
    }

    public static int getIndex(String name) {
        for (MenuEnum c : MenuEnum.values()) {
            if (c.getTypeName().equals(name) || c.getTypeName() == name) {
                return c.i;
            }
        }
        return 0;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "MenuEnum{" +
                "i=" + i +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
