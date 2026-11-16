my $content = do { local $/; <STDIN> };
$content =~ s/^\s*"accessWidener"\s*:\s*"[^"]+",?\s*\n//m;
$content =~ s/,\s*"accessWidener"\s*:\s*"[^"]+"//s;
$content =~ s/,\s*\n\}/\n\}/g;
$content =~ s/,\s*\n  \}/\n  \}/g;
print $content;
