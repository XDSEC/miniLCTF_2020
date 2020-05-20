FROM alpine AS builder

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories
RUN apk add --no-cache musl-dev gcc
COPY readflag.c /readflag.c
RUN gcc -o /readflag /readflag.c

FROM php:5.6-fpm-alpine

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories
RUN apk add --no-cache nginx
COPY nginx.conf /etc/nginx/nginx.conf
RUN mkdir -p /run/nginx
COPY index.php /var/www/html/index.php

COPY --from=builder /readflag /readflag
RUN chown root:root /readflag
RUN chmod u+s /readflag
COPY start.sh /start.sh
RUN chmod +x /start.sh
RUN chown -R root:root /var/www/html
RUN chmod -R 755 /var/www/html

ENTRYPOINT [ "/start.sh" ]