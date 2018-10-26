package compsci290.edu.duke.tutor.adapters;

/**
 * Created by mberger on 4/16/17.
 */
 import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import java.util.List;

 import compsci290.edu.duke.tutor.R;

public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.MyViewHolder> {

    private List<TutorSource> tutorList;
    public TextView name;
    public TextView date;

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.firstName);
            date = (TextView) view.findViewById(R.id.date);
        }
    }


    public TutorAdapter(List<TutorSource> tutorList) {
        this.tutorList = tutorList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tutor_row, parent, false);

        return new MyViewHolder(itemView);
    }

    //Set name and date in textviews from tutor class
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TutorSource tutor = tutorList.get(position);
        name.setText(tutor.getName());
        date.setText(tutor.getDate());
    }

    @Override
    public int getItemCount() {
        return tutorList.size();
    }
}
