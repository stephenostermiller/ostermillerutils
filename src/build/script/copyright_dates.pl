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
# See COPYING.TXT for details.

use strict;

if (! -e "pom.xml"){
  print "Expected this script to be called from ostermillerutils base directory";
  exit 1;
}

FILE_IN_LOOP: for my $file (split(/\n/, `find src/main src/build/script -type f`)){
  my @years=split(/[ \n]/, `git log --follow "$file" | grep "Date:" | grep -oE ' 20[0-9]{2} ' | sort | uniq | xargs`);
  my $yearCount = scalar @years;
  if ($yearCount == 0){
    print "No dates found from source control: $file\n";
    next FILE_IN_LOOP;
  }
  my $firstYear = $years[0];
  my $lastYear = $years[$yearCount-1];
  my $scmYears = $firstYear;
  if ($firstYear ne $lastYear){
    $scmYears = "$firstYear-$lastYear";
  }
  my $contents = &readFile($file);
  my @copyrights = $contents =~ /(Copyright \(C\) \d{4}(?:\-\d{4})? .*)/g;
  my $copyrightsCount = scalar @copyrights;
  my $firstYearFound = 0;
  my $lastYearFound = 0;
  for my $copyright (@copyrights){
    $firstYearFound = 1 if ($copyright =~ /$firstYear/);
    $lastYearFound = 1 if ($copyright =~ /$lastYear/);
  }
  next FILE_IN_LOOP if ($firstYearFound and $lastYearFound);
  if ($copyrightsCount == 0){
    print "No copyrights found: $file\n";
    print " Copyright (C) $scmYears Stephen Ostermiller\n";
    print " http://ostermiller.org/contact.pl?regarding=Java+Utilities\n";
    print "\n";
    next FILE_IN_LOOP;
  }
  if ($copyrightsCount == 1 and $contents =~ /Copyright \(C\) \d{4}(?:\-\d{4})? Stephen Ostermiller/){
    $contents =~ s/Copyright \(C\) \d{4}(?:\-\d{4})? Stephen Ostermiller/Copyright \(C\) $scmYears Stephen Ostermiller/g;
    print "Modified copyright dates in: $file\n";
    &saveFile($file, $contents);
    next FILE_IN_LOOP;
  }
  if ($firstYearFound and $contents =~ /Copyright \(C\) (\d{4})(?:\-\d{4})? Stephen Ostermiller/){
    my $stephenFirstYear = $1;
    my $stephenYears = "$stephenFirstYear-$lastYear";
    $contents =~ s/Copyright \(C\) \d{4}(?:\-\d{4})? Stephen Ostermiller/Copyright \(C\) $stephenYears Stephen Ostermiller/g;
    print "Modified copyright dates in: $file\n";
    &saveFile($file, $contents);
    next FILE_IN_LOOP;
  }
  print "Should be Copyright (C) $scmYears: $file\n";
  print join ("\n", @copyrights);
  print "\n\n";
}

sub readFile(){
  my ($file) = @_;
  my $contents = "";
  open(FILE, $file) or die $!;
  while (my $line = <FILE>){
    $contents .= $line;
  }
  close(FILE);
  return $contents;
}

sub saveFile(){
  my ($file, $contents) = @_;
  open(FILE, ">$file") or die $!;
  print FILE $contents;
  close(FILE);
}