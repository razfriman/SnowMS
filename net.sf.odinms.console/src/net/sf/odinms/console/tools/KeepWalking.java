/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.console.tools;

/**
 *
 * @author Raz
 */
public class KeepWalking {

	public static void main(String args[]) {
		int x = 1;
		int y = 1;
		int oldA = 1;

		while (x != 526) {
		int answer = x * y + oldA + 3;
		x++;
		y++;
		oldA = answer;
		System.out.println(x + ": " + answer);
		}
	}
}
