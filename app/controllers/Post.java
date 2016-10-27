package controllers;

import models.Category;
import models.Comment;
import models.User;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;

@Transactional
public class Post extends Controller {
  @Inject
  FormFactory formFactory;

  public Result postByAuthor(Long authorId, Integer pageNb) {
    Logger.info("Post.postByAuthor(pageNb: " + pageNb + ", authorId: " + authorId + ")");
    List<models.Post> posts = models.Post.findAllByAuthorFrom(User.find(authorId), (pageNb - 1)  * 5, 5);
    if (posts == null) {
      //TODO add custom 404 page
      return notFound();
    }
    List<models.Post> lastFivePost = models.Post.findLastFivePost();
    List<Category> allCategories = Category.findAll();
    return ok(views.html.index.render(pageNb, posts.size() / 5 + 1, posts, lastFivePost, allCategories));
  }

  public Result postByCategory(Long categoryId, Integer pageNb) {
    Logger.info("Post.postByCategory(pageNb: " + pageNb + ", categoryId: " + categoryId + ")");

    List<models.Post> posts = models.Post.findAllByCategoriesFrom(Category.find(categoryId), (pageNb - 1)  * 5, 5);
    if (posts == null) {
      //TODO add custom 404 page
      return notFound();
    }
    List<models.Post> lastFivePost = models.Post.findLastFivePost();
    List<Category> allCategories = Category.findAll();
    return ok(views.html.index.render(pageNb, posts.size() / 5 + 1, posts, lastFivePost, allCategories));
  }

  public Result post(Long id) {
    Logger.info("Post.post(id: " + id + ")");
    models.Post post = models.Post.find(id);
    if (post == null) {
      //TODO add custom 404 page
      return notFound();
    }
    List<models.Post> lastFivePost = models.Post.findLastFivePost();
    List<Category> allCategories = Category.findAll();
    return ok(views.html.post.render(post, lastFivePost, allCategories));
  }

  public Result bySlug(String slugName) {
    Logger.info("Post.post(slugName: " + slugName + ")");
    models.Post post = models.Post.find(slugName);
    if (post == null) {
      //TODO add custom 404 page
      return notFound();
    }
    List<models.Post> lastFivePost = models.Post.findLastFivePost();
    List<Category> allCategories = Category.findAll();
    return ok(views.html.post.render(post, lastFivePost, allCategories));
  }

  public Result addComment(Long postId) {
    Logger.info("Post.addComment(postId: " + postId + ")");
    Form<Comment.CommentForm> commentForm = formFactory.form(Comment.CommentForm.class).bindFromRequest();
    models.Post post = models.Post.find(postId);
    if (commentForm.hasErrors()) {
      flash("error", commentForm.globalErrors().toString());
      List<models.Post> lastFivePost = models.Post.findLastFivePost();
      List<Category> allCategories = Category.findAll();
      return badRequest(views.html.post.render(post, lastFivePost, allCategories));
    }
    Comment.CommentForm comment = commentForm.get();
    Comment comments = comment.toComment(post);
    Logger.info("Post.addComment(), new comment: " + comments);
    JPA.em().persist(comments);
    JPA.em().flush();
    return post(postId);
  }
}
