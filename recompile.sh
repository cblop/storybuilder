#!/bin/bash
lein clean
lein cljsbuild once min
rm -r css
rm -r js
rm -r vendor
rm index.html
cp -r resources/public/index.html .
cp -r resources/public/css .
cp -r resources/public/js .
cp -r resources/public/vendor . 
