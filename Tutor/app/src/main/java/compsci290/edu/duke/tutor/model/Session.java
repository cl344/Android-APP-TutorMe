package compsci290.edu.duke.tutor.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetDataCallback;


@AVClassName("Session")
public class Session extends AVObject {

    public Session() {}

    public Session(Request request){
        setRequest(request);
        setIsEnded(false);
    }

    public void setRequest(Request request) {
        put("request", request);
    }

    public Request getRequest() {
        return (Request) get("request");
    }

    public void setIsEnded(boolean isEnded) {
        put("isEnded", isEnded);
    }

    public boolean getIsEnded() {
        return getBoolean("isEnded");
    }

    public String getTutorName() {
        return getAVObject("tutorID").getAVObject("user").getString("username");
    }

    public String getTutorId() {
        return getAVObject("tutorID").getObjectId();
    }

    public String getTutorUserInfoId() {
        return getAVObject("tutorID").getAVObject("user").getObjectId();
    }

    public String getTuteeName() {
        return getAVObject("tuteeID").getString("username");
    }

    public String getTuteeId() {
        return getAVObject("tuteeID").getObjectId();
    }

    public String getCourseId() {
        return getAVObject("request").getAVObject("courseID").getObjectId();
    }

    @Override
    public boolean equals(Object session) {
        boolean retVal = false;

        if (session instanceof Session){
            Session ptr = (Session) session;
            retVal = ptr.getObjectId().equals(this.getObjectId());
        }

        return retVal;
    }

    public void setTutorID(String id) {
        put("tutorID", AVObject.createWithoutData("Tutor",id));
    }

    public void setTuteeID(String id) {
        put("tuteeID", AVObject.createWithoutData("UserInfo", id));
    }

    public int getRating() {
        Object obj = getAVObject("tutorID").get("ratingAsTutor");
        if(obj == null) {
            return 0;
        }
        return (int) obj;
    }

    public String getComment() {
        return getAVObject("request").getString("comment");
    }

}
