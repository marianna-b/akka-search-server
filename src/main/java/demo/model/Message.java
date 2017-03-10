package demo.model;

import java.util.ArrayList;
import java.util.List;

public class Message {

    final public List<String> results = new ArrayList<>();
    public String request = "";

    public Message(List<String> r, String s) {
        this.request = s;
        this.results.addAll(r);
    }

}
