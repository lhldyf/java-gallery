package com.lhldyf.gallery.utils.test.entity;

import java.util.List;

/**
 * @author lhldyf
 * @date 2019-07-09 10:04
 */
public class ObjEntity {
    private String labelFlex;
    private String inputFlex;
    private List<List<InnerObjEntity>> itemLists;

    public String getLabelFlex() {
        return labelFlex;
    }

    public void setLabelFlex(String labelFlex) {
        this.labelFlex = labelFlex;
    }

    public String getInputFlex() {
        return inputFlex;
    }

    public void setInputFlex(String inputFlex) {
        this.inputFlex = inputFlex;
    }

    public List<List<InnerObjEntity>> getItemLists() {
        return itemLists;
    }

    public void setItemLists(List<List<InnerObjEntity>> itemLists) {
        this.itemLists = itemLists;
    }
}
