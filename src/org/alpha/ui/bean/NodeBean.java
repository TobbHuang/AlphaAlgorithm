package org.alpha.ui.bean;

import org.alpha.Constants;

/**
 * Created by huangtao on 2017/1/20.
 */
public class NodeBean {

    private String name;
    private int type;

    public NodeBean(String name) {
        this.name = name;
        type = Constants.TYPE_NODE_TRANSITION;
    }

    public NodeBean(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public int getType() {
        return type;
    }

}
