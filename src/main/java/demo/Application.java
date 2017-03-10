package demo;

import com.google.gson.JsonArray;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.method;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        StubServer stubServer = null;
        try {
            JsonArray array1 = new JsonArray();
            JsonArray array2 = new JsonArray();
            JsonArray array3 = new JsonArray();
            for (int i = 0; i < 5; i++) {
                array1.add("yandex" + i);
                array2.add("bing" + i);
                array3.add("google" + i);
            }
            stubServer = new StubServer(8081);
            SpringApplication.run(Application.class, args);

            stubServer.run();
            //whenHttp(stubServer).match(method(Method.GET).startsWithUri("/google")).then(status(HttpStatus.NOT_FOUND_404));
            whenHttp(stubServer).match(method(Method.GET).startsWithUri("/google")).then(stringContent(array3.toString()));
            whenHttp(stubServer).match(method(Method.GET).startsWithUri("/yandex")).then(stringContent(array1.toString()));
            whenHttp(stubServer).match(method(Method.GET).startsWithUri("/bing")).then(stringContent(array2.toString()));

        } catch (Exception e) {}
    }

}