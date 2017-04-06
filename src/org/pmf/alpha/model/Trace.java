package org.pmf.alpha.model;

import java.util.ArrayList;

/**
 * Created by huangtao on 2016/12/29.
 */
public class Trace {
    public ArrayList<Integer> events;

    public Trace() {
        events = new ArrayList<>();
    }

    @Override
    public boolean equals(Object trace) {
        if (!trace.getClass().equals(getClass()) || ((Trace) trace).events.size() != events.size()) {
            return false;
        }

        for (int i = 0; i < events.size(); i++) {
            if (((Trace) trace).events.get(i) != events.get(i)) {
                return false;
            }
        }
        return true;
    }
}
