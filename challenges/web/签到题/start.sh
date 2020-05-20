#!/bin/sh

echo $FLAG > /flag
chown root:root /flag
chmod 600 /flag

export FLAG=
rm /start.sh

php-fpm &
nginx &
tail -F /var/log/nginx/access.log
