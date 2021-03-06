/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sf.odinms.exttools.maplepcap;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.sf.odinms.exttools.required.SaveFileFilter;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.data.input.ByteArrayByteStream;
import net.sf.odinms.tools.data.input.GenericSeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author  Raz
 */
public class MaplePcaptureGUI extends javax.swing.JFrame {

	private static final long serialVersionUID = -722111285770372532L;
	private boolean autoScroll = false;
	private int packetTotal = 0;
	private MaplePacketStructureViewer structureViewer;
	private MaplePcapture capture;

	/** Creates new form MaplePcaptureGUI */
	public MaplePcaptureGUI() {
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		initComponents();
		initSelectionListener();
	}

	public void initSelectionListener() {
		SelectionListener listener = new SelectionListener(packetTable);
		packetTable.getSelectionModel().addListSelectionListener(listener);
		packetTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        packetTable = new javax.swing.JTable();
        autoScrollButton = new javax.swing.JToggleButton();
        viewPacketSturctureButton = new javax.swing.JButton();
        viewPacketHeaderButton = new javax.swing.JButton();
        copyPacketDataButton = new javax.swing.JButton();
        packetCountLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        packetDataTable = new javax.swing.JTable();
        viewPacketTreeButton = new javax.swing.JButton();
        RIVLabel = new javax.swing.JLabel();
        SIVLabel = new javax.swing.JLabel();
        viewBlockedOpcodeButton = new javax.swing.JButton();
        blockPacketButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        closeMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        viewSettingsMenuItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        overallInformationMenuItem = new javax.swing.JMenuItem();
        resetPacketCountMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jSeparator1 = new javax.swing.JSeparator();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Snow's Packet Sniffer");
        setResizable(false);

        packetTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Counter", "Time", "Direction", "Opcode", "Opcode Name", "Length"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        packetTable.setToolTipText("Table view of recorded packets");
        packetTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        packetTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(packetTable);
        packetTable.getColumnModel().getColumn(0).setMinWidth(50);
        packetTable.getColumnModel().getColumn(0).setMaxWidth(50);
        packetTable.getColumnModel().getColumn(1).setMinWidth(120);
        packetTable.getColumnModel().getColumn(1).setMaxWidth(120);
        packetTable.getColumnModel().getColumn(2).setMinWidth(180);
        packetTable.getColumnModel().getColumn(2).setMaxWidth(180);
        packetTable.getColumnModel().getColumn(3).setMinWidth(70);
        packetTable.getColumnModel().getColumn(3).setMaxWidth(70);
        packetTable.getColumnModel().getColumn(5).setMinWidth(70);
        packetTable.getColumnModel().getColumn(5).setMaxWidth(70);

        autoScrollButton.setText("Auto-Scroll");
        autoScrollButton.setToolTipText("Scroll down to the newest packet everytime a new packet is received");
        autoScrollButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoScrollButtonActionPerformed(evt);
            }
        });

        viewPacketSturctureButton.setText("View Packet Structure");
        viewPacketSturctureButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewPacketSturctureButtonActionPerformed(evt);
            }
        });

        viewPacketHeaderButton.setText("View Packet Headers");
        viewPacketHeaderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewPacketHeaderButtonActionPerformed(evt);
            }
        });

        copyPacketDataButton.setText("Copy Packet Data");
        copyPacketDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyPacketDataButtonActionPerformed(evt);
            }
        });

        packetCountLabel.setText("Total Packet Count: 0");
        packetCountLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        statusLabel.setText("Status: Waiting For Maple-Story");
        statusLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        packetDataTable.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        packetDataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Offset", "Hex", "Ascii"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        packetDataTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        packetDataTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(packetDataTable);
        packetDataTable.getColumnModel().getColumn(0).setMinWidth(70);
        packetDataTable.getColumnModel().getColumn(0).setMaxWidth(60);
        packetDataTable.getColumnModel().getColumn(1).setMinWidth(385);
        packetDataTable.getColumnModel().getColumn(1).setMaxWidth(385);
        packetDataTable.getColumnModel().getColumn(2).setResizable(false);

        viewPacketTreeButton.setText("View Packet Tree");
        viewPacketTreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewPacketTreeButtonActionPerformed(evt);
            }
        });

        RIVLabel.setText("RIV: 00 00 00 00");
        RIVLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        SIVLabel.setText("SIV: 00 00 00 00");
        SIVLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        viewBlockedOpcodeButton.setText("View Blocked Opcodes");
        viewBlockedOpcodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewBlockedOpcodeButtonActionPerformed(evt);
            }
        });

        blockPacketButton.setText("Block Packet");
        blockPacketButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blockPacketButtonActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        closeMenuItem.setText("Close");
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(closeMenuItem);

        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(saveMenuItem);

        viewSettingsMenuItem.setText("View Settings");
        viewSettingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewSettingsMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(viewSettingsMenuItem);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Statistics");

        overallInformationMenuItem.setText("Overall Information");
        jMenu3.add(overallInformationMenuItem);

        resetPacketCountMenuItem.setText("Reset Packet Count");
        resetPacketCountMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetPacketCountMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(resetPacketCountMenuItem);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("Help");
        jMenu2.add(jSeparator1);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(aboutMenuItem);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RIVLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SIVLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(packetCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                .addGap(31, 31, 31))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(viewPacketTreeButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewPacketSturctureButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(blockPacketButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(copyPacketDataButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(210, 210, 210)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(viewPacketHeaderButton, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                            .addComponent(viewBlockedOpcodeButton, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoScrollButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(viewPacketSturctureButton)
                    .addComponent(copyPacketDataButton)
                    .addComponent(autoScrollButton)
                    .addComponent(viewPacketHeaderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(viewPacketTreeButton)
                    .addComponent(viewBlockedOpcodeButton)
                    .addComponent(blockPacketButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(packetCountLabel)
                    .addComponent(statusLabel)
                    .addComponent(RIVLabel)
                    .addComponent(SIVLabel)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
	private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuItemActionPerformed
		//CLOSE
		setVisible(false);
		dispose();
		System.exit(0);
}//GEN-LAST:event_closeMenuItemActionPerformed

	private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
		//ABOUT
		JOptionPane.showMessageDialog(null, "Snow's Packet Sniffer \r\nCreated By: Snow", "About", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_aboutMenuItemActionPerformed

	private void viewSettingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewSettingsMenuItemActionPerformed
		//SNIFFER SETTINGS
		String snifferSettings = "Capture Type: " + capture.getCapType().name() + "\r\n";
		snifferSettings += "Packet Filter: " + capture.getPacketFilter() + "\r\n";
		snifferSettings += "Server Output Type: " + capture.getServerOutputType().name() + "\r\n";
		snifferSettings += "Logging: " + (capture.isLogging() ? "ON" : "OFF") + "\r\n";
		snifferSettings += "Default Packet: " + (capture.isBlockDefault() ? "BLOCKED" : "UNBLOCKED");
		JOptionPane.showMessageDialog(null, snifferSettings, "Sniffer Settings", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_viewSettingsMenuItemActionPerformed

	private void autoScrollButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoScrollButtonActionPerformed
		//AUTOSCROLL
		autoScroll = autoScrollButton.getModel().isSelected();
}//GEN-LAST:event_autoScrollButtonActionPerformed

	private void viewPacketSturctureButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewPacketSturctureButtonActionPerformed
		//VIEW PACKET STRUCTURE
		int selectedRow = packetTable.getSelectedRow();
		if (selectedRow == -1) {
			return;
		}
		if (structureViewer != null) {
			structureViewer.setVisible(false);
			structureViewer = null;
		}
		structureViewer = new MaplePacketStructureViewer();
		MaplePacketRecord record = MaplePacketRecord.getById(selectedRow);
		structureViewer.setPacketRecord(record);
        structureViewer.setPacketData(record.getPacketData());
		structureViewer.setSend(record.isSend());
		structureViewer.setPacketLabelText("Packet: " + record.getHeader());
		structureViewer.setVisible(true);
		structureViewer.anazlyePacket();

}//GEN-LAST:event_viewPacketSturctureButtonActionPerformed

	private void viewPacketHeaderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewPacketHeaderButtonActionPerformed
		//VIEW MAPLEOPCODE
		new MapleOpcodeView().setVisible(true);
}//GEN-LAST:event_viewPacketHeaderButtonActionPerformed

	private void copyPacketDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyPacketDataButtonActionPerformed
		//COPY PACKET DATA
		int rowIndex = packetTable.getSelectedRow();
		if (rowIndex > -1) {
			StringSelection stringSelection = new StringSelection(HexTool.toString(MaplePacketRecord.getById(rowIndex).getPacketData()));
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, stringSelection);
			setStatus("Copied packet-data to clipboard");
		} else {
			JOptionPane.showMessageDialog(null, "Please select a packet to copy data from", "Warning", JOptionPane.WARNING_MESSAGE);
		}

}//GEN-LAST:event_copyPacketDataButtonActionPerformed

	private void resetPacketCountMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetPacketCountMenuItemActionPerformed
		//RESET PACKET TOTAL
		this.packetTotal = -1;
		updateAndIncreasePacketTotal();
		setStatus("Total packet count has been reset.");

}//GEN-LAST:event_resetPacketCountMenuItemActionPerformed

	private void viewPacketTreeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewPacketTreeButtonActionPerformed
		//VIEW PACKET DATA TREE
		int rowIndex = packetTable.getSelectedRow();
		MaplePacketRecord record = MaplePacketRecord.getById(rowIndex);
		if (record != null && record.isDataRecord()) {
			PacketDataTree tree = new PacketDataTree();

			tree.getJTree1().setModel(record.getTreeModel());
			tree.setVisible(true);
			tree.getJTree1().setRootVisible(false);
		}
}//GEN-LAST:event_viewPacketTreeButtonActionPerformed

	private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
		//SAVE PACKETS TO FILE
		File file = null;
		JFileChooser fileChooser = new JFileChooser(file);
		SaveFileFilter fileFilter = new SaveFileFilter("PCAP File(*.pcap)");
		fileChooser.setFileFilter(fileFilter);
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
		} else {
			return;
		}
		try {
			getCapture().dumpToFile(file.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
}//GEN-LAST:event_saveMenuItemActionPerformed

	private void viewBlockedOpcodeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewBlockedOpcodeButtonActionPerformed
		//VIEW BLOCKED OPCODES
		new MapleBlockedOpcodeViewer(capture).setVisible(true);
}//GEN-LAST:event_viewBlockedOpcodeButtonActionPerformed

	private void blockPacketButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blockPacketButtonActionPerformed
		//BLOCK PACKET OPCODE
		int rowIndex = packetTable.getSelectedRow();
		if (rowIndex > -1) {
			MaplePacketRecord record = MaplePacketRecord.getById(rowIndex);
			if (record.isDataRecord()) {
				String blockString = (record.isSend() ? "S" : "R") + "_" + (record.getHeader().equals("UNKNOWN") ? record.getOpcodeHex(false) : record.getHeader());
				capture.getBlockedOpcodes().put(blockString, Boolean.TRUE);
				setStatus("Packet Block Added (" + record.getHeader() + ") [" + record.getOpcodeHex(false) + "]");
			}
		}
	}//GEN-LAST:event_blockPacketButtonActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				new MaplePcaptureGUI().setVisible(true);
			}
		});
	}

	public void addRow(MaplePacketRecord record) {
		addRow(record.getRowData());
	}

	public void addRow(Object[] rowData) {
		int row = packetTable.getRowCount();
		((DefaultTableModel) packetTable.getModel()).addRow(rowData);
		if (autoScroll) {
			packetTable.changeSelection(row, 0, false, false);
			packetTable.scrollRectToVisible(packetTable.getCellRect(row, 0, true));
		}
	}

	public void showPacketSelectionData(MaplePacketRecord record) {
		if (record == null) {
			return;
		}
		if (record.isDataRecord()) {
			SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(record.getPacketData()));
			((DefaultTableModel) packetDataTable.getModel()).getDataVector().clear();

			while (true) {
				int len = Math.min((int) slea.available(), 16);
				Object[] rowData = new Object[3];
				rowData[0] = StringUtil.getLeftPaddedStr(Integer.toHexString((int) slea.getPosition()), '0', 8).toUpperCase();
				byte[] data = slea.read(len);
				rowData[1] = HexTool.toString(data);
				rowData[2] = HexTool.toStringFromAscii(data);
				((DefaultTableModel) packetDataTable.getModel()).addRow(rowData);
				if (len < 16) {
					break;
				}
			}
		}
	}

	public void setPacketCountLabelText(String s) {
		packetCountLabel.setText(s);
	}

	public void updateAndIncreasePacketTotal() {
		packetTotal++;
		setPacketCountLabelText("Total Packet Count: " + packetTotal);
	}

	public MaplePcapture getCapture() {
		return capture;
	}

	public void setCapture(MaplePcapture capture) {
		this.capture = capture;
	}

	public void setStatusText(String status) {
		statusLabel.setText(status);
	}

	public void setRIVStr(String riv) {
		RIVLabel.setText(riv);
	}

	public void setSIVStr(String siv) {
		SIVLabel.setText(siv);
	}

	public void setStatus(String text) {
		String status = "Status: " + text;
		statusLabel.setText(status);
		capture.outputWithLogging(status);
	}

	public class SelectionListener implements ListSelectionListener {

		JTable table;

		// It is necessary to keep the table since it is not possible
		// to determine the table from the event's source
		SelectionListener(JTable table) {
			this.table = table;
		}

		public void valueChanged(ListSelectionEvent e) {
			//VIEW PACKET DETAILS
			int rowIndex = packetTable.getSelectedRow();
			if (rowIndex > -1) {
				showPacketSelectionData(MaplePacketRecord.getById(rowIndex));
			}
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel RIVLabel;
    private javax.swing.JLabel SIVLabel;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JToggleButton autoScrollButton;
    private javax.swing.JButton blockPacketButton;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JButton copyPacketDataButton;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenuItem overallInformationMenuItem;
    private javax.swing.JLabel packetCountLabel;
    private javax.swing.JTable packetDataTable;
    private javax.swing.JTable packetTable;
    private javax.swing.JMenuItem resetPacketCountMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton viewBlockedOpcodeButton;
    private javax.swing.JButton viewPacketHeaderButton;
    private javax.swing.JButton viewPacketSturctureButton;
    private javax.swing.JButton viewPacketTreeButton;
    private javax.swing.JMenuItem viewSettingsMenuItem;
    // End of variables declaration//GEN-END:variables
}
