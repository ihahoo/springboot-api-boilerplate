# springboot-api-boilerplate
A Java RESTful API server with spring boot and docker

基于Spring Boot 2 (Java 8), Gradle, Docker, Postgresql, Redis开发后端RESTful API接口的脚手架

## 安装
- 安装[Docker](https://www.docker.com/) (请设置镜像加速器, 不用加速器拉镜像速度很慢)
- 安装[Docker Compose](https://github.com/docker/compose/releases) (在一些平台下，安装好Docker后，默认就安装好了，可以用命令`docker-compose`测试是否安装)
- 安装[Gradle](https://gradle.org/) (选择安装，推荐用Gradle作为自动化工具，也可自行配置Maven，如果未安装gradle可使用gradlew命令)

```
$ git clone 本库地址
```

## 启动开发环境(Docker)
````
$ ./gradlew runDockerDev
````
或者在项目根文件夹下运行命令：
````
$ docker-compose up
````

通过docker-compose启动api web, postgres, redis三个容器，api web端口为8080, postgres端口为5432, redis端口为6379。数据库可通过本地客户端工具连接进行操作和调试。

测试接口是否正常启动：请访问`http://localhost:8080/hello`看是否有反馈信息。

## 停止开发环境(Docker)
````
$ ./gradlew stopDockerDev
````
或者在项目根文件夹下运行命令：
````
$ docker-compose down
````

## 启动开发环境(非Docker环境，需jdk8以上)

以下命令可以支持热更新，也可以不使用下面命令，可配置IDE支持更新文件热加载。非Docker环境下，数据库请自动安装和配置。

打开一个命令窗口
````
$ ./gradlew dev 或 gradle dev
````
再打开一个命令窗口
````
$ ./gradlew bootRun 或 gradle bootRun
````



