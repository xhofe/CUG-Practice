package Rating;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.LinkedList;
import java.util.List;

public class Rating {
    private List<Grade> gradeList=new LinkedList<Grade>();
    public static void main(String[] args) {
        Rating rating=new Rating();
        rating.gradeList.add(new Grade(99));
        rating.gradeList.add(new Grade(59));
        rating.gradeList.add(new Grade(76));
        rating.gradeList.add(new Grade(88));
        KieServices ks=KieServices.Factory.get();
        KieContainer kc=ks.getKieClasspathContainer();
        rating.execute(kc);
        rating.print();
    }
    public void execute(KieContainer kc){
        try {
            KieSession kieSession=kc.newKieSession("rating-rules");
            for (Grade grade:
                 gradeList) {
                kieSession.insert(grade);
                kieSession.fireAllRules();
            }
            kieSession.dispose();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void print(){
        for (Grade grade:
             gradeList) {
            System.out.printf("%d: %s\n",grade.getScore(),grade.getGrade());
        }
    }
}
