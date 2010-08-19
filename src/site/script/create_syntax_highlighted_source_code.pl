#!/usr/bin/perl

use strict;

my $dir=`pwd`;
if ($dir !~ /\/src\/site\/script\/?$/){
  die "Must be run from src/site/script/";
}

`mkdir -p ../xdoc/javadoc`;
`mkdir -p ../apt/src`;

my $sourceList = "";
my $menuList = "";
my $javadocMenuList = "";
my $javadocList = "";
my $htaccessList = "";

$htaccessList .= "\n# No documentation and no javadoc redirects\n";
my @files = split(/\n/, `find ../../main/ -type f`);
for my $file (sort @files){
  my ($path, $barename, $ext) = $file =~ /^\.\.\/\.\.\/(.*\/)([^\/]+)(\.[^\/\.]+)/;
  my $classpath = $path;
  $classpath =~ s/^[^\/]+\/[^\/]+\///g;
  my $basename = "$barename$ext";
  my $mavenrootname = "src/$path$basename";
  my $srchtmlhref="$basename.html";
  my $srcAptVmFile="../apt/src/$basename.apt.vm";
  my $brush = "";
  my $javadocHref = "";
  my $javadocAptLink = "";
  my $javadocFile = "";
  my $docAptLink = "";
  my $docXdocLink = "";
  if ($basename =~ /\.java$/){
    $brush = "|brush=java";
    my $public = `grep -c -E 'public .*(class|interface)' $file`;
    if ($public > 0){
      $javadocHref = "../doc/$classpath$barename.html";
      $javadocAptLink = "    * {{{../javadoc/$barename.html}$barename Javadoc}}";
      $javadocMenuList .= "        <item name=\"$barename\" href=\"/javadoc/$barename.html\" />\n";
      $javadocList .= "    * {{{javadoc/$barename.html}$barename Javadoc}}\n\n";
      $javadocFile = "../xdoc/javadoc/$barename.xml";
    }
  }
  my $srcXdocLink = "<li><a href=\"../src/$srchtmlhref\">$basename Source Code</a></li>";
  if (-f "../apt/$barename.apt.vm"){
    $docAptLink = "    * {{{../$barename.html}$barename Documentation and Examples}}";
    $docXdocLink = "<li><a href=\"../$barename.html\">$barename Documentation and Examples</a></li>";
  } else {
    $htaccessList .= "Redirect permanent /utils/$barename.html http://ostermiller.org/utils/src/$srchtmlhref\n";
    $htaccessList .= "Redirect permanent /utils/javadoc/$barename.html http://ostermiller.org/utils/src/$srchtmlhref\n";
    $htaccessList .= "Redirect permanent /utils/doc/com/Ostermiller/util/$barename.html http://ostermiller.org/utils/src/$srchtmlhref\n";
  }
    
  $htaccessList .= "Redirect permanent /utils/$srchtmlhref http://ostermiller.org/utils/src/$srchtmlhref\n";
  $sourceList .= "    * {{{./src/$srchtmlhref}$basename Source Code}}\n\n";
  $menuList .= "        <item name=\"$basename\" href=\"/src/$srchtmlhref\" />\n";
  
  &createSrcFile($srcAptVmFile, $basename, $docAptLink, $javadocAptLink, $brush, $mavenrootname);
  if ($javadocHref){
    &createJavadocFile($javadocFile, $barename, $javadocHref, $docXdocLink, $srcXdocLink);
  }
}

