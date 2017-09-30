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

package net.sf.odinms.tools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;

/**
 *
 * @author Raz
 */
public class MapRender {

	private int w;
	private int h;
	private int cX;
	private int cY;
	private Map<String, BufferedImage> loadedImages = new HashMap<String, BufferedImage>();
	private Map<String, MapleData> loadedImgFiles = new HashMap<String, MapleData>();
	private Map<Integer, List<MapleLayerObject>> tileMap = new HashMap<Integer, List<MapleLayerObject>>();
	private Map<Integer, List<MapleLayerObject>> objMap = new HashMap<Integer, List<MapleLayerObject>>();
	private static MapRender instance;

	private MapRender() {
	}

	public static MapRender getInstance() {
		if (instance == null) {
			instance = new MapRender();
		}
		return instance;
	}

	public boolean renderAndSaveMap(int mapid) {
		try {
			ImageIO.write(renderMap(mapid), "PNG", new File(StringUtil.getLeftPaddedStr(Integer.toString(mapid), '0', 9) + ".png"));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public BufferedImage renderMap(int mapid) {
		MapleData mapData = getMapData(mapid);
		BufferedImage mapImage = getMapRender(mapData);
		return mapImage;
	}

	public BufferedImage renderMap(MapleData mapData) {
		BufferedImage mapImage = getMapRender(mapData);
		return mapImage;
	}

	private MapleData getMapData(int mapid) {
		String mapName = getMapName(mapid);
		return MapleDataProviderFactory.getWzFile("Map.wz").getData(mapName);
	}

	private String getMapName(int mapid) {
		String mapName = StringUtil.getLeftPaddedStr(Integer.toString(mapid), '0', 9);
		StringBuilder builder = new StringBuilder("Map/Map");
		int area = mapid / 100000000;
		builder.append(area);
		builder.append("/");
		builder.append(mapName);
		builder.append(".img");
		mapName = builder.toString();
		return mapName;
	}

	private BufferedImage getMapRender(MapleData data) {
                if (data == null) {
                    System.out.println("Invalid map data provided for Map Render");
                    return null;
                }
		w = 0;
		h = 0;
		cX = 0;
		cY = 0;
		MapleDataProvider mapDataProv = MapleDataProviderFactory.getWzFile("Map.wz", true, false);
		MapleDataProvider reactorDataProv = MapleDataProviderFactory.getWzFile("Reactor.wz", true, false);
		MapleDataProvider npcDataProv = MapleDataProviderFactory.getWzFile("Npc.wz", true, false);
		MapleDataProvider mobDataProv = MapleDataProviderFactory.getWzFile("Mob.wz", true, false);
		MapleData miniMapData = data.getChildByPath("miniMap");

		if (miniMapData != null) {
			w = MapleDataTool.getInt("miniMap/width", data, 0);
			h = MapleDataTool.getInt("miniMap/height", data, 0);
			cX = MapleDataTool.getInt("miniMap/centerX", data, 0);
			cY = MapleDataTool.getInt("miniMap/centerY", data, 0);
		} else {
			MapleData mapInfoData = data.getChildByPath("info");
			int left = MapleDataTool.getInt("VRLeft", mapInfoData, 0);
			int top = MapleDataTool.getInt("VRTop", mapInfoData, 0);
			int right = MapleDataTool.getInt("VRRight", mapInfoData, 0);
			int bottom = MapleDataTool.getInt("VRBottom", mapInfoData, 0);
			w = right - left;
			h = bottom - top;
			cX = -1 * left;
			cY = -1 * top;
		}
		if (w == 0 || h == 0) {
			System.out.println("Cannot render this map");
			return null;//No Map Dimensions
		}

		BufferedImage mapImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D map = mapImage.createGraphics();

		/*MapleData backDir = data.getChildByPath("back");
		back:
		for (MapleData backData : backDir.getChildren()) {
		int ani = MapleDataTool.getInt("ani", backData, 0);
		int type = MapleDataTool.getInt("type", backData);
		int front = MapleDataTool.getInt("front", backData, 0);
		boolean flip = MapleDataTool.getInt("f", backData, 0) > 0;
		int num = MapleDataTool.getInt("num", backData, 0);
		//MapleData backImageData = mapDataProv.getData("Back/" + MapleDataTool.getString(backData, "bS") + ".img");
		MapleData backImageData = mapDataProv.getData("Back/tutorial_jp.img");
		if (backImageData == null) {
		}
		backImageData = backImageData.getChildByPath("back/0");
		Point origin = MapleDataTool.getPoint("origin", backImageData);
		BufferedImage backImage = MapleDataTool.getImage(backImageData);
		Point pos = new Point(MapleDataTool.getInt("x", backData), MapleDataTool.getInt("y", backData));
		MapleLayerObject mlo = new MapleLayerObject(pos, origin, backImage, false);
		mlo.drawImage(map);
		}*/

		layer:
		for (MapleData layerData : data.getIntegerNamedChildren()) {
			objMap.clear();
			tileMap.clear();
			MapleData tileInfoData = null;
			MapleData objImageData = null;
			MapleData layerInfoData = layerData.getChildByPath("info");
			MapleData layerTileData = layerData.getChildByPath("tile");
			MapleData layerObjData = layerData.getChildByPath("obj");

			tile:
			for (MapleData tileData : layerTileData.getChildren()) {
				tileInfoData = getData(mapDataProv, "Tile/" + MapleDataTool.getString("tS", layerInfoData) + ".img");
				MapleData tileImageData = tileInfoData.getChildByPath(MapleDataTool.getString("u", tileData) + "/" + MapleDataTool.getInt("no", tileData));
				BufferedImage tileImage = getImage(tileImageData);
				Point origin = MapleDataTool.getPoint("origin", tileImageData);
				Point pos = new Point(MapleDataTool.getInt("x", tileData), MapleDataTool.getInt("y", tileData));
				int key = MapleDataTool.getInt("z", tileImageData);
				MapleLayerObject mlo = new MapleLayerObject(pos, origin, tileImage, false);
				if (!tileMap.containsKey(key)) {
					tileMap.put(key, new ArrayList<MapleLayerObject>());
				}
				tileMap.get(key).add(mlo);

			}

			obj:
			for (MapleData objData : layerObjData.getChildren()) {
				objImageData = getData(mapDataProv, "Obj/" + MapleDataTool.getString("oS", objData) + ".img");//////FIXXXX MEEEE!!!
				objImageData = objImageData.getChildByPath(MapleDataTool.getString("l0", objData));
				objImageData = objImageData.getChildByPath(MapleDataTool.getString("l1", objData));
				objImageData = objImageData.getChildByPath(MapleDataTool.getString("l2", objData));
				objImageData = objImageData.getChildByPath("0");
				BufferedImage objImage = getImage(objImageData);
				Point origin = MapleDataTool.getPoint("origin", objImageData);
				Point pos = new Point(MapleDataTool.getInt("x", objData), MapleDataTool.getInt("y", objData));
				int key = MapleDataTool.getInt("z", objData);
				boolean flip = MapleDataTool.getInt("f", objData) > 0;
				MapleLayerObject mlo = new MapleLayerObject(pos, origin, objImage, flip);
				if (!objMap.containsKey(key)) {
					objMap.put(key, new ArrayList<MapleLayerObject>());
				}
				objMap.get(key).add(mlo);
			}

			for (int layer : objMap.keySet()) {
				for (MapleLayerObject mlo : objMap.get(layer)) {
					mlo.drawImage(map);
				}
			}

			for (int layer : tileMap.keySet()) {
				for (MapleLayerObject mlo : tileMap.get(layer)) {
					mlo.drawImage(map);
				}
			}
		}



		MapleData reactorDir = data.getChildByPath("reactor");
		if (reactorDir != null) {
			reactor:
			for (MapleData reactorData : reactorDir.getChildren()) {//Reactors
				try {
					MapleData reactorImageData = getData(reactorDataProv, MapleDataTool.getString("id", reactorData) + ".img").getChildByPath("0/0");
					boolean flip = MapleDataTool.getInt("f", reactorData) > 0;
					Point pos = new Point(MapleDataTool.getInt("x", reactorData), MapleDataTool.getInt("y", reactorData));
					Point origin = MapleDataTool.getPoint("origin", reactorImageData);
					BufferedImage reactorImage = getImage(reactorImageData);
					MapleLayerObject mlo = new MapleLayerObject(pos, origin, reactorImage, flip);
					mlo.drawImage(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		MapleData lifeDir = data.getChildByPath("life");
		if (lifeDir != null) {
			life:
			for (MapleData lifeData : lifeDir.getChildren()) {
				String type = MapleDataTool.getString("type", lifeData);
				BufferedImage lifeImage = null;
				if (type.equals("n")) {//NPCS
					try {
						boolean flip = MapleDataTool.getInt("f", lifeData, 0) > 0;
						Point pos = new Point(MapleDataTool.getInt("x", lifeData), MapleDataTool.getInt("cy", lifeData));
						MapleData lifeImageData = getData(npcDataProv, MapleDataTool.getString("id", lifeData) + ".img");
						try {
							String link = MapleDataTool.getString("info/link", lifeImageData);
							lifeImageData = getData(npcDataProv, link + ".img");
						} catch (Exception e) {
							//Link to other npc only if there is a link value
						}
						lifeImageData = lifeImageData.getChildByPath("stand/0");
						Point origin = MapleDataTool.getPoint("origin", lifeImageData);
						lifeImage = getImage(lifeImageData);
						MapleLayerObject mlo = new MapleLayerObject(pos, origin, lifeImage, flip);
						mlo.drawImage(map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (type.equals("m")) {//MOBS
					try {
						boolean flip = MapleDataTool.getInt("f", lifeData) > 0;
						Point pos = new Point(MapleDataTool.getInt("x", lifeData), MapleDataTool.getInt("cy", lifeData));
						MapleData lifeImageData = getData(mobDataProv, MapleDataTool.getString("id", lifeData) + ".img");
						try {
							String link = MapleDataTool.getString("info/link", lifeImageData);
							lifeImageData = getData(mobDataProv, link + ".img");
						} catch (Exception e) {
							//Link to other mob only if there is a link value
						}
						if (lifeImageData.getChildByPath("stand/0") != null) {
							lifeImageData = lifeImageData.getChildByPath("stand/0");
						} else {
							lifeImageData = lifeImageData.getChildByPath("fly/0");
						}
						Point origin = MapleDataTool.getPoint("origin", lifeImageData);
						lifeImage = getImage(lifeImageData);
						MapleLayerObject mlo = new MapleLayerObject(pos, origin, lifeImage, flip);
						mlo.drawImage(map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		MapleData portalDir = data.getChildByPath("portal");
		if (portalDir != null) {
			MapleData portalImageData = mapDataProv.getData("MapHelper.img").getChildByPath("portal/game/pv/0");
			BufferedImage portalImage = getImage(portalImageData);
			Point origin = MapleDataTool.getPoint("origin", portalImageData);
			portal:
			for (MapleData portalData : portalDir.getChildren()) {
				try {
					int type = MapleDataTool.getInt("pt", portalData, 0);
					if (type == 2) {
						Point pos = new Point(MapleDataTool.getInt("x", portalData), MapleDataTool.getInt("y", portalData));
						MapleLayerObject mlo = new MapleLayerObject(pos, origin, portalImage, false);
						mlo.drawImage(map);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		map.dispose();
		return mapImage;
	}

	public BufferedImage getImage(MapleData data) {
		String path = MapleDataTool.getFullDataPath(data);
		if (loadedImages.containsKey(path)) {
			return loadedImages.get(path);
		} else {
			BufferedImage image = MapleDataTool.getImage(data);
			loadedImages.put(path, image);
			return image;
		}
	}

	public MapleData getData(MapleDataProvider dataProv, String path) {
		String storedPath = dataProv.getRoot().getName() + path;
		if (loadedImgFiles.containsKey(storedPath)) {
			return loadedImgFiles.get(storedPath);
		} else {
			MapleData data = dataProv.getData(path);
			loadedImgFiles.put(storedPath, data);
			return data;
		}
	}

	private class MapleLayerObject {

		private Point objectPos;
		private Point objectOrigin;
		private BufferedImage image;
		private boolean flip;

		public MapleLayerObject(Point objectPos, Point objectOrigin, BufferedImage image, boolean flip) {
			this.objectPos = objectPos;
			this.objectOrigin = objectOrigin;
			this.image = image;
			this.flip = flip;
		}

		public void drawImage(Graphics2D canvas) {
			if (!flip) {
				canvas.drawImage(image, (objectPos.x + cX) - objectOrigin.x, (objectPos.y + cY) - objectOrigin.y, null);
			} else {
				flipImage();
				canvas.drawImage(image, (objectPos.x + cX + objectOrigin.x) - image.getWidth(), (objectPos.y + cY + objectOrigin.y) - h, null);
			}
		}

		public void flipImage() {
			// Flip the image horizontally
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-image.getWidth(null), 0);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			image = op.filter(image, null);
		}
	}
}