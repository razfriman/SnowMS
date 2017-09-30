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

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.odinms.database.DatabaseConnection;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Matze
 */
public class DropSpider {

    private static SAXParserFactory spf;
    private static SAXParser parser;
    private static XPathFactory xfac;
    private static TransformerFactory tfac;
    private static Transformer trans;
    private static Connection con;
    private static PreparedStatement ps;
    private static DropSpiderInformationProvider ip;

    public static void main(String[] args) {
	try {
	    spf = SAXParserFactory.newInstance("org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl", ClassLoader.getSystemClassLoader());
	    parser = spf.newSAXParser();

	    xfac = XPathFactory.newInstance();
	    tfac = TransformerFactory.newInstance();
	    trans = tfac.newTransformer();
	    ip = DropSpiderInformationProvider.getInstance();
	    Properties dbProp = new Properties();
	    dbProp.load(new FileReader("db.properties"));
	    DatabaseConnection.setProps(dbProp);
	    con = DatabaseConnection.getConnection();
	    con.createStatement().executeUpdate("TRUNCATE monsterdrops");
	    ps = con.prepareStatement("INSERT INTO monsterdrops VALUES (NULL, ?, ?, ?)");
	    parseIndex("http://www.mapletip.com/all-maplestory-monsters");
	    ps.close();
	    con.close();
	} catch (SQLException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	} catch (TransformerConfigurationException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	} catch (ParserConfigurationException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	} catch (SAXException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	} catch (Exception ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public static void parseIndex(String file) {
	try {
	    URL url = new URL(file);
	    Source sax = new SAXSource(parser.getXMLReader(), new InputSource(url.toExternalForm()));
	    DOMResult dom = new DOMResult();
	    trans.transform(sax, dom);
	    Document doc = (Document) dom.getNode();
	    XPath xpath = xfac.newXPath();
	    xpath.setNamespaceContext(new XHTMLNamespaceContext());
	    NodeList files = (NodeList) xpath.evaluate("//h:html/h:body/h:table/h:tr/h:td/h:div[@class='content_text']/h:table/h:tr/h:td/h:table/h:tr/h:td/h:a/@href", doc, XPathConstants.NODESET);
	    for (int i = 0; i < files.getLength(); i++) {
		Attr fileEntry = (Attr) files.item(i);
		String[] urlParts = fileEntry.getValue().split("/");
		parseFile("http://www.mapletip.com/maplestory-monster/blah/" + urlParts[urlParts.length - 1]);
	    }
	} catch (XPathExpressionException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	} catch (TransformerException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	} catch (SAXException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	} catch (MalformedURLException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public static void parseFile(String file) {
	try {
	    URL url = new URL(file);
	    Source sax = new SAXSource(parser.getXMLReader(), new InputSource(url.toExternalForm()));
	    DOMResult dom = new DOMResult();
	    trans.transform(sax, dom);
	    Document doc = (Document) dom.getNode();
	    System.out.println(doc.getChildNodes().getLength());
	    // Test scenario: Show all item names

	    XPath xpath = xfac.newXPath();
	    xpath.setNamespaceContext(new XHTMLNamespaceContext());
	    System.out.println("barato:");
	    NodeList monsters = (NodeList) xpath.evaluate("/h:html/h:body/h:table/h:tr/h:td/h:div/h:div[@class='monster_wrap']", doc, XPathConstants.NODESET);
	    for (int i = 0; i < monsters.getLength(); i++) {
		System.out.println(monsters.item(i).getAttributes().getNamedItem("class").getNodeValue());
		Attr att = (Attr) xpath.evaluate("/h:html/h:body/h:table/h:tr/h:td/h:div/h:div[@class='monster_wrap'][" + (i + 1) + "]/h:div[@class='monster_title']/h:b/h:a/@href", doc, XPathConstants.NODE);
		String[] urlParts = att.getValue().split("/");
		ps.setInt(1, Integer.parseInt(urlParts[urlParts.length - 1]));
		System.out.println("drops:");
		NodeList drops = (NodeList) xpath.evaluate("/h:html/h:body/h:table/h:tr/h:td/h:div/h:div[@class='monster_wrap'][" + (i + 1) + "]/h:div[@class='monster_box'][1]/h:table/h:tr/h:td/h:div[@class='topicpreview']/@id", doc, XPathConstants.NODESET);
		for (int j = 0; j < drops.getLength(); j++) {
		    Attr drop = (Attr) drops.item(j);
		    urlParts = drop.getValue().split("_");
		    ps.setInt(2, Integer.parseInt(urlParts[0]));
		    ps.setInt(3, ip.makeDropChance(Integer.parseInt(urlParts[0])));
		    System.out.println(urlParts[0]);
		    ps.executeUpdate();
		}
	    }
	} catch (SQLException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	} catch (TransformerException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	} catch (XPathExpressionException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	} catch (SAXException ex) {
	    Logger.getLogger(DropSpider.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
}
