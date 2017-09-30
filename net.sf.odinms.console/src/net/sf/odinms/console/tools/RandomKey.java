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

import java.util.Random;

/**
 *
 * @author Raz
 */
public class RandomKey {

    public static void main(String args[]) {
	System.out.println("Snow's Random Key");
	System.out.println("Please enter a number");
	while(true) {
	    try {
		int length = Integer.parseInt(System.console().readLine());
		Random rand = new Random();
		byte[] result = new byte[length];
		rand.nextBytes(result);
		for(byte b : result) {
		    System.out.print(Integer.toHexString(b).toUpperCase() + " ");
		}
		System.out.println();
	    } catch (Exception e) {
		System.out.println("Error");
	    }
	}
    }
}
