package compsci290.edu.duke.tutor.adapters;

/**
 * Created by mberger on 4/16/17.
 */

public class TutorSource {
    private String name;
    private String date;

    public TutorSource() {
    }

    public TutorSource(String name, String date) {

        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }
    public String getDate() {return date;}
}