package ontorama.backends.p2p.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import ontorama.backends.p2p.model.Change;
import ontorama.backends.p2p.model.EdgeChange;
import ontorama.backends.p2p.model.NodeChange;

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
    
    protected ButtonGroup _checkboxesButtonGroup = new ButtonGroup();
    
    /**
     * keys - checkbox, values - change obj
     */
    protected Hashtable _checkboxToChangeMapping = new Hashtable();

    public ChangePanel() {
        super();

        _myModel = new MyTableModel();
        _table = new JTable(_myModel);
        
        Dimension d = new Dimension(250, 400);
        
        //_table.setPreferredScrollableViewportSize(d);

        JScrollPane scrollPane = new JScrollPane(_table);
        scrollPane.setPreferredSize(d);
        
        JButton acceptButton = new JButton("Accept");
        acceptButton.setToolTipText("Accept these changes and add them to your model");
        acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Enumeration e = _checkboxesButtonGroup.getElements();
				while (e.hasMoreElements()) {
					JCheckBox checkbox = (JCheckBox) e.nextElement();
					Change change = (Change) _checkboxToChangeMapping.get(checkbox);
					System.out.println("selected: " + change);
				}
			}
        });
        
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(acceptButton);
        

        add(scrollPane);
        add(buttonsPanel);
    }

    public void addChange (Change change) {
        _myModel.addRow(change);
        repaint();
    }

    public void empty() {
         _myModel.clearTable();
         repaint();
    }

    class MyTableModel extends AbstractTableModel {
        private final static int rowsNum = 10;
        private final static int columnsNum = 5;

        String[] columnNames = {"","","Action","Details", "Peer"};

        List rowsList = new LinkedList();

        public MyTableModel () {
            // initialise
            for (int i = 0; i < rowsNum; i++) {
                rowsList.add(null);
            }
        }

        public void clearTable () {
            rowsList = new LinkedList();
            for (int i = 0; i < rowsNum; i++) {
                rowsList.add(null);
            }
        }

        public void addRow (Change change) {
        	TableRow newRow = new TableRow(change);
            int emptyRowNum = findEmptyRow();
            if (emptyRowNum < rowsNum) {
                rowsList.set(emptyRowNum, newRow);
            }
            else {
                rowsList.remove(0);
                rowsList.add(newRow);
            }
            repaint();
        }

        private int findEmptyRow () {
            for (int i = 0; i < rowsNum; i++) {
                TableRow curRow = getValueAt(i);
                if (curRow == null) {
                    return i;
                }
            }
            return rowsNum + 1;
        }


        public TableRow getValueAt(int row) {
            return  (TableRow) rowsList.get(row);
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
            TableRow tableRow = (TableRow) rowsList.get(row);
            if (tableRow == null) {
                return new String();
            }
            return tableRow.getValueAt(col);
        }

        public Class getColumnClass(int c) {
			Class result = null;
			switch (c) {
			  case 1 :
				  result = JCheckBox.class;
				  break;
			  case 2 :
				  result = String.class;
				  break;
			  case 3 :
				  result = String.class;
				  break;
			  case 4 :
				  result = String.class;
				  break;
			  default :
				  result = String.class;
				  break;
		  	}

            return result;
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
    
    private class TableRow {
    	private Change _change;
    	Object[] row = new Object[5];
    	
		public TableRow(Change change) {
			_change = change;
			
			JCheckBox checkbox = new JCheckBox();
			_checkboxesButtonGroup.add(checkbox);
			_checkboxToChangeMapping.put(checkbox, change);
					 
			row[0] = checkbox;
			
			row[2] = change.getAction();
			
			if (change instanceof NodeChange ) {
				row[1] = "node";
				NodeChange nodeChange = (NodeChange) change;
				row[3] = nodeChange.getNodeName();
			}
			else {
				row[1] = "edge";
				EdgeChange edgeChange = (EdgeChange) change;
				row[3] = edgeChange.getFromNode() + " -> " + edgeChange.getToNode();
			}
			row[4] = change.getPeer().getName();
		}
		
		public Object getValueAt (int col) {
			return row[col];
		}
		
		public String toString() {
			String res = "Row: ";
			for (int i = 0; i < row.length; i++) {
				Object element = row[i];
				res = res + element + ", ";
			}
			return res;
		}
    }
}
