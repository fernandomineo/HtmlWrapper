import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wrapper.WrapperApplication;

import static org.junit.Assert.*;

public class WrapperControllerTest extends AbstractWrapperTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testWrongParamName() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("url", AbstractWrapperTest.GOOGLE_HOST) // Changed from URI to URL
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    public void getEmptyHost() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractWrapperTest.EMPTY_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(406, status);
    }

    @Test
    public void getUnknownHost() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractWrapperTest.UNKNOWN_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(503, status);
    }

    @Test
    public void getIncompleteHost() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractWrapperTest.INCOMPLETE_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(503, status);
    }

    @Test
    public void getAmazonHost() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractWrapperTest.AMAZON_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(500, status);
    }

    @Test
    public void getNumberedAndPortHost() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractWrapperTest.NUMBER_PORT_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        assertTrue(isValid);
    }

    @Test
    public void getGoogleUrl() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractWrapperTest.GOOGLE_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        assertTrue(isValid);
    }

    @Test
    public void getUolPortalUrl() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractWrapperTest.UOL_PORTAL_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        assertTrue(isValid);
    }

    @Test
    public void getExploratory_1() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractWrapperTest.EXPL_HOST1)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        assertTrue(isValid);
    }

    @Test
    public void getExploratory_2() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractWrapperTest.EXPL_HOST2)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        assertTrue(isValid);
    }

    @Test
    public void getExploratory_3() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractWrapperTest.EXPL_HOST3)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        assertTrue(isValid);
    }

    @Test
    public void getExploratory_4() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractWrapperTest.EXPL_HOST4)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        boolean isValid = isValidJson(mvcResult.getResponse().getContentAsString());
        assertTrue(isValid);
    }

    public boolean isValidJson(String input){
        try {
            new JSONArray(input);
        } catch (JSONException ex1) {
            return false;
        }
        return true;
    }
}

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WrapperApplication.class)
@WebAppConfiguration
class AbstractWrapperTest {
    protected MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    protected void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    protected static String EMPTY_HOST = "";
    protected static String INCOMPLETE_HOST = "http://www.";
    protected static String UNKNOWN_HOST = "http://www.terrrraa.com.br";
    protected static String NUMBER_PORT_HOST = "http://216.58.202.206:80"; // google.com host
    protected static String AMAZON_HOST = "http://www.amazon.com.br"; // Page with no <a href> lings

    // Short list ~ 15 Links in page
    protected static String GOOGLE_HOST = "http://www.google.com.br";
    // Large list ~ 450 links in page
    protected static String UOL_PORTAL_HOST = "http://www.uol.com.br";

    // Exploratory Tests = 4 links
    protected static String EXPL_HOST1 = "http://www.terra.com.br";
    protected static String EXPL_HOST2 = "http://www.yahoo.com.br";
    protected static String EXPL_HOST3 = "http://www.meutimao.com.br";
    protected static String EXPL_HOST4 = "http://www.milliondollarhomepage.com/";
}

