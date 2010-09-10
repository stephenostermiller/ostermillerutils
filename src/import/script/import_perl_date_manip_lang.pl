#!/usr/bin/perl

# Copyright (C) 2010 Stephen Ostermiller
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

# Import language data from the perl library Date::Manip.
# This library is licensed under the GPL/artistic license like Perl,
# so it can be imported into GPL project and used with proper attribution.

use strict;

if (! -e "pom.xml"){
  print "Expected this script to be called from ostermillerutils base directory";
  exit 1;
}

if ( ! -e 'target/import/datemanip' ){
  `mkdir -p 'target/import/datemanip'`;
  my $downloadurl="http://search.cpan.org".`curl -s http://search.cpan.org/dist/Date-Manip/ | grep -oE '[^\"]+Date-Manip-[0-9\.]+\.tar\.gz'`;
  `cd target/import/datemanip && wget $downloadurl`;
  `cd target/import/datemanip && tar xfvz *.tar.gz`;
}

my $words;

my $keyForIntMap = {
  "month_abb" => "monthWords",
  "ampm" => "ampmWords",
  "nth" => "ordinalWords",
  "month_name" => "monthWords",
  "day_name" => "weekdayWords",
  "day_char" => "weekdayWords",
  "day_abb" => "weekdayWords",
};

my $keysForCharList = {
  "sepfr" => "fractionSep",
  "sephm" => "hourMinuteSep",
  "sepms" => "minuteSecondSep",
};

my $keysForList = {
  "each" => "eachWords",
  "at" => "atWords",
  "fields-1" => "yearsWords",
  "fields-2" => "monthsWords",
  "fields-3" => "weeksWords",
  "fields-4" => "daysWords",
  "fields-5" => "hoursWords",
  "fields-6" => "minutesWords",
  "fields-7" => "secondsWords",
  "when-1" => "pastWords",
  "when-2" => "futureWords",
  "last" => "lastWords",
  "nextprev-1" => "nextWords",
  "nextprev-2" => "prevWords",
  "of" => "ofWords",
  "on" => "onWords",
  "times-00:00:00" => "midnightWords",
  "times-12:00:00" => "noonWords",
  "offset_date--0:0:0:2:0:0:0" => "twoDaysAgoWords",
  "offset_date-+0:0:0:2:0:0:0" => "twoDaysFromNowWords",
  "offset_date-0:0:0:0:0:0:0" => "todayWords",
  "offset_time-0:0:0:0:0:0:0" => "todayWords",
  "offset_date-+0:0:0:1:0:0:0" => "tomorrowWords",
  "offset_date--0:0:0:1:0:0:0" => "yesterdayWords",
  "offset_date-+0:0:1:0:0:0" => "tomorrowWords",
  "offset_date--0:0:1:0:0:0" => "yesterdayWords",
  "offset_date--0::00:2:0:0:0" => "twoDaysFromNowWords",
  "times-12:30:00" => "halfWords",

};

my $localeMap = {
  "catalan" => "ca ",
  "danish" => "da",
  "dutch" => "nl",
  "english" => "en",
  "french" => "fr",
  "german" => "de",
  "index" => "-",
  "italian" => "it",
  "polish" => "pl",
  "portugue" => "pt",
  "romanian" => "ro",
  "russian" => "ru",
  "spanish" => "es",
  "swedish" => "sv",
  "turkish" => "tr",
};

my $charsetMap = {
  "russian" => "koi8-r",
  "turkish" => "ISO-8859-1",
};

