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

import java.util.ArrayList;
import java.util.List;

import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Matze
 */
public class ItemSearch implements DataSearch {
	private String text;
	private String lowerText;
	private MapleInventoryType type;
	private boolean searchDescription;
	private List<Integer> results = new ArrayList<Integer>();
	private MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
	

	public List<Integer> executeQuery() {
		results.clear();
		lowerText = text.toLowerCase();
		filterSearch();
		SearchCache.getInstance().add(this);
		return results;
	}
	
	private void filterSearch() {
		    List<Pair<Integer, String>> allItems =  ii.getAllItems();
		    for(Pair<Integer, String> item : allItems) {
				int itemId = item.getLeft();
				boolean stop = false;
				if (text != null) {
					final String name = item.getRight();
					stop = !name.toLowerCase().contains(lowerText);
					if (searchDescription) {
						MapleData data = ii.getStringData(itemId);
						final String desc = MapleDataTool.getString("desc", data, "");
						stop = stop && !desc.toLowerCase().contains(lowerText);
					}}
				
				if(type != null && type != MapleInventoryType.UNDEFINED && ii.getInventoryType(itemId) != type)
				    stop = true;
				
				if(!stop)
				    results.add(itemId);
		    }
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Integer> getResults() {
		return results;
	}

	public MapleInventoryType getType() {
		return type;
	}

	public void setType(MapleInventoryType type) {
		this.type = type;
	}
	
	public void setSearchDescription(boolean searchDescription) {
		this.searchDescription = searchDescription;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ItemSearch)) return false;
		ItemSearch o = (ItemSearch) obj;
		return (text == null && o.text == null || text != null && o.text != null && text.equals(o.text)) &&
			type == o.type && searchDescription == o.searchDescription;
	}

	@Override
	public int hashCode() {
		return (text != null ? text.hashCode() : 0) + type.hashCode() + (searchDescription ? 1 : 2);
	}

	@Override
	public String toString() {
		StringBuilder sInfo = new StringBuilder("Item [");
		if (text != null && text.length() > 0) {
			if (sInfo.length() > 6) sInfo.append(",");
			sInfo.append("text=");
			sInfo.append(text);
		}
		if (type != null) {
			if (sInfo.length() > 6) sInfo.append(",");
			sInfo.append("type=");
			sInfo.append(type);			
		}
		sInfo.append("]");
		return sInfo.toString();
	}
	
}
