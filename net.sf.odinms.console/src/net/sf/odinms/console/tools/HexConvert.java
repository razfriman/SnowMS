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
public class HexConvert {

    private static ConvertType convertType = ConvertType.INT_TO_HEX;
    
    public static void main(String args[]) {
	System.out.println("Snow's Hex Converter");
	while(true) {
	    String input = System.console().readLine();
	    String splitted[] = input.split(" ");
	    if(splitted.length > 0) {
		if(splitted[0].equalsIgnoreCase("/mode")) {
		    System.out.println(convertType.name());
		} else if (splitted[0].equals("/quit")) {
		    System.exit(0);
		} else if (splitted[0].equals("/listmodes")) {
		    for(ConvertType convertTypeSingle : convertType.values()) {
			System.out.println("(" + convertTypeSingle.getType() + ") " + convertTypeSingle.name());
		    }
		} else if (splitted[0].equals("/setmode")) {
		    try {
			convertType = ConvertType.getByType(Integer.parseInt(splitted[1]));
			System.out.println("ConvertType set to: " + convertType.name());
		    } catch (NumberFormatException e) {
			convertType = ConvertType.getByName(splitted[1]);
			System.out.println("ConvertType set to: " + convertType.name());
		    } catch (Exception e) {
			System.out.println("Error Setting ConvertType");
		    }
		} else if (splitted[0].equalsIgnoreCase("/c")) {
		    try {
			switch(convertType) {
			    case INT_TO_HEX:
				System.out.println(Integer.toHexString(Integer.parseInt(splitted[1])));
				break;
			    case HEX_TO_INT:
				System.out.println(Integer.parseInt(splitted[1], 16));
				break;
			    default:
				break;
			}
		    } catch (NumberFormatException nfe) {
			System.out.println("Error Reading Numbers");
		    } catch (ArrayIndexOutOfBoundsException aibe) {
			System.out.println("Index Out Of Bounds");
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		} else if (splitted[0].equalsIgnoreCase("/h") || splitted[0].equalsIgnoreCase("/help") || splitted[0].equalsIgnoreCase("help") || splitted[0].equalsIgnoreCase("?")) {
		    System.out.println("Available Commands:");
		    System.out.println("/c <INPUT> - Converts input");
		    System.out.println("/setmode <MODE #/NAME> - Sets convert mode");
		    System.out.println("/listmodes - Lists all modes available");
		    System.out.println("/mode - Print current mode");
		    System.out.println("help | /help | /h | ?");
		}
	    }
	}
    }
    
    private static enum ConvertType {
	HEX_TO_INT(1),
	INT_TO_HEX(2),
	;
	final int type;
	
	private ConvertType(int type) {
	    this.type = type;
	}
	
	public int getType() {
	    return type;
	}
	
	public static ConvertType getByType(int type) {
		for (ConvertType l : ConvertType.values()) {
			if (l.getType() == type) {
				return l;
			}
		}
		return null;
	}
	
	public static ConvertType getByName(String name) {
		for (ConvertType l : ConvertType.values()) {
			if (l.name().equalsIgnoreCase(name)) {
				return l;
			}
		}
		return null;
	}
    }
}
