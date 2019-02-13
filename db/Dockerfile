FROM mysql:5.7

ENV MYSQL_ROOT_PASSWORD=pismo \
    MYSQL_USER=pismo \
    MYSQL_PASSWORD=pismo \
    MYSQL_DATABASE=pismolabs

ADD schema.sql /docker-entrypoint-initdb.d

EXPOSE 3306