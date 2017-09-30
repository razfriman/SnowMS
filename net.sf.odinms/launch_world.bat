@echo off
@title Snow's OdinMS World Server
set CLASSPATH=dist/*;
java -Dnet.sf.odinms.listwz=1 -Dnet.sf.odinms.recvops=recvops.properties -Dnet.sf.odinms.sendops=sendops.properties -Dnet.sf.odinms.wzpath=C:\Nexon\MapleStory -Djavax.net.ssl.keyStore=key.keystore -Djavax.net.ssl.keyStorePassword=passwd -Djavax.net.ssl.trustStore=key.keystore -Djavax.net.ssl.trustStorePassword=passwd net.sf.odinms.net.world.WorldServer 
pause