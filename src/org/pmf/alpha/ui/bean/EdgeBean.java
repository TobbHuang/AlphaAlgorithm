package org.pmf.alpha.ui.bean;

/**
 * Created by huangtao on 2017/1/20.
 */
public class EdgeBean {

    private NodeBean sourceNodeBean;
    private NodeBean targetNodeBean;
    private Long ageLong;

    public EdgeBean(NodeBean sourceNodeBean, NodeBean targetNodeBean, Long ageLong) {
        this.sourceNodeBean = sourceNodeBean;
        this.targetNodeBean = targetNodeBean;
        this.ageLong = ageLong;
    }

    public Long getStatCount() {
        return ageLong;
    }

    public void setStatCount(Long ageLong) {
        this.ageLong = ageLong;
    }

    public NodeBean getsourceNodeBean() {
        return sourceNodeBean;
    }

    public void setsourceNodeBean(NodeBean sourceNodeBean) {
        this.sourceNodeBean = sourceNodeBean;
    }

    public NodeBean gettargetNodeBean() {
        return targetNodeBean;
    }

    public void settargetNodeBean(NodeBean targetNodeBean) {
        this.targetNodeBean = targetNodeBean;
    }
}
