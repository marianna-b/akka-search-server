package demo.actor;

import akka.actor.UntypedActor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import demo.model.Message;
import demo.model.UrlReader;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component("childActor")
@Scope("prototype")
public class ChildActor extends UntypedActor {

    private String source;
    private static final int PORT = 8081;

    public ChildActor(CompletableFuture<Message> future, String source) {
        this.source = source;
    }

    private List<String> requestData(String str) {
        String result = new UrlReader().readAsText("http://localhost:" + PORT + "/" + source + "?" + str);
        JsonArray entries = (JsonArray) new JsonParser().parse(result);
        List<String> infos = new ArrayList<>(entries.size());
        for (JsonElement e : entries) {
            infos.add(e.getAsString());
        }
        return infos;
    }
    @Override
    public void onReceive(Object request) throws Exception {
        if (request instanceof String) {
            getSender().tell(new Message(requestData((String)request), getSelf().path().name()), getSelf());
        } else {
            unhandled(request);
        }
        getContext().stop(self());
    }
}
