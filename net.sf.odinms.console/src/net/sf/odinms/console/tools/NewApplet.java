/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.console.tools;

import java.applet.Applet;
import java.awt.Graphics;

/**
 *
 * @author Raz
 */
public class NewApplet extends Applet {

	@Override
	public void paint(Graphics g) {
		int x1 = 0;
		int x2 = 500;
		int y1 = 0;
		int y2 = 500;

		for(int i = 0; i < 500; i+= 3) {
		g.drawLine(x1, y1 + i, x2 - i, y1);
		g.drawLine(x2, y2 - i, x2 - i, y1);

		g.drawLine(x1 + i, y2, x2, y2 - i);
		g.drawLine(x1, y1 + i, x1 + i, y2);
		}
	}
}