$htaccessList .= "\n# Unit test file redirects\n";
@files = split(/\n/, `find ../../test/ -type f`);
for my $file (sort @files){
  my ($path, $barename, $ext) = $file =~ /^\.\.\/\.\.\/(.*\/)([^\/]+)(\.[^\/\.]+)/;
  if ($barename =~ /Test/){
      my $barenametest = $barename;
      my $barenametests = $barename;  
      if ($barename =~ /Tests/){
        $barenametest =~ s/Tests/Test/g;
      } else {
        $barenametests =~ s/Test/Tests/g;
      }
      $htaccessList .= "Redirect permanent /utils/$barenametest.html http://ostermiller.org/utils/\n";
      $htaccessList .= "Redirect permanent /utils/javadoc/$barenametest.html http://ostermiller.org/utils/\n";
      $htaccessList .= "Redirect permanent /utils/doc/com/Ostermiller/util/$barenametest.html http://ostermiller.org/utils/\n";
      $htaccessList .= "Redirect permanent /utils/$barenametests.html http://ostermiller.org/utils/\n";
      $htaccessList .= "Redirect permanent /utils/javadoc/$barenametests.html http://ostermiller.org/utils/\n";
      $htaccessList .= "Redirect permanent /utils/doc/com/Ostermiller/util/$barenametests.html http://ostermiller.org/utils/\n";
   } else {
      $htaccessList .= "Redirect permanent /utils/$barename.html http://ostermiller.org/utils/\n";
      $htaccessList .= "Redirect permanent /utils/javadoc/$barename.html http://ostermiller.org/utils/\n";
      $htaccessList .= "Redirect permanent /utils/doc/com/Ostermiller/util/$barename.html http://ostermiller.org/utils/\n";
   }
}

&replaceInFile("../apt/source.apt.vm", "GENERATED SOURCES LIST", $sourceList);
&replaceInFile("../apt/doc.apt.vm", "GENERATED JAVADOC LIST", $javadocList);
&replaceInFile("../site.xml", "GENERATED SOURCES MENU", $menuList);
&replaceInFile("../site.xml", "GENERATED JAVADOC MENU", $javadocMenuList);
&replaceInFile("../resources/.htaccess", "GENERATED HTACCESS LIST", $htaccessList);

sub createSrcFile(){
  my ($srcAptVmFile, $basename, $docAptLink, $javadocAptLink, $brush, $mavenrootname) = @_;
  open (FILE, ">$srcAptVmFile") or die $?;
  print FILE "  ----
  $basename Source Code
  ----
  Stephen Ostermiller;
  meta-description=View the source code for $basename from the OstermillerUtils Java utilities.;
  ----

$basename Source Code

$docAptLink

$javadocAptLink

\%{code-snippet$brush|file=$mavenrootname}
";

  close (FILE);
  
}

sub createJavadocFile(){
  my ($javadocFile, $barename, $javadocHref, $docXdocLink, $srcXdocLink) = @_;
  open (FILE, ">$javadocFile") or die $?;
  print FILE "
<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<document xmlns=\"http://maven.apache.org/XDOC/2.0\"
  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
  xsi:schemaLocation=\"http://maven.apache.org/XDOC/2.0 http://maven.apache.org/xsd/xdoc-2.0.xsd\">
  <properties>
    <title>$barename OstermillerUtils JavaDoc</title>
    <author>Stephen Ostermiller</author>
  </properties>
  <head>
    <meta name=\"description\" content=\"View JavaDoc for $barename from the com.Ostermiller.util package.\" />
  </head>
  <body>
    <section name=\"$barename OstermillerUtils JavaDoc\">
      <ul>
        $docXdocLink
        $srcXdocLink
      </ul>
      <iframe src=\"$javadocHref\" width=\"100%\" height=\"800\"></iframe>
    </section>
  </body>
</document>
";
  close (FILE);
  
}

sub replaceInFile(){
  my ($filename, $section, $generated) = @_;
  
  open(FILE, $filename) or die $?;
  my $contents = "";
  while (my $line = <FILE>){
    $contents .= $line;
  }
  close(FILE);
  $contents =~ s/(BEGIN $section[^\r\n]*)(?:.|[\r\n])*?([^\r\n]*END $section)/\1\n$generated\2/g;
  open(FILE, ">$filename") or die $?;
  print FILE $contents;
  close(FILE);  
}
