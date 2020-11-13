package cn.hahoo.boilerplate.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ClientUtil {

    public static Map<String, String> getClientInfo(HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<>();

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress.equals("0:0:0:0:0:0:0:1")) ipAddress = "127.0.0.1";

        map.put("ip", ipAddress);
        map.put("userAgent", request.getHeader("User-Agent"));
        map.put("clientType", "10");
        map.put("clientAgent", request.getHeader("Client-Agent"));

        return  map;
    }
}
