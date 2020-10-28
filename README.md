# springboot-api-boilerplate
A Java RESTful API server with spring boot and docker

Spring Boot 2.1 (Java 8), Gradle, Docker, Mysql, Redis, Mybatis开发后端RESTful API接口脚手架

## 安装
- 安装[Docker](https://www.docker.com/) (可选, 请设置镜像加速器, 不用加速器拉镜像速度很慢)
- 安装[Docker Compose](https://github.com/docker/compose/releases) (可选, 在一些平台下，安装好Docker后，默认就安装好了，可以用命令`docker-compose`测试是否安装)

```
$ git clone 本库地址
```

## 启动本地开发环境数据库(Docker,可选)
````
$ docker-compose up
````

通过docker-compose启动mysql, redis两个容器，mysql端口为3306, redis端口为6379。数据库可通过本地客户端工具连接进行操作和调试。

## 停止本地开发环境数据库(Docker, 可选)
````
$ docker-compose down
````
