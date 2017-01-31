FROM alpine:3.4

RUN apk add --update python \
  && rm -rf /var/cache/apk/* \
  && mkdir /app

COPY server.py /app/
COPY htdocs/ /app/htdocs

WORKDIR /app/htdocs

CMD ["/usr/bin/python", "/app/server.py"]
