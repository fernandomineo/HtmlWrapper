package com.yamanaka.wrapper;

import com.yamanaka.wrapper.utils.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class WrapperController {
    private static Log log = LogFactory.getLog(WrapperController.class);


    @RequestMapping("/wrapper")
    public List<StatusResponse> getReachableList(@RequestParam(value = "uri") String uri, @RequestParam(required = false) boolean debug) {
        Utils.DBG = debug;
        Optional<Document> page;
        if (WrapperService.isValidURI(uri)) {
            try {
                page = Optional.of(Jsoup.connect(uri).userAgent(Utils.userAgent).get());
            } catch (Exception e) {// Jsoup get page not found, normally its related to Unknown Host.
                log.error("Jsoup failed to get page contents, normally its related to unknown hosts: getLocalizedMessage: " + e.getLocalizedMessage());
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE, "Unknown host");
            }
            WrapperService wrap = new WrapperService();
            return wrap.getAllLinkStatusResponse(page.orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error - Page is Null")));
        } else { // Not valid URI
            log.error("URI validation failed, need to input a valid URI");
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, "URI Not acceptable");
        }
    }
}
