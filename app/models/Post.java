package models;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.pegdown.PegDownProcessor;
import play.Logger;
import play.data.validation.Constraints;
import play.db.jpa.JPA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {
  @Id @GeneratedValue
  public Long id;
  @NotEmpty @NotNull @Constraints.Required
  public String name = "";
  @Column(unique = true) @NotEmpty @NotNull @Constraints.Required
  public String slug = "";
  @Column(columnDefinition = "TEXT") @Constraints.Required
  public String content = "";
  // Map DateTime object to DATETIME's database format
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  public DateTime created = DateTime.now();
  @ManyToOne @JoinColumn(name = "category_id", referencedColumnName = "id")
  public Category category;
  @ManyToOne @JoinColumn(name = "user_id", referencedColumnName = "id")
  public User user;
  @OneToMany(mappedBy = "post") @OrderBy("created DESC")
  public List<Comment> comments = new ArrayList<>();

  public Post() {
  }

  public static Post find(Long id) {
    try {
      return JPA.em().createQuery("SELECT p FROM Post p WHERE p.id = :id", Post.class)
              .setParameter("id", id)
              .getSingleResult();
    } catch (Exception e) {
      Logger.error(e.getMessage());
      return null;
    }
  }

  public static Post find(String slug) {
    try {
      return JPA.em().createQuery("SELECT p FROM Post p WHERE p.slug = :slug", Post.class)
              .setParameter("slug", slug)
              .getSingleResult();
    } catch (Exception e) {
      Logger.error(e.getMessage());
      return null;
    }
  }

  public static List<Post> findAll() {
    try {
      return JPA.em().createQuery("SELECT p FROM Post p ORDER BY p.created DESC", Post.class).getResultList();
    } catch (Exception e) {
      Logger.error(e.getMessage());
      return new ArrayList<>();
    }
  }

  public static List<Post> findAllByAuthor(User user) {
    return findAllByAuthorFrom(user, 0, 0);
  }

  public static List<Post> findAllByAuthorFrom(User user, Integer from, Integer limit) {
    try {
      TypedQuery<Post> query = JPA.em().createQuery("SELECT p FROM Post p WHERE p.user =:user ORDER BY p.created DESC", Post.class)
              .setFirstResult(from)
              .setParameter("user", user);
      if (limit != 0) {
        query = query.setMaxResults(limit);
      }
      return query.getResultList();
    } catch (Exception e) {
      Logger.error(e.getMessage());
      return new ArrayList<>();
    }
  }

  public static List<Post> findAllByCategories(Category c) {
    return findAllByCategoriesFrom(c, 0, 0);
  }

  public static List<Post> findAllByCategoriesFrom(Category category, Integer from, Integer limit) {
    try {
      TypedQuery<Post> query = JPA.em().createQuery("SELECT p FROM Post p WHERE p.category =:c ORDER BY p.created DESC", Post.class)
              .setFirstResult(from)
              .setParameter("c", category);
      if (limit != 0) {
        query = query.setMaxResults(limit);
      }
      return query.getResultList();
    } catch (Exception e) {
      Logger.error(e.getMessage());
      return new ArrayList<>();
    }
  }

  public static List<Post> findLastFivePost() {
    try {
      return JPA.em().createQuery("SELECT p FROM Post p ORDER BY p.created DESC", Post.class)
              .setMaxResults(5)
              .getResultList();
    } catch (Exception e) {
      Logger.error(e.getMessage());
      return new ArrayList<>();
    }
  }

  public static List<Post> findFivePostFrom(Integer startPosition) {
    try {
      return JPA.em().createQuery("SELECT p FROM Post p ORDER BY p.created DESC", Post.class)
              .setFirstResult(startPosition)
              .setMaxResults(5)
              .getResultList();
    } catch (Exception e) {
      Logger.error(e.getMessage());
      return new ArrayList<>();
    }
  }

  public String toHtml() {
    PegDownProcessor pegDownProcessor = new PegDownProcessor();
    return pegDownProcessor.markdownToHtml(this.content);
  }

  public String toResume() {
    String s = this.toHtml().replaceAll("<[^>]*>", "");
    return (s.length() > 450 ? s.substring(0, 450) + "..." : s);
  }
}
