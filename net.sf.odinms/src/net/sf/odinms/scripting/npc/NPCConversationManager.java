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

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.scripting.AbstractPlayerInteraction;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Matze
 */
public class NPCConversationManager extends AbstractPlayerInteraction {

    private int npc;
    private String script;
    private NPCEvent lastEvent;
    private String text;
    private String scriptPath;

    public NPCConversationManager(MapleClient c, int npc, String script) {
	  super(c);
	  this.npc = npc;
	  this.script = script;
	  this.text = "";
	  getPlayer().setNpcScriptInfo(new NPCScriptInfo(npc, script, c));
    }

    public void dispose() {
	  NPCScriptManager.getInstance().dispose(this);
    }

    public void addText(String text) {
	  this.text += text;
    }

    public void resetText() {
	  this.text = "";
    }

    public String getText() {
	  return text;
    }

    public NPCEvent sendOk() {
	  return sendNormalDialog(false, false);
    }

    public NPCEvent sendOk(String text) {
	  this.text += text;
	  return sendNormalDialog(false, false);
    }

    public NPCEvent sendNext() {
	  return sendNormalDialog(false, true);
    }

    public NPCEvent sendNext(String text) {
	  this.text += text;
	  return sendNormalDialog(false, true);
    }

    public NPCEvent sendPrev() {
	  return sendNormalDialog(true, false);
    }

    public NPCEvent sendPrev(String text) {
	  this.text += text;
	  return sendNormalDialog(true, false);
    }

    public NPCEvent sendNextPrev() {
	  return sendNormalDialog(true, true);
    }

    public NPCEvent sendNextPrev(String text) {
	  this.text += text;
	  return sendNormalDialog(true, true);
    }

