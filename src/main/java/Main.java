import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
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
        getPost();
        nasaFromJson();
        getFile("https://apod.nasa.gov/apod/image/2202/Chamaeleon_RobertEder1024.jpg",
                "Chamaeleon_RobertEder1024.jpg");
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

    public static void nasaFromJson() throws IOException {

        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=kPedpeLjmVsmSuMTGCb0zQfvYfcK7ppBuExfjOJr");
        //request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        //отправка запроса
        CloseableHttpResponse response = httpClient.execute(request);

        //вывод полученных заголовков
        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        Nasa nasa = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );

        System.out.println(nasa);

        response.close();
        httpClient.close();
        //return nasa;
    }

    public static void getFile(String urlStr, String file) throws IOException{

            URL url = new URL(urlStr);
            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            FileOutputStream fis = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count=0;
            while((count = bis.read(buffer,0,1024)) != -1)
            {
                fis.write(buffer, 0, count);
            }
            fis.close();
            bis.close();
    }
}
