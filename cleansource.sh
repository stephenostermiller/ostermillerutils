for file in *.*.html
do
  prob=`egrep -l "<a.*${file%.*.html}.*\(${file%.*.html}\.[a-z]*\).*a>" $file`
  if [ ! -e "../${file%.*.html}.html" ]
  then
	if [ "a$prob" != "a" ]
	then
      mv  "$file" temp
      sed "s/<a.*${file%.*.html}.*\(${file%.*.html}\.[a-z]*\).*a>/\1/g" temp > $file
	fi
  fi
  prob=`egrep -l "<small.*JavaDoc.*small>" $file`
  if [ ! -e "../doc/com/Ostermiller/util/${file%.*.html}.html" ]
  then
	if [ "a$prob" != "a" ]
	then
      mv  "$file" temp
	  sed "s/<small.*JavaDoc.*small>//g" temp > $file
	fi
  fi
done
