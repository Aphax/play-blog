package models;

import play.Logger;
import play.db.jpa.JPA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
  @Id
  @GeneratedValue
  public Long id;
  @Column(length = 50)
  public String name = "";
  @Column(length = 50)
  public String slug = "";
  @Column(length = 50, name = "post_count")
  public Long postCount = 0L;
  @OneToMany(mappedBy = "category")
  public List<Post> posts;

  public static Category find(Long id) {
    try {
      return JPA.em().createQuery("SELECT c FROM Category c WHERE c.id = :id", Category.class).setParameter("id", id).getSingleResult();
    } catch (Exception e) {
      Logger.error(e.getMessage());
      return null;
    }
  }

  public static Category find(String name) {
    try {
      return JPA.em().createQuery("SELECT c FROM Category c WHERE c.name = :name", Category.class).setParameter("name", name).getSingleResult();
    } catch (Exception e) {
      Logger.error(e.getMessage());
      return null;
    }
  }

  public static List<Category> findAll() {
    try {
      return JPA.em().createQuery("SELECT c FROM Category c", Category.class).getResultList();
    } catch (Exception e) {
      Logger.error(e.getMessage());
      return new ArrayList<>();
    }
  }
}
