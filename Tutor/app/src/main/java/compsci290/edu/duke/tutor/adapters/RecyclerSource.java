package compsci290.edu.duke.tutor.adapters;

/**
 * Created by mberger on 4/18/17.
 */

public class RecyclerSource {
    private String name;
    private String tutorId;
    private String courseId;

    public RecyclerSource() {
    }

    public RecyclerSource(String name, String tutorId, String courseId) {
        this.name = name;
        this.tutorId = tutorId;
        this.courseId = courseId;
    }

    public String getName() {return name;}

    public String getTutorId() {
        return tutorId;
    }

    public String getCourseId() {
        return courseId;
    }
}
