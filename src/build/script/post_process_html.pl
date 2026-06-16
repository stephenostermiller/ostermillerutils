#!/usr/bin/perl

# Inject highlight.js syntax highlighting into generated HTML site

use strict;

my $highlightSetupTemplate = <<'EOF';
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/xcode.min.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
	if (typeof hljs !== 'undefined') {
		document.querySelectorAll('pre code').forEach(function(block) {
			hljs.highlightElement(block);
		});
	}
});
</script>
<link rel="stylesheet" href="%CSSPREFIX%css/custom-styles.css">
EOF

# Find all HTML files in the target/site directory AND target/javadoc for javadoc
my @files = split(/\n/, `find target/site target/javadoc -type f -name '*.html' 2>/dev/null`);

for my $file (@files) {
	open(my $in, '<', $file) or next;
	my $content = do { local $/; <$in> };
	close($in);

	my $modified = 0;

	# Remove Maven Doxia/Fluido generated comments
	if ($content =~ s{<!--.*?-->}{}is) {
		$modified = 1;
	}

	# Fix empty hrefs that were originally "/" (for Home links stripped by Fluido)
	if ($content =~ s{href\s*=\s*""\s+title\s*=\s*"Home"}{href="/" title="Home"}g) {
		$modified = 1;
	}

	# Check if this is a javadoc file (in target/javadoc/ or target/site/doc/)
	my $isJavadoc = ($file =~ m{target/javadoc/} || $file =~ m{target/site/doc/});

	# For javadoc files, skip further processing but still write if modified
	if ($isJavadoc) {
		if ($modified) {
			open(my $out, '>', $file) or next;
			print $out $content;
			close($out);
		}
		next;
	}

	# Calculate relative path prefix based on file depth
	# Files in target/site/src/ need ../css/, files in target/site/ need css/ or ./css/
	my $depth = ($file =~ tr/\///) - 2; # -2 for 'target/site/' part
	my $cssPrefix = "";
	$cssPrefix = "../" x $depth if $depth > 0;

	# Build the highlight setup with correct CSS path for this file's location
	my $highlightSetup = $highlightSetupTemplate;
	$highlightSetup =~ s{%CSSPREFIX%}{$cssPrefix}g;

	# Skip if already has highlight.js
	if ($content =~ /highlight\.js/) {
		# But write if we made modifications (removed comments)
		if ($modified) {
			open(my $out, '>', $file) or next;
			print $out $content;
			close($out);
		}
		next;
	}

	# Remove poweredBy section (can be in div or as standalone link)
	$content =~ s{<div id="poweredBy">.*?</div>}{}is;
	$content =~ s{<a[^>]*class="poweredBy"[^>]*>.*?</a>}{}is;

	# Remove trailing whitespace from empty divs left after removing poweredBy
	$content =~ s{<div class="well sidebar-nav">[\s\n]*(?:<div class="clear"></div>[\s\n]*)*</div>}{}is;

	# Convert semantic HTML5 tags to divs for Bootstrap grid compatibility
	$content =~ s{<header\s+id="leftColumn"([^>]*)>}{<div id="leftColumn"$1>}is;
	$content =~ s{</header>}{</div>}is;
	$content =~ s{<main\s+id="bodyColumn"([^>]*)>}{<div id="bodyColumn"$1>}is;
	$content =~ s{</main>}{</div>}is;

	# Determine language based on file context
	my $lang = "";
	if ($file =~ /\/src\//) {
		# Source code pages are Java files
		$lang = "java";
	} elsif ($file =~ /\/javadoc\//) {
		# JavaDoc pages contain HTML code
		$lang = "html";
	}

	# Replace prettyprint class with language-specific class for highlight.js
	# and wrap content in <code> tags for highlight.js to recognize
	if ($lang) {
		# First, replace the class name from prettyprint to language-java
		$content =~ s/class="prettyprint"/class="language-$lang"/g;
		# Then wrap the pre content in <code> tags for highlight.js
		$content =~ s{<pre class="language-$lang">(.*?)</pre>}{<pre><code class="language-$lang">$1</code></pre>}is;
	}

	# Move <div class="source"> blocks outside of list items and lists
	# APT's snippet macro gets nested inside the last <li> due to parser behavior
	$content =~ s{
		(\s*)(<div\s+class="source">.*?</div>)     # Capture the source div
		(\s*</li>\s*</ul>)                          # Followed by closing list tags
	}{$3\n$2}xis;

	# Insert before closing </head> tag
	if ($content =~ s{(</head>)}{$highlightSetup\n$1}i) {
		open(my $out, '>', $file) or next;
		print $out $content;
		close($out);
	}
}
