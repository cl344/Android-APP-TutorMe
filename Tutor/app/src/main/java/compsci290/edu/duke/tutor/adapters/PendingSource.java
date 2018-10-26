package compsci290.edu.duke.tutor.adapters;

/**
 * Created by mberger on 4/20/17.
 */

public class PendingSource {
    private String name;
    private String date;
    private String status;

    public PendingSource() {
    }

    public PendingSource(String name, String date, String status) {
        this.name = name;
        this.date = date;
        this.status = status;
    }

    public String getName() {return name;}

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}

