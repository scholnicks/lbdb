#!bin/bash

./gradlew clean build || exit -1

rm -rf lbdb.app
mkdir -p lbdb.app/Contents/MacOS lbdb.app/Contents/Resources

cp macos/lbdb.sh        lbdb.app/Contents/MacOS
cp macos/Info.plist     lbdb.app/Contents
cp macos/PkgInfo        lbdb.app/Contents
cp macos/book_case.icns lbdb.app/Contents/Resources

cp build/libs/laurel-book-database-*.jar lbdb.app/Contents/Resources

./gradlew clean

exit 0