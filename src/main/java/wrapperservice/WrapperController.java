package wrapperservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// https://stackoverflow.com/questions/238547/how-do-you-programmatically-download-a-webpage-in-java
// https://spring.io/guides/tutorials/bookmarks/
// https://www.baeldung.com/httpclient-connection-management
// https://github.com/briansjavablog/multi-threading-with-executorservice/blob/master/src/main/java/com/briansjavablog/concurrency/threads/ExecutorServiceCallableSample.java
// https://www.baeldung.com/spring-boot-custom-error-page
// https://www.logicbig.com/tutorials/misc/java-logging/jcl-simple-log.html

@RestController
public class WrapperController {
    private static Log log = LogFactory.getLog(WrapperController.class);

    @RequestMapping("/wrapper")
    public List<StatusResponse> getReachableList(@RequestParam(value="uri") String uri) {
        Document page;
        if (WrapperService.isValidURI(uri)) {
            try {
                page = Jsoup.connect(uri).get();
            } catch (Exception e) {// Jsoup get page not found, normally its related to Unknown Host.
                log.error("Jsoup failed to get page contents, normally its related to unknown hosts: getLocalizedMessage: " + e.getLocalizedMessage());
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE, "Unknown host");
            }
            if (null != page) {
                WrapperService wrap = new WrapperService();
                return wrap.getAllLinkStatusResponse(page);
            } else { // In case of null page, some other cause failed, consider as "Internal Server Error"
                log.error("Jsoup return null page, server error");
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
            }
        } else { // Not valid URI
            log.error("URI validation failed, need to input a valid URI");
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, "URI Not acceptable");
        }
    }
}