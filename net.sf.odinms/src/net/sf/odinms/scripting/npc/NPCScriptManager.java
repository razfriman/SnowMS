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

package net.sf.odinms.scripting.npc;

import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.scripting.AbstractScriptManager;
import net.sf.odinms.server.life.MapleNPC;

/**
 *
 * @author Matze
 */
public class NPCScriptManager extends AbstractScriptManager {

    private Map<MapleClient, NPCConversationManager> cms = new HashMap<MapleClient, NPCConversationManager>();
    private Map<MapleClient, NPCScript> scripts = new HashMap<MapleClient, NPCScript>();
    private Map<MapleClient, Thread> scriptThreads = new HashMap<MapleClient, Thread>();
    private static NPCScriptManager instance = new NPCScriptManager();

    public synchronized static NPCScriptManager getInstance() {
	  return instance;
    }

    public boolean isRunningNPC(MapleClient c) {
	  return cms.containsKey(c);
    }

    public void start(MapleClient c, MapleNPC npc) {
	  start(c, npc.getId(), npc.getStats().getScript());
    }

    public void start(MapleClient c, int npc, String script) {
	  try {
		NPCConversationManager cm = new NPCConversationManager(c, npc, script);
		NPCScriptManager npcsm = NPCScriptManager.getInstance();
		if (cms.containsKey(c)) {
		    return;
		}
		cms.put(c, cm);
		Invocable iv = null;

		if (script != null) {
		    String path = "npc/" + script + ".js";
		    iv = getInvocable(path, c);
		    cm.setScriptPath(path);
		}

		if (iv == null) {
		    String path = "npc/" + npc + ".js";
		    iv = getInvocable(path, c);
		    cm.setScriptPath(path);
		}

		if (iv == null || npcsm == null) {
		    cm.dispose();
		    return;
		}
		engine.put("cm", cm);
		NPCScript ns = iv.getInterface(NPCScript.class);
		scripts.put(c, ns);
		scriptThreads.put(c, startInNewThread(ns));
	  } catch (Exception e) {
		log.error("Error executing NPC script." + "NPC-ID: (" + npc + ")", e);
		cms.remove(c);
		dispose(c);
	  }
    }

    public Thread startInNewThread(final NPCScript ns) {
	  Thread scriptThread = new Thread() {

		@Override
		public void run() {
		    try {
			  ns.start();
		    } catch (Exception e) {
			  log.error("Error running NPC script", e);
		    }
		}
	  };
	  scriptThread.start();
	  return scriptThread;
    }

    public void dispose(NPCConversationManager cm) {
	  cms.remove(cm.getC());
	  scripts.remove(cm.getC());
	  if (scriptThreads.containsKey(cm.getC())) {
		scriptThreads.remove(cm.getC()).stop();
	  }
	  if (cm.getScriptPath() != null) {
		resetContext(cm.getScriptPath(), cm.getC());
	  }
    }

    public void dispose(MapleClient c) {
	  NPCConversationManager npccm = cms.get(c);
	  if (npccm != null) {
		dispose(npccm);
	  }
    }

    public NPCConversationManager getCM(MapleClient c) {
	  return cms.get(c);
    }
}
