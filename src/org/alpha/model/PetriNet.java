package org.alpha.model;

import java.util.ArrayList;

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

}
