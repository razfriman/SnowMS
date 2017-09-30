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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Matze
 */
public class SearchCache {

	private static SearchCache instance = new SearchCache();
	private Map<Integer, DataSearch> cache = new LinkedHashMap<Integer, DataSearch>();
	
	private SearchCache() {
		
	}
	
	public static SearchCache getInstance() {
		return instance;
	}

	public boolean add(DataSearch e) {
		if (cache.size() > 20) {
			DataSearch old = cache.values().iterator().next();
			cache.remove(old.hashCode());
		}
		cache.put(e.hashCode(), e);
		return true;
	}
	
	public DataSearch get(DataSearch searchparm) {
		final DataSearch search = cache.get(searchparm.hashCode());
		if (search != null && search.equals(searchparm)) {
			return search;
		}
		return null;
	}
	
	public Map<Integer, DataSearch> getCache() {
		return cache;
	}
	
}
