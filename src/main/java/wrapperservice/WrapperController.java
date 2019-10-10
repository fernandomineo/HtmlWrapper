package wrapperservice;

import org.apache.commons.validator.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;


@RestController
public class WrapperController {

    private UrlValidator validator = new UrlValidator();

    // https://stackoverflow.com/questions/238547/how-do-you-programmatically-download-a-webpage-in-java
    // https://spring.io/guides/tutorials/bookmarks/
    // https://www.baeldung.com/httpclient-connection-management
    // https://github.com/briansjavablog/multi-threading-with-executorservice/blob/master/src/main/java/com/briansjavablog/concurrency/threads/ExecutorServiceCallableSample.java
    // https://www.baeldung.com/spring-boot-custom-error-page


    @RequestMapping("/wrapper")
    public List<StatusResponse> getReachableList(@RequestParam(value="uri") String uri) {
        Document page;
        if (validator.isValid(uri)) {
            try {
                page = Jsoup.connect(uri).get();
            } catch (Exception e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e.getCause());
            }
            if (null != page) {
                WrapperService wrap = new WrapperService();
                return wrap.getAllLinkStatusResponse(page);
            } else { // Jsoup get page not found, normally its related to Unknown Host.
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE, "Unknown host");
            }
        } else { // Not valid URI
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, "URI Not acceptable");
        }
    }
}