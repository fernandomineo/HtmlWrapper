package wrapperservice;

import org.apache.commons.validator.UrlValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;


@RestController
public class IndexController {

    private UrlValidator validator = new UrlValidator();

    // https://stackoverflow.com/questions/238547/how-do-you-programmatically-download-a-webpage-in-java
    // https://spring.io/guides/tutorials/bookmarks/
    // https://www.baeldung.com/httpclient-connection-management
    // https://github.com/briansjavablog/multi-threading-with-executorservice/blob/master/src/main/java/com/briansjavablog/concurrency/threads/ExecutorServiceCallableSample.java
    // https://www.baeldung.com/spring-boot-custom-error-page


    @RequestMapping("/wrapper")
    public List<StatusResponse> wrapper(@RequestParam(value="uri") String uri) {
        boolean reachable = false;
        if (validator.isValid(uri)) {
            try {
                reachable = InetAddress.getByName(uri).isReachable(500);
            } catch (UnknownHostException e) {

            } catch (IOException e){
            }
            if (reachable) {
                WrapperService wrap = new WrapperService(uri);
                Set<String> sl = wrap.createSetOfLinks(uri);
                if (null != sl && sl.size() > 0) {
                    return wrap.createListOfStatusResponse(sl);
                }
            } else {
                throw new ExceptionWrapper.UnknownHost();
            }
            //return new StatusResponse(uri, "True", "200", "null");
        } else {
             throw new ExceptionWrapper.NotValidUri(uri);
        }
        return null;
    }
}