package cn.hahoo.boilerplate.api.hello;

import cn.hahoo.boilerplate.api.user.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Hello", description = "返回测试字符串")
@RestController
public class HelloController {
    private final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Operation(summary = "返回hello信息", description = "返回hello信息")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Hello.class),
                            examples = {
                                    @ExampleObject(value="{" +
                                            "\"msg\": \"Hello\"" +
                                            "}")
                            })
            )
    })

    @RequestMapping(value="/hello", method = RequestMethod.GET)
    public Hello hello() {
        logger.info("Hello");
        return new Hello("Hello");
    }
}
