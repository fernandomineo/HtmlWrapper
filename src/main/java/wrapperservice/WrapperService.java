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
import java.util.*;
import java.util.concurrent.*;


public class WrapperService{

    private String uri;
    private UrlValidator validator = new UrlValidator();
    private static List<StatusResponse> responseList = Collections.synchronizedList(new ArrayList<StatusResponse>());


    public WrapperService(String uri){
        this.uri = uri;
    }

    public Set<String>  createSetOfLinks(String uri)  {
        Set<String> links_set = new HashSet<String>();
        try{
            Document page = Jsoup.connect(uri).get();
            Elements links = page.getElementsByTag("a");
            for (Element link : links) {
                String href = link.attr("href");
                // Only add to set valid and not duplicated urls
                if (validator.isValid(href)){
                    links_set.add(href.trim());
                }
            }
            //System.out.println(links_set);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return links_set;
    }
    /*public List<StatusResponse> createSetOfStatus(Set<String> setOfLinks){
        List<StatusResponse> responseList = new ArrayList<StatusResponse>();
        StatusResponse res = null;
        HttpClient httpClient = HttpClients.createDefault();
        String uri = "http://www.uol.com.br";
        HttpGet httpGet = new HttpGet(uri);

        StatusResponse a = new StatusResponse("http://www.terra.com.br", true,200, "OK");
        list.add(a);
        try {
            Boolean reacheable = false;
            HttpResponse response = httpClient.execute(httpGet);
            Integer code = response.getStatusLine().getStatusCode();
            if (code >= 200 & code < 300){
                reacheable = true;
            }
            String msg = response.getStatusLine().getReasonPhrase();
            list.add(new StatusResponse(uri,reacheable,code,msg));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }*/

    public List<StatusResponse> createListOfStatusResponse2(Set<String> setOfLinks){
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultMaxPerRoute(5);
        connManager.setMaxTotal(5);
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connManager)
                .build();

        int size = setOfLinks.size();
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        Collection<Callable<StatusResponse>> callables = new ArrayList<Callable<StatusResponse>>();
        int i =  0;
        for (String link: setOfLinks) {
            callables.add(createCallables(client, link));
            i++;
        }
        List<StatusResponse> responseList = Collections.synchronizedList(new ArrayList<StatusResponse>());
        try {
            List<Future<StatusResponse>> taskFutureList = executorService.invokeAll(callables);
            for (Future<StatusResponse> future : taskFutureList) {
                StatusResponse value = future.get(4, TimeUnit.SECONDS);
                responseList.add(value);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally{
            executorService.shutdown();
        }
        return responseList;

    }

    // https://github.com/briansjavablog/multi-threading-with-executorservice/blob/master/src/main/java/com/briansjavablog/concurrency/threads/ExecutorServiceCallableSample.java
    private Callable<StatusResponse> createCallables(CloseableHttpClient client, String page){
        final String link = page;
        final CloseableHttpClient cli = client;
        return new Callable<StatusResponse>() {
            public StatusResponse call() throws Exception {
                HttpGet get = new HttpGet(link);
                HttpResponse response = cli.execute(get);
                return responseStatus(response);
            }
        };
    }

    public StatusResponse responseStatus(HttpResponse response){
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
        return new StatusResponse("www.uol.com.br", reacheable, error_code, msg);
    }


    public void createListOfStatusResponse(Set<String> setOfLinks){

        // Setup PoolingHttpClientConnection Manager
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultMaxPerRoute(5);
        connManager.setMaxTotal(5);
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connManager)
                .build();
        // Set number max of thread the same of set of links size
        int lenght = setOfLinks.size();
        MultiHttpClientConnThread[] threads = new  MultiHttpClientConnThread[lenght];
        int i =  0;
        for (String link: setOfLinks) {
            HttpGet get = new HttpGet(link);
            threads[i] = new MultiHttpClientConnThread(client, get);
            i++;
        }
        /*for(int i = 0; i < threads.length; i++){
            HttpGet get = new HttpGet(setOfLinks.stream().forEach(uri););
            threads[i] = new MultiHttpClientConnThread(client, get, connManager);
        }*/
        for (MultiHttpClientConnThread thread: threads) {
            thread.start();
        }
        for (MultiHttpClientConnThread thread: threads) {
            try {
                thread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            client.close();
            connManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

class MultiHttpClientConnThread extends Thread {
    private CloseableHttpClient client;
    private HttpGet get;

    public MultiHttpClientConnThread(CloseableHttpClient client, HttpGet get) {
        this.client = client;
        this.get = get;
    }

    // standard constructors
    public void run(){
        try {
            HttpResponse response = client.execute(get);
            //responseStatus(response).toString();
            EntityUtils.consume(response.getEntity());
        } catch (ClientProtocolException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

