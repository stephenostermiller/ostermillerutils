#!/bin/bash

for file in *.java *.properties *.lex;
do
  base=`echo $file | sed -r 's/\.[^\.]+$//g'`
  if [[ ! -e src/$base.html ]]
  then
    if [[ `grep -c "Redirect.*/utils/$base.html " .htaccess` == 0 ]]
    then
      echo Redirect permanent /utils/$base.html http://ostermiller.org/utils/ >> .htaccess
    fi
  fi
done


(
  for doc in `grep doc src/*.html | grep -oE "doc/com/[^\'\" ]+"`; 
  do 
    if [[ ! -e src/$doc && `grep -c "Redirect.*/utils/$doc " .htaccess` == 0 ]]
     then echo $doc; 
    fi;  
  done
) | sort | uniq  | sed -r 's|^(.*)$|Redirect permanent /utils/\1 http://ostermiller.org/utils/|g' >> .htaccess
