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
            flash("rating_result_close","Please wait until the rating session is closed. Sorry for the inconvenience.");
            return redirect(routes.ProjectList.index());
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
                if(count == 0){
                    sum = 0;
                }else{
                    sum = sum/count;
                }
                allSum+=sum;
                sum = Math.round(sum*100)/100;
                perProject.add(sum);

            }
            if(criteria.size() == 0){
                allSum =0;
            }else {
                allSum= allSum/criteria.size();
            }
            allSum = Math.round(allSum*100)/100;
            perProject.add(allSum);
            result.add(perProject);
        }
        return ok(ratecalpage.render(projects,criteria,comments,result));
    }
}