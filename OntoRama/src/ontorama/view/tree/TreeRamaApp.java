package ontorama.view.ontotree;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;
import java.net.URL;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;

import java.util.Iterator;

import ontorama.model.graph.Graph;
import ontorama.model.graph.GraphNode;

public class TreeRamaApp extends JFrame {
    private JEditorPane htmlPane;
    private static boolean DEBUG = false;
    private URL helpURL;

    //Optionally play with line styles.  Possible values are
    //"Angled", "Horizontal", and "None" (the default).
    private boolean playWithLineStyle = false;
    private String lineStyle = "Angled";

    public TreeRamaApp() {
        super("TreeRamaApp");


        //Create a tree that allows one selection at a time.
        //final JTree tree = new JTree(top);
        GraphBuilder xmlReader = new GraphBuilder();
        Graph graph = xmlReader.getGraph();

        //final JTree tree = new Graph ();

        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                   tree.getLastSelectedPathComponent();

                if (node == null) return;

                Object nodeInfo = node.getUserObject();
                if (node.isLeaf()) {
                    BookInfo book = (BookInfo)nodeInfo;
                    displayURL(book.bookURL);
                    if (DEBUG) {
                        System.out.print(book.bookURL + ":  \n    ");
                    }
                } else {
                    displayURL(helpURL);
                }
                if (DEBUG) {
                    System.out.println(nodeInfo.toString());
                }
            }
        });

        if (playWithLineStyle) {
            tree.putClientProperty("JTree.lineStyle", lineStyle);
        }

        //Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(tree);

        //Create the HTML viewing pane.
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        initHelp();
        JScrollPane htmlView = new JScrollPane(htmlPane);

        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(htmlView);

        Dimension minimumSize = new Dimension(100, 50);
        htmlView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(100); //XXX: ignored in some releases
                                           //of Swing. bug 4101306
        //workaround for bug 4101306:
        //treeView.setPreferredSize(new Dimension(100, 100));

        splitPane.setPreferredSize(new Dimension(500, 300));

        //Add the split pane to this frame.
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    /*
    private DefaultMutableTreeNode buildTreeModel () {
      String termName = "comms#CommsObject";
      //String termName = "wn#TrueCat";
      //String termName = "wn#Dog";
      //System.out.println("termName = " + termName);

      DefaultMutableTreeNode top = null;

      try {
          TypeQuery query = new TypeQueryImplementation();
          //Iterator ontIterator = query.getTypeRelative(termName,ontorama.OntoramaConfig.SUBTYPE);
          Iterator ontIterator = query.getTypeRelative(termName);
          // create top node for the tree
          top = new DefaultMutableTreeNode(termName);
          createNodes(top,ontIterator);

          //while (ontIterator.hasNext()) {
          //    OntologyType ot = (OntologyTypeImplementation) ontIterator.next();
          //    //System.out.println ("node: " + ot);
          //    createNodes(top,ontIterator);
          //}

      }
      catch (ClassNotFoundException ce) {
          System.out.println("ClassNotFoundException: " + ce);
          System.exit(1);
      }
      catch (Exception e) {
          System.out.println("Exception: " + e);
      }
      return top;

    }
    */

    private class BookInfo {
        public String bookName;
        public URL bookURL;
        public String prefix = "file:"
                               + System.getProperty("user.dir")
                               + System.getProperty("file.separator");
        public BookInfo(String book, String filename) {
            bookName = book;
            try {
                bookURL = new URL(prefix + filename);
            } catch (java.net.MalformedURLException exc) {
                System.err.println("Attempted to create a BookInfo "
                                   + "with a bad URL: " + bookURL);
                bookURL = null;
            }
        }

        public String toString() {
            return bookName;
        }
    }

    private void initHelp() {
        String s = null;
        try {
            s = "file:"
                + System.getProperty("user.dir")
                + System.getProperty("file.separator")
                + "TreeRamaAppHelp.html";
            if (DEBUG) {
                System.out.println("Help URL is " + s);
            }
            helpURL = new URL(s);
            displayURL(helpURL);
        } catch (Exception e) {
            System.err.println("Couldn't create help URL: " + s);
        }
    }

    private void displayURL(URL url) {
        try {
            htmlPane.setPage(url);
        } catch (IOException e) {
            System.err.println("Attempted to read a bad URL: " + url);
        }
    }
    /*

    private void createNodes(DefaultMutableTreeNode node, Iterator ontIterator)
                                    throws NoSuchRelationLinkException {

      //DefaultMutableTreeNode node = new DefaultMutableTreeNode(ot.getName());
      //Iterator ontIterator = ot.getIterator(ontorama.OntoramaConfig.SUBTYPE);
      while (ontIterator.hasNext()) {
          OntologyType child = (OntologyTypeImplementation) ontIterator.next();
          DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child.getName());
          node.add(childNode);
          if (child.getIterator(ontorama.OntoramaConfig.SUBTYPE) != null ) {
            createNodes(childNode,child.getIterator(ontorama.OntoramaConfig.SUBTYPE));
          }
      }
    }
    */

    public static void main(String[] args) {
        JFrame frame = new TreeRamaApp();

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}

