package unittest;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wrapperservice.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class AbstractTest {
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
