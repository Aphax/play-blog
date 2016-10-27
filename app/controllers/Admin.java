package controllers;

import helpers.Secured;
import models.Post;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;

@Transactional
public class Admin extends Controller {

  @Security.Authenticated(Secured.class)
  public Result index(Integer pageNb) {
    Logger.info("Admin.index()");
    List<Post> all = Post.findFivePostFrom((pageNb - 1)  * 5);

    return ok(views.html.admin_index.render(pageNb, all.size() / 5 + 1, all));
  }

  public Result adminLogin() {
    Logger.info("Admin.adminLogin()");
    if (session().get("id") != null) {
      return redirect(routes.Admin.index(1));
    }
    return ok(views.html.login.render());
  }

  public Result adminPostLogin() {
    DynamicForm dynamicForm = Form.form().bindFromRequest();

    Logger.info("Admin.indexPostLogin(); formData: " + dynamicForm.data());

    String username = dynamicForm.get("username");
    String password = dynamicForm.get("password");
    User user = User.find(username);
    if (user == null || !password.equals(user.password)) {
      return badRequest(views.html.login.render());
    }

    session().clear();
    //TODO replace by token
    session("email", username);
    session("id", user.id.toString());
    return redirect(routes.Admin.index(1));
  }

  public Result adminLogout() {
    Logger.info("Admin.adminLogout()");

    session().clear();
    return redirect(routes.Index.index(1));
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
