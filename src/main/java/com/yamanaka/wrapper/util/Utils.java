package com.yamanaka.wrapper.util;

import com.yamanaka.wrapper.service.WrapperService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;

public class Utils {

    public static Log log = LogFactory.getLog(WrapperService.class);
    public static String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36";
    public static Boolean DBG = false;

    public static boolean isValidURI(String uri) {
        try {
            new URL(uri).toURI();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
