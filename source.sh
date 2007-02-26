echo "<%bte.doc super=page.bte %>" > source.bte
echo "<%bte.tpl name=description%>Ostermiller Java utilities source code.<%/bte.tpl%>" >> source.bte
echo "<%bte.tpl name=firstLine%>Source Code -- Java Utilities - OstermillerUtils<%/bte.tpl%>" >> source.bte
echo "<%bte.tpl name=keywords%>java, java, java, util, utils, utilities, source, code, open source, source code, gpl, gnu, general public license<%/bte.tpl%>" >> source.bte
echo "<%bte.tpl name=pageTitle%>Java Utilities Source Code<%/bte.tpl%>" >> source.bte
echo "<%bte.tpl name=topcontent%>" >> source.bte
echo "A list of all source files for the Ostermiller Java Utilites appears below.  Each has been has syntax highlighting for easy reading.  All the source code is also included in the downloadable .jar file." >> source.bte
echo "<%/bte.tpl%>" >> source.bte
echo "<%bte.tpl name=content%>" >> source.bte
echo "<ul>" >> source.bte
for file in *.*.html
do
echo "<li><a href='$file'>${file%.html}</a></li>" >> source.bte
done
echo "</ul>" >> source.bte
echo "<%/bte.tpl%>" >> source.bte
echo "<%bte.tpl name=linksource%><b>Browse Source</b><%/bte.tpl%>" >> source.bte
echo "<%/bte.doc%>" >> source.bte
