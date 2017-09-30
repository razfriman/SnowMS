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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.Deflater;

import java.util.zip.DeflaterOutputStream;
import javax.imageio.ImageIO;

import net.sf.odinms.provider.MapleCanvas;

public class FileStoredPngMapleCanvas implements MapleCanvas {

	private File file;
	private int width;
	private int height;
	private int format;
	private int size;
	private BufferedImage image;

	public FileStoredPngMapleCanvas(int width, int height, File fileIn) {
		this.width = width;
		this.height = height;
		this.file = fileIn;
		this.format = -1;
		this.size = -1;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	@Override
	public BufferedImage getImage() {
		loadImageIfNescessary();
		return image;
	}

	public byte[] getWzData() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] wzBuff = new byte[size];
		
		format = 2;//Format 2 allows for a larger range of colors
		
		if (getFormat() == 1) {
			wzBuff = new byte[width * height * 4];
			int curPos = 0;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					int pixelCol = image.getRGB(j, i);
					int a = (pixelCol >>> 24) & 0xff;
					int r = (pixelCol >>> 16) & 0xff;
					int g = (pixelCol >>> 8) & 0xff;
					int b = pixelCol & 0xff;
					wzBuff[curPos] = (byte)(((b | (b << 4)) & 15) + ((g | (g >> 4)) & 240));
					wzBuff[curPos + 1] = (byte)(((r | (r << 4)) & 15) + ((a | (a >> 4)) & 240));
					curPos += 2;
				}
			}
		} else if (getFormat() == 2) {
			wzBuff = new byte[width * height * 8];
			int curPos = 0;
			for (int i = 0; i < height; i++)
				for (int j = 0; j < width; j++)
				{
					int pixelCol = image.getRGB(j, i);
					int a = (pixelCol >>> 24) & 0xff;
					int r = (pixelCol >>> 16) & 0xff;
					int g = (pixelCol >>> 8) & 0xff;
					int b = pixelCol & 0xff;
					wzBuff[curPos] = (byte) b;
					wzBuff[curPos + 1] = (byte) g;
					wzBuff[curPos + 2] = (byte) r;
					wzBuff[curPos + 3] = (byte) a;
					curPos += 4;
				}
		} else if (getFormat() == 513) {
			//TODO
			int x = 0;
			int y = 0;
			wzBuff = new byte[width * height * 4];
			int curPos = 0;
			for (int i = 0; i < wzBuff.length; i += 2) {
				if (x == width) {
					x = 0;
					y++;
					if (y == height) {
						break;
					}
				}
			}
		} else if (getFormat() == 517) {
			wzBuff = new byte[width * height / 128];
			int curPos = 0;
		}

		try {
			Deflater d = new Deflater();
			DeflaterOutputStream dout = new DeflaterOutputStream(baos, d);
			dout.write(wzBuff);
			dout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	public void loadImageIfNescessary() {
		if (image == null) {
			try {
				image = ImageIO.read(file);
				width = image.getWidth();
				height = image.getHeight();
				size = ((DataBufferByte) getImage().getRaster().getDataBuffer()).getSize();

				if (size == getHeight() * getWidth() * 4) {
					format = 1;
				} else if (size == getHeight() * getWidth() * 8) {
					format = 2;
				} else if (size == getHeight() * getWidth() / 128) {
					format = 517;
				}
			} catch (IOException e) {
				throw new RuntimeException("Error on: " + file.getAbsolutePath(), e);
			}
		}
	}
}
