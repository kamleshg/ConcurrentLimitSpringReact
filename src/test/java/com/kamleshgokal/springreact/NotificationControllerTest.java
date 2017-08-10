package com.kamleshgokal.springreact;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.IntStream;

public class NotificationControllerTest {

    @Test
    public void exampleTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject("http://localhost:8080/rollupStatus/10", String.class);
    }

    @Test
    public void testBlockingRequests() throws Exception {

        sendGet("http://localhost:8080/rollupStatus/1/dcs/25H-1-1");
        sendGet("http://localhost:8080/rollupStatus/2/dcs/25H-2-1");
        sendGet("http://localhost:8080/rollupStatus/3/dcs/25H-3-1");
        sendGet("http://localhost:8080/rollupStatus/1/dcs/25H-1-2");
        sendGet("http://localhost:8080/rollupStatus/2/dcs/25H-2-2");
        sendGet("http://localhost:8080/rollupStatus/3/dcs/25H-3-2");
    }

    @Test
    public void loadTest() throws Exception {
        for(int requests =1; requests<=50; requests++) {
            for(int dcs=1; dcs<=5; dcs++) {
                sendGet("http://localhost:8080/rollupStatus/" + requests + "/dcs/" + dcs + "");
            }
        }
    }

    @Test
    public void testAcquireLockTimeout() throws Exception {
        sendGet("http://localhost:8080/rollupStatus/1/dcs/25H-1-1/wait/40000");
        sendGet("http://localhost:8080/rollupStatus/2/dcs/25H-2-1");
        sendGet("http://localhost:8080/rollupStatus/3/dcs/25H-3-3");
        sendGet("http://localhost:8080/rollupStatus/1/dcs/25H-1-2");//<------ Try this guy.....
        sendGet("http://localhost:8080/rollupStatus/4/dcs/25H-4-1");
        sendGet("http://localhost:8080/rollupStatus/1/dcs/25H-1-2");
    }

    private void sendGet(String url) throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println(result.toString());

    }


    @Test
    public void test() {
        IntStream.range(1,10).forEach((i) -> System.out.println("i=" + i));
    }
}
