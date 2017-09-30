/* 
	Amon - 2030010
	Zakum teleport outer
*/


function start() {
    if (cm.sendYesNo("Would you like to leave #r#m280030000#?")) {
        cm.warp(211000000);
		cm.dispose()
    } else {
        cm.sendOk("Wise choice");
        cm.dispose();
    }
}