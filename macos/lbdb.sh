#!/bin/sh
#
cd `dirname $0`/../Resources
java -Dapple.laf.useScreenMenuBar=true -Xdock:name="Laurel Book Database" -Xdock:icon=book_case.icns \
     -Dcom.apple.mrj.application.live-resize=true \
     -Dlbdb.database.type=production \
-jar laurel-book-database-0.1.0.jar 1>> /Users/steve/Library/Logs/lbdb.log 2>&1

