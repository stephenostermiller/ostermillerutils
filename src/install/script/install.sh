#!/bin/bash --noprofile

binDir=/usr/local/bin
libDir=/usr/lib/
RandPass=RandPass
LineEnds=LineEnds
MD5=MD5Sum
Tabs=Tabs
Base64=Base64

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

targetDir=`pwd`
utilsJar=`ls $targetDir/ostermillerutils*.jar 2>/dev/null | grep -oE 'ostermillerutils-[0-9\.]+SNAPSHOT?\.jar' | head -n 1`

if [ -z "$utilsJar" ]
then
  targetDir="$targetDir/target"
  utilsJar=`ls $targetDir/ostermillerutils*.jar 2>/dev/null | grep -oE 'ostermillerutils-[0-9\.]+SNAPSHOT?\.jar' | head -n 1`
fi

if [ -z "$utilsJar" ]
then
    echo "Could not find ostermillerutils-X.XX.XX.jar"
    echo "Make sure you execute this script from"
    echo "the directory that contains ostermillerutils-X.XX.XX.jar"
    exit 1
fi

if [ ! -w "$libDir" ]
then
    libDir=~/lib
    mkdir -p "$libDir"
fi

if [ ! -w "$libDir" ]
then
    echo "You do not have permission to write in $libDir"
    exit 1
fi

utilLibDir="$libDir/ostermillerutils"
mkdir -p "$utilLibDir"
cp  -v "$targetDir/$utilsJar" "$utilLibDir"


if [ ! -w "$binDir" ]
then
    binDir=~/bin
    mkdir -p "$binDir"
fi


if [ ! -w "$binDir" ]
then
    echo "You do not have permission to write in $binDir"
    exit 1
fi

if [ ! -e $binDir/$RandPass ] || [ ! -z $1 ]
then
    echo "#!/bin/bash --noprofile" > $binDir/$RandPass
    echo "java -classpath $utilLibDir/$utilsJar com.Ostermiller.util.RandPass \"\$@\"" >> $binDir/$RandPass
    chmod 755 $binDir/$RandPass
    echo "$RandPass installed in $binDir."
else
    echo "$binDir/$RandPass already exists.  Use -f to overwrite."
fi

if [ ! -e $binDir/$LineEnds ] || [ ! -z $1 ]
then
    echo "#!/bin/bash --noprofile" > $binDir/$LineEnds
    echo "java -classpath $utilLibDir/$utilsJar com.Ostermiller.util.LineEnds \"\$@\"" >> $binDir/$LineEnds
    chmod 755 $binDir/$LineEnds
    echo "$LineEnds installed in $binDir."
else
    echo "$binDir/$LineEnds already exists.  Use -f to overwrite."
fi

if [ ! -e $binDir/$MD5 ] || [ ! -z $1 ]
then
    echo "#!/bin/bash --noprofile" > $binDir/$MD5
    echo "java -classpath $utilLibDir/$utilsJar com.Ostermiller.util.MD5 \"\$@\"" >> $binDir/$MD5
    chmod 755 $binDir/$MD5
    echo "$MD5 installed in $binDir."
else
    echo "$binDir/$MD5 already exists.  Use -f to overwrite."
fi

if [ ! -e $binDir/$Tabs ] || [ ! -z $1 ]
then
    echo "#!/bin/bash --noprofile" > $binDir/$Tabs
    echo "java -classpath $utilLibDir/$utilsJar com.Ostermiller.util.Tabs \"\$@\"" >> $binDir/$Tabs
    chmod 755 $binDir/$Tabs
    echo "$Tabs installed in $binDir."
else
    echo "$binDir/$Tabs already exists.  Use -f to overwrite."
fi

if [ ! -e $binDir/$Base64 ] || [ ! -z $1 ]
then
    echo "#!/bin/bash --noprofile" > $binDir/$Base64
    echo "java -classpath $utilLibDir/$utilsJar com.Ostermiller.util.Base64 \"\$@\"" >> $binDir/$Base64
    chmod 755 $binDir/$Base64
    echo "$Base64 installed in $binDir."
else
    echo "$binDir/$Base64 already exists.  Use -f to overwrite."
fi


