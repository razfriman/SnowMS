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

package net.sf.odinms.console.tools;

import java.util.Scanner;

import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Raz
 */
public class MaskConvert {
   
    public static void main(String args[]) {
	System.out.println("Snow's Mask Convert");
	System.out.println("Please enter a Buff-Mask");
	Scanner in = new Scanner(System.in);
	long mask = 0;
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	while(true) {
	    try {
	    String input = System.console().readLine();
	    if(input.equalsIgnoreCase("clear")) {
		mask = 0;
		System.out.println("Mask Is Reset To (" + mask + ")");
	    } else {
	    mask |= Long.decode(input);
	    mplew.writeLong(mask);
	    System.out.println("V55: " + HexTool.toString(mplew.getPacket().getBytes()));
	    mplew.clear();
	    mplew.writeInt((int)((mask >> 32) & 0xffffffffL)); 
	    mplew.writeInt((int)(mask & 0xffffffffL));
	    System.out.println("V59: " + HexTool.toString(mplew.getPacket().getBytes()));
	    mplew.clear();
	    }
	    } catch (Exception e) {
		System.out.println("Error");
	    }
	}
    }
}
