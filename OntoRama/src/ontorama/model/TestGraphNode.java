

package ontorama.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Tester for GraphNode.java
 */

public class TestGraphNode {

  private GraphNode node;
  private String nodeName;
  private LinkedList childrenList;

  public TestGraphNode (String nodeName, LinkedList childrenList) {
    this.nodeName = nodeName;
    this.childrenList = childrenList;

    node = new GraphNode (nodeName);

    printMessage ("Testing GraphNode");
    printMessage ("testing getName...");
    getNameTest();

    GraphNode childNode1 = new GraphNode((String) childrenList.get(0));
    GraphNode childNode2 = new GraphNode ((String) childrenList.get(1));
    node.addChild(childNode1);
    node.addChild(childNode2);

    printMessage("testing getChildrenIterator....");
    getChildrenIteratorTest();

    printMessage("testing getChildrenList....");
    getChildrenListTest();


    printMessage("---not sure how to test addChild...");
    printMessage("---not sure how to test addClone...");
    printMessage("---not sure how to test addObserver...");
    printMessage("---not sure how to test calculateDepths...");
    printMessage("---not sure how to test getClones...");
    printMessage("---not sure how to test getDepth...");

    printMessage ("all tests were successfull");
  }

  private void getNameTest () {
    if ( ! ((node.getName()).equals(nodeName))  ) {
      error ("getName failed, returned " + node.getName() + " instead of " + nodeName);
    }
  }

  private void getChildrenIteratorTest () {
    Iterator it = node.getChildrenIterator();
    int count = 0;
    while (it.hasNext()) {
      GraphNode child = (GraphNode) it.next();
      String testName = (String) childrenList.get(count);
      String childName = child.getName();
      if ( ! childName.equals(testName) ) {
        error ("getChildrenIterator failed, returned " + childName + " instead of " + testName);
      }
      //printMessage("count = " + count + ", childName = " + childName + ", testName = " + testName);
      count++;
    }
  }

  private void getChildrenListTest () {
    List testList = (List) node.getChildrenList();
    List childList = (List) this.childrenList;
    int size = childList.size();
    int count = 0;
    while (count < size) {
      String childName = (String) childList.get(count);
      GraphNode testNode = (GraphNode) testList.get(count);
      String testNodeName = testNode.getName();
      if ( ! (testNodeName.equals(childName) )) {
        error ("getChildrenList failed, returned " + childName + " instead of " + testNodeName);
      }
      //printMessage("count = " + count + ", child = " + childName + ", testName = " + testNodeName);
      count++;
    }

  }

  private void error (String message) {
    printMessage(message);
    System.exit(0);
  }

  private void printMessage (String message) {
    System.out.println(message);
  }

  public static void main(String[] args)  {

    String nodeName = "testNode";
    LinkedList children = new LinkedList();
    children.add("child1");
    children.add("child2");

    TestGraphNode test = new TestGraphNode(nodeName, children);

  }

}