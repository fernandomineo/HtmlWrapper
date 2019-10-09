package wrapperservice;

import org.apache.commons.validator.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WrapperService {

    private String uri;
    private UrlValidator validator = new UrlValidator();

    public WrapperService(String uri){
        this.uri = uri;
    }

    public void createSetOfLinks(String uri)  {
        Set<String> links_set = new HashSet<String>();
        try{
            Document page = Jsoup.connect(uri).get();
            Elements links = page.getElementsByTag("a");
            for (Element link : links) {
                String href = link.attr("href");
                // Only add to set valid and not duplicated urls
                if (validator.isValid(href)){
                    links_set.add(href);
                }
            }
            //System.out.println(links_set);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
