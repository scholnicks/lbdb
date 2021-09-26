#!/bin/sh

export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home
cd `dirname $0`/../Resources
java -Dapple.laf.useScreenMenuBar=true -Xdock:name="Laurel Book Database" -Xdock:icon=book_case.icns \
     -Dcom.apple.mrj.application.live-resize=true \
     -Dlbdb.environment=production \
     -jar laurel-book-database-4.6.1.jar 1>>/Users/steve/Library/Logs/lbdb.log 2>&1

