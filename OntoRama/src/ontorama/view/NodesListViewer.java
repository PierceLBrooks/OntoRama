package ontorama.view;

import ontorama.model.GraphNode;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * @author nataliya
 *
 * Display selectable list of nodes
 */
public class NodesListViewer extends JScrollPane {

    /**
     * array of nodes to display
     */
    private GraphNode[] _nodes;

    /**
     *
     */
    private JList _nodesList;

    /**
     *
     */
    private OntoRamaApp _mainApp;

    /**
     *
     */
    public NodesListViewer(OntoRamaApp ontoramaApp, List nodes)  {
        _mainApp = ontoramaApp;
//        setTitle("Unconnected Nodes");

        setNodesList(nodes);


//        getContentPane().add(scrollPane);
//
//        pack();
    }


    /**
     *
     */
    public void setNodesList (List nodes) {
        System.out.println("\n\n\nsetNodesList, nodes list = " + nodes);
        System.out.println("num of components = " + getComponents().length);
//        if (getComponents().length != 0) {
//            remove(_nodesList);
//        }
        Vector nodesVector = new Vector((Collection) nodes);
        _nodesList = new JList(nodesVector);
        _nodesList.setCellRenderer(new NodeListCellRenderer());
        _nodesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        _nodesList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                GraphNode selectedNode =
                        (GraphNode) _nodesList.getSelectedValue();
                _mainApp.resetGraphRoot(selectedNode);
            }
        });
        setViewportView(_nodesList);
        //add(_nodesList);
        repaint();
    }

    /**
     *
     */
    public void showList(boolean setVisibleFlag) {
        setVisible(setVisibleFlag);
    }

//    /**
//     *
//     */
//    public void close() {
//        dispose();
//    }

    //        /**
    //         *
    //         */
    //        public JList getList () {
    //          return _nodesList;
    //        }

    class NodeListCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            GraphNode node = (GraphNode) value;
            String s = node.getName();
            s = s + "   (" + node.getBranchNodesNum() + ")";
            setText(s);
            setToolTipText("number of all children: " + node.getBranchNodesNum());
            return this;
        }
    }

}
