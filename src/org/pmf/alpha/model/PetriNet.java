package org.pmf.alpha.model;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangtao on 2016/12/29.
 */
public class PetriNet {

    public ArrayList<Transition> transitions;
    public ArrayList<Place> places;

    public PetriNet(ArrayList<String> transitions) {
        this.transitions = new ArrayList<>();
        for (String transition : transitions) {
            this.transitions.add(new Transition(transition));
        }
        places = new ArrayList<>();
    }

    public void addPlace(Place place) {
        places.add(place);
    }

    public void showPlace() {
        String str = "{";
        for (Place place : places) {
            str += place.toString();
            str += ",";
        }
        str = str.substring(0, str.length() - 1);
        str += "}";
        System.out.println(str);
    }

    public JSONObject buildJsn() {
        JSONObject jsn = new JSONObject();
        Map<PetrinetNode, Integer> nodeIndex = new HashMap<>();
        int index = 0;

        JSONArray nodeJsns = new JSONArray();
        for (Transition transition : transitions) {
            JSONObject transitionJsn = new JSONObject();
            transitionJsn.element("label", transition.name);
            transitionJsn.element("detail", transition.name);
            transitionJsn.element("type", "PN_TRANSITION");
            nodeJsns.add(transitionJsn);
            nodeIndex.put(transition, index);
            index++;
        }

        int pIndex = 0;
        for (Place place : places) {
            JSONObject placeJsn = new JSONObject();
            placeJsn.element("label", "P" + pIndex);
            placeJsn.element("detail", "P" + pIndex);
            placeJsn.element("type", "PN_PLACE");
            nodeJsns.add(placeJsn);
            nodeIndex.put(place, index);
            pIndex++;
            index++;
        }
        jsn.element("nodes", nodeJsns);

        JSONArray links = new JSONArray();
        for (Place place : places) {
            for (Integer leftIndex : place.leftTransition) {
                JSONObject linkJsn = new JSONObject();
                linkJsn.element("source", nodeIndex.get(transitions.get(leftIndex)));
                linkJsn.element("target", nodeIndex.get(place));
                linkJsn.element("type", 1);
                links.add(linkJsn);
            }
            for (Integer rightIndex : place.rightTransition) {
                JSONObject linkJsn = new JSONObject();
                linkJsn.element("source", nodeIndex.get(place));
                linkJsn.element("target", nodeIndex.get(transitions.get(rightIndex)));
                linkJsn.element("type", 1);
                links.add(linkJsn);
            }
        }
        jsn.element("links", links);

        return jsn;
    }

}
