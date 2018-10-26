package compsci290.edu.duke.tutor.model;


import android.util.Log;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@AVClassName("Request")
public class Request extends AVObject {

    public Request() {}

    public Request(String courseID, String tuterID, String tuteeID, String comment, int duration, Date sentTime) {

        setCourseID(courseID);
        setTuteeID(tuteeID);
        setTuterID(tuterID);
        setRequestFormComment(comment);
        setDuration(duration);
        setHasChosen(false);
        setState("waiting");
        setTime(sentTime);
    }

    /*get comment for the request*/
    public String getRequestFormComment() {
        return getString("comment");
    }

    /*set comment for the request*/
    public void setRequestFormComment(String comment) {
        put("comment", comment);
    }

    public void setCourseID(String id) {
        AVObject avObject = AVObject.createWithoutData("Course", id);
        put("courseID", avObject);
    }

    public void setTuterID(String id) {
        AVObject avObject = AVObject.createWithoutData("Tutor", id);
        put("tutorID", avObject);
    }

    public void setTuteeID(String id) {
        AVObject avObject = AVObject.createWithoutData("UserInfo", id);
        put("tuteeID", avObject);
    }

    public void setDuration(int duration) {
        put("duration", duration);
    }

    public void setHasChosen(Boolean accept) {
        put("hasChosen", accept);
    }

    public void setTime(Date sentTime) {put("createdAt",sentTime);}

    public boolean hasChosen() {
        return (boolean) get("hasChosen");
    }

    public int getSessionDuration() {
        Object object = get("duration");
        if (object != null) {
            return (int) object;
        } else {
            return 0;
        }
    }

    public String getSentTime() {
        //TODO: cast status to string
        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd");
        SimpleDateFormat hourFormatter = new SimpleDateFormat("hh");
        SimpleDateFormat minuteFormatter = new SimpleDateFormat("mm");
        Date today = Calendar.getInstance().getTime();
        String createdDay = dayFormatter.format(get("createdAt"));
        String todayDay = dayFormatter.format(today);
        String createdHour = hourFormatter.format(get("createdAt"));
        String todayHour = hourFormatter.format(today);
        String createdMinute = minuteFormatter.format(get("createdAt"));
        String todayMinute = minuteFormatter.format(today);
        Log.d("today",todayDay);
        int daysAgo = Integer.parseInt(todayDay) - Integer.parseInt(createdDay);
        int hourAgo = Integer.parseInt(todayHour) - Integer.parseInt(createdHour);
        int minuteAgo = Integer.parseInt(todayMinute) - Integer.parseInt(createdMinute);
        System.out.println(daysAgo);
        if (daysAgo > 1 && daysAgo < 365){
            return daysAgo + " days ago";
//        return (String) get("createdAt");
        }
        else if (hourAgo >= 1 && daysAgo < 1) {
            return hourAgo + " hours ago";
        }
        else if (minuteAgo >= 1 && hourAgo < 1) {
            return minuteAgo + " minutes ago";
        }
        else if (minuteAgo < 1){
            return "0 minutes ago";
        }
        else{
            return ">1 years ago";
        }
    }

    public String getTutorName() {
        return getAVObject("tutorID").getAVObject("user").getString("username");
    }

    public String getTuteeName() {
        return getAVObject("tuteeID").getString("username");
    }

    @Override
    public boolean equals(Object request) {
        boolean retVal = false;

        if (request instanceof Request){
            Request ptr = (Request) request;
            retVal = ptr.getObjectId().equals(this.getObjectId());
        }

        return retVal;
    }

    public String getState() {
        String ret = getString("state");
        if (ret == null) {
            return "unknown";
        }
        return ret;
    }

    public String getTutorId() {
        return getAVObject("tutorID").getObjectId();
    }

    public String getTutorUserInfoId() {
        return getAVObject("tutorID").getAVObject("user").getObjectId();
    }

    public String getCourseId() {
        return getAVObject("courseID").getObjectId();
    }

    public String getTuteeId() {
        return getAVObject("tuteeID").getObjectId();
    }

    /*for tutee to check if this request is updated*/
    public boolean isUpdatedRequestSent() {
        return getBoolean("isUpdatedRequestSent");
    }

    public void setUpdatedRequestSent(boolean b) {
        put("isUpdatedRequestSent", b);
    }

    /*for tutor to check if this request is updated*/
    public boolean isUpdated() {
        return getBoolean("isUpdated");
    }

    public void setIsUpdated(boolean isUpdated) {
        put("isUpdated", false);
    }

    /*set request state from strings: accept, waiting and decline*/
    public void setState(String str) {
        put("state", str);
    }
}