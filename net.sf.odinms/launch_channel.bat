@echo off
@title Snow's OdinMS Channel Server
set CLASSPATH=.;dist/*;
java -Xmx1024M -Dnet.sf.odinms.listwz=1 -Dnet.sf.odinms.recvops=recvops.properties -Dnet.sf.odinms.sendops=sendops.properties -Dnet.sf.odinms.wzpath=C:\Nexon\MapleStory -Dnet.sf.odinms.channel.config=channel.properties -Djavax.net.ssl.keyStore=key.keystore -Djavax.net.ssl.keyStorePassword=passwd -Djavax.net.ssl.trustStore=key.keystore -Djavax.net.ssl.trustStorePassword=passwd net.sf.odinms.net.channel.ChannelServer -Dcom.sun.management.jmxremote.port=13373 -Dcom.sun.management.jmxremote.password.file=jmxremote.password -Dcom.sun.management.jmxremote.access.file=jmxremote.access java
pause