FROM frankli0324/lnmp:debian-5.6

LABEL maintainer="frankli0324@hotmail.com"

COPY src /var/www/html

COPY flag.sh /flag.sh
COPY db.sql /db.sql
RUN mysqld_safe & until mysqladmin ping -s; do sleep 1; done \
    && mysqladmin -uroot password 'root' \
    && mysql -e 'source /db.sql' -uroot -proot

RUN rm -f /db.sql
