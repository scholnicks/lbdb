#!/bin/bash

./gradlew clean build || exit -1

app_directory=build/lbdb.app

mkdir -p $app_directory/Contents/MacOS $app_directory/Contents/Resources

cp macos/lbdb.sh        $app_directory/Contents/MacOS
cp macos/Info.plist     $app_directory/Contents
cp macos/PkgInfo        $app_directory/Contents
cp macos/book_case.icns $app_directory/Contents/Resources

cp build/libs/laurel-book-database-*.jar $app_directory/Contents/Resources

exit 0
