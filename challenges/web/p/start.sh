#!/bin/sh

echo $FLAG > /flag_56ebb17872df3a7a0b5cff88c0623173
unset FLAG
export FLAG=
rm /docker-php-entrypoint

php-fpm &
nginx

tail -F /var/log/nginx/access.log
