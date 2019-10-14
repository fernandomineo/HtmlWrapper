package wrapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import wrapper.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class WrapperService{

    public List<StatusResponse> getAllLinkStatusResponse(Document page)  {
        Set<String> linkSet = new LinkedHashSet<String>();
        if (null != page) {
            Elements links = page.select("a[href]");
            List<String> listLinks = links.eachAttr("abs:href");
            if (listLinks.size() > 0)
                if (Utils.DBG) listLinks.stream().forEach(link -> Utils.log.info(link));
                linkSet = listLinks.stream().filter(link -> isValidURI(link.trim())).collect(Collectors.toSet());
            }
        if (linkSet.size() > 0) {
            return validateEachLinkStatus(linkSet);
        } else {
            Utils.log.error("Page return is NULL or EMPTY, some sites can reject this connection, for ex. using CAPTCHA validation");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    public List<StatusResponse> validateEachLinkStatus(Set<String> setOfLinks){
        // Configuring PoolingHttpClient Manager, following lib documentation related to multithreaded request execution.
        // http://hc.apache.org/httpcomponents-client-4.3.x/tutorial/html/connmgmt.html#d5e380
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connManager)
                .build();

        // Ussing newCachedThreadPool to re-use thread and not overload usage.
        ExecutorService executor = Executors.newCachedThreadPool();
        Collection<Callable<StatusResponse>> callables = new ArrayList<Callable<StatusResponse>>();

        setOfLinks.stream().forEach(link -> callables.add(createCallables(client, link)));

        List<StatusResponse> responseList = Collections.synchronizedList(new ArrayList<StatusResponse>());
        try {
            List<Future<StatusResponse>> taskFutureList = executor.invokeAll(callables);
            taskFutureList.stream().forEach(future -> {
                Optional<StatusResponse> sr = Optional.empty();
                try {
                    sr = Optional.of(future.get());
                    responseList.add(sr.orElseGet(() -> new StatusResponse("N/A",false, 500, "Internal Server Error - Exception")));
                } catch (InterruptedException e) {
                    Utils.log.error( "InterruptedException = " + e.toString());
                    //e.printStackTrace();
                } catch (ExecutionException e) {
                    Utils.log.error("ExecutionException = " + e.toString());
                }
            });
        } catch (InterruptedException e) {
            //    e.printStackTrace();
              Utils.log.error("InterruptedException = " + e.toString());
        } finally{
            connManager.shutdown();
            executor.shutdown();
        }
        return responseList;
    }

    private Callable<StatusResponse> createCallables(CloseableHttpClient client, String page){
        final String link = page;
        final CloseableHttpClient cli = client;
        Callable<StatusResponse> callable = () -> call(cli,link);
        return callable;
        /*{
            /*public StatusResponse call()  {
                if (isValidURI(link)) {
                    try {
                        HttpGet get = new HttpGet(link);
                        HttpResponse response = cli.execute(get);
                        EntityUtils.consume(response.getEntity());
                        Utils.log.info("Task thread completed: " + Thread.currentThread().getName());
                        return getStatusResponse(link,response);
                    } catch (HttpClientErrorException e) {
                        String body = e.getResponseBodyAsString();
                        Utils.log.error("HttpClientErrorException Body = " + body);
                        Utils.log.error("Task thread completed in Exception: " + Thread.currentThread().getName());
                    } catch (ClientProtocolException e) {
                        Utils.log.error("ClientProtocolException = " + e.getCause());
                    } catch (IOException e) {
                        Utils.log.error("IOException = " + e.getMessage());
                    }
                }
                return null;
            }*/
        //};
    }

    private StatusResponse call(CloseableHttpClient cli, String link) throws InterruptedException {
        Optional<StatusResponse> sr = Optional.empty();
        try {
            HttpGet get = new HttpGet(link);
            HttpResponse response = cli.execute(get);
            EntityUtils.consume(response.getEntity());
            Utils.log.info("Thread completed: " + Thread.currentThread().getName());
            if (Utils.DBG) Utils.log.info("Link: " + link);
            sr = Optional.of(getStatusResponse(link,response));
        } catch (HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            Utils.log.error("HttpClientErrorException Body = " + body);
            Utils.log.error("Task thread completed in Exception: " + Thread.currentThread().getName());
        } catch (ClientProtocolException e) {
            Utils.log.error("ClientProtocolException = " + e.getCause());
        } catch (IOException e) {
            Utils.log.error("IOException = " + e.getMessage());
        }
        return sr.orElseGet(() -> new StatusResponse(link,false, 500, "Internal Server Error - Exception"));
    }

    private StatusResponse getStatusResponse(String link, HttpResponse response){
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
        return new StatusResponse(link, reacheable, error_code, msg);
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

