/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.console.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 *
 * @author Raz
 */
public class MakeFileSend {

	public static void main(String args[]) {
		try {
		File inputFile = new File("C:/Users/Raz/Desktop/input.txt");
		File outputFile = new File("C:/Users/Raz/Desktop/output.txt");

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		String line;
		while (true) {
			line = reader.readLine();
			if (line == null) {
				break;
			}
			line.replaceAll("\"", "\"\"");
			System.out.println("Send (\"" + line + "\")" + "\r\n");
			writer.write("Send (\"" + line + "\")" + "\r\n");
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
