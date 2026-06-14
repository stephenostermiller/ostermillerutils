#!/bin/bash

# Copyright (C) 2010-2011 Stephen Ostermiller
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
# See LICENSE.txt for details.

# Check that files use tab indentation (not spaces for indentation)
#
# OK:
#   - hello
#   - 		 * hello
#   -  * hello
#   - 	hello
#
# Not OK:
#   -   hello
#   -    hello
#   -       * hello
#   -      	hello
#   -          hello
#   -  	    hello
#   -  		 hello

exit_code=0

for file in "$@"
do
	if [ -f "$file" ]
	then
		matches=$(grep -En '^ +[^*]|^\t+ {2,}|^\t+ [^ *]' "$file" | head -3 | cut -d: -f1 | paste -sd, -)
		if [ -n "$matches" ]
		then
			echo "File has improper indentation (lines $matches): $file"
			exit_code=1
		fi
	fi
done

exit $exit_code
