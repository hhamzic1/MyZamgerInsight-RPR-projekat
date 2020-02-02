package ba.etf.unsa.rpr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class ZamgerApiDemo {


    private final CloseableHttpClient httpclient = HttpClients.createDefault();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ZAMGER_URL = "https://zamger.etf.unsa.ba";
    private static final String AUTH_URL = "/api_v5/auth";
    private static final String REPORT_PROGRESS_URL = "/index.php?sta=izvjestaj/progress&student=%d&razdvoji_ispite=1";


    public UserInfo login(final String username, final String password) {
        final HttpPost req = new HttpPost(ZAMGER_URL + AUTH_URL);
        List<NameValuePair> fields = new ArrayList<NameValuePair>();
        fields.add(new BasicNameValuePair("login", username));
        fields.add(new BasicNameValuePair("pass", password));
        CloseableHttpResponse res = null;
        try {
            req.setEntity(new UrlEncodedFormEntity(fields));
            res = httpclient.execute(req);
            final int statusCode = res.getStatusLine().getStatusCode();
            if (statusCode == 401 || statusCode == 403) {
                throw new IllegalArgumentException("Username or password not valid");
            }
            if (statusCode != 200) {
                throw new IllegalArgumentException("Login failed, status code: " + statusCode);
            }
            final String content = convertStreamToString(res.getEntity().getContent());
            //TODO: umjesto u mapu, bolje bi bilo konvertovati u ZamgerResponse.class npr ili nešto slično (tipizirati)
            final Map<String, Object> map = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {
            });
            if (!map.getOrDefault("success", "false").equals("true")) {
                throw new IllegalArgumentException("Username or password not valid");
            }
            final String sessionId = (String) map.get("sid");
            if (sessionId == null) {
                throw new IllegalArgumentException("Username or password not valid");
            }
            return new UserInfo(sessionId, (Integer) map.get("userid"));
        } catch (final Exception e) {
            throw new RuntimeException("exception while logIn: " + e.getMessage(), e);
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getProgress(final UserInfo userInfo) {
        final HttpGet req = new HttpGet(ZAMGER_URL + String.format(REPORT_PROGRESS_URL, userInfo.getId()));
        req.addHeader(new BasicHeader("7P/XE", "Cookie: PHPSESSID=" + userInfo.getSid())); //ovo 7P/XE i Cookie: jer je biblioteka glupa :D
        System.out.println("Headers: " + Arrays
                .stream(req.getAllHeaders())
                .flatMap(h -> Arrays.stream(h.getElements()))
                .map(h -> String.format("%s = %s", h.getName(), h.getValue()))
                .collect(Collectors.joining(", ")));
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(req);

            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) { //TODO: dodati i 401, 403 i reći "sesija istekla"
                throw new IllegalArgumentException("getProgress failed, status code: " + statusCode);
            }
            return convertStreamToString(response.getEntity().getContent());
        } catch (final Exception e) {
            throw new RuntimeException("exception while getProgress: " + e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
