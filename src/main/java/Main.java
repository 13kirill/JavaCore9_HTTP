import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static ObjectMapper mapper = new ObjectMapper();
    /*создание клиента с дэфолтными параметрами
        CloseableHttpClient httpClient = HttpClients.createDefault();*/
    public static CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setUserAgent("Cats analysis")
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(30000)
                    .setRedirectsEnabled(false)
                    .build())
            .build();

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
        //getPost();
        nasaFromJson();
    }

    public static void getPost() throws IOException {

        //создание объекта запроса с произвольными заголовками
        HttpGet request = new HttpGet("https://raw.githubusercontent.com/netology-code/jd-homeworks/" +
                "master/http/task1/cats");

        //отправка запроса
        CloseableHttpResponse response = httpClient.execute(request);

        //вывод полученных заголовков
        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

//        чтение тела ответа
//        String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
//        System.out.println(body);

        List<Post> posts = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );

        posts.stream().filter(value -> value.getUpvotes() != 0 && value.getUpvotes() > 0)
                .forEach(System.out::println);


        response.close();
        httpClient.close();
    }

    public static Nasa nasaFromJson() throws IOException {

        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=kPedpeLjmVsmSuMTGCb0zQfvYfcK7ppBuExfjOJr");
        //request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        //отправка запроса
        CloseableHttpResponse response = httpClient.execute(request);

        //вывод полученных заголовков
        System.out.println("Headers--------------------");
        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        Nasa nasa = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );
        System.out.println(nasa);
        response.close();

        String url = nasa.getUrl();
        HttpGet request2 = new HttpGet(url);
        CloseableHttpResponse response2 = httpClient.execute(request2);
        HttpEntity entity = response.getEntity();


        try {

            URL uri = new URL(url);
            //InputStream is = entity.getContent();
            InputStream is = uri.openStream();
            String[] filePath = nasa.getUrl().split("/");
            FileOutputStream fos = new FileOutputStream(new File(filePath[6]));
            int inByte;
            while ((inByte = is.read()) != -1) {
                fos.write(inByte);
                fos.flush();
            }
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
        response2.close();
        httpClient.close();
        return nasa;
    }

//    public static Nasa getFileName() throws IOException {
//        String url = nasaFromJson().getUrl();
//        HttpGet request2 = new HttpGet(url);
//        CloseableHttpResponse response2 = httpClient.execute(request2);
//        response2.close();
//        System.out.println("Headers--------------------");
//        Arrays.stream(response2.getAllHeaders()).forEach(System.out::println);
//        Nasa nasa = mapper.readValue(
//                response2.getEntity().getContent(),
//                new TypeReference<>() {
//                }
//        );
//        httpClient.close();
//        return nasa;
//    }

//    public static void getFile(String urlStr, String file) throws IOException {
//
//        URL url = new URL(urlStr);
//        BufferedInputStream bis = new BufferedInputStream(url.openStream());
//        FileOutputStream fis = new FileOutputStream(file);
//        byte[] buffer = new byte[1024];
//        int count = 0;
//        while ((count = bis.read(buffer, 0, 1024)) != -1) {
//            fis.write(buffer, 0, count);
//        }
//        fis.close();
//        bis.close();
//    }
}
