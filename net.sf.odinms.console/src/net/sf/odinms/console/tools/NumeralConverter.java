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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raz
 */
public class NumeralConverter {

	public static void main(String args[]) {
		System.out.println("Snow's Roman-Numeral Converter\r\n");
		while(true) {
			String input = System.console().readLine();
			if (input == null) {
				continue;
			}
			System.out.println(input + " :: " + RomanNumeral.getTotalValue(input));
		}
	}

	public enum RomanNumeral {
		I(1),
		V(5),
		X(10),
		L(50),
		C(100),
		D(500),
		M(1000);
		private int i;

		private RomanNumeral(int i) {
			this.i = i;
		}

		public int getValue() {
			return i;
		}
		
		public static RomanNumeral getByName(char name) {
			for (RomanNumeral n : RomanNumeral.values()) {
				if (n.name().equalsIgnoreCase(Character.toString(name))) {
					return n;
				}
			}
			return null;
		}

		public static List<RomanNumeral> parseRomanNumeral(String input) {
			List<RomanNumeral> ret = new ArrayList<RomanNumeral>();
			for(char chr : input.toCharArray()) {
				RomanNumeral num = RomanNumeral.getByName(chr);
				if (num != null) {
					ret.add(num);
				}
			}
			return ret;
		}

		public static int getTotalValue(String input) {
			List<RomanNumeral> numerals = parseRomanNumeral(input);
			int ret = 0;
			for (int i = 0; i < numerals.size(); i++) {
				RomanNumeral curNum = numerals.get(i);
				RomanNumeral nextNum = null;
				if (numerals.size() > i + 1) {
					nextNum = numerals.get(i + 1);
				}
				if (nextNum != null && curNum.getValue() < nextNum.getValue()) {
					ret += (nextNum.getValue() - curNum.getValue());
					i++;
				} else {
				ret += curNum.getValue();
				}
			}
			return ret;
		}
	}
}
