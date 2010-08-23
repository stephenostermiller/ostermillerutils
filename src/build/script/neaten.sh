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

for file in `find src/ -type f`
do
  if [ "$file" != "src/build/spell/util.dict" ]
  then
    trailCount=`grep -c -E '[ 	]+$' $file`
    if [ $trailCount != 0 ]
    then
      echo "Removing trailing white space from: $file"
      sed -r -i 's/[ 	]+$//' "$file"
    fi
  fi
done

find src/*/java -name "*.java" | xargs java -classpath "$CLASSES" com.Ostermiller.util.Tabs -tv -w 4
java -classpath "$CLASSES" com.Ostermiller.util.Tabs -s 4 -v -w 4 src/site/snippet/*.java.snippet
