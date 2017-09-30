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

/**
 *
 * @author Raz
 */
public class MarkNumber {

    public static void main(String args[]) {
	System.out.println("Snow's Mark Number");
	System.out.println("Please enter a number");
	while(true) {
	    try {
	    int numbers = Integer.parseInt(System.console().readLine());
	    int j = 0;
	    for(int i = 0; i <= numbers; i++) {
		System.out.print(j);
		j++;
		if(j == 10) j = 0;
	    }
	    System.out.println();
	    } catch (Exception e) {
		System.out.println("Error");
	    }
	}
    }
}
