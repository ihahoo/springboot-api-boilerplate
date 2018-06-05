package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
  
    @Autowired
    Config config;

    @RequestMapping("/hello")
    public Hello hello() {
        return new Hello(config.getName());
    }
}
