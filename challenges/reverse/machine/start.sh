#!/bin/sh

sed -i "s/flag_here/$FLAG/" index.js
export FLAG=
node build.js
mv output.js /var/www/html/index.js
mv index.html /var/www/html/