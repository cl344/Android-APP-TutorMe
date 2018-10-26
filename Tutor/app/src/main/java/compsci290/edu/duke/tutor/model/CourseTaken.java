package compsci290.edu.duke.tutor.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

import compsci290.edu.duke.tutor.profile.CourseTakenDetail;


@AVClassName("CourseTaken")
public class CourseTaken extends AVObject {

    /*
    * default constructor required for AVObject subclass
    * */
    public CourseTaken() {}

    public CourseTaken(Course course, CourseTakenDetail courseTakenDetail, String tutorInfoId) {
        setCourse(course);
        setGrade(courseTakenDetail.getGrade());
        setIsTA(courseTakenDetail.mWasTA);
        setSemester(courseTakenDetail.getSemester());
        setTutor(tutorInfoId);
    }

    public void setCourse(Course course) {
        put("courseId", course);
    }

    public void setGrade(String grade) {
        put("grade", grade);
    }

    public void setIsTA(String isTA) {
        if (isTA.equalsIgnoreCase("true") || isTA.equalsIgnoreCase("yes")) {
            put("isTA", true);
        } else {
            put("isTA", false);
        }
    }

    public void setSemester(String semester) {
        put("semester", semester);
    }

    public void setTutor(String id) {
        AVObject tutor = AVObject.createWithoutData("UserInfo", id);
        put("uid", tutor);
    }
}
