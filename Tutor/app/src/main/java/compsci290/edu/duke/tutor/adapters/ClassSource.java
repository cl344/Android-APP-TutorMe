package compsci290.edu.duke.tutor.adapters;

import org.json.JSONArray;

/**
 * Created by mberger on 4/16/17.
 */

public class ClassSource {
    private String course;
    private String available;
    private JSONArray jArray;
    private String courseId;

    public ClassSource(String course, String available, JSONArray array, String courseId) {

        this.course = course;
        this.available = available;
        this.jArray = array;
        this.courseId = courseId;
    }

    //Get information needed for each course
    public String getCourse() {
        return course;
    }

    public String getAvailable() {
        return available;
    }

    public JSONArray getArray() {
        return jArray;
    }

    public String getCourseId() {
        return courseId;
    }
}