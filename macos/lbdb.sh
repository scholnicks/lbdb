#!/usr/bin/env bash
# vi: set syntax=sh ts=4 sw=4 sts=4 et ff=unix ai si :

export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home
cd `dirname $0`/../Resources
java -Dapple.laf.useScreenMenuBar=true -Xdock:name="Laurel Book Database" -Xdock:icon=book_case.icns \
     -Dcom.apple.mrj.application.live-resize=true \
     -Dlbdb.environment=production \
     -jar laurel-book-database.jar 1>>/Users/steve/Library/Logs/lbdb.log 2>&1

