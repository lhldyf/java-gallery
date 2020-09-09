package com.lhldyf.gallery.utils.test.entity;

/**
 * @author lhldyf
 * @date 2019-07-09 10:05
 */
public class InnerObjEntity {
    private String label;
    private String type;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "InnerObjEntity{" + "label='" + label + '\'' + ", type='" + type + '\'' + '}';
    }
}
