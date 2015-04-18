package controllers;

import models.*;
import play.data.*;
import play.mvc.*;
import views.html.ratecalpage;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class RateCalculator extends Controller {

    //@Security.Authenticated(Secured.class)
    public static Result index() {
        boolean isTimeUp = Settings.isTimeUp();
        Long userId = Long.parseLong(session().get("userId"));
        User thisUser = User.findByUserId(userId);
        if(!isTimeUp && thisUser.idtype != User.ADMINISTRATOR ) {
            flash("time_up","Time is already up. Sorry for the inconvenience.");
            return redirect(routes.Application.index());
        }
        List<Project> projects = Project.findAll();
        List<RateCriterion> criteria = RateCriterion.findAll();
        List<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
        List<Comment> comments = Comment.findAll();
        for(Project p : projects){
            ArrayList<Double> perProject = new ArrayList<Double>();
            double allSum = 0;
            for(RateCriterion c : criteria){
                List<Rate> rates = Rate.findListByProjectIdAndCriteriaId(p.id,c.id);
                double sum = 0;
                int count = 0;
                for(Rate r : rates ){
                    sum += r.score;
                    count++;
                }
                sum = sum/count;
                allSum+=sum;
                perProject.add(sum);

            }
            allSum= allSum/criteria.size();
            perProject.add(allSum);
            result.add(perProject);
        }
        return ok(ratecalpage.render(projects,criteria,comments,result));
    }
}