package Rating;

public class Grade {
    private int score=0;
    private String grade;

    public Grade(int _score){
        score=_score;
    }
    public int getScore() {
        return score;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }
}
