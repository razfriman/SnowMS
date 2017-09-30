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

package net.sf.odinms.exttools.dropspider;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.NamespaceContext;

/**
 *
 * @author Matze
 */
public class XHTMLNamespaceContext implements NamespaceContext {

    public String getNamespaceURI(String prefix) {
	return "http://www.w3.org/1999/xhtml";
    }

    public String getPrefix(String namespaceURI) {
	return "h";
    }

    public Iterator<String> getPrefixes(String namespaceURI) {
	List<String> px = new LinkedList<String>();
	px.add("h");
	return Collections.unmodifiableList(px).iterator();
    }
}
