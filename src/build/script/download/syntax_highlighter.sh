#!/bin/sh
# Copyright (C) 2010 Stephen Ostermiller
# http://ostermiller.org/contact.pl?regarding=Java+Utilities

BASE=`pwd`

cd /tmp/

file=`ls -1 syntaxhighlighter*.zip 2>/dev/null | head -n 1`
if [ "z$file" == "z" ]
then
  wget 'http://alexgorbatchev.com/SyntaxHighlighter/download/download.php?sh_current' -O syntaxhighlighter.zip
fi
file=`ls -1 syntaxhighlighter*.zip 2>/dev/null | head -n 1`
if [ "z$file" == "z" ]
then
  echo "Could not download syntax highlighting libraries"
  exit 1;
fi

rm -rf syntaxhighlighter*/
unzip $file
cd syntaxhighlighter*/

SITE="$BASE/src/site/resources/syntax"

rm -rf "$SITE"
mkdir -p "$SITE/scripts"
mkdir -p "$SITE/styles"

cp -v scripts/shCore.js "$SITE/scripts"
cp -v styles/shCore.css "$SITE/styles"
cp -v scripts/shBrushJava.js "$SITE/scripts"
cp -v "$BASE/src/site/syntax/shTheme.css" "$SITE/styles"

cd "$SITE"

cat "$BASE/src/site/syntax/shBrushJava.js.patch" | patch -p0
rm "$BASE/src/site/resources/syntax/scripts/shBrushJava.js.orig"

cd "$BASE"
./src/build/script/neaten.sh
