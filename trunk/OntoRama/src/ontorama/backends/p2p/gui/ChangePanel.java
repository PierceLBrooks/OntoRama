package ontorama.backends.p2p.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
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
				for (int i = 0; i < _myModel.getRowCount(); i++) {
					System.out.println("col 1 value for row " + i + " is " + _myModel.getValueAt(i,0));
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
        private final static int columnsNum = 5;

        String[] columnNames = {"","","Action","Details", "Peer"};

        List rowsList;

        public MyTableModel () {
        	rowsList = new LinkedList();
        }

        public void clearTable () {
        	rowsList.clear();
        }

        public void addRow (Change change) {
        	TableRow newRow = new TableRow(change);
            rowsList.add(newRow);
            fireTableDataChanged();
            repaint();
        }

        public TableRow getValueAt(int row) {
            return  (TableRow) rowsList.get(row);
        }

        public int getColumnCount() {
            return columnsNum;
        }

        public int getRowCount() {
			return rowsList.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
        	if (row >= rowsList.size()) {
        		return null;
        	}
            TableRow tableRow = (TableRow) rowsList.get(row);
            return tableRow.getValueAt(col); 
        }

        public Class getColumnClass(int c) {
			Class result = String.class;
			switch (c) {
				case 0 :
					result = Boolean.class;
					break;
				default :
					result = String.class;
					break;
			}
            return result;
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col == 0) {
            	// only first column with checkboxes is editable to
            	// allow us to select/deselect checkboxes.
            	return true;
            }
            return false;
        }

		public void setValueAt(Object value, int row, int col) {
			TableRow tableRow = (TableRow) rowsList.get(row);
			tableRow.setValueAt(value, col); 
			fireTableRowsUpdated(row, col);
		}

    }
    
    private class TableRow {
    	private Change _change;
    	Object[] row = new Object[5];
    	
		public TableRow(Change change) {
			_change = change;
			
			row[0] = new Boolean(false);
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

		public void setValueAt (Object value, int col) {
			row[col] = value;
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
