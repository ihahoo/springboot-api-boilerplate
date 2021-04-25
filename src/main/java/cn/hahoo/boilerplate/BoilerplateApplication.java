package cn.hahoo.boilerplate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        info = @Info(
                title = "API接口文档",
                version = "1.0.0",
                description = "`日期时间格式`: 日期时间采用时间戳，使用UTC时间，用ISO8601格式化 `YYYY-MM-DDTHH:MM:SSZ` `{\"created_at\": \"2011-09-06T17:26:27Z\"}`。" +
                        "在显示的时候，需要客户端转换为本地时区显示时间。<br /><br />" +
                        "`返回码`: 首先判断http的状态码，2xx表示成功，4xx表示用户输入有错误，5xx表示服务器端有错误。如果有错误，会返回以下" +
                        "类似数据: `{\"errcode\":100203,\"errmsg\":\"Captcha not found\"}`"
        )
)
@SecurityScheme(
        name = HttpHeaders.AUTHORIZATION,
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)

@SpringBootApplication
@EnableCaching
public class BoilerplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoilerplateApplication.class, args);
    }

}
