package demo.spring.reactive.reactor.app.model;

import java.io.Serializable;

import org.springframework.util.StringUtils;

public class User implements Serializable {
        
    /***/
    private static final long serialVersionUID = -3622953293143394943L;
    
	private String firstName;
    private String lastName;

    public User(String firstName, String lastName) {
        this.firstName =  StringUtils.capitalize(firstName);
        this.lastName =  StringUtils.capitalize(lastName);
    }

    /**
     * @return String return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return String return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString(){
        return new StringBuilder(this.getFirstName()).append(" ").append(this.getLastName()).toString();
    }

}