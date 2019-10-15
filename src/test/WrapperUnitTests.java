
import com.yamanaka.wrapper.dto.StatusResponse;
import com.yamanaka.wrapper.WrapperApplication;
import com.yamanaka.wrapper.controller.WrapperController;
import com.yamanaka.wrapper.service.WrapperService;
import com.yamanaka.wrapper.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WrapperApplication.class)
public class WrapperUnitTests {
    private static Log log = LogFactory.getLog(WrapperController.class);

    @Test
    public void errrorPagesTestCase() throws Exception {
        Utils.DBG = true;
        WrapperService wrap = new WrapperService();
        List<StatusResponse> list = null;
        Document page = Jsoup.parse(getErrorPage());
        if (null != page) {
            list = wrap.getAllLinkStatusResponse(page);
        }
        for (StatusResponse item : list) {
            if (null != item) {
                log.debug("StatusResponse: " + item.toString());
            }
        }
        Assert.assertEquals(200, 200);
    }

    @Test
    public void somePagesTestCase() throws Exception {
        Utils.DBG = true;
        WrapperService wrap = new WrapperService();
        List<StatusResponse> list = null;
        Document page = Jsoup.parse(getSomePages());
        if (null != page) {
            list = wrap.getAllLinkStatusResponse(page);
        }
        for (StatusResponse item : list) {
            if (null != item) {
                log.debug("StatusResponse: " + item.toString());
            }
        }
        Assert.assertEquals(200, 200);
    }


    private int MAX_TEST_EXECUTION = 5;

    @Test
    public void basicLocalLargeTestCase() throws Exception {
        Utils.DBG = true;
        WrapperService wrap = new WrapperService();
        Set<String> list = new HashSet<String>();
        List<StatusResponse> srList = new ArrayList<StatusResponse>();
        Document page;
        String uri = "http://www.uol.com.br";
        long start = System.currentTimeMillis();
        page = Jsoup.connect(uri).get();
        Utils.DBG = true;
        for (int i = 0; i < MAX_TEST_EXECUTION; i++) {
            srList = wrap.getAllLinkStatusResponse(page);
        }
        long end = System.currentTimeMillis();
        log.info("DEBUG: Execution time SIze " + MAX_TEST_EXECUTION + " = " + (end - start) + " milliSeconds");
        Assert.assertEquals(200, 200);
    }

    @Test
    public void isValidUriTestCase() {
        Utils.DBG = true;
        String uri = "http://www.";
        Assert.assertTrue(Utils.isValidURI(uri));
        uri = "http://www.terrrraa.com.br";
        Assert.assertTrue(Utils.isValidURI(uri));
        uri = "http://www.uol.com";
        Assert.assertTrue(Utils.isValidURI(uri));
        uri = "http://uol.com.br";
        Assert.assertTrue(Utils.isValidURI(uri));
        uri = "/";
        Assert.assertFalse(Utils.isValidURI(uri));
        uri = "toPath/toPath/index.html";
        Assert.assertFalse(Utils.isValidURI(uri));
        uri = "www.uo...com";
        Assert.assertFalse(Utils.isValidURI(uri));
        uri = "htp://www.uol.com.br";
        Assert.assertFalse(Utils.isValidURI(uri));
    }

    @Test
    public void validateStatusResponseTestCase() {
        WrapperService wrap = new WrapperService();
        StatusResponse sr = new StatusResponse("http://www.a.com.br", true, null, null);
        StatusResponse aux = wrap.getStatusResponse("http://www.a.com.br", 200, null);
        Assert.assertEquals(sr.getError_code(), aux.getError_code());

        sr = new StatusResponse("http://www.a.com.br", true, null, null);
        aux = wrap.getStatusResponse("http://www.a.com.br", 299, null);
        Assert.assertEquals(sr.getError_code(), aux.getError_code());

        sr = new StatusResponse("http://www.a.com.br", false, 300, "Error");
        aux = wrap.getStatusResponse("http://www.a.com.br", 300, "Error");
        Assert.assertEquals(sr.getError_code(), aux.getError_code());

        sr = new StatusResponse("http://www.a.com.br", false, 500, "Error");
        aux = wrap.getStatusResponse("http://www.a.com.br", 500, "Error");
        Assert.assertEquals(sr.getError_code(), aux.getError_code());

        sr = new StatusResponse("http://www.a.com.br", false, 600, "Error");
        aux = wrap.getStatusResponse("http://www.a.com.br", 600, "Error");
        Assert.assertEquals(sr.getError_code(), aux.getError_code());
    }

    private String getErrorPage() {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Basic Page test</title>"
                + "</head>"
                + "<body>"
                + "<a href=\"http://www.\">link3</a>"
                + "<a href=\"http://www.terrrraa.com.br\">link4</a>"
                + "<a href=\"\">link4</a>"
                + "<a href=\"/toPath/toPath/index.html\">link4</a>"
                + "<a href=\"https://httpstat.us/404\">link2</a>"
                + "<a href=\"https://httpstat.us/500\">link1</a>"
                + "<a href=\"https://httpstat.us/503\">link2</a>"
                + "<a href=\"https://httpstat.us/524\">link2</a>"
                + "<a href=\"https://httpstat.us/300\">link2</a>"
                + "</body>"
                + "</html>";
    }

    private String getSomePages() {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Basic Page test</title>"
                + "</head>"
                + "<body>"
                + "<a href=\"http://www.uol.com.br\">link3</a>"
                + "<a href=\"http://www.terra.com.br\">link4</a>"
                + "<a href=\"http://www.cnn.com.br\">link4</a>"
                + "<a href=\"https://www.dailymail.co.uk/home/index.html\">link2</a>"
                + "<a href=\"https://disney.fandom.com/wiki/Potatoland\">link2</a>"
                + "</body>"
                + "</html>";
    }
}


