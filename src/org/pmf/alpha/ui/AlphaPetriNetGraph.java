package org.pmf.alpha.ui;

import com.realpersist.gef.layout.NodeJoiningDirectedGraphLayout;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Node;
import org.jgraph.JGraph;
import org.jgraph.graph.*;
import org.pmf.alpha.Constants;
import org.pmf.alpha.model.PetriNet;
import org.pmf.alpha.model.Place;
import org.pmf.alpha.ui.bean.EdgeBean;
import org.pmf.alpha.ui.bean.NodeBean;
import org.pmf.alpha.ui.bean.UnionEdge;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

/**
 * Created by huangtao on 2017/1/2.
 */
public class AlphaPetriNetGraph {

    private Map gefNodeMap;

    private Map graphNodeMap;

    private List<UnionEdge> edgeList;

    private List<EdgeBean> edgeBeanList;

    private DirectedGraph directedGraph;

    private JGraph graph;

    private GraphModel model;

    private PetriNet petriNet;

    public AlphaPetriNetGraph(PetriNet petriNet) {
        // petri网数据
        this.petriNet = petriNet;

        // 初始化各变量
        gefNodeMap = new HashMap();
        graphNodeMap = new HashMap();
        edgeList = new ArrayList<>();
        edgeBeanList = new ArrayList<>();
        directedGraph = new DirectedGraph();

        // 初始化graph
        model = new DefaultGraphModel();
        graph = new JGraph(model);
        graph.setSelectionEnabled(true);
    }

