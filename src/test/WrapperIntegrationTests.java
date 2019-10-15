import com.yamanaka.wrapper.WrapperApplication;
import com.yamanaka.wrapper.controller.WrapperController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WrapperApplication.class)
public class WrapperIntegrationTests {
    private static String INCOMPLETE_HOST = "http://www.";
    private static String EMPTY_HOST = "";
    private static String UNKNOWN_HOST = "http://www.terrrraa.com.br";
    private static String NUMBER_PORT_HOST = "http://216.58.202.206:80";
    private static String AMAZON_HOST = "http://www.amazon.com.br";
    //Short list ~ 15 Links in page
    private static String GOOGLE_HOST = "http://www.google.com.br";
    // Large list ~ 450 links in page
    private static String UOL_PORTAL_HOST = "http://www.uol.com.br";
    // Exploratory Tests = 4 links
    private static String EXPL_HOST1 = "http://www.terra.com.br";
    private static String EXPL_HOST2 = "http://www.yahoo.com.br";
    private static String EXPL_HOST3 = "http://www.meutimao.com.br";
    private static String EXPL_HOST4 = "http://www.milliondollarhomepage.com/";

    private static Log log = LogFactory.getLog(WrapperController.class);
    private MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void wrongParamNameTestCase() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("url", WrapperIntegrationTests.GOOGLE_HOST) // Changed from URI to URL
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(400, status);
    }

    @Test
    public void emptyHostTestCase() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", WrapperIntegrationTests.EMPTY_HOST)
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(406, status);
    }

    @Test
    public void unknownHostTestCase() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", WrapperIntegrationTests.UNKNOWN_HOST)
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(503, status);
    }

    @Test
    public void incompleteHostTestCase() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", WrapperIntegrationTests.INCOMPLETE_HOST)
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(503, status);
    }

    @Test
    public void amazonHostTestCase() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", WrapperIntegrationTests.AMAZON_HOST)
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        Assert.assertTrue(isValid);
    }

    @Test
    public void numberedAndPortHostTestCase() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", WrapperIntegrationTests.NUMBER_PORT_HOST)
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        Assert.assertTrue(isValid);
    }

    @Test
    public void googleUrlTestCase() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", WrapperIntegrationTests.GOOGLE_HOST)
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        Assert.assertTrue(isValid);
    }

    @Test
    public void uolPortalUrlTestCase() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", WrapperIntegrationTests.UOL_PORTAL_HOST)
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        Assert.assertTrue(isValid);
    }

    @Test
    public void exploratory1TestCase() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", WrapperIntegrationTests.EXPL_HOST1)
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        Assert.assertTrue(isValid);
    }

    @Test
    public void exploratory2TestCase() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", WrapperIntegrationTests.EXPL_HOST2)
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        Assert.assertTrue(isValid);
    }

    @Test
    public void exploratory3TestCase() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", WrapperIntegrationTests.EXPL_HOST3)
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        Assert.assertTrue(isValid);
    }

    @Test
    public void exploratory4TestCase() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", WrapperIntegrationTests.EXPL_HOST4)
                .param("debug", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        Assert.assertTrue(isValid);
    }

    public boolean isValidJson(String input) {
        try {
            new JSONArray(input);
        } catch (JSONException ex1) {
            return false;
        }
        return true;
    }
}
