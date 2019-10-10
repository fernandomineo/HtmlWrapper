package unittest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;

public class WrapperControllerTest extends AbstractTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void getEmptyHost() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractTest.EMPTY_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(406, status);
    }

    @Test
    public void getUnknownHost() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractTest.UNKNOWN_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(503, status);
    }

    @Test
    public void getIncompleteHost() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractTest.INCOMPLETE_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(406, status);
    }

    @Test
    public void getNumberedAndPortHost() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractTest.NUMBER_PORT_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void getGoogleUrl() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractTest.GOOGLE_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void getUolPortalUrl() throws Exception {
        // Short test ~ 15 links
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wrapper")
                .param("uri", AbstractTest.UOL_PORTA_HOST)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

}
