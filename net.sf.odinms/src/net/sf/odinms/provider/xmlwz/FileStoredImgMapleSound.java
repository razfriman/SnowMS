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

package net.sf.odinms.provider.xmlwz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.odinms.provider.MapleSound;

/**
 *
 * @author Raz
 */
public class FileStoredImgMapleSound implements MapleSound {

    private File file;
    private byte[] soundData;

    public FileStoredImgMapleSound(File fileIn) {
	this.file = fileIn;
    }

    @Override
    public byte[] getSoundData() {
	loadSoundIfNescessary();
	return soundData;
    }

    private void loadSoundIfNescessary() {
	if (soundData == null) {
	    try {
	    RandomAccessFile raf = new RandomAccessFile(file, "r");
	    soundData = new byte[(int) raf.length()];
	    raf.readFully(soundData);
	    raf.close();
	    } catch (FileNotFoundException fnfe) {
		fnfe.printStackTrace();
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	}
    }
}
