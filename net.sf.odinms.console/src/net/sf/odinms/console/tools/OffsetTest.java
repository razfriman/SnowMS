/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.console.tools;

/**
 *
 * @author Raz
 */
public class OffsetTest {


	public static void main(String args[]) {
		asd(0xc9);
	}

		public static void asd(int encver) {
		int[] ver = new int[5];
		int[] h_ver = new int[5];
		int start_ver = -1;
		for (int i = 0; i < 5; i++) {
			ver[i] = findVersion(encver, start_ver);
			h_ver[i] = findVersionHash(encver, ver[i]);
			start_ver = ver[i];
			System.out.println(ver[i] + " " + start_ver);
		}
	}

	public static int findVersion(int encver, int start_version) {
		int sum;
		String versionStr;
		int a = 0, b = 0, c = 0, d = 0, e = 0, i = start_version, l = 0;
		do {
			i++;
			sum = 0;
			versionStr = Integer.toString(i);
			l = versionStr.length();
			for (int j = 0; j < l; j++) {
				sum <<= 5;
				sum += (int) versionStr.charAt(j) + 1;
			}
			a = (sum >> 24) & 0xFF;
			b = (sum >> 16) & 0xFF;
			c = (sum >> 8) & 0xFF;
			d = sum & 0xFF;
			e = 0xFF ^ a ^ b ^ c ^ d;
		} while (e != encver);

		return i;
	}

	public static int findVersionHash(int encver, int realver) {
		int EncryptedVersionNumber = encver;
		int VersionNumber = realver;
		int VersionHash = 0;
		int DecryptedVersionNumber = 0;
		String VersionNumberStr;
		int a = 0, b = 0, c = 0, d = 0, l = 0;

		VersionNumberStr = Integer.toString(VersionNumber);

		l = VersionNumberStr.length();
		for (int i = 0; i < l; i++) {
			VersionHash = (32 * VersionHash) + (int) VersionNumberStr.charAt(i) + 1;
		}
		a = (VersionHash >> 24) & 0xFF;
		b = (VersionHash >> 16) & 0xFF;
		c = (VersionHash >> 8) & 0xFF;
		d = VersionHash & 0xFF;
		DecryptedVersionNumber = 0xff ^ a ^ b ^ c ^ d;

		if (EncryptedVersionNumber == DecryptedVersionNumber) {
			return VersionHash;
		} else {
			return 0;
		}
	}
}
