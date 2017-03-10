package demo.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import demo.di.SpringExtension;
import demo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Component("masterActor")
@Scope("prototype")
public class MasterActor extends UntypedActor {

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private SpringExtension springExtension;

    final private CompletableFuture<Message> future;
    private String[] sources = new String[3];
    private final Random random = new Random();
    private List<String> list = new ArrayList<>();
    private boolean[] count = new boolean[3];
    private String requestStr;

    public MasterActor(CompletableFuture<Message> future, String request) {
        this.future = future;
        sources[0] = "google";
        sources[1] = "yandex";
        sources[2] = "bing";
        this.requestStr = request;
    }

    @Override
    public void onReceive(Object request) throws Exception {
        if (request instanceof String) {

            for (int i = 0; i < 3; i++) {
                ActorRef childActor = actorSystem.actorOf(
                        springExtension.props("childActor", future, sources[i]),
                        sources[i] + ":" + random.nextInt());
                childActor.tell(request, getSelf());
                count[i] = false;
            }
            getContext().setReceiveTimeout(Duration.create("3 seconds"));

        } else if (request == ReceiveTimeout.getInstance()) {

            future.complete(new Message(list, requestStr));
            getContext().stop(self());

        } else if (request instanceof Message) {

            Message message = (Message) request;
            String[] r = message.request.split(":");
            boolean f = true;
            for (int i = 0; i < sources.length; i++) {
                if (sources[i].equals(r[0])) {
                    count[i] = true;
                    list.addAll(message.results);
                }
                f &= count[i];
            }
            if (f) {
                future.complete(new Message(list, requestStr));
                getContext().stop(self());
            }
        } else {
            unhandled(request);
        }
    }
}
