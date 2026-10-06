#!/bin/sh
# Builds the Marp decks in .preso into _site/ (HTML + PDF) for GitHub Pages.
# The code on the slides is written in the markdown itself, so every build gives the same slides: no caching needed.
set -eu

site="$(pwd)/_site"
mkdir -p "$site"
cd .preso

for md in *_*.md; do
  name="${md%.md}"
  echo "Building ${md}"
  npx @marp-team/marp-cli --output "$site/$name.html" "$md"
  npx @marp-team/marp-cli --pdf --output "$site/$name.pdf" "$md"
done

cp *.svg "$site/"

# The root URL opens the deck.
cat > "$site/index.html" <<'HTML'
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Exquisite Evals for Skills &amp; MCPs</title>
  <meta http-equiv="refresh" content="0; url=evals_javadocs_dev.html">
</head>
<body>
  <p><a href="evals_javadocs_dev.html">Exquisite Evals for Skills &amp; MCPs</a> (<a href="evals_javadocs_dev.pdf">PDF</a>)</p>
</body>
</html>
HTML
