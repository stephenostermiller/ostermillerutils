#!/bin/bash

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

tablist=""

for file in `find src/*/java -name "*.java"`
do
  trailCount=`grep -c -E '[ 	]+$' $file`
  if [ $trailCount != 0 ]
  then
    echo "Removing trailing white space from: $file"
    sed -ir 's/[ 	]+$//' "$file"
  fi
  tablist="$tablist $file"  
done

java -classpath "$CLASSES" com.Ostermiller.util.Tabs -tv -w 4 $tablist
