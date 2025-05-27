CREATE DATABASE IF NOT EXISTS proyecto_integrador_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

DROP USER IF EXISTS 'admin_user'@'%';
CREATE USER 'admin_user'@'%' IDENTIFIED WITH mysql_native_password BY 'admin_pass';
GRANT ALL PRIVILEGES ON proyecto_integrador_db.* TO 'admin_user'@'%';
FLUSH PRIVILEGES;