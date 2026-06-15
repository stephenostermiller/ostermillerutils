#!/bin/bash

# Copyright (C) 2010-2011 Stephen Ostermiller
# https://ostermiller.org/contact.pl?regarding=Java+Utilities
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
# See LICENSE.txt for details.

# Remove multiple consecutive blank lines and condense them into one

for file in "$@"
do
	if [ -f "$file" ]
	then
		# Create temp file with condensed blank lines
		cat -s "$file" > "$file.tmp"
		# Check if it differs from the original
		if ! diff -q "$file.tmp" "$file" > /dev/null 2>&1
		then
			# Replace original with the fixed version
			mv "$file.tmp" "$file"
		else
			# No changes needed, remove temp file
			rm "$file.tmp"
		fi
	fi
done