for my $file (split(/\n/, `ls -1 target/import/datemanip/Date-Manip-*/lib/Date/Manip/Lang/*.pm`)){

  my ($lang) = $file =~ /\/([^\/]+)\.pm$/;
  my $locale = $localeMap->{$lang};
  die "No locale for $lang" if (!$locale);
  if ($locale ne "-"){

    print "Parsing: $file\n";

    open (FILE, $file);
    my $section;
    my $sectionCount;
    my $inData = 0;
    my $copyright = "";
    $words = {};

    while (my $line = <FILE>){
      if ($line =~ /^[ \t]*$/){
        # ignore blank lines
      } elsif ($line =~/^\# Copyright/){
         $copyright .= $line;
      } elsif ($line =~ /^__DATA__$/){
          $inData = 1;
      } elsif($inData){
        if ($line=~/^([\'a-zA-Z\_]+)\:/){
          $section = $1;
          $sectionCount = 0;
          if ($section =~ /^\'(.*)\'$/){
              $section = $1;
          }
        } elsif ($section){
          if ($line =~ /^  \-\s*$/){
            $sectionCount++;
          } elsif ($line =~ /^(?:  )?  \- \'\'\s*$/){
            #ignore
          } elsif ($line =~ /^(?:  )?  - (.*)/){
            my $value = &toPropStyle($1);
            &toProperty($section, $sectionCount, $value);
          } elsif ($line =~ /^  ([^\:]+)\: (.*)/){
            my $value = &toPropStyle($1);
            my $offset = $2;
            &toProperty($section, $offset, $value);
          } else {
              print STDERR $line;
          }
        }
      }
    }
    close (FILE);

    $copyright =~ s/\(c\)/(C)/g;

    my $propFile = "src/main/resources/com/Ostermiller/util/DateTimeParse_$locale.properties";

    if ( -e $propFile){
      print "File exists: $propFile\n";
      print "Move it out of the way to import new data\n";
      print "\n";
    } else {
      print "Creating: $propFile\n";

      open (PROP, ">$propFile");

      print PROP $copyright;
      print PROP "#\n";
      print PROP "# Copyright (C) 2010 Stephen Ostermiller\n";
      print PROP "# http://ostermiller.org/contact.pl?regarding=Java+Utilities\n";
      print PROP "#\n";
      print PROP "# This program is free software; you can redistribute it and/or modify\n";
      print PROP "# it under the terms of the GNU General Public License as published by\n";
      print PROP "# the Free Software Foundation; either version 2 of the License, or\n";
      print PROP "# (at your option) any later version.\n";
      print PROP "#\n";
      print PROP "# This program is distributed in the hope that it will be useful,\n";
      print PROP "# but WITHOUT ANY WARRANTY; without even the implied warranty of\n";
      print PROP "# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n";
      print PROP "# GNU General Public License for more details.\n";
      print PROP "#\n";
      print PROP "# See LICENSE.txt for details.\n";
      print PROP "\n";

      for my $word (sort keys %$words){
        my $value = $words->{$word};
        print PROP "$word=$value\n";
      }
      close(PROP);

      my $charset = $charsetMap->{$lang};
      if ($charset){
        `mv "$propFile" /tmp/cs.tmp; cat /tmp/cs.tmp | iconv -f "$charset" -t "UTF-8" > "$propFile"; rm /tmp/cs.tmp`;
      }
    }
  }
}

sub toProperty(){
  my ($section, $subSection, $value) = @_;
  return if ($section eq "mode");
  my $key = $keyForIntMap->{$section};
  if ($key){
    &addToProp($key,",","$value>$subSection");
    return;
  }

  my $key = $keysForCharList->{$section};
  if ($key){
    $value =~ s/[\[\]]//g
    &addToProp($key,"",$value);
    return;
  }

  $key = $keysForList->{$section};
  $key = $keysForList->{"$section-$subSection"} if (!$key);
  if (!$key){
    print STDERR "No key found for $section-$subSection, $value\n";
    return;
  }
  &addToProp($key,",",$value);
  return;
}

sub addToProp(){
  my ($key, $sep, $value) = @_;
  my $prop = $words->{$key};
  if ($prop){
    $prop.="$sep";
  } else {
    $prop="";
  }
  $prop.=$value;
  $words->{$key} = $prop;
}


sub toPropStyle(){
  my ($s) = @_;
  if ($s =~ /^\"(.*)\"$/){
    $s = $1;
  }
  if ($s =~ /^\'(.*)\'$/){
    $s = $1;
  }
  $s =~ s/\\x([0-9A-Fa-f]{2})/chr(hex($1))/eg;
  return $s;
}
