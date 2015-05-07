package controllers;

import models.*;
import play.mvc.*;
import views.html.ratecalpage;
import views.html.ratecalpagebycriteria;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class RateCalculator extends Controller {

    //@Security.Authenticated(Secured.class)
    public static Result index() {
        boolean isTimeUp = Setting.isTimeUp();
        User thisUser; 
        if(session().get("userId") == null){
            thisUser = new User();
            thisUser.setIdtype(-1);
            thisUser.setId(Long.parseLong("-1"));
        }else{
            Long userId = Long.parseLong(session().get("userId"));
            thisUser = User.findByUserId(userId);    
        }
        if(thisUser.getIdtype() == -1 && !isTimeUp && thisUser.getIdtype() != User.ADMINISTRATOR ) {
            flash("error","Please wait until the rating session is closed.");
            return redirect(routes.Application.index());
        }
        if(thisUser.getIdtype() != -1 &&!isTimeUp && thisUser.getIdtype() != User.ADMINISTRATOR ) {
            flash("error","Please wait until the rating session is closed.");
            return redirect(routes.ProjectList.index());
        }
        List<Project> projects = Project.findAll();
        List<RateCriterion> criteria = RateCriterion.findAll();
        List<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        for(Project p : projects){
            ArrayList<String> perProject = new ArrayList<String>();
            double allSum = 0;
            for(RateCriterion c : criteria){
                List<Rate> rates = Rate.findListByProjectAndCriteria(p, c);
                double sum = 0;
                int count = 0;
                for(Rate r : rates ){
                    sum += r.getScore();
                    count++;
                }
                if(count == 0){
                    sum = 0;
                }else{
                    sum = sum/count;
                }
                allSum+=sum;
                perProject.add(String.format("%.2f",sum));

            }
            if(criteria.size() == 0){
                allSum =0;
            }else {
                allSum= allSum/criteria.size();
            }           
            perProject.add(String.format("%.2f",allSum));
            result.add(perProject);
        }
        response().setHeader("Cache-Control","no-cache");
        return ok(ratecalpage.render(thisUser,projects,criteria,result));
    }
    public static Result rateSortByCriteria() {
        boolean isTimeUp = Setting.isTimeUp();
        User thisUser; 
        if(session().get("userId") == null){
            thisUser = new User();
            thisUser.setIdtype(-1);
            thisUser.setId(Long.parseLong("-1"));
        }else{
            Long userId = Long.parseLong(session().get("userId"));
            thisUser = User.findByUserId(userId);    
        }
        if(!isTimeUp && thisUser.getIdtype() != User.ADMINISTRATOR ) {
            flash("error","Please wait until the rating session is closed.");
            return redirect(routes.ProjectList.index());
        }
        List< List< Map<String,String> > > result = new ArrayList < List< Map<String,String> > >(); 
        List <RateCriterion> criteria = RateCriterion.findAll();
        List<Project> projects = Project.findAll();
        for(RateCriterion c : criteria){
            List<Map<String,String>> score  = new ArrayList<Map<String,String>>();
            for(Project p : projects){
                Map<String, String> i = new HashMap<String,String>();
                double sum = 0;
                int count = 0;
                List<Rate> rates = Rate.findListByProjectAndCriteria(p, c);
                for(Rate r : rates){
                    sum += r.getScore();
                    count++;
                }
                if(count == 0){
                    sum = 0;
                }else{
                    sum = sum/count;
                }
                i.put("projectName", p.getProjectName());
                i.put("score", String.format("%.2f",sum));
                score.add(i);
            }

            Collections.sort(score, Collections.reverseOrder(new Comparator() {
                public int compare(Object o1, Object o2) {
                    Double sum1 = (Double.parseDouble(((Map<String,String>)o1).get("score")));
                    Double sum2 = (Double.parseDouble(((Map<String,String>)o2).get("score")));
                    return sum1.compareTo(sum2);
                }
            }));
            result.add(score);
        }
        response().setHeader("Cache-Control","no-cache");
        return ok(ratecalpagebycriteria.render(thisUser,criteria,result));
    }
}