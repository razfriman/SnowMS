/*
LPQ
Stage 2
922010200
Map->922010201 (TOWERS TRAP)

WARP
*/


function act(){
	var nextMap = 922010201;//Towers Trap
	var eim = rm.getPlayer().getEventInstance();
	var target = eim.getMapInstance(nextMap);
	var targetPortal = target.getPortal("st00");
	rm.getPlayer().changeMap(target, targetPortal);
}