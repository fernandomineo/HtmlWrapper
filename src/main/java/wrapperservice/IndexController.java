package wrapperservice;

import org.apache.commons.validator.UrlValidator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;


@RestController
public class IndexController {

    private UrlValidator validator = new UrlValidator();

    // https://stackoverflow.com/questions/238547/how-do-you-programmatically-download-a-webpage-in-java
    // https://spring.io/guides/tutorials/bookmarks/
    // https://www.baeldung.com/httpclient-connection-management

    @RequestMapping("/wrapper")
    public List<StatusResponse> wrapper(@RequestParam(value="uri") String uri) {

        if (validator.isValid(uri)) {
            WrapperService wrap = new WrapperService(uri);
            Set<String> sl = wrap.createSetOfLinks(uri);

            return wrap.createListOfStatusResponse2(sl);
            //return new StatusResponse(uri, "True", "200", "null");
        } else {
             throw new ExceptionWrapper.NotValidUri(uri);
        }
    }
}