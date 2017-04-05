package org.alpha.model;

import org.alpha.Alpha;

import java.util.ArrayList;

/**
 * Created by huangtao on 2016/12/29.
 */
public class Place {

    public ArrayList<Integer> leftTransition;
    public ArrayList<Integer> rightTransition;
    private ArrayList<Transition> transitions;

    public Place(ArrayList<Transition> transitions) {
        leftTransition = new ArrayList<>();
        rightTransition = new ArrayList<>();
        this.transitions = transitions;
    }

    public Place(int a, int b, ArrayList<Transition> transitions) {
        leftTransition = new ArrayList<>();
        rightTransition = new ArrayList<>();
        leftTransition.add(a);
        rightTransition.add(b);
        this.transitions = transitions;
    }

    public boolean canMerge(int a, int b, int[][] footprint) {
        // 如果完全一样则不算可合并
        if (leftTransition.size() == 1 && leftTransition.get(0) == a && rightTransition.size() == 1 &&
                rightTransition.get(0) == b) {
            return false;
        }

        for (Integer i : leftTransition) {
            if (footprint[a][i] != Alpha.FOOTPRINT_NONE || footprint[i][b] != Alpha.FOOTPRINT_FRONT) {
                return false;
            }
        }

        for (Integer i : rightTransition) {
            if (footprint[b][i] != Alpha.FOOTPRINT_NONE || footprint[a][i] != Alpha.FOOTPRINT_FRONT) {
                return false;
            }
        }

        return true;
    }

    public boolean canContain(Place place) {
        for (Integer i : place.leftTransition) {
            if (!leftTransition.contains(i)) {
                return false;
            }
        }
        for (Integer i : place.rightTransition) {
            if (!rightTransition.contains(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String str = "({";
        for (Integer i : leftTransition) {
            str += transitions.get(i).name + ",";
        }
        str = str.substring(0, str.length() - 1);
        str += "},{";
        for (Integer i : rightTransition) {
            str += transitions.get(i).name + ",";
        }
        str = str.substring(0, str.length() - 1);
        str += "})";
        return str;
    }
}
