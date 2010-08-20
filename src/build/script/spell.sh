#!/bin/bash

if [ ! -e pom.xml ]
then
  echo "Expected this script to be called from ostermillerutils base directory"
  exit 1;
fi

DICT=./src/build/spell/util.dict

for file in `(find src -name "*.java" && find src -name "*.java.snippet"&& find src -name "*.apt.vm")`
do
  aspell check --mode=url -x -p "$DICT" "$file"
done

# Sort the dict file
TMPFILE=$(mktemp) || { echo "Failed to create temp file"; exit 1; }
head -n 1 "$DICT" > $TMPFILE
tail -n +2 "$DICT" | sort | uniq >> $TMPFILE
mv $TMPFILE "$DICT"