    public int sendYesNo() {
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.YES_NO, text);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getMode();
    }

    public int sendYesNo(String text) {
	  this.text += text;
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.YES_NO, text);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getMode();
    }

    public int sendAcceptDecline() {
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.ACCEPT_DECLINE, text);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getMode();
    }

	public int sendAcceptDeclineNoExit() {
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.ACCEPT_DECLINE_NO_EXIT, text);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getMode();
    }

    public int sendAcceptDecline(String text) {
	  this.text += text;
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.ACCEPT_DECLINE, text);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getMode();
    }

    public int sendSimple() {
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.SIMPLE, text);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getSelection();
    }

	public int sendQuiz(byte type, int objectId, int correct, int questions, int time) {
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.QUIZ, text);
	  mplew.write(0);
	  mplew.writeInt(type); // 0 = NPC, 1 = Mob, 2 = Item
	  mplew.writeInt(objectId);
	  mplew.writeInt(correct);
	  mplew.writeInt(questions);
	  mplew.writeInt(time);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getSelection();
	}

    public int sendSimple(String text) {
	  this.text += text;
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.SIMPLE, text);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getSelection();
    }

    public int sendStyle(int styles[]) {
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.STYLE, text);
	  mplew.write(styles.length);
	  for (int style : styles) {
		mplew.writeInt(style);
	  }
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getSelection();
    }

    public int sendStyle(String text, int styles[]) {
	  this.text += text;
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.STYLE, text);
	  mplew.write(styles.length);
	  for (int style : styles) {
		mplew.writeInt(style);
	  }
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getSelection();
    }

    public int sendGetNumber(int def, int min, int max) {
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.GET_NUMBER, text);
	  mplew.writeInt(def);
	  mplew.writeInt(min);
	  mplew.writeInt(max);
	  mplew.writeInt(0);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getSelection();
    }

    public int sendGetNumber(String text, int def, int min, int max) {
	  this.text += text;
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.GET_NUMBER, text);
	  mplew.writeInt(def);
	  mplew.writeInt(min);
	  mplew.writeInt(max);
	  mplew.writeInt(0);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getSelection();
    }

    public String sendGetText(int min, int max) {
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.GET_TEXT, text);
	  mplew.writeInt(0);
	  mplew.writeShort(max);
	  mplew.writeShort(min);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getReturnText();
    }

    public String sendGetText(String text, int min, int max) {
	  this.text += text;
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.GET_TEXT, text);
	  mplew.writeInt(0);
	  mplew.writeShort(max);
	  mplew.writeShort(min);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent().getReturnText();
    }

    public void sendDialog(NPCStateInfo state) {
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.NORMAL, state.getText());
	  mplew.write(state.isPrev() ? 1 : 0);
	  mplew.write(state.isNext() ? 1 : 0);
	  getClient().getSession().write(mplew.getPacket());
    }

    public NPCEvent sendNormalDialog(boolean prev, boolean next) {
	  getPlayer().getNpcScriptInfo().addPreviousState(new NPCStateInfo(text, prev, next));
	  MaplePacketLittleEndianWriter mplew = MaplePacketCreator.getNPCTalk(npc, NPCDialogType.NORMAL, text);
	  mplew.write(prev ? 1 : 0);
	  mplew.write(next ? 1 : 0);
	  getClient().getSession().write(mplew.getPacket());
	  return getEvent();
    }

    public NPCEvent getLastEvent() {
	  return lastEvent;
    }

    /**
     * use getPlayer().getMeso() instead
     * @return The player's mesos
     * @deprecated
     */
    @Deprecated
    public int getMeso() {
	  return getPlayer().getMeso();
    }

    public String getScript() {
	  return script;
    }

    public void setScriptPath(String scriptPath) {
	  this.scriptPath = scriptPath;
    }

    public String getScriptPath() {
	  return scriptPath;
    }

    public int getNpc() {
	  return npc;
    }

    /**
     * Use getPlayer() instead (for consistency with MapleClient)
     * @return the MapleCharacter.
     * @deprecated
     */
    @Deprecated
    public MapleCharacter getChar() {
	  return getPlayer();
    }

    public MapleClient getC() {
	  return getClient();
    }

    @Override
    public String toString() {
	  return "Conversation with NPC: " + npc;
    }

    public void startQuest(int id) {
	  MapleQuest.getInstance(id).start(getPlayer(), npc);
    }

    public void completeQuest(int id) {
	  MapleQuest.getInstance(id).complete(getPlayer(), npc);
    }

    public void forfeitQuest(int id) {
	  MapleQuest.getInstance(id).forfeit(getPlayer());
    }

    public synchronized NPCEvent getEvent() {
	  while (getPlayer().getNpcScriptInfo().getNpcEventQueue().size() < 1) {
		continue;
	  }
	  NPCEvent event = getPlayer().getNpcScriptInfo().getNpcEventQueue().poll();
	  this.lastEvent = event;
	  return event;
    }

    public static class NPCStateInfo {

	  private String text;
	  private boolean prev;
	  private boolean next;

	  public NPCStateInfo(String text, boolean prev, boolean next) {
		this.text = text;
		this.prev = prev;
		this.next = next;
	  }

	  public boolean isNext() {
		return next;
	  }

	  public boolean isPrev() {
		return prev;
	  }

	  public String getText() {
		return text;
	  }
    }

    public static enum NPCDialogType {

	  NORMAL(0),
	  YES_NO(1),
	  GET_TEXT(2),
	  GET_NUMBER(3),
	  SIMPLE(4),
	  QUIZ(6),
	  STYLE(7),
	  ACCEPT_DECLINE(0x0C),
	  ACCEPT_DECLINE_NO_EXIT(0x0D);

	  final int value;

	  /**
	   * Creates a new MapleSkinColor
	   * @param id
	   */
	  private NPCDialogType(int value) {
		this.value = value;
	  }

	  /**
	   * Get the skincolor's id
	   * @return skincolor's id.
	   */
	  public int getValue() {
		return value;
	  }

	  /**
	   * Get a MapleSkinColor by id
	   * @param id
	   * @return MapleSkinColor with the id
	   */
	  public static NPCDialogType getByValue(int value) {
		for (NPCDialogType l : NPCDialogType.values()) {
		    if (l.getValue() == value) {
			  return l;
		    }
		}
		return null;
	  }
    }
}
