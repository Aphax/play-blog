package controllers;

import models.Category;
import models.Post;
import play.Logger;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Transactional
public class Index extends Controller {
  public Result index(Integer pageNb) {
    Logger.info("Index.index(pageNb: " + pageNb + ");" );

    List<Post> all = Post.findFivePostFrom((pageNb - 1)  * 5);
    List<Post> lastFivePost = Post.findLastFivePost();
    List<Category> allCategories = Category.findAll();
    return ok(views.html.index.render(pageNb, all.size() / 5 + 1, all, lastFivePost, allCategories));
  }
}

