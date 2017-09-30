/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.console.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.sf.odinms.client.MapleJob;

/**
 *
 * @author Raz
 */
public class TestClasss {
    public static void main(String args[]) {
	  outputClass(MapleJob.class);
    }

    public static void outputClass(Class c) {
	  //for(Method m : c.getMethods()) {
	  for(Method m : c.getDeclaredMethods()) {
		System.out.println("M\t" + m.getName());
		int mod = m.getModifiers();
		System.out.println("MM\t" + Modifier.toString(mod));
		System.out.println();
	  }
	  System.out.println();
	  for(Field f : c.getFields()) {
		System.out.println("F\t" + f.getName());
		int mod = f.getModifiers();
		System.out.println("FF\t" + Modifier.toString(mod));
		System.out.println();
	  }
	  System.out.println();
    }
}
