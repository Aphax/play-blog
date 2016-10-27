package models;

import org.hibernate.validator.constraints.NotEmpty;
import play.db.jpa.JPA;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue
  public Long id;
  @NotEmpty @NotNull
  public String username = "";
  //Replace by Hash
  @NotEmpty @NotNull
  public String password = "";
  @OneToMany(mappedBy = "user")
  public List<Post> posts = new ArrayList<>();

  public static User find(String username) {
    try {
      return JPA.em().createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
              .setParameter("username", username)
              .getSingleResult();
    } catch (Exception e) {
      return null;
    }
  }

  public static User find(Long id) {
    try {
      return JPA.em().createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
              .setParameter("id", id)
              .getSingleResult();
    } catch (Exception e) {
      return null;
    }
  }

  public static List<User> findAll() {
    try {
      return JPA.em().createQuery("SELECT u FROM User u", User.class)
              .getResultList();
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }
}
