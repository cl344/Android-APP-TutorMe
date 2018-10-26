package compsci290.edu.duke.tutor.adapters;

/**
 * Created by mberger on 4/16/17.
 */
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import compsci290.edu.duke.tutor.activities.AvailableTutors;
import compsci290.edu.duke.tutor.R;

import static compsci290.edu.duke.tutor.mainfragments.CoursesFragment.toStringArray;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.MyViewHolder> {

    private List<ClassSource> classList;
    public Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView courseName;
        public TextView available;
        public View course_row;


        public MyViewHolder(View view) {
            super(view);
            courseName = (TextView) view.findViewById(R.id.course);
            available = (TextView) view.findViewById(R.id.available);
            course_row = view.findViewById(R.id.courses_row_layout);
        }
    }


    public ClassAdapter(List<ClassSource> classList, Context context) {

        this.classList = classList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.courses_row, parent, false);

        return new MyViewHolder(itemView);
    }

    //Set click listener to recycler view and create intent
    @Override
    public void onBindViewHolder(final ClassAdapter.MyViewHolder holder, final int position) {
        final ClassSource classSource = classList.get(position);
        holder.courseName.setText(classSource.getCourse());
        holder.available.setText(classSource.getAvailable());
        holder.course_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AvailableTutors.class);
                Bundle b = new Bundle();
                b.putStringArray("people", toStringArray(classSource.getArray()));
                b.putString("courseID", classSource.getCourseId());
                b.putString("name", classSource.getCourse());
                intent.putExtras(b);
                mContext.startActivity(intent);
            }
        });
    }


    //Return list size
    @Override
    public int getItemCount() {
        return classList.size();
    }
}
