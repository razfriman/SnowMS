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

package net.sf.odinms.tools;

/**
 *
 * @author Raz
 */
public class MapleRandom {

    private int x;
    private int y;
    private int z;

    public MapleRandom() {
        int ticks = (int) (System.nanoTime() / 10000);
        int seeder = 1170746341 * ticks - 755606699;
        seed(seeder, seeder, seeder);
    }

    public void seed(int x, int y, int z) {
        this.x = x | 0x100000;
        this.y = y | 0x1000;
        this.z = z | 0x10;
    }

    public int random() {
        x = ((x & 0xFFFFFFFE) << 12) ^ (((x << 13) ^ x) >> 19) & 0xFF;
        y = ((y & 0xFFFFFFF8) << 4) ^ (((y << 2) ^ y) >> 25) & 0xFF;
        z = ((z & 0xFFFFFFF0) << 17) ^ (((z << 3) ^ z) >> 11) & 0xFF;
        return x ^ y ^ z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
