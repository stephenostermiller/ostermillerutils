#!/usr/bin/perl

use strict;

my $dir=`pwd`;
if ($dir !~ /\/src\/site\/script\/?$/){
  die "Must be run from src/site/script/";
}

my $sourceList = "";

my @files = split(/\n/, `find ../../main/ -type f`);
for my $file (sort @files){
  my ($path, $base, $ext) = $file =~ /^\.\.\/\.\.\/(.*\/)([^\/]+)(\.[^\/\.]+)/;
  my $classpath = $path;
  $classpath =~ s/^[^\/]+\/[^\/]+\///g;
  my $name = "$base$ext";
  $file = "src/$path$name";
  my $htmlname="$name.html";
  my $aptvmname="$name.apt.vm";
  my $brush = "";
  my $javadocLink = "";
  my $docLink = "";
  if ($name =~ /\.java$/){
    $brush = "|brush=java";
    $javadocLink = "  * {{{./doc/$classpath$base.html}$base Javadoc}}";
  }
  if (-f "../apt/$base.apt.vm"){
    $docLink = "  * {{{./$base.html}$base Documentation and Examples}}"
  }
  
  open (FILE, ">../apt/$aptvmname") or die $?;
  print FILE "  ----
  $name Source Code
  ----
  Stephen Ostermiller;
  meta-description=View the source code for $name from the OstermillerUtils Java utilities.;
  ----

$docLink

$javadocLink

$name Source Code

\%{code-snippet$brush|file=$file}
";

  close (FILE);
  
  $sourceList .= "  * {{{./$htmlname}$name Source Code}}\n\n";
  
}

  
open (FILE, ">../apt/source.apt.vm") or die $?;
print FILE "  ----
  Source Code
  ----
  Stephen Ostermiller;
  meta-description=View the source code from the OstermillerUtils Java utilities.;
  ----

Source Code -- Java Utilities - OstermillerUtils

  A list of all source files for the Ostermiller Java Utilites appears below. Each has been has syntax highlighting for easy reading. All the source code is also included in the downloadable .jar file. 

$sourceList
";

close (FILE);

