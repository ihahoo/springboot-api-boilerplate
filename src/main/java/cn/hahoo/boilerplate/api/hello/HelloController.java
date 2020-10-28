package cn.hahoo.boilerplate.api.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @RequestMapping(value="/hello", method = RequestMethod.GET)
    public Hello hello() {
        logger.info("Hello");
        return new Hello("Hello");
    }
}
