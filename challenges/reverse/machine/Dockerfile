FROM ctftraining/base_image_nginx

RUN apk add --update nodejs --no-cache

COPY . /opt
WORKDIR /opt

COPY ./start.sh /flag.sh