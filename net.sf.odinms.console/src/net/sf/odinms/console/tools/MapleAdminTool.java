/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.console.tools;

import java.net.*;

/**
 *
 * @author Raz
 */
public class MapleAdminTool {

	public static void main(String args[]) {
		try {
			int port = 7071;
			Socket s = new Socket("127.0.0.1", port);
			System.out.println(s.isConnected() ? "YES" : "NO");
			byte[] data = new byte[] {0x1, 0x1, 0x1, 0x1};
			s.getOutputStream().write(data);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
