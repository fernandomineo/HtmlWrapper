package wrapperservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.UrlValidator;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.net.ssl.SSLHandshakeException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;


public class WrapperService{

    private static Log log = LogFactory.getLog(WrapperService.class);
    private Document page;
    private static List<StatusResponse> responseList = Collections.synchronizedList(new ArrayList<StatusResponse>());
    public WrapperService(){
    }

    public List<StatusResponse> getAllLinkStatusResponse(Document page)  {
        Set<String> links_set = null;
        try {
            if (null != page) {
                links_set = new HashSet<String>();
                Elements links = page.getElementsByTag("a");
                for (Element link : links) {
                    String href = link.attr("href");
                    // Only add to set valid java.net URI's and not duplicated urls
                    if (isValidURI(href)) {
                        links_set.add(href.trim());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Some internal failure happen in obtain all <a href=XXX > from page: : getLocalizedMessage:" + e.getLocalizedMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error ", e.getCause());
        }
        if (links_set.size() > 0) {
            return validateEachLinkStatus(links_set);
        } else {
            log.error("Page return is NULL or EMPTY, some sites can reject this connection, for ex. using CAPTCHA validation");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private List<StatusResponse> validateEachLinkStatus(Set<String> setOfLinks){
        // Configuring PoolingHttpClient Manager
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultMaxPerRoute(5);
        connManager.setMaxTotal(5);
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connManager)
                .build();

        int size = setOfLinks.size();
        // Create 1 thread for each link, once it'll be requested only the header there wont be so over charged process.
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        Collection<Callable<StatusResponse>> callables = new ArrayList<Callable<StatusResponse>>();
        for (String link: setOfLinks) {
            callables.add(createCallables(client, link));
        }
        List<StatusResponse> responseList = Collections.synchronizedList(new ArrayList<StatusResponse>());
        try {
            List<Future<StatusResponse>> taskFutureList = executorService.invokeAll(callables);
            for (Future<StatusResponse> future : taskFutureList) {
                StatusResponse value = future.get(500, TimeUnit.MILLISECONDS);
                responseList.add(value);
            }
        } catch (Exception e) {
            // Ignore all exceptions and add only valid links, i.e: exclude SSLHandshakeExecption, WrongURIRedirections, etc.
            log.error("Internal failure at invokeAll: getLocalizedMessage: " + e.getCause().getMessage());
        } finally{
            connManager.shutdown();
            executorService.shutdown();
        }
        return responseList;
    }

    private Callable<StatusResponse> createCallables(CloseableHttpClient client, String page){
        final String link = page;
        final CloseableHttpClient cli = client;
        return new Callable<StatusResponse>() {
            public StatusResponse call() throws Exception {
                if (isValidURI(link)) {
                    HttpGet get = new HttpGet(link);
                    HttpResponse response = cli.execute(get);
                    EntityUtils.consume(response.getEntity());
                    log.info("Task thread completed: " + Thread.currentThread().getName());
                    return responseStatus(response, link);
                }
                return null;
            }
        };
    }

    private StatusResponse responseStatus(HttpResponse response, String uri){
        boolean reacheable = false;
        String msg = null;
        Integer error_code = null;
        Integer code = response.getStatusLine().getStatusCode();
        if (code >= 200 & code < 300){
            reacheable = true;
        } else {
            error_code = code;
            msg = response.getStatusLine().getReasonPhrase();
        }
        return new StatusResponse(uri, reacheable, error_code, msg);
    }
    public static boolean isValidURI(String uri){
        try{
            new URL(uri).toURI();
        }catch (Exception e){
            return false;
        }
        return true;
    }
}

