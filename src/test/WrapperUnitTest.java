import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wrapper.StatusResponse;
import wrapper.WrapperApplication;
import wrapper.WrapperController;
import wrapper.WrapperService;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= WrapperApplication.class)
public class WrapperUnitTest {
    private static Log log = LogFactory.getLog(WrapperController.class);

    @Test
    public void basicLocalTestCase() throws Exception {
        WrapperService wrap = new WrapperService();
        List<StatusResponse> list = null;
        Document page = Jsoup.parse(getTestHtml());
        if (null != page) {
            list = wrap.getAllLinkStatusResponse(page);
        }
        for (StatusResponse item : list) {
            if (null != item){
                log.debug("StatusResponse: "+ item.toString());
            }
        }
        Assert.assertEquals(200, 200);
    }

    private String getTestHtml(){
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Basic Page test</title>"
                + "</head>"
                + "<body>"
                + "<a href=\"https://httpstat.us/404\">link2</a>"
                + "<a href=\"https://httpstat.us/500\">link1</a>"
                + "<a href=\"https://httpstat.us/503\">link2</a>"
                + "<a href=\"https://httpstat.us/524\">link2</a>"
                + "<a href=\"http://www.\">link3</a>"
                + "<a href=\"http://www.terrrraa.com.br\">link4</a>"
                + "<a href=\"http://216.58.202.206:80\">link5</a>"
                + "<a href=\"http://www.amazon.com.br\">link6</a>"
                + "<a href=\"https://disney.fandom.com/wiki/Potatoland\">link6</a>"
                + "</body>"
                + "</html>";
    }
}

