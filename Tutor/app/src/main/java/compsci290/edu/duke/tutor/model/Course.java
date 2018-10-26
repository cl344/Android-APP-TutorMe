package compsci290.edu.duke.tutor.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@AVClassName("Course")
public class Course extends AVObject {

    public Course() {}

    /*
    * update availabletutors based on tutorUserInfoId
    * */
    public void updateAvailableTutors(String tutorUserInfoId) {
        String[] array = toStringArray(getJSONArray("availableTutors"));
        List<String> list = new ArrayList<>(Arrays.asList(array));
        if (!list.contains(tutorUserInfoId)) {
            list.add(tutorUserInfoId);
            put("availableTutors", list.toArray());
        }
    }

    /*
    * helper method to convert jsonarray to array
    * should be put in Utils later
    * */
    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
    }
}
