package ontorama.backends.p2p.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ontorama.OntoramaConfig;
import ontorama.backends.p2p.model.Change;
import ontorama.backends.p2p.model.EdgeChange;
import ontorama.backends.p2p.model.NodeChange;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.NodeType;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 13:04:07
 * To change this template use Options | File Templates.
 */
public class ChangePanel extends JPanel {


    ChangesTableModel _tableModel;
    JTable _table;

    public ChangePanel() {
        super();

        _tableModel = new ChangesTableModel();
        
        _table = new JTable(_tableModel);
        initColumnSizes(_table, _tableModel);
        	
		TableCellRenderer renderer_0 = _table.getDefaultRenderer(_table.getColumnClass(0));
		if (renderer_0 instanceof DefaultTableCellRenderer) {
			((DefaultTableCellRenderer) renderer_0).setToolTipText("Click to Select/Deselect");
	    }
        
		_table.setDefaultRenderer(Change.class, new ChangeCellRenderer());
        
              
        Dimension d = new Dimension(300, 300);
        
        //_table.setPreferredScrollableViewportSize(d);

        JScrollPane scrollPane = new JScrollPane(_table);
        scrollPane.setPreferredSize(d);
        
        JButton acceptButton = new JButton("Accept");
        acceptButton.setToolTipText("Accept changes selected in the table and add them to your model");
        acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				List selectedChanges = getChangesForSelectedRows();
				Iterator it = selectedChanges.iterator();
				while (it.hasNext()) {
					Change element = (Change) it.next();
					
				}
			}
        });
        
		JButton rejectButton = new JButton("Reject");
		rejectButton.setToolTipText("Reject changes selected in the table");
		rejectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				List selectedChanges = getChangesForSelectedRows();
			}
		});

		JButton ignoreButton = new JButton("Ignore");
		ignoreButton.setToolTipText("Ignore selected changes");
		ignoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				List selectedChanges = getChangesForSelectedRows();
			}
		});

        JPanel buttonsPanel = new JPanel(new BorderLayout());

        JPanel panel1 = new JPanel(new FlowLayout());
        panel1.add(acceptButton);
        panel1.add(rejectButton);
        panel1.add(ignoreButton);

        JButton resetButton = new JButton("Clear table");
        resetButton.setToolTipText("Remove all changes from the table");
        resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				empty();
			} 
        });
        
        buttonsPanel.add(panel1, BorderLayout.CENTER);
        buttonsPanel.add(resetButton, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    public void addChange (Change change) {
        _tableModel.addRow(change);
        repaint();
    }

    public void empty() {
         _tableModel.clearTable();
         repaint();
    }

	private List getChangesForSelectedRows() {
		List result = new LinkedList();
		for (int i = 0; i < _tableModel.getRowCount(); i++) {
			System.out.println("col 1 value for row " + i + " is " + _tableModel.getValueAt(i,0));
			TableRow row = (TableRow) _tableModel.getValueAt(i);
			for (int j = 0; j < _tableModel.getColumnCount(); j++) {
				if (row.getValueAt(j) instanceof Change) {
					result.add(row.getValueAt(j));
					break;
				}
			}
		}
		return result;
	}

	/*
	  * This method picks good column sizes.
	  * If all column heads are wider than the column's cells' 
	  * contents, then you can just use column.sizeWidthToFit().
	  */
	 private void initColumnSizes (JTable table, ChangesTableModel model) {
		 for (int i = 0; i < model.getColumnCount(); i++) {
			 TableColumn column = table.getColumnModel().getColumn(i);

			 Component comp = table.getDefaultRenderer(model.getColumnClass(i)).
							  getTableCellRendererComponent(
								  table, _tableModel.getLongValues()[i],
								  false, false, 0, i);
			 int cellWidth = comp.getPreferredSize().width;

			 //System.out.println("Initializing width of column "	+ i + "; cellWidth = " + cellWidth);

			column.setPreferredWidth(cellWidth);
		 }
	 } 

    class ChangesTableModel extends AbstractTableModel {
        
        private final static int columnsNum = 5;

        private String[] columnNames = {"","","+/-","Details", "Peer"};
        
		private final Object[] longValues = {Boolean.TRUE, "node", "+/-", 
										"http://www.ontorama.org/wn#TrueCat -> wn#Cat", 
										"peer 1"};        

        private List rowsList;

        public ChangesTableModel () {
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
				case 1 :
					result = Change.class;
					break;
				case 2 :
					result = Change.class;
					break;
				case 3 :
					result = Change.class;
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
		
		public Object [] getLongValues () {
			return longValues;
		}

    }
    
    private class TableRow {
    	private Change _change;
    	Object[] row = new Object[5];
    	
		public TableRow(Change change) {
			_change = change;
			
			row[0] = new Boolean(false);
			row[1] = change;
			row[2] = change;
			row[3] = change;
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
    
    private class ChangeCellRenderer extends JLabel implements TableCellRenderer  {

		public Component getTableCellRendererComponent(JTable table, Object value, 
													boolean isSelected, boolean hasFocus, 
													int row, int col) {

			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}
			switch (col) {
				case 1 :
					ImageIcon icon = null;
					if (value instanceof NodeChange) {
						NodeChange nodeChange = (NodeChange) value;
						setToolTipText("Node");
						icon = getNodeIcon(nodeChange);
					}
					else {
						EdgeChange edgeChange = (EdgeChange) value;
						setToolTipText("Edge: " + edgeChange.getEdgeType());
						icon = getEdgeIcon(edgeChange);
					}
					setText("");
					setIcon(icon);
					break;
				case 2 :
					String action = ((Change) value).getAction();
					if (action.equalsIgnoreCase(Change.ASSERT)) {
						setText("+");
						setToolTipText("Asserted");
					}
					else {
						setText("-");
						setToolTipText("Rejected");
					}
					setIcon(null);
					break;
				case 3:
					String cellText = "";
					if (value instanceof NodeChange ) {
						NodeChange nodeChange = (NodeChange) value;
						cellText = nodeChange.getNodeName();
					}
					else {
						EdgeChange edgeChange = (EdgeChange) value;
						cellText = edgeChange.getFromNode() + " -> " + edgeChange.getToNode();
					}
					setText(cellText);
					setToolTipText(cellText);
					setIcon(null);
					break;
				default :
					break;
			}
			return this;
		}
		
		private ImageIcon getNodeIcon (NodeChange nodeChange) {
			String nodeTypeStr = nodeChange.getNodeType();
			Iterator it = OntoramaConfig.getNodeTypesCollection().iterator();
			NodeType nodeType = null;
			while (it.hasNext()) {
				NodeType cur = (NodeType) it.next();
				if (cur.getName().equals(nodeTypeStr)) {
					nodeType = cur;
				}
			}
			if (nodeType == null) {
				nodeType = OntoramaConfig.UNKNOWN_TYPE;
			}
			Image nodeImage = OntoramaConfig.getNodeTypeDisplayInfo(nodeType).getImage();
			//Image scaledImage = nodeImage.getScaledInstance(10,10, Image.SCALE_DEFAULT);
			return new ImageIcon(nodeImage);
		}
		
		private ImageIcon getEdgeIcon (EdgeChange edgeChange) {
			String edgeTypeString = edgeChange.getEdgeType();
			Iterator it = OntoramaConfig.getEdgeTypesList().iterator();
			EdgeType edgeType = null;
			while (it.hasNext()) {
				EdgeType cur = (EdgeType) it.next();
				if (cur.getName().equals(edgeTypeString)) {
					edgeType = cur;
				}
			}
			Image edgeImage = OntoramaConfig.getEdgeDisplayInfo(edgeType).getImage();
			//Image scaledImage = edgeImage.getScaledInstance(20,10,Image.SCALE_DEFAULT);
			return new ImageIcon( edgeImage);
		}
    	
    }
}
