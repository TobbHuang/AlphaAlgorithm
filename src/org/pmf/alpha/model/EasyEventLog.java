package org.pmf.alpha.model;

import java.util.ArrayList;

/**
 * Created by huangtao on 2016/12/29.
 */
public class EasyEventLog {

    public ArrayList<Trace> traces;
    public ArrayList<String> eventName;

    public EasyEventLog() {
        traces = new ArrayList<>();
        eventName = new ArrayList<>();
    }

}
