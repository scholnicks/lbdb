#!/bin/sh
#
cd `dirname $0`/../Resources
java -Dapple.laf.useScreenMenuBar=true -Xdock:name="Laurel Book Database" -Xdock:icon=book_case.icns -Dcom.apple.mrj.application.live-resize=true \
-jar laurel-book-database-0.1.0.jar

