package wrapperservice;

import org.apache.commons.validator.UrlValidator;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;


public class WrapperService{

    private String uri;
    private UrlValidator validator = new UrlValidator();
    private static List<StatusResponse> responseList = Collections.synchronizedList(new ArrayList<StatusResponse>());
    public WrapperService(String uri){
        this.uri = uri;
    }


    public Set<String> createSetOfLinks(String uri)  {
        Set<String> links_set = null;
        Document page = null;
        try{
            page = Jsoup.connect(uri).get();
        } catch (UnknownHostException e) {
            throw new ExceptionWrapper.UnknownHost();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        if (null != page) {
            links_set = new HashSet<String>();
             Elements links = page.getElementsByTag("a");
             for (Element link : links) {
                  String href = link.attr("href");
                  // Only add to set valid and not duplicated urls
                  if (validator.isValid(href)) {
                      links_set.add(href.trim());
                  }
              }
        }
        return links_set;
    }

    public List<StatusResponse> createListOfStatusResponse(Set<String> setOfLinks){
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
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
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
                System.out.println(String.format("starting expensive task thread %s",
                        Thread.currentThread().getName()));
                HttpGet get = new HttpGet(link);
                HttpResponse response = cli.execute(get);
                EntityUtils.consume(response.getEntity());
                System.out.println(String.format("finished expensive task thread %s",
                        Thread.currentThread().getName()));
                return responseStatus(response, link);
            }
        };
    }

    public StatusResponse responseStatus(HttpResponse response, String uri){
        Boolean reacheable = false;
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

}

