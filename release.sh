#!/bin/bash

size=`ls -lah utils.jar`
size=${size:38:4}
if [ -z "`grep -i $size download.html`" ]
then
    echo "utils.jar size is $size but download.html does not show that."
    exit 1
fi

latestversion=`grep -oE 'ostermillerutils_[0-9]_[0-9]{2}_[0-9]{2}\.jar' download.bte | sort | tail -1`
cp utils.jar "$latestversion"

FILES="$@ $latestversion"
FILES=${FILES/package.html/} 
if [ "$FILES" ]
then
	echo Make: Uploading to web site: $FILES
    chmod -x install.sh
	scp -r $FILES deadsea@ostermiller.org:www/utils
    chmod +x install.sh
fi

rm "$latestversion"
