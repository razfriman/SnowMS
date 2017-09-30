/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.exttools.wzextract;

import net.sf.odinms.tools.MapRender;

/**
 *
 * @author Raz
 */
public class MapRenderer {

	private static MapRender mapMaker = MapRender.getInstance();

	public static void main(String args[]) {

		for (String arg : args) {
			try {
				int mapid = Integer.parseInt(arg);
				render(mapid);
			} catch (Exception e) {
				System.out.println("Invalid MapID");
			}
		}
		while (true) {
			try {
				int mapid = Integer.parseInt(System.console().readLine());
				render(mapid);
			} catch (Exception e) {
				System.out.println("Invalid MapID");
			}
		}
	}

	public static void render(int mapid) {
		try {
			System.out.println("Rendering Map: " + mapid);
			long startTime = System.currentTimeMillis();
			if (!mapMaker.renderAndSaveMap(mapid)) {
				System.out.println("Error Rendering Map");
			}
			System.out.println("Finished Rendering Map In: " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			System.out.println("Error Rendering Map");
			e.printStackTrace();
		}
	}
}
