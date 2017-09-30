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

package net.sf.odinms.exttools.wzextract;

public class XmlUtil {

	private static final char[] specialCharacters = {'"', '\'', '&', '<', '>'};
	private static final String[] replacementStrings = {"&quot;", "&apos;", "&amp;", "&lt;", "&gt;"};

	public static String sanitizeText(String text) {
		StringBuffer buffer = new StringBuffer(text);

		for (int i = 0; i < buffer.length(); i++) {
			for (int k = 0; k < specialCharacters.length; k++) {
				if (buffer.charAt(i) == specialCharacters[k]) {
					buffer.replace(i, i + 1, replacementStrings[k]);
					i += replacementStrings[k].length() - 1;
				}
			}
		}
		return buffer.toString();
	}

	public static String unsanitizeText(String text) {
		StringBuffer buffer = new StringBuffer(text);
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < buffer.length(); i++) {
			if (buffer.charAt(i) == '&' && indexOf(buffer, ';', i) > 0) {
				String sanitized = buffer.substring(i, indexOf(buffer, ';', i));
				if (sanitized.startsWith("&#")) {
					String charString = sanitized.replaceAll("&#", "");
					charString = charString.replaceAll(";", "");
					ret.append((char) Integer.parseInt(charString));
				} else {
					for (int j = 0; j < replacementStrings.length; j++) {
						if (sanitized.equals(replacementStrings[j])) {
							ret.append(specialCharacters[j]);
							break;
						}
					}
				}
				i += sanitized.length();
			} else {
				ret.append(buffer.charAt(i));
			}
		}
		return ret.toString();
	}

	public static int indexOf(StringBuffer buffer, char chr, int indexStart) {
		for (int i = indexStart; i < buffer.length(); i++) {
			if (buffer.charAt(i) == chr) {
				return i;
			}
		}
		return -1;
	}
}
