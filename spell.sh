#!/bin/bash

files=$@

for file in $files
do
	ext="${file/*./}"
	if [ "$ext" == "bte" ] 
	then
		mode="sgml"
	else
		mode="url"
	fi
	if [ "$ext" != "java" ] || [ ! -e "${file/java/lex}" ]
	then
		cp "$file" temp
		aspell check --mode=$mode -x -p ./util.dict temp
		if [ "`diff "temp" "$file"`" ] 
		then
			mv temp "$file"
		fi
	fi
	rm -f temp temp.bak
done
