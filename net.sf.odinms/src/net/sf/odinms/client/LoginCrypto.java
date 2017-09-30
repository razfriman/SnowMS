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

package net.sf.odinms.client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import net.sf.odinms.tools.HexTool;

/**
 * 
 * @author Frz
 */
public class LoginCrypto {
	
	private LoginCrypto() {
	    
	}

	/**
	 * 
	 * @param bytes
	 * @return the bytes in a string without any spaces in lowercase
	 */
	private static String toSimpleHexString(byte[] bytes) {
		return HexTool.toString(bytes).replace(" ", "").toLowerCase();
	}
	
	/**
	 * 
	 * @param in
	 * @param digest
	 * @return a hash with the input and digest
	 */
	private static String hashWithDigest(String in, String digest) {
		try {
			MessageDigest Digester = MessageDigest.getInstance(digest);
			Digester.update(in.getBytes("UTF-8"), 0, in.length());
			byte[] sha1Hash = Digester.digest();
			return toSimpleHexString(sha1Hash);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Hashing the password failed", ex);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Encoding the string failed", e);
		}
		
	}

	/**
	 * 
	 * @param in
	 * @return in encrypted in sha1
	 */
	private static String hexSha1(String in) {
		return hashWithDigest(in, "SHA-1");
	}
	
	/**
	 * 
	 * @param in
	 * @return in encrypted in sha512
	 */
	public static String hexSha512(String in) {
		return hashWithDigest(in, "SHA-512");
	}

	/**
	 * 
	 * @param hash
	 * @param password
	 * @return checks the hash against the password encrypyed with sha1
	 */
	public static boolean checkSha1Hash(String hash, String password) {
		return hash.equals(hexSha1(password));
	}

	/**
	 * 
	 * @param hash
	 * @param password
	 * @return checks the hash against the password encrypyed with sha512 and the salt
	 */
	public static boolean checkSaltedSha512Hash(String hash, String password, String salt) {
		return hash.equals(makeSaltedSha512Hash(password, salt));
	}

	/**
	 * 
	 * @param password
	 * @param salt
	 * @return encrypt password with sha512 and a salt
	 */
	public static String makeSaltedSha512Hash(String password, String salt) {
		return hexSha512(password + salt);
	}

	/**
	 * 
	 * @return a new random salt
	 */
	public static String makeSalt() {
		byte[] salt = new byte[16];
		new Random().nextBytes(salt);
		return toSimpleHexString(salt);
	}
}
