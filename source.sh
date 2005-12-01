echo "<!doctype html public '-//w3c//dtd html 4.0 transitional//en'>" > source.html
echo "<html>" >> source.html
echo "<head>" >> source.html
echo "<meta name='description' content='Ostermiller Java utilities source code.'>" >> source.html
echo "<meta name='keywords' content='java, java, java, util, utils, utilities, source, code, open source, source code, gpl, gnu, general public license'>" >> source.html
echo "<title>Java Utilities Source Code</title>" >> source.html
echo "<base href='http://ostermiller.org/utils/'>" >> source.html
echo "<body text='#000000' bgcolor='#FFFFFF' link='#0000FF' vlink='#800080' alink='#FF0000'>" >> source.html
echo "<h1><a href="http://ostermiller.org/utils/">Java Utilities</a> Source Code</h1>" >> source.html
echo -e '<script type="text/javascript"><!--\nvar google_ad_client = "pub-2385172974335864";\nvar google_ad_width = 728;\nvar google_ad_height = 90;\nvar google_ad_format = "728x90_as";\nvar google_ad_type = "text";\nvar google_ad_channel ="";\nvar google_color_border = "A8DDA0";\nvar google_color_bg = "EBFFED";\nvar google_color_link = "0000CC";\nvar google_color_url = "008000";\nvar google_color_text = "6F6F6F";\n//--></script>\n<script type="text/javascript"\n  src="http://pagead2.googlesyndication.com/pagead/show_ads.js">\n</script>' >> source.html
echo "<ul>" >> source.html
for file in *.*.html
do
echo "<li><a href='$file'>${file%.html}</a></li>" >> source.html
done
echo "</ul>" >> source.html
echo "" >> source.html
echo "<h2><a href='./'>Download and Information</a></h2>" >> source.html
echo "" >> source.html
echo "<h2><a name='license'>License</a></h2>" >> source.html
echo "" >> source.html
echo "<P>Copyright (c) 2001-2004 by <a href='http://ostermiller.org/contact.pl?regarding=Java+Utilities'>Stephen Ostermiller</a></P>" >> source.html
echo "" >> source.html
echo "<p>This library is free software; you can redistribute it and/or modify" >> source.html
echo "it under the terms of the GNU General Public License as published" >> source.html
echo "by the Free Software Foundation; either version 2 of the License or (at" >> source.html
echo "your option) any later version.</P>" >> source.html
echo "" >> source.html
echo "<p>This program is distributed in the hope that it will be useful, but" >> source.html
echo "WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY" >> source.html
echo "or FITNESS FOR A PARTICULAR PURPOSE. See the" >> source.html
echo "<a href='http://www.gnu.org/copyleft/gpl.html'>GNU General Public License</a> for more details.</p>" >> source.html
echo "</div>" >> source.html
echo "</body>" >> source.html
echo "</html>" >> source.html
