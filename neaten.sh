#!/bin/bash

tablist=""

for file in *.java
do
    generated='0'
    if [ ! -e $file ]
    then
        echo "$file does not exists."
    elif [ -e ${file%.java}.lex ]
    then
        generated='1'
    elif [ `egrep -l "[ \t]+$" $file` ]
    then
        echo "Removing trailing white space from $file."
        mv "$file" temp
        sed 's/[ \t]*$//' temp > "$file"
    fi
    if [ $generated -eq '0' ]
    then
        tablist="$tablist $file"  
    fi    
done
java com.Ostermiller.util.Tabs -gtv $tablist

