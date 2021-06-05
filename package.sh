#!/bin/bash

appName="Laurel Book Database"
version="4.6.1"
vendor="Steve Scholnick"

rm -f "${appName}-${version}".dmg

./gradlew clean build || exit -1

jpackage \
  --input            build \
  --name             "${appName}" \
  --description      "Laurel's Book Database" \
  --main-jar         "$HOME/development/java/lbdb/build/libs/laurel-book-database-${version}.jar" \
  --icon             "$HOME/development/java/lbdb/macos/book_case.icns" \
  --java-options     "-Dlbdb.environment=production" \
  --app-version      "${version}" \
  --vendor           "${vendor}" \
  --copyright        "Copyright 2021 ${vendor}" \
  --mac-package-name "${appName}" \
  --verbose

cp "$HOME/development/java/lbdb/build/libs/laurel-book-database-${version}.jar" $HOME/.laurel-book-database.jar
open "${appName}-${version}".dmg

exit 0
