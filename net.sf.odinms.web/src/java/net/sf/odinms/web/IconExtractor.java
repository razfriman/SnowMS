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

package net.sf.odinms.web;




import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataDirectoryEntry;
import net.sf.odinms.provider.MapleDataFileEntry;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.provider.wz.MapleDataType;
import net.sf.odinms.tools.MapRender;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Matze
 */
public class IconExtractor extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static MapleDataProvider itemDataProv = MapleDataProviderFactory.getWzFile("Item.wz", true, false);
	private static MapleDataProvider equipDataProv = MapleDataProviderFactory.getWzFile("Character.wz", true, false);
	private static MapleDataProvider mobDataProv = MapleDataProviderFactory.getWzFile("Mob.wz", true, false);
	private static MapleDataProvider npcDataProv = MapleDataProviderFactory.getWzFile("Npc.wz", true, false);
	private static MapleDataProvider mapDataProv = MapleDataProviderFactory.getWzFile("Map.wz", true, false);
	private static MapleDataProvider skillDataProv = MapleDataProviderFactory.getWzFile("Skill.wz", true, false);
	private static MapleDataProvider reactorDataProv = MapleDataProviderFactory.getWzFile("Reactor.wz", true, false);
	private static MapleDataProvider morphDataProv = MapleDataProviderFactory.getWzFile("Morph.wz", true, false);

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * @param request servlet request
	 * @param response servlet response
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OutputStream out = response.getOutputStream();
		try {
			MapleData imgData = null;
			BufferedImage img = null;

			if (request.getParameter("monster") != null) {
				MapleData monsterData = mobDataProv.getData(StringUtil.getLeftPaddedStr(request.getParameter("monster"), '0', 7) + ".img");
				try {
					String link = MapleDataTool.getString("info/link", monsterData);
					monsterData = mobDataProv.getData(link + ".img");
				} catch (Exception e) {
					//Link to other mob only if there is a link value
				}
				if (monsterData.getChildByPath("stand/0") != null) {
					imgData = monsterData.getChildByPath("stand/0");
				} else {
					imgData = monsterData.getChildByPath("fly/0");
				}
			} else if (request.getParameter("npc") != null) {
				MapleData npcData = npcDataProv.getData(StringUtil.getLeftPaddedStr(request.getParameter("npc"), '0', 7) + ".img");
				if (npcData.getChildByPath("info/link") != null) {
					String link = MapleDataTool.getString("info/link", npcData);
					npcData = npcDataProv.getData(link + ".img");
				}
				imgData = npcData.getChildByPath("stand/0");
			} else if (request.getParameter("map") != null) {
				String idStr = StringUtil.getLeftPaddedStr(request.getParameter("map"), '0', 9);
				imgData = mapDataProv.getData("Map/Map" + idStr.charAt(0) + "/" + idStr + ".img").getChildByPath("miniMap/canvas");
			} else if (request.getParameter("mapMark") != null) {
				imgData = mapDataProv.getData("MapHelper.img").getChildByPath("mark").getChildByPath((request.getParameter("mapMark")));
			} else if (request.getParameter("skill") != null) {//BUG
				String skillIdStr = StringUtil.getLeftPaddedStr(request.getParameter("skill"), '0', 7);
				imgData = skillDataProv.getData((skillIdStr.substring(0, 3) + ".img")).getChildByPath("skill").getChildByPath(StringUtil.getLeftPaddedStr(request.getParameter("skill"), '0', 7) + "/icon");
			} else if (request.getParameter("job") != null) {
				String jobIdStr = StringUtil.getLeftPaddedStr(request.getParameter("job"), '0', 3);
				imgData = skillDataProv.getData(jobIdStr + ".img").getChildByPath("info/icon");
			} else if (request.getParameter("reactor") != null) {
				imgData = reactorDataProv.getData(StringUtil.getLeftPaddedStr(request.getParameter("reactor"), '0', 7) + ".img").getChildByPath("0/0");
			} else if (request.getParameter("morph") != null) {
				imgData = morphDataProv.getData(StringUtil.getLeftPaddedStr(request.getParameter("morph"), '0', 4) + ".img").getChildByPath("stand/0");
			} else if (request.getParameter("id") != null) {
				int itemId = Integer.parseInt(request.getParameter("id"));
				MapleData data = getItemData(itemId);
				if (data != null) {
					imgData = data.getChildByPath("info/icon");
				}
			} else if (request.getParameter("maprender") != null) {
				try {
					int mapid = Integer.parseInt(request.getParameter("maprender"));
					img = MapRender.getInstance().renderMap(mapid);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (request.getParameter("path") != null) {
				String path = request.getParameter("path");
				String[] paths = path.split("/");
				int imgFileIndex = 1;
				for (int i = 0; i < paths.length; i++) {
					if (paths[i].endsWith(".img")) {
						imgFileIndex = i;
						break;
					}
				}
				MapleDataProvider dataProv = MapleDataProviderFactory.getWzFile(paths[0], true, false);
				imgData = dataProv.getData(paths[imgFileIndex]).getChildByPath(StringUtil.joinStringFrom(paths, imgFileIndex + 1, "/"));
			}
			response.setContentType("image/png");

			if (img != null) {
				ImageIO.write(img, "png", out);
			} else if (imgData != null) {
				if (imgData.getType() == MapleDataType.UOL) {
					imgData = ((MapleData) imgData.getParent()).getChildByPath(MapleDataTool.getString(imgData));
				}
				ImageIO.write(MapleDataTool.getImage(imgData), "png", out);
			}
		} finally {
			out.close();
		}
	}

	protected MapleData getItemData(int itemId) {
		MapleData ret = null;
		String idStr = "0" + String.valueOf(itemId);
		MapleDataDirectoryEntry root = itemDataProv.getRoot();
		for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
			// we should have .img files here beginning with the first 4 IID
			for (MapleDataFileEntry iFile : topDir.getFiles()) {
				if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
					ret = itemDataProv.getData(topDir.getName() + "/" + iFile.getName());
					if (ret == null) {
						return null;
					}
					ret = ret.getChildByPath(idStr);
					return ret;
				} else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
					return itemDataProv.getData(topDir.getName() + "/" + iFile.getName());
				}
			}
		}
		root = equipDataProv.getRoot();
		for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
			for (MapleDataFileEntry iFile : topDir.getFiles()) {
				if (iFile.getName().equals(idStr + ".img")) {
					return equipDataProv.getData(topDir.getName() + "/" + iFile.getName());
				}
			}
		}
		return ret;
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/** 
	 * Handles the HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/** 
	 * Handles the HTTP <code>POST</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/** 
	 * Returns a short description of the servlet.
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}
	// </editor-fold>
}
