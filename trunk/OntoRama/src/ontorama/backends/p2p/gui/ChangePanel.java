package ontorama.backends.p2p.gui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.Dimension;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 13:04:07
 * To change this template use Options | File Templates.
 */
public class ChangePanel extends JPanel {


    MyTableModel _myModel;
    JTable _table;

    public ChangePanel() {
        super();

        _myModel = new MyTableModel();
        _table = new JTable(_myModel);
        _table.setPreferredScrollableViewportSize(new Dimension(200, 300));

        //Create the scroll pane and add the _table to it.
        JScrollPane scrollPane = new JScrollPane(_table);

        add(scrollPane);

        addChange("test1", "peer1");
        addChange("test2", "peer2");
        addChange("test3", "peer3");
        addChange("test4", "peer4");
        addChange("test5", "peer5");
        addChange("test6", "peer6");


    }

    public void addChange (String changeStr, String peerName) {
        System.out.println("adding change " + changeStr );
        _myModel.addRow(new Change(changeStr, peerName));
        repaint();
    }

    class MyTableModel extends AbstractTableModel {
        private final static int rowsNum = 4;
        private final static int columnsNum = 2;

        String[] columnNames = {"Change", "Peer"};
//        String[][] data = new String[rowsNum][columnsNum];

        List changesList = new LinkedList();

        public MyTableModel () {
            // initialise
            for (int i = 0; i < rowsNum; i++) {
                changesList.add(null);
            }
        }

        public void addRow (Change change) {
            int emptyRowNum = findEmptyRow();
            if (emptyRowNum < rowsNum) {
                changesList.set(emptyRowNum, change);
            }
            else {
                changesList.remove(0);
                changesList.add(change);
            }
        }

        private int findEmptyRow () {
            for (int i = 0; i < rowsNum; i++) {
                Change change = getValueAt(i);
                if (change == null) {
                    return i;
                }
            }
            return rowsNum + 1;
        }


        public Change getValueAt(int row) {
            Change change = (Change) changesList.get(row);
            return change;
        }

        public int getColumnCount() {
            return columnsNum;
        }

        public int getRowCount() {
            return rowsNum;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            Change change = (Change) changesList.get(row);
            if (change == null) {
                return new String();
            }
            if (col <= 1 ) {
                return change.getChange();
            }
            return change.getPeerName();
//            return data[row][col];
        }

        public Class getColumnClass(int c) {
            return String.class;
            //return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your _table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
//            if (col < 2) {
//                return false;
//            } else {
//                return true;
//            }
            return true;
        }
    }



    private class Change {
        private String _change;
        private String _peerName;

        public Change (String change, String peerName) {
            _change = change;
            _peerName = peerName;
        }

        public String getChange() {
            return _change;
        }

        public String getPeerName() {
            return _peerName;
        }
    }
}
