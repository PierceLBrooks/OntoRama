package ontorama.backends.p2p.gui;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

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
    }

    public void addChange (String changeStr, String peerName) {
        _myModel.addRow(new Change(changeStr , peerName));
        repaint();
    }

    public void empty() {
         _myModel.clearTable();
         repaint();
    }

    class MyTableModel extends AbstractTableModel {
        private final static int rowsNum = 10;
        private final static int columnsNum = 2;

        String[] columnNames = {"Change", "Peer"};

        List changesList = new LinkedList();

        public MyTableModel () {
            // initialise
            for (int i = 0; i < rowsNum; i++) {
                changesList.add(null);
            }
        }

        public void clearTable () {
            changesList = new LinkedList();
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
            if (col < 1 ) {
                return change.getChange();
            }
            return change.getPeerName();
        }

        public Class getColumnClass(int c) {
            return String.class;
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