#!/bin/sh
# One database per service. The MariaDB image creates the monolith's (MYSQL_DATABASE) and runs this once, on an
# empty data volume. The application user gets every service database (separate users per service: see docs).
set -e
mariadb -uroot -p"${MYSQL_ROOT_PASSWORD}" <<SQL
CREATE DATABASE IF NOT EXISTS notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON notification_db.* TO '${MYSQL_USER}'@'%';
FLUSH PRIVILEGES;
SQL
