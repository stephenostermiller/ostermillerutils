#!/bin/bash

# Copyright (C) 2002-2010 Stephen Ostermiller
# http://ostermiller.org/contact.pl?regarding=Java+Utilities
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# See COPYING.TXT for details.

if [ ! -e pom.xml ]
then
  echo "Expected this script to be called from ostermillerutils base directory"
  exit 1;
fi

DICT=./src/build/spell/util.dict

for file in `(find src -name "*.java" && find src -name "*.java.snippet"&& find src -name "*.apt.vm")`
do
  aspell check --mode=url -x -p "$DICT" "$file"
done

# Sort the dict file
TMPFILE=$(mktemp) || { echo "Failed to create temp file"; exit 1; }
head -n 1 "$DICT" > $TMPFILE
tail -n +2 "$DICT" | sort | uniq >> $TMPFILE
mv $TMPFILE "$DICT"
