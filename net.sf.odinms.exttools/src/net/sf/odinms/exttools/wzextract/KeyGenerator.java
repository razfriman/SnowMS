/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.exttools.wzextract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import net.sf.odinms.tools.BitTools;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.MapleAESOFB;
import net.sf.odinms.tools.PropertyTool;

/**
 *
 * @author Raz
 */
public class KeyGenerator {

	public static void main(String args[]) {
		Properties settings = new Properties();
		FileOutputStream fos;
		Cipher cipher;
		int length;
		byte[] key;
		try {//LOAD SETTINGS
			FileInputStream inputStream = new FileInputStream("settings.properties");
			settings.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			System.out.println("Cannot Find File - settings.properties");
			return;
		}
		PropertyTool propTool = new PropertyTool(settings);

		String ivStr = propTool.getSettingStr("IV", null);
		String outputFile = propTool.getSettingStr("OUTPUT_FILE", null);
		length = propTool.getSettingInt("KEY_LENGTH", 4);

		try {//LOAD FILES
			key = HexTool.getByteArrayFromHexString(ivStr);
			//zlz.dll 0x10040
			//key = HexTool.getByteArrayFromHexString("4D 23 C7 2B");//GMS
			//key = HexTool.getByteArrayFromHexString("B9 7D 63 E9");//SEA
			cipher = Cipher.getInstance("AES");
			SecretKeySpec skeySpec = new SecretKeySpec(MapleAESOFB.MAPLE_AES_KEY, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		} catch (Exception e) {
			System.out.println("Error Initiating Cipher");
			e.printStackTrace();
			return;
		}

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] iv = BitTools.multiplyBytes(key, 4, 4);
			for (int i = 0; i < length; i += 16) {
				iv = cipher.doFinal(iv, 0, 16);
				baos.write(iv, 0, 16);
			}
			File file = new File(outputFile);
			fos = new FileOutputStream(file);
			fos.write(baos.toByteArray(), 0, length);
			System.out.println("Generated Key: " + baos.size() + " bytes");
		} catch (Exception e) {
			System.out.println("Error Generating Key");
			e.printStackTrace();
			return;
		}
	}
}
