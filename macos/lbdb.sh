#!/bin/sh

export JAVA_HOME=/Users/steve/.sdkman/candidates/java/16.0.0.hs-adpt

cd `dirname $0`/../Resources
java -Dapple.laf.useScreenMenuBar=true -Xdock:name="Laurel Book Database" -Xdock:icon=book_case.icns \
     -Dcom.apple.mrj.application.live-resize=true \
     -Dlbdb.environment=production \
-jar laurel-book-database-4.6.1.jar 1>/dev/null 2>&1

