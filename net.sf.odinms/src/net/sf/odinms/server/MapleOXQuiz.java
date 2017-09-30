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

package net.sf.odinms.server;

import java.util.ArrayList;
import java.util.List;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;

/**
 *
 * @author Raz
 */
public class MapleOXQuiz {

	private static MapleOXQuiz instance;
	private List<List<OXQuizEntry>> entries;

	private MapleOXQuiz() {
		init();
	}

	public static MapleOXQuiz getInstance() {
		if (instance == null) {
			instance = new MapleOXQuiz();
		}
		return instance;
	}

	private void init() {
		entries = new ArrayList<List<OXQuizEntry>>();
		MapleData oxQuiz = MapleDataProviderFactory.getWzFile("Etc.wz").getData("OXQuiz.img");
		for (MapleData roundData : oxQuiz.getChildren()) {
			int round = Integer.parseInt(roundData.getName());
			List<OXQuizEntry> roundEntryList = new ArrayList<OXQuizEntry>();
			for (MapleData entryData : roundData.getChildren()) {
				int id = Integer.parseInt(entryData.getName());
				String question = MapleDataTool.getString("q", entryData);
				int answer = MapleDataTool.getInt("a", entryData);
				String response = MapleDataTool.getString("d", entryData);
				OXQuizEntry entry = new OXQuizEntry(round, id, question, answer, response);
				roundEntryList.add(entry);
			}
			entries.add(roundEntryList);
		}
	}

	public OXQuizEntry getEntry(int round, int question) {
		return entries.get(round).get(question);
	}

	public List<OXQuizEntry> getRoundEntries(int round) {
		return entries.get(round);
	}

	public class OXQuizEntry {

		private int round;
		private int id;
		private String question;
		private int answer;
		private String response;

		public OXQuizEntry(int round, int id, String question, int answer, String response) {
			this.round = round;
			this.id = id;
			this.question = question;
			this.answer = answer;
			this.response = response;
		}

		public int getAnswer() {
			return answer;
		}

		public int getId() {
			return id;
		}

		public String getQuestion() {
			return question;
		}

		public String getResponse() {
			return response;
		}

		public int getRound() {
			return round;
		}
	}
}
