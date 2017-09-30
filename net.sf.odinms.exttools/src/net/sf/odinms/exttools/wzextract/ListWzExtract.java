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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.wz.ListWZFile;

public class ListWzExtract {

    private static PrintWriter logStream = null;

    public ListWzExtract() throws Exception {

    }

    public static void main(String args[]) {
	long startTime = System.currentTimeMillis();

	try {
	    logStream = new PrintWriter(new File("ListWzData.txt"));
	    ListWZFile listWzFile = new ListWZFile(MapleDataProviderFactory.fileInWZPath("List.wz"));
	    
	    for (String entry : listWzFile.getEntries()) {
		outputWithLogging(entry);
	    }
	    logStream.close();

	} catch (IOException e) {
	    System.out.println("ERROR");
	    System.exit(0);
	}

	long endTime = System.currentTimeMillis();
	double elapsedSeconds = (endTime - startTime) / 1000.0;
	int elapsedSecs = (((int) elapsedSeconds) % 60);
	int elapsedMinutes = (int) (elapsedSeconds / 60.0);

	System.out.println("Finished in " + elapsedMinutes + " minutes " + elapsedSecs + " seconds");
    }

    public static void outputWithLogging(String buff) {
	System.out.println(buff);

	try {
	    logStream.write(buff + "\r\n");
	    logStream.flush();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
