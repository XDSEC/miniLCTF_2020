FROM php:7.4-fpm-alpine

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories
RUN apk add --no-cache nginx
COPY nginx.conf /etc/nginx/nginx.conf
RUN mkdir -p /run/nginx
COPY index.php /var/www/html/index.php