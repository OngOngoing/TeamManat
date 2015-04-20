package controllers;

import models.*;
import play.mvc.*;
import views.html.ratecalpage;

import java.util.List;
import java.util.ArrayList;


public class RateCalculator extends Controller {

    //@Security.Authenticated(Secured.class)
    public static Result index() {
        boolean isTimeUp = Settings.isTimeUp();
        Long userId = Long.parseLong(session().get("userId"));
        User thisUser = User.findByUserId(userId);
        if(!isTimeUp && thisUser.idtype != User.ADMINISTRATOR ) {
            flash("error","Please wait until the rating session is closed. Sorry for the inconvenience.");
            return redirect(routes.ProjectList.index());
        }
        List<Project> projects = Project.findAll();
        List<RateCriterion> criteria = RateCriterion.findAll();
        List<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        List<Comment> comments = Comment.findAll();
        for(Project p : projects){
            ArrayList<String> perProject = new ArrayList<String>();
            double allSum = 0;
            for(RateCriterion c : criteria){
                List<Rate> rates = Rate.findListByProjectIdAndCriteriaId(p.id,c.id);
                double sum = 0;
                int count = 0;
                for(Rate r : rates ){
                    sum += r.score;
                    count++;
                }
                if(count == 0){
                    sum = 0;
                }else{
                    sum = sum/count;
                }
                allSum+=sum;
                perProject.add(String.format("%.1f",sum));

            }
            if(criteria.size() == 0){
                allSum =0;
            }else {
                allSum= allSum/criteria.size();
            }           
            perProject.add(String.format("%.1f",allSum));
            result.add(perProject);
        }
        return ok(ratecalpage.render(thisUser,projects,criteria,comments,result));
    }
}