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

package net.sf.odinms.server.attack;

import java.awt.Point;

/**
 *
 * @author Raz
 */
 public class PGMRInfo {
	private int reduction = 0;
	private int x = 0;
	private int y = 0;
	private int damage = 0;
	private int oid = 0;
	private boolean physical = false;
	
	public PGMRInfo() {

	}
	
	public PGMRInfo(int reduction, int x, int y, int damage, int oid, boolean physical) {
	    this.reduction = reduction;
	    this.x = x;
	    this.y = y;
	    this.damage = damage;
	    this.oid = oid;
	    this.physical = physical;
	}
	
	public int getReduction() {
	    return reduction;
	}
	
	public int getX() {
	    return x;
	}
	
	public int getY() {
	    return y;
	}
	
	public Point getPosition() {
	    return new Point(x, y);
	}
	
	public int getDamage() {
	    return damage;
	}
	
	public int getObjectId() {
	    return oid;
	}
	
	public boolean isPhysical() {
	    return physical;
	}
	
	public void setReduction(int reduction) {
	    this.reduction = reduction;
	}
	
	public void setX(int x) {
	    this.x = x;
	}
	
	public void setY(int y) {
	    this.y = y;
	}
	
	public void setXY(Point pos) {
	    this.x = pos.x;
	    this.y = pos.y;
	}
	
	public void setDamage(int damage) {
	    this.damage = damage;
	}
	
	public void setObjectId(int oid) {
	    this.oid = oid;
	}
	
	public void setPhysical(boolean physical) {
	    this.physical = physical;
	}
    }