    public JGraph createGraph() {
        // 填充图数据
        fillGraphData();

        Map attributes = new Hashtable();
        // Set Arrow
        Map edgeAttrib = new Hashtable();
        GraphConstants.setLineEnd(edgeAttrib, GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(edgeAttrib, true);
        graph.setJumpToDefaultPort(true);

        if (edgeBeanList == null || edgeBeanList.size() == 0) {
            graph.repaint();
            return null;
        }
        Iterator edgeBeanIt = edgeBeanList.iterator();
        while (edgeBeanIt.hasNext()) {
            EdgeBean edgeBean = (EdgeBean) edgeBeanIt.next();
            NodeBean sourceAction = edgeBean.getsourceNodeBean();
            NodeBean targetAction = edgeBean.gettargetNodeBean();
            Long ageLong = edgeBean.getStatCount();
            String edgeString = "(" + ageLong + ")";
            addEdge(sourceAction, targetAction, 20, edgeString);
        }

        // 自动布局 首先用DirectedGraphLayout如果出现异常（图不是整体连通的）则采用NodeJoiningDirectedGraphLayout
        // 后者可以对非连通图进行布局坐标计算，但效果不如前者好，所以首选前者，当前者不可以处理时采用后者
        try {
            new DirectedGraphLayout().visit(directedGraph);
        } catch (Exception e1) {
            new NodeJoiningDirectedGraphLayout().visit(directedGraph);
        }

        int self_x = 50;
        int self_y = 50;
        int base_y = 10;
        if (graphNodeMap != null && gefNodeMap != null && graphNodeMap.size() > gefNodeMap.size()) {
            base_y = self_y + GraphProp.NODE_HEIGHT;
        }

        // 向图中添加节点node
        Collection nodeCollection = graphNodeMap.values();
        Iterator nodeIterator = nodeCollection.iterator();
        while (nodeIterator.hasNext()) {
            DefaultGraphCell node = (DefaultGraphCell) nodeIterator.next();
            NodeBean userObject = (NodeBean) node.getUserObject();
            if (userObject == null) {
                continue;
            }
            Node gefNode = (Node) gefNodeMap.get(userObject);
            if (gefNode == null) {
                // 这是因为当一个节点有一个指向自身的边的时候，我们在gefGraph中并没有计算这条边（gefGraph不能计算包含指向自己边的布局），
                // 所以当在所要画的图中该节点只有一条指向自身的边的时候，我们在gefNodeMap中就找不到相应节点了
                // 这个时候，我们手工给出该节点的 X,Y 坐标
                gefNode = new Node();
                gefNode.x = self_x;
                gefNode.y = self_y - base_y;
                self_x += (10 + GraphProp.NODE_WIDTH);
            }
            Map nodeAttrib = new Hashtable();
            GraphConstants.setBorderColor(nodeAttrib, Color.black);

            Rectangle2D bounds = new Rectangle2D.Double(gefNode.x, gefNode.y + base_y, GraphProp.NODE_WIDTH,
                    GraphProp.NODE_HEIGHT);
            GraphConstants.setBounds(nodeAttrib, bounds);
            attributes.put(node, nodeAttrib);
        }// while

        // 向图中添加边
        if (edgeList == null) {
            return null;
        }
        for (int i = 0; i < edgeList.size(); i++) {
            UnionEdge unionEdge = edgeList.get(i);
            if (unionEdge == null) {
                continue;
            }
            ConnectionSet cs = new ConnectionSet(unionEdge.getEdge(), unionEdge.getSourceNode().getChildAt(0),
                    unionEdge.getTargetNode().getChildAt(0));
            Object[] cells = new Object[]{unionEdge.getEdge(), unionEdge.getSourceNode(), unionEdge.getTargetNode()};
            attributes.put(unionEdge.getEdge(), edgeAttrib);
            model.insert(cells, attributes, cs, null, null);
        }

        return graph;
    }

    /**
     * 将PetriNet内的数据填充到edgeBeanList中
     */
    private void fillGraphData() {

        List<NodeBean> tranNode = new ArrayList<>();

        for (Place place : petriNet.places) {
            // 库所结点
            NodeBean placeNode = new NodeBean("", Constants.TYPE_NODE_PLACE);

            // count=0处理左变迁，=1处理右变迁
            for (int count = 0; count <= 1; count++) {
                ArrayList<Integer> curTransition;

                if (count == 0) {
                    curTransition = place.leftTransition;
                } else {
                    curTransition = place.rightTransition;
                }

                for (Integer index : curTransition) {
                    String transitionName = petriNet.transitions.get(index).name;
                    NodeBean tmpBean = null;
                    boolean isExisted = false;
                    for (NodeBean bean : tranNode) {
                        if (bean.getName().equals(transitionName)) {
                            isExisted = true;
                            tmpBean = bean;
                            break;
                        }
                    }
                    if (!isExisted) {
                        tmpBean = new NodeBean(transitionName);
                        tranNode.add(tmpBean);
                    }
                    if (count == 0) {
                        edgeBeanList.add(new EdgeBean(tmpBean, placeNode, new Long(20)));
                    } else {
                        edgeBeanList.add(new EdgeBean(placeNode, tmpBean, new Long(20)));
                    }
                }
            }

        }
    }

    /**
     * 用于创建graph
     *
     * @param source
     * @param target
     * @param weight
     * @param edgeString
     */
    private void addEdge(NodeBean source, NodeBean target, int weight, String edgeString) {

        if (source == null || target == null) {
            return;
        }
        if (gefNodeMap == null) {
            gefNodeMap = new HashMap();
        }
        if (graphNodeMap == null) {
            graphNodeMap = new HashMap();
        }
        if (edgeList == null) {
            edgeList = new ArrayList();
        }
        if (directedGraph == null) {
            directedGraph = new DirectedGraph();
        }

        // 建立GEF的 node edge将来用来计算graph node的layout
        addEdgeGef(source, target, weight);

        // 建立真正要用的graph的 node edge
        DefaultGraphCell sourceNode = null;
        DefaultGraphCell targetNode = null;
        sourceNode = (DefaultGraphCell) graphNodeMap.get(source);
        if (sourceNode == null) {
            sourceNode = new DefaultGraphCell(source);
            sourceNode.addPort();
            graphNodeMap.put(source, sourceNode);
        }
        targetNode = (DefaultGraphCell) graphNodeMap.get(target);
        if (targetNode == null) {
            targetNode = new DefaultGraphCell(target);
            targetNode.addPort();
            graphNodeMap.put(target, targetNode);
        }
        DefaultEdge edge = new DefaultEdge(edgeString);
        UnionEdge unionEdge = new UnionEdge();
        unionEdge.setEdge(edge);
        unionEdge.setSourceNode(sourceNode);
        unionEdge.setTargetNode(targetNode);

        edgeList.add(unionEdge);

    }

    /**
     * 用于计算布局
     *
     * @param source
     * @param target
     * @param weight
     */
    private void addEdgeGef(NodeBean source, NodeBean target, int weight) {
        if (source.equals(target)) {
            return;
        }

        // 建立GEF的 node edge将来用来计算graph node的layout
        Node gefSourceNode = null;
        Node gefTargetNode = null;
        gefSourceNode = (Node) gefNodeMap.get(source);
        if (gefSourceNode == null) {
            gefSourceNode = new Node();
            gefSourceNode.width = GraphProp.NODE_WIDTH;
            gefSourceNode.height = GraphProp.NODE_WIDTH;
            //gefSourceNode.setPadding(new Insets(GraphProp.NODE_TOP_PAD,GraphProp.NODE_LEFT_PAD, GraphProp
            // .NODE_BOTTOM_PAD,GraphProp.NODE_RIGHT_PAD));
            directedGraph.nodes.add(gefSourceNode);
            gefNodeMap.put(source, gefSourceNode);
        }

        gefTargetNode = (Node) gefNodeMap.get(target);
        if (gefTargetNode == null) {
            gefTargetNode = new Node();
            gefTargetNode.width = GraphProp.NODE_WIDTH;
            gefTargetNode.height = GraphProp.NODE_WIDTH;
            //gefTargetNode.setPadding(new Insets(GraphProp.NODE_TOP_PAD,GraphProp.NODE_LEFT_PAD, GraphProp
            // .NODE_BOTTOM_PAD,GraphProp.NODE_RIGHT_PAD));
            directedGraph.nodes.add(gefTargetNode);
            gefNodeMap.put(target, gefTargetNode);
        }

        org.eclipse.draw2d.graph.Edge gefEdge1 = null;
        try {
            gefEdge1 = new org.eclipse.draw2d.graph.Edge(gefSourceNode, gefTargetNode);
            gefEdge1.weight = weight;
            directedGraph.edges.add(gefEdge1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
