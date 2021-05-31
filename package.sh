./gradlew clean build || exit -1

jpackage --input build/ \
  --name "Laurel-Book-Database" \
  --main-jar /Users/steve/development/java/lbdb/build/libs/laurel-book-database-4.6.1.jar \
  --icon "/Users/steve/development/java/lbdb/macos/book_case.icns" \
  --app-version "4.6.1" \
  --vendor "Steve Scholnick" \
  --copyright "Copyright 2021 Steve Scholnick" \
  --mac-package-name "Laurel Book Database" \
  --verbose
