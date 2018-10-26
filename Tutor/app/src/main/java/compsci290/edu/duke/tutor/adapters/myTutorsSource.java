package compsci290.edu.duke.tutor.adapters;

/**
 * Created by mberger on 4/28/17.
 */

public class myTutorsSource {

    private String tutor;
    private String date;
    private String tutorID;
    private String courseID;

    public myTutorsSource(){
    }

    public myTutorsSource(String tutor, String date, String tutorID, String courseID) {

        this.tutor = tutor;
        this.date = date;
        this.courseID = courseID;
        this.tutorID = tutorID;
    }

    //Get information needed for the tutor
    public String getTutor() {
        return tutor;
    }

    public String getDate() {
        return date;
    }
    public String getCourseID(){return courseID;}
    public String getTutorID() {return tutorID;}
}
