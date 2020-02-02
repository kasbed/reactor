package demo.spring.reactive.reactor.app.model;

import java.io.Serializable;

public class UserComment implements Serializable {

    /***/
    private static final long serialVersionUID = 2437214375224462300L;

    private User user;
    private Comment comments;

    public UserComment(User user, Comment comments) {
        this.user = user;
        this.comments = comments;
    }

    /**
     * @return User return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @return Comment return the comments
     */
    public Comment getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "UserComment [comments=" + comments + ", user=" + user + "]";
    }

}