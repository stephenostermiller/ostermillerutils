#!/bin/bash

# Copyright (C) 2010 Stephen Ostermiller
# http://ostermiller.org/contact.pl?regarding=Java+Utilities
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# See LICENSE.txt for details.

if [ ! -e pom.xml ]
then
  echo "Expected this script to be called from ostermillerutils base directory"
  exit 1;
fi

CLASSES=target/classes
TABS_CLASS_FILE=$CLASSES/com/Ostermiller/util/Tabs.class

if [ ! -f $TABS_CLASS_FILE ]
then
  echo "Tabs class file does not exist: $TABS_CLASS_FILE"
  echo "Ensure the project has been built"
fi

for file in `find src/ -type f`
do
  if [ "$file" != "src/build/spell/util.dict" ]
  then
    trailCount=`grep -c -E '[ 	]+$' $file`
    if [ "$trailCount" != 0 ]
    then
      echo "Removing trailing white space from: $file"
      sed -r -i 's/[ 	]+$//' "$file"
    fi
  fi
done

(find src/*/java -name "*.java"; find src/*/jflex -name "*.lex") | xargs java -classpath "$CLASSES" com.Ostermiller.util.Tabs -tv -w 4
java -classpath "$CLASSES" com.Ostermiller.util.Tabs -s 4 -v -w 4 src/site/snippet/*.java.snippet
