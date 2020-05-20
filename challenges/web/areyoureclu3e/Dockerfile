FROM frankli0324/lnmp:debian-7.4

LABEL maintainer="frankli0324@hotmail.com"

COPY db.sql /db.sql
RUN mysqld_safe & until mysqladmin ping -s; do sleep 1; done \
    && mysqladmin -uroot password 'root' \
    && mysql -uroot -proot < /db.sql

COPY flag.sh /flag.sh
RUN chmod +x /flag.sh

COPY src /var/www/html
RUN chown -R www-data:www-data /var/www/html

COPY php.ini $PHP_INI_DIR/php.ini
