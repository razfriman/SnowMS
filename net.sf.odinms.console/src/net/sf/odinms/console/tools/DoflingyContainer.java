/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.console.tools;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raz
 */
public class DoflingyContainer {

    public static void main(String args[]) {
	System.out.println("Snow's Doflingy Container");
	System.out.println("Please enter a number");
	while (true) {
	    String input = System.console().readLine();
	    List<DoflingyContainerType> totalContainers = new ArrayList<DoflingyContainerType>();
	    int number = -1;
	    try {
		number = Integer.parseInt(input);
		System.out.println("Container Size\tNumber Required");
		System.out.println("--------------\t---------------");
		while (number > 0) {
		    DoflingyContainerType containerType = DoflingyContainerType.getClosestType(number);
		    number -= containerType.getType();
		    totalContainers.add(containerType);
		}
		for (DoflingyContainerType container : DoflingyContainerType.values()) {
		    System.out.println(container.name() + "\t\t\t" + countContainerType(totalContainers, container));
		}
	    } catch (NumberFormatException nfe) {
		System.out.println("Please enter a valid number");
	    }
	}
    }
    
    private static int countContainerType(List<DoflingyContainerType> containerList, DoflingyContainerType containerType) {
	int ret = 0;
	for(DoflingyContainerType singleContainer : containerList) {
	    if(singleContainer == containerType)
		ret++;
	}
	
	return ret;
    }
    
    
    private static enum DoflingyContainerType {
	HUGE(50),
	LARGE(20),
	MEDIUM(5),
	SMALL(1),
	;
	
	private final int i;
	
	private DoflingyContainerType(int i) {
	    this.i = i;
	}
	
	private int getType() {
	    return i;
	}
	
	private static DoflingyContainerType getClosestType(int i) {
	    for(DoflingyContainerType containerType : DoflingyContainerType.values()) {
		if(i >= containerType.getType()) {
		    return containerType;
		}
	    }
	    return null;
	}
    }
}
