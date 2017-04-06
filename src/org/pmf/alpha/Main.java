package org.pmf.alpha;

import org.pmf.alpha.model.PetriNet;
import org.pmf.alpha.ui.AlphaPetriNetGraph;

import javax.swing.*;

/**
 * Created by huangtao on 2016/12/29.
 * <p>
 * 主函数，程序执行入口
 */
public class Main {

    public static void main(String[] args) {
        Alpha alpha = new Alpha("/Users/huangtao/Documents/本科毕设/example-logs/exercise1.xes");
        PetriNet petriNet = alpha.getPetriNet();

        // Show in Frame
        AlphaPetriNetGraph alphaPetriNetGraph = new AlphaPetriNetGraph(petriNet);

        JFrame frame = new JFrame();
        frame.getContentPane().add(new JScrollPane(alphaPetriNetGraph.createGraph()));
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

}
