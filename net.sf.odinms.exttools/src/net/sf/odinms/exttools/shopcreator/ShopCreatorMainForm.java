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
package net.sf.odinms.exttools.shopcreator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataDirectoryEntry;
import net.sf.odinms.provider.MapleDataFileEntry;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.provider.wz.MapleDataType;

/**
 *
 * @author  andy
 */
public class ShopCreatorMainForm extends javax.swing.JFrame {

	private static final long serialVersionUID = -3518856195096613636L;
	private Map<MapleInventoryType, ArrayList<ShopItem>> items = new HashMap<MapleInventoryType, ArrayList<ShopItem>>();
	private List<ShopItem> shopItems = new ArrayList<ShopItem>();
	private MapleInventoryType inventoryTypeListed;

	/** Creates new form ShopCreatorMainForm */
	public ShopCreatorMainForm() {
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jItemCount2 = new javax.swing.JLabel();
        jItemCount1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("OdinMS Shop Creator - Fuko-chan v0.0.1");

        jList2.setModel(new javax.swing.AbstractListModel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -3749697088603197599L;
			String[] strings = { "" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane2.setViewportView(jList2);

        jList1.setModel(new javax.swing.AbstractListModel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 2981141588959616216L;
			String[] strings = { "" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jList1);

        jButton1.setText("Create SQL");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("USE");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("EQP");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("SET-UP");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("ETC");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setText("CASH");
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton5ActionPerformed(evt);
            }
        });

        jItemCount2.setText("Items: ");

        jItemCount1.setText("Items: ");

        jTextField1.setText("NPC-ID(opt)");

        jTextField2.setText("SHOPID(req)");

        jButton2.setText("UP");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("DOWN");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Add");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jItemCount1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(70, 70, 70))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3))
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4)
                            .addComponent(jRadioButton2, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                            .addComponent(jRadioButton1, javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jRadioButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jRadioButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                            .addComponent(jRadioButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jItemCount2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jRadioButton1, jRadioButton2, jRadioButton3, jRadioButton4, jRadioButton5});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jItemCount1)
                    .addComponent(jItemCount2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3)
                                .addGap(45, 45, 45))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jRadioButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton5)
                                .addGap(18, 18, 18)
                                .addComponent(jButton4)))
                        .addGap(50, 50, 50)
                        .addComponent(jButton1)))
                .addGap(31, 31, 31))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton5ActionPerformed
		//LOAD ITEM LIST (CASH)
		initializeItemList(MapleInventoryType.CASH);
    }//GEN-LAST:event_jRadioButton5ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
		//LOAD ITEM LIST (ETC)
		initializeItemList(MapleInventoryType.ETC);
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
		//LOAD ITEM LIST (SETUP)
		initializeItemList(MapleInventoryType.SETUP);
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
		//LOAD ITEM LIST (USE)
		initializeItemList(MapleInventoryType.USE);
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		//CREATE SQL
		int npcid = -1;
		int shopid = -1;
		boolean completedShopid = false;
		try {
			shopid = Integer.parseInt(this.jTextField2.getText());
			completedShopid = true;
			npcid = Integer.parseInt(this.jTextField1.getText());
		} catch (Exception e) {
			if (completedShopid) {
				//Nothing, user didnt want to enter an npc-id
			} else {
				//Need a shopid to continue
				JOptionPane.showMessageDialog(null, "Invalid shopid entered", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		String query = "-- SHOP_ID " + shopid + "\r\n-- ITEM_SIZE " + shopItems.size() + "\r\n";
		query += "INSERT INTO shops\r\n(`shopid`, `npcid`)\r\nVALUES\r\n(" + shopid + ", " + npcid + ");\r\n\r\n";
		query += "INSERT INTO shopitems\r\n(`shopid`, `itemid`, `price`, `position`)\r\nVALUES";
		for (int i = 1; i <= shopItems.size(); i++) {
			char lineMarker = i == shopItems.size() ? ';' : ',';
			ShopItem item = shopItems.get(i - 1);
			query += "\r\n(" + shopid + ", " + item.getItemId() + ", " + item.getPrice() + ", " + i + ")" + lineMarker;
		}
		System.out.println(query);

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
		//MOVE UP
		moveItem(jList1.getSelectedIndex(), true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
		int index = jList1.getSelectedIndex();
		this.jButton2.setEnabled(index > 0);
		this.jButton3.setEnabled(index < shopItems.size() - 1);
    }//GEN-LAST:event_jList1ValueChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
		//MOVE DOWN
		moveItem(jList1.getSelectedIndex(), false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
		//ADD ITEM
		try {
			ShopItem tempItem = items.get(inventoryTypeListed).get(jList2.getSelectedIndex());
			ShopItem item = new ShopItem(tempItem.getValue(), tempItem.getItemId(), tempItem.getImage());
			int price = Integer.parseInt(JOptionPane.showInputDialog("Please enter a price:"));
			item.setPrice(price);
			shopItems.add(item);
			this.jList1.setCellRenderer(new ShopItemListCellRenderer());
			this.jList1.setListData(shopItems.toArray());
			this.jItemCount1.setText("Items: " + shopItems.size());
		} catch (Exception e) {
			//TODO - Find a better way to handle this exception
		}
    }//GEN-LAST:event_jButton4ActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {

		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				ShopCreatorMainForm sc = new ShopCreatorMainForm();
				sc.initializeShopItemList();

				//Load Default
				sc.initializeItemList(MapleInventoryType.SETUP);
				sc.jRadioButton3.setSelected(true);

				sc.setVisible(true);

			}
		});
	}

	public void moveItem(int index, boolean up) {
		int change = up ? -1 : 1;
		Collections.swap(shopItems, index, index + change);
		this.jList1.setListData(shopItems.toArray());
		this.jList1.setSelectedIndex(index + change);

	}

	public void initializeShopItemList() {
		final ShopItem[] shopItemsInit = {};
		this.jList1.setCellRenderer(new ShopItemListCellRenderer());
		this.jList1.setListData(shopItemsInit);
	}

	public void initializeItemList(MapleInventoryType invType) {

		inventoryTypeListed = invType;
		ArrayList<ShopItem> sItems = items.get(invType);
		if (sItems == null) {
			sItems = new ArrayList<ShopItem>();
			MapleDataProvider strings = MapleDataProviderFactory.getWzFile("String.wz");

			MapleDataProvider data = MapleDataProviderFactory.getWzFile("Item.wz", true, false);
			MapleDataProvider equipData = MapleDataProviderFactory.getWzFile("Character.wz", true, false);
			MapleDataDirectoryEntry root = data.getRoot();
			MapleDataDirectoryEntry topDir;
			switch (invType) {
				case EQUIP:
					//topDir = equipData.getRoot();
					return;//:D
				case USE:
					topDir = root.getDirectoryEntry("Consume");
					break;
				case SETUP:
					topDir = root.getDirectoryEntry("Install");
					break;
				case ETC:
					topDir = root.getDirectoryEntry("Etc");
					break;
				case CASH:
					topDir = root.getDirectoryEntry("Cash");
					break;
				default:
					return;//:D
			}
			MapleData stringTopDir = null;

			if (topDir.getName().equals("Install")) {
				stringTopDir = strings.getData("Ins.img");
			} else if (topDir.getName().equals("Consume")) {
				stringTopDir = strings.getData("Consume.img");
			} else if (topDir.getName().equals("Cash")) {
				stringTopDir = strings.getData("Cash.img");
			} else if (topDir.getName().equals("Etc")) {
				stringTopDir = strings.getData("Etc.img").getChildByPath("Etc");
			} else if (topDir.getName().equals("Pet")) {
				stringTopDir = strings.getData("Pet.img");
			} else if (topDir.getName().equals("Special")) {
				return;//oh well
			} else if (topDir.getName().equals("Character.wz")) {
				stringTopDir = strings.getData("Eqp.img");
			}

			for (MapleDataFileEntry iFile : topDir.getFiles()) {
				/**
				 * Mesos & MaplePoints
				if(iFile.getName().equals("0900.img") || iFile.getName().equals("MaplePoint.img"))
				{
				continue;
				}
				for(MapleData item : data.getData(topDir.getName() +"/"+ iFile.getName()).getChildren())
				{
				String itemname = theItemName(item.getName());
				String a = MapleDataTool.getString("name", item, item.getName());
				items.add(new ShopItem(a, new ImageIcon()));
				}	*/
					MapleData itemsData = data.getData(topDir.getName() + "/" + iFile.getName());
					for (MapleData item : itemsData.getChildren()) {
						String itemname = item.getName();
						for (int i = 0; i < itemname.length(); i++) {
							if (itemname.charAt(i) != '0') {
								itemname = itemname.substring(i);
								break;
							}
						}

						String name = MapleDataTool.getString(itemname + "/name", stringTopDir, item.getName());

						MapleData imgData = item.getChildByPath("info/icon");
						BufferedImage img = null;
						try {
							if (imgData.getType() == MapleDataType.UOL) {
								imgData = imgData.getChildByPath("../" + MapleDataTool.getString(imgData));
							}

							img = MapleDataTool.getImage(imgData);

							if (img == null) {
								img = ImageIO.read(new File("error.gif"));
							}
						} catch (Exception e) {
							System.out.println("Unable to load image");
							e.printStackTrace();
						}

						sItems.add(new ShopItem(name, Integer.parseInt(itemname), new ImageIcon(img)));
					}
			}
			items.put(invType, sItems);
		}
		this.jItemCount2.setText("Items: " + items.get(invType).size());
		this.jList2.setCellRenderer(new ShopItemListCellRenderer());
		this.jList2.setListData(sItems.toArray());
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jItemCount1;
    private javax.swing.JLabel jItemCount2;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}