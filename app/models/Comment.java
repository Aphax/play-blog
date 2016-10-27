package models;

import helpers.TimeAgo;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "comments")
public class Comment {
  @Id @GeneratedValue
  public Long id;
  @NotEmpty @NotNull @Constraints.Required
  public String username = "";
  @Column(name = "mail") @NotEmpty @NotNull @Constraints.Email @Constraints.Required
  public String email = "";
  @Column(columnDefinition = "MEDIUMTEXT") @NotEmpty @Constraints.Required
  public String content = "";
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  public DateTime created = DateTime.now();
  @ManyToOne @JoinColumn(name = "post_id", referencedColumnName = "id")
  public Post post;

  public String toAgo() {
    return TimeAgo.toDuration((DateTime.now().getMillis() - this.created.getMillis()));
  }

  public static class CommentForm {
    public String email;
    public String username;
    public String content;

    public CommentForm() {
    }

    public CommentForm(String email, String username, String content) {
      this.email = email;
      this.username = username;
      this.content = content;
    }

    @Override
    public String toString() {
      return "CommentForm{" +
              "email='" + email + '\'' +
              ", username='" + username + '\'' +
              ", content='" + content + '\'' +
              '}';
    }

    public Comment toComment(Post post) {
      Comment comment = new Comment();
      comment.id = null;
      comment.content = this.content;
      comment.username = this.username;
      comment.email = this.email;
      comment.post = post;
      comment.created = DateTime.now();
      return comment;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }
}
