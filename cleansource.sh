for file in *.*.html
do
  if [ ! -e "${file%.*.html}.html" ]
    then
    mv  "$file" temp
    sed "s/<a.*\(${file%.*.html}\).*${file%.*.html}.*>/\1/g" temp > $file
  fi
  if [ ! -e "doc/com/Ostermiller/util/${file%.*.html}.html" ]
    then
    mv  "$file" temp
    sed "s/<small.*javadoc.*small>/\1/g" temp > $file
  fi
done
