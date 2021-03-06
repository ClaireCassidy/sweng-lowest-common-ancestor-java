import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;

@RunWith(JUnit4.class)
public class LowestCommonAncestorDagTest {

    @Test
    public void testDagBfsGeneric() {
        // Create graph shown in slides:
        //                   [1]
        //                  /   \
        //               [2]     [3]
        //              /           \
        //           [4]             [5]
        //          /               /   \
        //       [6]             [7]     [8]
        //                        |
        //                       [10]
        //                      / |  \
        //                    [9] |   [11]
        //                       [13]     \
        //                                 [12]

        // This prototype BFS is used to implement other specialised BFS for the LCA algorithm
        // The traversal in the specialised BFS is in the same order, just different side effects occur
        // So if this traversal is correct, then the traversal in the specialised BFS's are correct
        DirectedAcyclicGraph testDag = generateTestGraph1();

        // extract a starting node; in this case 1:
        Node startNode = testDag.getNodes().get(0);

        ArrayList<Integer> expectedOrder = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,10,9,13,11,12));
        ArrayList<Integer> actualOrder = testDag.bfs(startNode);

        assertTrue("Testing BFS traverses the graph in the expected order given start node 1",
                expectedOrder.equals(actualOrder));

        // try again with a different node (n5)
        startNode = testDag.getNodes().get(4);

        expectedOrder = new ArrayList<>(Arrays.asList(5,7,8,10,9,13,11,12));
        actualOrder = testDag.bfs(startNode);

        assertTrue("Testing BFS traverses the graph in the expected order given start node 5",
                expectedOrder.equals(actualOrder));

        // and with one more node (n8)
        startNode = testDag.getNodes().get(7);

        expectedOrder = new ArrayList<>(Arrays.asList(8));
        actualOrder = testDag.bfs(startNode);

        assertTrue("Testing BFS traverses the graph in the expected order given start node 8",
                expectedOrder.equals(actualOrder));
    }

    @Test
    public void testAddParent() {
        Node n1 = new Node(7, null,null);
        Node n2 = new Node(5, null, null);

        n1.addParent(n2);

        assertTrue("Test that a node rejects attempts to insert a duplicate parent",
                !n1.addParent(n2));
    }

    @Test
    public void testAddChild() {
        Node n1 = new Node(7, null,null);
        Node n2 = new Node(5, null, null);

        n1.addChild(n2);

        assertTrue("Test that a node rejects attempts to insert a duplicate child",
                !n1.addChild(n2));
    }

    @Test
    public void testGetNodeWithVal() {

        Node n1 = new Node(5, null, null);

        DirectedAcyclicGraph testDag = new DirectedAcyclicGraph(new ArrayList<>(Arrays.asList(n1)));

        assertEquals("Testing retrieval of a node from a graph given a value n", n1,
                testDag.getNodeWithValue(5));

        assertEquals("Test that attempts to retrieve a node with a value not in the graph returns null",
                null, testDag.getNodeWithValue(7));
    }

    @Test
    public void testDagBfsForTarget() {
        // Create graph shown in slides:
        //                   [1]
        //                  /   \
        //               [2]     [3]
        //              /           \
        //           [4]             [5]
        //          /               /   \
        //       [6]             [7]     [8]
        //                        |
        //                       [10]
        //                      / |  \
        //                    [9] |   [11]
        //                       [13]     \
        //                                 [12]

        // Slightly altered version of the generic BFS algorithm that tests if a 'target' node
        // is a descendent of a 'start node' (i.e. 'startNode' is an ancestor of 'target'

        DirectedAcyclicGraph testDag = generateTestGraph1();

        // Test if n1 is an ancestor of n6
        Node startNode = testDag.getNodeWithValue(1);
        Node target = testDag.getNodeWithValue(6);

        assertTrue("Confirm 1 is an ancestor of 6", testDag.bfsForTarget(startNode, target));
        assertTrue("Confirm 6 is not an ancestor of 1", !testDag.bfsForTarget(target, startNode));

        startNode = testDag.getNodeWithValue(12);
        target = testDag.getNodeWithValue(10);

        assertTrue("Confirm 12 is not an ancestor of 10", !testDag.bfsForTarget(startNode, target));
        assertTrue("Confirm 10 is an ancestor of 12", testDag.bfsForTarget(target, startNode));

        startNode = testDag.getNodeWithValue(8);
        target = testDag.getNodeWithValue(10);

        assertTrue("Confirm 10 is not an ancestor of 8", !testDag.bfsForTarget(startNode, target));
        assertTrue("Confirm 8 is not an ancestor of 10", !testDag.bfsForTarget(target, startNode));
    }

    @Test
    public void testColorAncestorsBlue() {
        // Create graph shown in slides:
        //                   [1]
        //                  /   \
        //               [2]     [3]
        //              /           \
        //           [4]             [5]
        //          /               /   \
        //       [6]             [7]     [8]
        //                        |
        //                       [10]
        //                      / |  \
        //                    [9] |   [11]
        //                       [13]     \
        //                                 [12]

        DirectedAcyclicGraph testDag = generateTestGraph1();

        Node target = testDag.getNodeWithValue(6);

        testDag.colourAncestorsBlue(target);

        ArrayList<Node> actualBlueNodes = testDag.getBlueNodes();
        ArrayList<Node> expectedBlueNodes = new ArrayList<>(Arrays.asList(testDag.getNodeWithValue(1),
                testDag.getNodeWithValue(2), testDag.getNodeWithValue(4)));

        assertTrue("Blue Nodes for target n6 => [n1,n2,n4]",
                expectedBlueNodes.equals(actualBlueNodes));

        testDag.resetColors();

        target = testDag.getNodeWithValue(11);

        testDag.colourAncestorsBlue(target);

        actualBlueNodes = testDag.getBlueNodes();
        expectedBlueNodes = new ArrayList<>(Arrays.asList(testDag.getNodeWithValue(1), testDag.getNodeWithValue(3),
                testDag.getNodeWithValue(5), testDag.getNodeWithValue(7), testDag.getNodeWithValue(10)));

        assertTrue("Blue nodes for target n11 => [n5, n7, n10]",
                expectedBlueNodes.equals(actualBlueNodes));

        testDag.resetColors();
    }


    @Test
    public void testColourAncestorsRed() {
        // Create graph shown in slides:
        //                   [1]
        //                  /   \
        //               [2]     [3]
        //              /           \
        //           [4]             [5]
        //          /               /   \
        //       [6]             [7]     [8]
        //                        |
        //                       [10]
        //                      / |  \
        //                    [9] |   [11]
        //                       [13]     \
        //                                 [12]

        DirectedAcyclicGraph testDag = generateTestGraph1();

        // suppose we had target nodes of n6 and n5
        // Then we call colorAncestorsBlue on n6 and colorAncestorsRed on n5:

        Node target1 = testDag.getNodeWithValue(6);
        Node target2 = testDag.getNodeWithValue(5);

        testDag.colourAncestorsBlue(target1);
        testDag.colourAncestorsRed(target2);

        ArrayList<Node> actualRedNodes = testDag.getRedNodes();
        ArrayList<Node> expectedRedNodes = new ArrayList<>(Arrays.asList(testDag.getNodeWithValue(1)));

        assertTrue("Test that the red nodes for the graph using target1 = n6, target2 = n5 = [n1]",
                actualRedNodes.equals(expectedRedNodes));

        testDag.resetColors();

        target1 = testDag.getNodeWithValue(8);
        target2 = testDag.getNodeWithValue(9);

        testDag.colourAncestorsBlue(target1);
        testDag.colourAncestorsRed(target2);

        actualRedNodes = testDag.getRedNodes();
        expectedRedNodes = new ArrayList<>(Arrays.asList(testDag.getNodeWithValue(1), testDag.getNodeWithValue(3),
                testDag.getNodeWithValue(5)));

        assertTrue("Test that the red nodes for the graph using target1 = n8, target2 = n9 => [n1, n3, n5]",
                actualRedNodes.equals(expectedRedNodes));

        testDag.resetColors();
    }

    @Test
    public void testLCAs() {
        // Create graph shown in slides:
        //                   [1]
        //                  /   \
        //               [2]     [3]
        //              /           \
        //           [4]             [5]
        //          /               /   \
        //       [6]             [7]     [8]
        //                        |
        //                       [10]
        //                      / |  \
        //                    [9] |   [11]
        //                       [13]     \
        //                                 [12]

        DirectedAcyclicGraph testDag = generateTestGraph1();

        // Test the LCAs are correct for target1, target2
        Node target1 = testDag.getNodeWithValue(4);
        Node target2 = testDag.getNodeWithValue(5);

        ArrayList<Node> actualLCAs = testDag.getLCAs(target1, target2);
        ArrayList<Node> expectedLCAs = new ArrayList<>(Arrays.asList(testDag.getNodeWithValue(1)));

        System.out.println("Actual: " + actualLCAs);
        System.out.println("Expected: " + expectedLCAs);

        assertTrue("Testing that the LCAs of n4 and n5 = [n1]",
                actualLCAs.equals(expectedLCAs));

        // With target1 = 12, target2 = 13...
        testDag.resetColors();

        target1 = testDag.getNodeWithValue(13);
        target2 = testDag.getNodeWithValue(12);

        actualLCAs = testDag.getLCAs(target1, target2);
        expectedLCAs = new ArrayList<>(Arrays.asList(testDag.getNodeWithValue(10)));

        assertTrue("Testing that the LCAs of n12 and n13 = [n10]",
                actualLCAs.equals(expectedLCAs));

        // With target1 = 6, target2 = 4...
        testDag.resetColors();

        target1 = testDag.getNodeWithValue(6);
        target2 = testDag.getNodeWithValue(4);

        actualLCAs = testDag.getLCAs(target1, target2);
        expectedLCAs = new ArrayList<>(Arrays.asList(testDag.getNodeWithValue(2)));

        assertTrue("Testing that the LCAs of n6 and n4 = [n2]",
                actualLCAs.equals(expectedLCAs));


        testDag = generateTestGraph2();

        target1 = testDag.getNodeWithValue(4);
        target2 = testDag.getNodeWithValue(5);

        actualLCAs = testDag.getLCAs(target1, target2);
        expectedLCAs = new ArrayList<>(Arrays.asList(testDag.getNodeWithValue(3), testDag.getNodeWithValue(1)));



        System.out.println("Actual: " + actualLCAs);
        System.out.println("Expected: "+expectedLCAs);



        assertTrue("Testing that the LCAs of n4 and n5 are [n3, n1]", actualLCAs.containsAll(expectedLCAs)
                && actualLCAs.size() == expectedLCAs.size());
    }

    @Test
    public void testShortestPath() {
        // Create graph shown in slides:
        //                   [1]
        //                  /   \
        //               [2]     [3]
        //              /           \
        //           [4]             [5]
        //          /               /   \
        //       [6]             [7]     [8]
        //                        |
        //                       [10]
        //                      / |  \
        //                    [9] |   [11]
        //                       [13]     \
        //                                 [12]

        DirectedAcyclicGraph testDag = generateTestGraph1();

        Node startNode = testDag.getNodeWithValue(1);
        Node endNode = testDag.getNodeWithValue(8);

        ArrayList<Node> actualShortestPath = testDag.shortestPath(startNode, endNode);
        ArrayList<Node> expectedShortestPath = new ArrayList<>(Arrays.asList(testDag.getNodeWithValue(1),
                testDag.getNodeWithValue(3), testDag.getNodeWithValue(5), testDag.getNodeWithValue(8)));
        System.out.println("Actual: "+actualShortestPath);
        System.out.println("Expected: "+expectedShortestPath);

        assertTrue("Testing that the shortest path from n1 to n8 is n1 -> n3 -> n5 -> n8",
                actualShortestPath.equals(expectedShortestPath));

        testDag = generateTestGraph2();

        startNode = testDag.getNodeWithValue(1);
        endNode = testDag.getNodeWithValue(5);

        actualShortestPath = testDag.shortestPath(startNode, endNode);
        expectedShortestPath = new ArrayList<>(Arrays.asList(testDag.getNodeWithValue(1), testDag.getNodeWithValue(5)));

        assertTrue("Testing that the shortest path from n1 to n5 is n1 -> n5",
                actualShortestPath.equals(expectedShortestPath));

        // shortest path that doesn't exist
        startNode = testDag.getNodeWithValue(3);
        endNode = testDag.getNodeWithValue(1);

        actualShortestPath = testDag.shortestPath(startNode, endNode);

        assertTrue("Testing that the shortest path from n3 to n1 is null",
                actualShortestPath == null);
    }

    public static DirectedAcyclicGraph generateTestGraph1() {
        // Create graph shown in slides:
        //                   [1]
        //                  /   \
        //               [2]     [3]
        //              /           \
        //           [4]             [5]
        //          /               /   \
        //       [6]             [7]     [8]
        //                        |
        //                       [10]
        //                      / |  \
        //                    [9] |   [11]
        //                       [13]     \
        //                                 [12]

        // create the nodes
        Node n1     = new Node(1, null, null);
        Node n2     = new Node(2, null, null);
        Node n3     = new Node(3, null, null);
        Node n4     = new Node(4, null, null);
        Node n5     = new Node(5, null, null);
        Node n6     = new Node(6, null, null);
        Node n7     = new Node(7, null, null);
        Node n8     = new Node(8, null, null);
        Node n9     = new Node(9, null, null);
        Node n10    = new Node(10, null, null);
        Node n11    = new Node(11, null, null);
        Node n12    = new Node(12, null, null);
        Node n13    = new Node(13, null, null);

        // create their relationships
        n1.addChild(n2);
        n1.addChild(n3);

        n2.addParent(n1);
        n2.addChild(n4);

        n3.addParent(n1);
        n3.addChild(n5);

        n4.addParent(n2);
        n4.addChild(n6);

        n5.addParent(n3);
        n5.addChild(n7);
        n5.addChild(n8);

        n6.addParent(n4);

        n7.addParent(n5);
        n7.addChild(n10);

        n8.addParent(n5);

        n9.addParent(n10);

        n10.addParent(n7);
        n10.addChild(n9);
        n10.addChild(n13);
        n10.addChild(n11);

        n11.addParent(n10);
        n11.addChild(n12);

        n12.addParent(n11);

        n13.addParent(n10);

        ArrayList<Node> dagNodes = new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6, n7,
                                                                    n8, n9, n10, n11, n12, n13));

        return new DirectedAcyclicGraph(dagNodes);
    }

    public static DirectedAcyclicGraph generateTestGraph2() {
        // Create the graph shown in the slides:
        //          [1]----\
        //         / | \    \
        //      [2]  |  [3]  |
        //        \  |  /|   |
        //         \ | / |   |
        //          [4]  |   /
        //            \  |  /
        //             [5]-/

        // Create the nodes
        Node n1     = new Node(1, null, null);
        Node n2     = new Node(2, null, null);
        Node n3     = new Node(3, null, null);
        Node n4     = new Node(4, null, null);
        Node n5     = new Node(5, null, null);

        // Create their relationships
        n1.addChild(n2);
        n1.addChild(n3);
        n1.addChild(n4);
        n1.addChild(n5);

        n2.addParent(n1);
        n2.addChild(n4);

        n3.addParent(n1);
        n3.addChild(n4);
        n3.addChild(n5);

        n4.addParent(n1);
        n4.addParent(n2);
        n4.addParent(n3);
        n4.addChild(n5);

        n5.addParent(n1);
        n5.addParent(n3);
        n5.addParent(n4);

        // Create the graph:
        ArrayList<Node> nodes = new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5));

        return new DirectedAcyclicGraph(nodes);
    }

}
