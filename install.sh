#!/bin/bash

directory=/usr/local/bin
RandPass=RandPass
LineEnds=LineEnds
MD5=MD5Sum

if [ ! -z $1 ]
    then 
    if [ "$1" == "--help" ]
    then
        echo "Install the ostermiller.org Java utilities."
        echo "-f force"
        exit 0
    elif [ "$1" != "-f" ]
    then
        echo "Unknown option: $1" 
        exit 1
    fi
fi

workingdir=`pwd`

if [ ! -e $workingdir/utils.jar ]
then
    echo "Could not find 'utils.jar'."
    echo "Make sure you execute this script from"
    echo "the directory that contains 'utils.jar'."
    exit 1
fi

if [ ! -w "$directory" ]
then
    echo "You do not have permission to write in"
    echo "$directory"
    echo "Please become superuser."
    exit 1
fi

if [ ! -e $directory/$RandPass ] || [ ! -z $1 ]
then
    echo "#!/bin/bash" > $directory/$RandPass
    echo "java -classpath $workingdir/utils.jar com.Ostermiller.util.RandPass \$@" >> $directory/$RandPass
    chmod 755 $directory/$RandPass
    echo "$RandPass installed."
else
    echo "$directory/$RandPass already exists.  Use -f to overwrite."
fi

if [ ! -e $directory/$LineEnds ] || [ ! -z $1 ]
then
    echo "#!/bin/bash" > $directory/$LineEnds
    echo "java -classpath $workingdir/utils.jar com.Ostermiller.util.LineEnds \$@" >> $directory/$LineEnds
    chmod 755 $directory/$LineEnds
    echo "$LineEnds installed."
else
    echo "$directory/$LineEnds already exists.  Use -f to overwrite."
fi

if [ ! -e $directory/$MD5 ] || [ ! -z $1 ]
then
    echo "#!/bin/bash" > $directory/$MD5
    echo "java -classpath $workingdir/utils.jar com.Ostermiller.util.MD5 \$@" >> $directory/$MD5
    chmod 755 $directory/$MD5
    echo "$MD5 installed."
else
    echo "$directory/$MD5 already exists.  Use -f to overwrite."
fi


