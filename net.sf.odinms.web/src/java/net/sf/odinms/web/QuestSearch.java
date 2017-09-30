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

import net.sf.odinms.server.MapleItemInformationProvider;

/**
 *
 * @author Raz
 */

public class QuestSearch implements DataSearch {
	//TODO
	private String text;
	private String lowerText;
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

}
