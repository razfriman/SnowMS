/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server.life;

/**
 *
 * @author Raz
 */
public class MapleMonsterBanishInfo {

    private String banishMessage;
    private int mapId;
    private String mapPortal;

    public MapleMonsterBanishInfo(String banMessage, int mapId, String mapPortal) {
        this.banishMessage = banMessage;
        this.mapId = mapId;
        this.mapPortal = mapPortal;
    }

    public String getBanishMessage() {
        return banishMessage;
    }

    public void setBanishMessage(String banMessage) {
        this.banishMessage = banMessage;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public String getMapPortal() {
        return mapPortal;
    }

    public void setMapPortal(String mapPortal) {
        this.mapPortal = mapPortal;
    }
}
