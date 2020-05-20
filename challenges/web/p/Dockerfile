FROM php:7.0.9-fpm-alpine

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tencentyun.com/g' /etc/apk/repositories \
    && apk add --update --no-cache nginx \
    && mkdir /run/nginx

COPY start.sh /docker-php-entrypoint
RUN chmod +x /docker-php-entrypoint
COPY default.conf /etc/nginx/nginx.conf
COPY src /var/www/html
RUN chown -R www-data:www-data /var/www/html

WORKDIR /var/www/html

EXPOSE 80

VOLUME ["/var/log/nginx"]

CMD ["/bin/sh", "-c", "/docker-php-entrypoint"]
