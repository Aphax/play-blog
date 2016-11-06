package controllers;

import actions.Access;
import models.Category;
import models.Post;
import models.User;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.db.Database;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import security.AdminAuthenticator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 * Transactional -> All methods inherits of a JPA transaction context
 * https://www.playframework.com/documentation/2.5.x/JavaJPA#Annotating-JPA-actions-with-@Transactional
 * <p>
 * Singleton -> This Controller will never be instantiated more than once (avoid memory leaks)
 * https://www.playframework.com/documentation/2.5.x/JavaDependencyInjection#Singletons
 * <p>
 * 3 way of handling Admin control access, each one encapsulated in an authorizing-checking action
 *  - Security.Authenticated -> Play out of the box Security API
 *  @see AdminAuthenticator for more details
 *  - With(SecuredAction.class) : Custom action handling admin access
 *  - @Access(type = "admin") : Custom action annotation handling admin access (almost the same as previous one)
 * <p>
 * Inject -> Play uses Dependency Injection with Guice and try to remove Global State from framework since version 2.4
 * https://www.playframework.com/documentation/2.5.x/JavaDependencyInjection
 */
@Singleton
@Transactional
//@Security.Authenticated(AdminAuthenticator.class)
//@With(SecuredAction.class)
@Access(type = "admin")
public class Admin extends Controller {
  /**
   * Form helper that will help us handle form submission and validation
   */
  @Inject
  protected FormFactory formFactory;

  /**
   * If you need to access directly to JDBC driver
   * Just call db.getConnection() in any action and you're good to go
   */
  @Inject
  protected Database db;

  public Result index(Integer pageNb) {
    Logger.info("Admin.index()");
    List<Post> all = Post.findFivePostFrom((pageNb - 1) * 5);

    return ok(views.html.admin_index.render(pageNb, all.size() / 5 + 1, all));
  }

  public Result logout() {
    Logger.info("Admin.logout()");
    session().remove("username");
    return redirect(routes.Blog.all(1));
  }

  /**
   * In order to use Play form helpers, we need to use the play.data.Form wrapper on an entity
   * Form objects are immutable, all methods that induce a state change returns a new object
   * (like Form::fill which allows to populate a Form's fields objects)
   */
  public Result newPost() {
    Form<Post> emptyForm = formFactory.form(Post.class);
    Form<Post> filledForm = emptyForm.fill(new Post());
    // emptyForm != filledForm
    return ok(views.html.admin_edit.render(filledForm, User.findAll(), Category.findAll()));
  }

  public Result editPost(Long postId) {
    Post post = Post.find(postId);
    if (post == null) {
      return notFound();
    }
    Form<Post> formPost = formFactory.form(Post.class).fill(post);
    return ok(views.html.admin_edit.render(formPost, User.findAll(), Category.findAll()));
  }

  /**
   * TODO use REST API instead
   */
  public Result createPost() {
    Form<Post> form = formFactory.form(Post.class);
    Form<Post> boundForm = form.bindFromRequest();
    Logger.debug("form data : " + boundForm.data());
//    if (boundForm.hasErrors()) {
//      return badRequest(views.html.admin_edit.render(boundForm, User.findAll(), Category.findAll()));
//    }
    Post post = boundForm.get();
    Logger.debug("post : " + post.toString());
    JPA.em().persist(post);
    flash("success", "Article has been created");
    return redirect(routes.Admin.index(1));
  }

  @BodyParser.Of(BodyParser.FormUrlEncoded.class)
  public Result savePost(Long postId) {
    return TODO;
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

  public Result adminDeletePost(Long postId) {
    Logger.info("Admin.adminDeletePost(postId: " + postId + ")");
    if (session().get("id") == null) {
      return forbidden();
    }
    JPA.em().remove(Post.find(postId));
    JPA.em().flush();
    return redirect(routes.Admin.index(1));
  }
}
