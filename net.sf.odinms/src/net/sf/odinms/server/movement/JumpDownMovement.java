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

package net.sf.odinms.server.movement;

import java.awt.Point;

import net.sf.odinms.tools.data.output.LittleEndianWriter;

public class JumpDownMovement extends AbstractLifeMovement {
	private Point pixelsPerSecond;
        private int fh2;

	public JumpDownMovement(int type, Point position, int fh,int newstate, int duration) {
		super(type, position, fh, newstate, duration);
	}

	public Point getPixelsPerSecond() {
		return pixelsPerSecond;
	}

	public void setPixelsPerSecond(Point wobble) {
		this.pixelsPerSecond = wobble;
	}

        public int getFh2() {
            return fh2;
        }

        public void setFh2(int fh2) {
            this.fh2 = fh2;
        }

	@Override
	public void serialize(LittleEndianWriter lew) {
		lew.write(getType());
		lew.writeShort(getPosition().x);
		lew.writeShort(getPosition().y);
		lew.writeShort(pixelsPerSecond.x);
		lew.writeShort(pixelsPerSecond.y);
		lew.writeShort(getFh());
		lew.writeShort(getFh2());
		lew.write(getNewstate());
		lew.writeShort(getDuration());
	}
}
