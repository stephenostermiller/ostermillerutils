#!/bin/bash

size=`ls -lah utils.jar`
size=${size:38:4}
if [ -z "`grep $size index.html`" ]
then
    echo "utils.jar size is $size but index.html does not show that."
    exit 1
fi

FILES=$@
FILES=${FILES/package.html/} 
if [ "$FILES" ]
then
	echo Make: Uploading to web site: $FILES
    chmod -x install.sh
	scp -r $FILES deadsea@ostermiller.org:www/utils
    chmod +x install.sh
fi
