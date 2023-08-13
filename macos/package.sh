#!/bin/bash

appName="Laurel Book Database"
vendor="Steve Scholnick"

rm -f ${appName}.dmg

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
  --copyright        "Copyright 2023 ${vendor}" \
  --mac-package-name "${appName}" \
  --verbose

cp "$HOME/development/java/lbdb/build/libs/laurel-book-database.jar" $HOME/.laurel-book-database.jar
open "${appName}-${version}".dmg

./gradlew clean

exit 0
