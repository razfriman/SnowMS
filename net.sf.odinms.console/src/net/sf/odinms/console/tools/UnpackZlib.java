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

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 *
 * @author Raz
 */
public class UnpackZlib {

    
     public static void main(String args[]) {
	 System.out.println("Snow's Zlib Unpacker");
	try {
	    File in = new File(args[0]);
	    File out = new File(args[1]);
	    out.createNewFile();
	    RandomAccessFile raf = new RandomAccessFile(in, "r");
	    byte[] data = new byte[(int) raf.length()];
	    raf.readFully(data);	    
	    Inflater inflater = new Inflater();
	    inflater.setInput(data);
	    data = new byte[(int) raf.length() * 2];
	    int len = inflater.inflate(data);
	    inflater.end();
	    FileOutputStream outStream = new FileOutputStream(out, false);
	    outStream.write(data, 0, len);
	    outStream.close();
	    System.out.println("Unpacked " + in.getName() + " To " + out.getName());
	} catch ( DataFormatException ex) {
	    throw new RuntimeException("ZLib fucked", ex);
	} catch (Exception e) {
	    System.out.println("Error: Unpacking Patch Data");
	    System.out.println(e.getMessage());
	    e.printStackTrace();
	}
    }
}
