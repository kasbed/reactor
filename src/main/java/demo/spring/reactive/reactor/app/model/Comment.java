package demo.spring.reactive.reactor.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Comment implements Serializable {

    /***/
    private static final long serialVersionUID = -6764682018210813639L;

    private List<String> comments;

    public Comment(List<String> comments) {
        this.comments = comments;
    }

    public void addComment(String comment) {
        if(this.comments == null)
            this.comments = new ArrayList<String>();
        this.comments.add(comment);
    }

    @Override
    public String toString() {
        return "comments=" + comments;
    }
    
}