package wrapperservice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class IndexController {

    private static final String template = "Hello, %s!";
    private final Random counter = new Random();

    @RequestMapping("/wrapper")
    public Uri wrapper(@RequestParam(value="name", defaultValue="World") String name) {
        return new Uri(counter.nextInt(),  String.format(template, name));
    }
}
