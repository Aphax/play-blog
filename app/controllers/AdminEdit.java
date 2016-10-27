package controllers;

import helpers.Secured;
import models.Category;
import models.Post;
import models.User;
import org.joda.time.DateTime;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Objects;

@Transactional
@Security.Authenticated(Secured.class)
public class AdminEdit extends Controller {

  public Result newPost() {
    return ok(views.html.admin_edit.render(new Post(), User.findAll(), Category.findAll()));
  }

  public Result editPost(Long postId) {
    Post post = Post.find(postId);
    return ok(views.html.admin_edit.render(post, User.findAll(), Category.findAll()));
  }

  public Result savePost(Long postId) {
    DynamicForm dynamicForm = Form.form().bindFromRequest();
    String name = dynamicForm.get("name");
    String slug = dynamicForm.get("slug");
    String category_id = dynamicForm.get("category_id");
    String user_id = dynamicForm.get("user_id");
    String content = dynamicForm.get("content");

    Post post;

    if (postId == 0) {
      post = new Post();
    } else {
      post = Post.find(postId);
      if (post == null) {
        post = new Post();
        post.content = content;
        post.name = name;
        flash("error", "This post id doesn't exist");
        return badRequest(views.html.admin_edit.render(post, User.findAll(), Category.findAll()));
      }
    }
    post.content = content;
    post.name = name;
    if (post.name.length() == 0 || post.content.length() == 0) {
      return badRequest(views.html.admin_edit.render(post, User.findAll(), Category.findAll()));
    }
    if (assignSlug(name, slug, post)) {
      return internalServerError();
    }
    try {
      post.user = User.find(Long.parseLong(user_id));
      post.category = Category.find(Long.parseLong(category_id));
    } catch (NumberFormatException e) {
      Logger.error(e.getMessage());
      return internalServerError(e.getMessage());
    }
    post.created = DateTime.now();
    JPA.em().persist(post);
    return redirect(routes.Admin.index(1));
  }

  private boolean assignSlug(String name, String slug, Post post) {
    try {
      String decode = URLDecoder.decode(slug, "UTF-8");
      if (Objects.equals(decode, name)) {
        return false;
      }
    } catch (UnsupportedEncodingException ignored) {
    }
    try {
      post.slug = URLEncoder.encode(slug.length() > 0 ? slug : name, "UTF-8");
      while (post.id == null) {
        post.slug += "_new";
      }
    } catch (UnsupportedEncodingException e) {
      Logger.error(e.getMessage());
      return true;
    }
    return false;
  }
}
