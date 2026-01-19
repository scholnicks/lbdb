#!/usr/bin/env bash
# vi: set syntax=sh ts=4 sw=4 sts=4 et ff=unix ai si :

cd $HOME/development/java/lbdb
rm -f LBDB-*.dmg

appName="LBDB"
vendor="Steve Scholnick"
version="8.2.0"

rm -f ${appName}.dmg

./gradlew clean build || exit -1

jpackage \
  --input            build \
  --type             dmg \
  --name             "${appName}" \
  --description      "LBDB" \
  --main-jar         "$HOME/development/java/lbdb/build/libs/laurel-book-database.jar" \
  --icon             "$HOME/development/java/lbdb/macos/book_case.icns" \
  --java-options     "-Dlbdb.environment=production" \
  --app-version      "${version}" \
  --vendor           "${vendor}" \
  --copyright        "Copyright 2025 ${vendor}" \
  --mac-package-name "${appName}" \
  --verbose

# cp "$HOME/development/java/lbdb/build/libs/laurel-book-database.jar" $HOME/.laurel-book-database.jar
# open "${appName}".dmg

./gradlew clean

exit 0
