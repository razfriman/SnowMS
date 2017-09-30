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

package net.sf.odinms.client.messages.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataFileEntry;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.StringUtil;

/**
*
* @author Raz
*/
public class SearchCommands implements Command {

	private final static MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
	private final static Map<SearchQuery, List<SearchResult>> searchCache = new HashMap<SearchQuery, List<SearchResult>>();
	private final static int MAX_RESULTS = 15;
	private MapleDataProvider dataProvider = MapleDataProviderFactory.getWzFile("String.wz");

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
		if (splitted.length == 1) {
			mc.dropMessage(splitted[0] + ": <NPC> <MOB> <ITEM> <MAP> <SKILL>");
		} else {

			String type = splitted[1];
			String search = StringUtil.joinStringFrom(splitted, 2).toLowerCase();
			MapleData data = null;
			mc.dropMessage("[" + type + "] [" + search + "]");

			int searchType = -1;
			if (type.equalsIgnoreCase("NPC") || type.equalsIgnoreCase("NPCS")) {
				searchType = 0;
			} else if (type.equalsIgnoreCase("MAP") || type.equalsIgnoreCase("MAPS")) {
				searchType = 1;
			} else if (type.equalsIgnoreCase("MOB") || type.equalsIgnoreCase("MOBS") || type.equalsIgnoreCase("MONSTER") || type.equalsIgnoreCase("MONSTERS")) {
				searchType = 2;
			} else if (type.equalsIgnoreCase("REACTOR") || type.equalsIgnoreCase("REACTORS")) {
				searchType = 3;
			} else if (type.equalsIgnoreCase("ITEM") || type.equalsIgnoreCase("ITEMS")) {
				searchType = 4;
			} else if (type.equalsIgnoreCase("SKILL") || type.equalsIgnoreCase("SKILLS")) {
				searchType = 5;
			}

			List<SearchResult> results = null;
			SearchQuery query = new SearchQuery(search, searchType);
            for(SearchQuery sQuery : searchCache.keySet()) {
                if (sQuery.getSearchType() == searchType && sQuery.getSearch().equals(search)) {
                    results = searchCache.get(sQuery);
                    break;
                }
            }
			if (results == null) {
                results = new ArrayList<SearchResult>();
				switch (searchType) {
					case 0:
						data = dataProvider.getData("Npc.img");
						for (MapleData searchData : data.getChildren()) {
							int resultId = Integer.parseInt(searchData.getName());
							String resultName = MapleDataTool.getString(searchData.getChildByPath("name"), "MISSING-NAME");
							if (resultName.toLowerCase().contains(search)) {
								results.add(new SearchResult(resultId, resultName, searchType));
							}
						}
						break;
					case 1:
						data = dataProvider.getData("Map.img");
						for (MapleData mapAreaData : data.getChildren()) {
							for (MapleData mapIdData : mapAreaData.getChildren()) {
								int resultId = Integer.parseInt(mapIdData.getName());
								String resultName = MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - " + MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME");
								if (resultName.toLowerCase().contains(search)) {
									results.add(new SearchResult(resultId, resultName, searchType));
								}
							}
						}
						break;
					case 2:
						data = dataProvider.getData("Mob.img");
						for (MapleData mobIdData : data.getChildren()) {
							int resultId = Integer.parseInt(mobIdData.getName());
							String resultName = MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME");
							if (resultName.toLowerCase().contains(search)) {
								results.add(new SearchResult(resultId, resultName, searchType));
							}
						}
						break;
					case 3:
                        MapleDataProvider reactorProvider = MapleDataProviderFactory.getWzFile("Reactor.wz");
                        for (MapleDataFileEntry reactorEntry : reactorProvider.getRoot().getFiles()) {
                            MapleData reactorData = reactorProvider.getData(reactorEntry.getName());
                            int resultId = Integer.parseInt(reactorEntry.getName().replaceFirst(".img", ""));
                            String resultName = MapleDataTool.getString("action", reactorData, "NO-NAME");
                            if (resultName.toLowerCase().contains(search)) {
                                results.add(new SearchResult(resultId, resultName, searchType));
                            }
                        }
						break;
					case 4:
						for (Pair<Integer, String> itemPair : ii.getAllItems()) {
							if (itemPair.getRight().toLowerCase().contains(search)) {
								results.add(new SearchResult(itemPair.getLeft(), itemPair.getRight(), searchType));
							}
						}
						break;
					case 5:
						data = dataProvider.getData("Skill.img");
						for (MapleData skillIdData : data.getChildren()) {
							int resultId = Integer.parseInt(skillIdData.getName());
							String resultName = MapleDataTool.getString(skillIdData.getChildByPath("name"), "NO-NAME");
							if (resultName.toLowerCase().contains(search)) {
								results.add(new SearchResult(resultId, resultName, searchType));
							}
						}
				}
				searchCache.put(query, results);
			}
			outputResults(results, mc);
		}
	}

	private void outputResults(List<SearchResult> results, MessageCallback mc) {
		for (int i = 0; i < Math.min(results.size(), MAX_RESULTS); i++) {
			SearchResult result = results.get(i);
			mc.dropMessage(result.getId() + " - " + result.getName());
		}
		if (results.size() == 0) {
			mc.dropMessage("No results found");
		}
	}

	private class SearchQuery {

		private String search;
		private int searchType;

		public SearchQuery(String search, int searchType) {
			this.search = search;
			this.searchType = searchType;
		}

		public String getSearch() {
			return search;
		}

		public int getSearchType() {
			return searchType;
		}
	}

	private class SearchResult {

		private int id;
		private String name;
		private int searchType;

		public SearchResult(int id, String name, int searchType) {
			this.id = id;
			this.name = name;
			this.searchType = searchType;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getSearchType() {
			return searchType;
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("find", "", "", 100),
					new CommandDefinition("lookup", "", "", 100),
					new CommandDefinition("search", "", "", 100),};
	}
}
