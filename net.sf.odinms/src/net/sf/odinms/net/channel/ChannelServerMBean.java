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

package net.sf.odinms.net.channel;


public interface ChannelServerMBean {
	void shutdown(int time);
	void shutdownWorld(int time);

	String getServerMessage();
	void setServerMessage(String serverMessage);
	
	int getChannel();
	
	int getExpRate();
	int getMesoRate();
	int getDropRate();
	int getPetExpRate();

	void setExpRate(int expRate);
	void setMesoRate(int mesoRate);
	void setDropRate(int dropRate);
	void setPetExpRate(int petExpRate);
	
	boolean allowUndroppablesDrop();
	boolean allowMoreThanOne();
	boolean allowMultiLevel();
	boolean allowReports();
	
	void setUndroppablesDrop(boolean allow);
	void setMoreThanOne(boolean allow);
	void setMultiLevel(boolean allow);


    int getMaxAP();
    void setMaxAP(int maxAP);

	int getConnectedClients();
	int getLoadedMaps();
}
