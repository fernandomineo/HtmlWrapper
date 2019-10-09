package wrapperservice;

import org.apache.commons.validator.UrlValidator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class IndexController {

    private static final String template = "Hello, %s";
    private final AtomicInteger counter = new AtomicInteger();
    private UrlValidator validator = new UrlValidator();

    // https://stackoverflow.com/questions/238547/how-do-you-programmatically-download-a-webpage-in-java
    // https://spring.io/guides/tutorials/bookmarks/

    @RequestMapping("/wrapper")
    public StatusResponse wrapper(@RequestParam(value="uri") String uri) {
        /*if (null == uri || uri.length() <= 0)
            throw new ExceptionWrapper.UriNotProvided();
        else {*/
        if (validator.isValid(uri)) {
            WrapperService wrap = new WrapperService(uri);
            wrap.createSetOfLinks(uri);
            return new StatusResponse(uri, "True", "200", "null");
        } else {
             throw new ExceptionWrapper.NotValidUri(uri);
        }
    }
}

