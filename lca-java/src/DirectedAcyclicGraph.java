import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class DirectedAcyclicGraph {

    ArrayList<Node> nodes;
    Queue<Node> blueNodes;


    public DirectedAcyclicGraph() {
        nodes = new ArrayList<>();

    }

    public DirectedAcyclicGraph(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void printGraph() {
        for (Node node:nodes) {
            System.out.printf("\n"+node+"{ \n\tParents: [");
            for (Node parent: node.getParents()) {
                System.out.print(parent+", ");
            }
            System.out.print("], \n\tChildren: [");
            for (Node child: node.getChildren()) {
                System.out.print(child+", ");
            }
            System.out.println("], \n\tColor: "+node.getColor()+", \n\tCount: "+node.getCount()+"\n}");
        }
    }

    // testing a generic BFS algorithm
    public ArrayList<Integer> bfs(Node startNode) {
        LinkedList<Node> q = new LinkedList<>();
        ArrayList<Integer> order = new ArrayList<>();

        q.add(startNode);
        System.out.println("\nPerforming BFS w starting node "+startNode+"\n");

        while(!q.isEmpty()) {
            System.out.println("Queue: "+Arrays.toString(q.toArray()));
            Node curNode = q.remove();
            order.add(curNode.getVal());

            System.out.println("Cur Node: " + curNode);

            for (Node n: curNode.getChildren()) {
                q.add(n);
                System.out.println("Adding "+n+" to queue");
            }
            System.out.println();
        }
        return order;
    }

    public boolean bfsForTarget(Node startNode, Node target) {
        LinkedList<Node> q = new LinkedList<>();
        q.add(startNode);
        boolean targetFound = false;

        System.out.println("\nPerforming BFS w starting node "+startNode+" seeking target " + target + "\n");

        while(!q.isEmpty()) {
            System.out.println("Queue: "+Arrays.toString(q.toArray()));
            Node curNode = q.remove();

            System.out.println("Cur Node: " + curNode);

            if (curNode.getChildren().contains(target)) {
                targetFound = true;
                break;
            }
            for (Node n: curNode.getChildren()) {
                q.add(n);
                System.out.println("Adding "+n+" to queue");
            }
            System.out.println();
        }

        return targetFound;
    }

    public void colourAncestorsBlue(Node target) {
        // performs a BFS from each node 'n' to determine if n is an ancestor of 'target'
        // If so, colours 'n' blue.

        // Create a queue containing all the nodes in the graph
        LinkedList<Node> nodesInGraph = new LinkedList<>();
        nodesInGraph.addAll(nodes);

        while (!nodesInGraph.isEmpty()) {
            Node curStartNode = nodesInGraph.remove();

            boolean ancestorOfTarget = bfsForTarget(curStartNode, target);

            if (ancestorOfTarget) {
                curStartNode.setColor(Node.Color.BLUE);
            }
        }
    }

    // colours any BLUE ancestors of the other target node RED if they are ancestors of this 'target'
    public void colourAncestorsRed(Node target) {

        // get the blue nodes
        LinkedList<Node> blueNodes = new LinkedList<>(getBlueNodes());

        // bfs from each blue node and if it can reach 'target' colour it red
        while (!blueNodes.isEmpty()) {

            Node curNode = blueNodes.remove();

            boolean isAncestor = bfsForTarget(curNode, target);

            if (isAncestor) {
                curNode.setColor(Node.Color.RED);
            }

        }

    }

    public ArrayList<Node> getBlueNodes() {

        ArrayList<Node> blueNodes = new ArrayList<>();

        System.out.println("Blue nodes: ");
        for (Node n:nodes) {
            if (n.getColor().toLowerCase() == "blue") {
                System.out.println("\t"+n);
                blueNodes.add(n);
            }
        }

        return blueNodes;

    }

    public ArrayList<Node> getRedNodes() {

        ArrayList<Node> redNodes = new ArrayList<>();

        System.out.println("\nRed nodes: ");
        for (Node n:nodes) {
            if (n.getColor().toLowerCase() == "red") {
                System.out.println("\t"+n);
                redNodes.add(n);
            }
        }

        return redNodes;

    }

    // increment each red node's parents' counts by 1
    // any red node with count 0 is an LCA
    public void adjustCount() {

        LinkedList<Node> redNodes = new LinkedList<>(getRedNodes());

        for (Node n: redNodes) {
            for (Node parent: n.getParents()) {
                parent.incrCount();
            }
        }
    }

    // Sets all nodes' colours back to white
    public void resetColors() {
        for (Node n: nodes) {
            n.setColor(Node.Color.WHITE);
        }
    }

    // sets all nodes' counts back to 0
    public void resetCounts() {
        for (Node n:nodes) {
            n.setCount(0);
        }
    }

    // returns a reference to the node in the graph with a given value val
    public Node getNodeWithValue(int val) {
        Node theNode = null;

        for (Node n: nodes) {
            if (n.getVal() == val) {
                return n;
            }
        }

        return theNode;
    }

}
