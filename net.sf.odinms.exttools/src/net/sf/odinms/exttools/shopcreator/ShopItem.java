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

import javax.swing.Icon;

/**
 *
 * @author andy
 */
public class ShopItem {

    private String value;
    private int itemId;
    private Icon image;
    private int price = -1;

    public ShopItem(String value, int itemId, Icon image) {
	this.value = value;
	this.itemId = itemId;
	this.image = image;
    }

    public String getValue() {
	return value;
    }

    public Icon getImage() {
	return image;
    }

    public int getPrice() {
	return price;
    }

    public void setPrice(int price) {
	this.price = price;
    }

    public int getItemId() {
	return itemId;
    }
}
