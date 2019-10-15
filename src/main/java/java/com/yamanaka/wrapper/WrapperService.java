package java.com.yamanaka.wrapper;

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
import java.com.yamanaka.wrapper.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class WrapperService{

    public List<StatusResponse> getAllLinkStatusResponse(Document page)  {
        Set<String> linkSet = new LinkedHashSet<String>();
        if (null != page) {
            Elements links = page.getElementsByTag("a");
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

    // https://medium.com/@senanayake.kalpa/fantastic-completablefuture-allof-and-how-to-handle-errors-27e8a97144a0
    // https://stackoverflow.com/questions/27723546/completablefuture-supplyasync-and-thenapply
    // nurkiewicz.com/2013/05/java-8-completablefuture-in-action.html

    public List<StatusResponse> validateEachLinkStatus(Set<String> setOfLinks)  {
        List<StatusResponse> responseList = Collections.synchronizedList(new ArrayList<StatusResponse>());
        // Configuring PoolingHttpClient Manager, following lib documentation related to multithreaded request execution.
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connManager)
                .build();
        // Using newCachedThreadPool to re-use thread and not overload usage.
        ExecutorService executor = Executors.newCachedThreadPool();
        List<CompletableFuture<StatusResponse>> futureStatus = setOfLinks.stream().
                map(link -> CompletableFuture.supplyAsync(() -> getHostStatus(client, link), executor)).
                collect(Collectors.<CompletableFuture<StatusResponse>>toList());

        CompletableFuture<List<StatusResponse>> allDone = sequence(futureStatus);
        try {
            responseList = allDone.get();
        } catch (InterruptedException e) {
            Utils.log.error("InterruptedException = " + e.toString());
        } catch (ExecutionException e) {
            Utils.log.error("ExecutionException = " + e.toString());
        } finally {
            connManager.shutdown();
            executor.shutdown();
        }
        return responseList;
    }

    private static <StatusResponse> CompletableFuture<List<StatusResponse>> sequence(List<CompletableFuture<StatusResponse>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v -> futures.stream().
                map(future -> future.join()).
                collect(Collectors.<StatusResponse>toList())
        );
    }

    private StatusResponse getHostStatus(CloseableHttpClient cli, String link) {
        Optional<StatusResponse> sr = Optional.empty();
        try {
            HttpGet get = new HttpGet(link);
            HttpResponse response = cli.execute(get);
            EntityUtils.consume(response.getEntity());
            sr = Optional.of(getStatusResponse(link,response.getStatusLine().getStatusCode(),response.getStatusLine().getReasonPhrase()));
            if (Utils.DBG) Utils.log.info("Completed: " + Thread.currentThread().getName() + " Url= "+ link);
        } catch (HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            Utils.log.error("HttpClientErrorException Body = " + body);
            Utils.log.error("Task thread completed in Exception: " + Thread.currentThread().getName());
        } catch (ClientProtocolException e) {
            Utils.log.error("ClientProtocolException = " + e.getCause());
        } catch (IOException e) {
            Utils.log.error("IOException = " + e.getMessage());
        }
        Utils.log.debug(sr);
        return sr.orElseGet(() -> new StatusResponse(link, false, 500, "Internal Server Error - Exception"));
    }

    public StatusResponse getStatusResponse(String link, int code, String msg){
        boolean reach = false;
        String error_msg = null;
        if (code >= 200 & code < 300){
            reach = true;
        } else {
            error_msg = msg;
        }
        return new StatusResponse(link,reach, code, error_msg);
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

