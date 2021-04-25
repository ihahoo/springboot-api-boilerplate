# springboot-api-boilerplate
A Java RESTful API server with spring boot and docker

Spring Boot 2 (Java 8), Gradle, Docker, Mysql, Redis, Mybatis开发后端RESTful API接口脚手架

## 说明

虽然Spring Boot已经组合了各种工具，也有一定的编程约定，不过具体到细节还是有很多需要约定和规范的。
后端对前端应该隐藏语言习惯性，也就是说后端不管用什么语言都可以，不应局限于Java的约定，对于前端不用
关心这么多，只要用一致的调用习惯即可。 而对于后端各种语言，可以规范一些通用的约定，这样在不同语言下
开发输出是一致的，文件组织方式也可以更类似，方便在跨语言开发后端时候会有一致的习惯。这里整理了一些我们的习惯规约。

对于微服务，应该是可以跨语言开发的，而不应局限于某一个固定语言下的实现，比如微服务不只是Spring Cloud。

### 模块文件组织在一起

一个模块下的各种文件组织在一起，包括Entity，Controller，Service和Service的实现，mybatis的XML mapper文件等。因为如果按类型放置，如果接口
文件增多，就会在不同的目录下去找相应的文件，跳来跳去，容易迷失，组织到一起结构清晰，后期维护也可以容易直接按照模块找到相关文件。例：查看`api/user`
下的文件结构。

### 目录结构

* `api` 放置各个接口模块，为何放在api目录下，因为我们用Go语言或者Nodejs语言开发后端，也是约定api目录放置各个接口模块。
* `config` 放置各种配置类
* `exception` 放置异常类
* `utils` 放置各种工具类

### 日期时间格式

日期时间采用时间戳，使用UTC时间，用ISO8601格式化 `YYYY-MM-DDTHH:MM:SSZ`
在显示的时候，需要客户端转换为本地时区显示时间。

```
{
    "created_at": "2011-09-06T17:26:27Z"
}
```

### 返回数据

返回的数据采用JSON数据结构。

返回错误：首先判断http的状态码，2xx表示成功，4xx表示用户输入有错误，5xx表示服务器端有错误。如果有错误，会返回以下类似数据:
```
{"errcode":100203,"errmsg":"Captcha not found"}
```
(其他语言的后端接口我们也是统一返回类似的错误码，这样对于前端不用关心后端使用什么语言，使用统一的错误格式即可)

### 接口文档

使用[springdoc](https://springdoc.org/) 生成OpenAPI 3格式的文档，可以使用`Swagger-ui`查看文档。通过相应注解生成文档。

### 一些技术栈和规范

* 使用Redis和Spring Boot Cache的组合为主Cache方式，可使用Cache注解使用Cache
* Json使用`Jackson`处理。对于时间自动输出UTC格式，对于空值输出""空字串而非`null`。
* 输出JSON的时候，统一使用`SNAKE_CASE`模式，用下划线分隔名称，在`JacksonConfig`中已经做了camel case到snake case的自动转换的配置，`SwaggerConfig`做了Swagger自动转换
的配置。在代码中，仍然使用`camelCase`模式。
* 错误输出使用`ApiException`，如：`throw new ApiException(100301, "新密码和确认密码不一致", 422);` 会输出 `{"errcode":100301,"errmsg":"新密码和确认密码不一致"}`
的JSON，并且http头中返回码是422
* 鉴权使用`JWT`方式。
* 安全使用Spring Boot Security，`SecurityConfig`做了一些配置，当需要判断是否有某接口的访问权限，可以使用类似注解：`@PreAuthorize("hasRole('ROLE_ADMIN')")`  判断，
  对于权限`ROLE_ADMIN`权限，写到jwt的scopes中。
* 配置文件采用yaml格式，`application.yml` 替代 `application.properties`
* 包和构建工具使用`Gradle`
* 日志输出到`logs`目录下，程序中输出日志可使用 `private final static Logger logger = LoggerFactory.getLogger(HelloController.class);` 然后 `logger.info("...");`方式使用。  
* 使用Spring Boot集成的工具，尽量简洁的使用方式，使用一些集成的注解简化开发。

## 安装
- 安装[Docker](https://www.docker.com/) (可选, 请设置镜像加速器, 不用加速器拉镜像速度很慢)
- 安装[Docker Compose](https://github.com/docker/compose/releases) (可选, 在一些平台下，安装好Docker后，默认就安装好了，可以用命令`docker-compose`测试是否安装)

```
$ git clone 本库地址
```

## 启动
```
$ ./gradlew bootRun
```

默认启用8080端口，可通过 http://localhost:8080/hello 测试是否启动成功

## API文档

Swagger文档：http://localhost:8080/docs/api

OpenAPI3 JSON格式：http://localhost:8080/docs/openapi

OpenAPI3 YAML格式：http://localhost:8080/docs/openapi.yaml

## 启动本地开发环境数据库(Docker,可选)
````
$ docker-compose up
````

通过docker-compose启动mysql, redis两个容器，mysql端口为3306, redis端口为6379。数据库可通过本地客户端工具连接进行操作和调试。

## 停止本地开发环境数据库(Docker, 可选)
````
$ docker-compose down
````
