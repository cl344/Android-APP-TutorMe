package compsci290.edu.duke.tutor.profile;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import compsci290.edu.duke.tutor.R;


public class CourseTakenAdapter extends RecyclerView.Adapter<CourseTakenAdapter.CourseTakenViewHolder> {

    private Context mContext;
    private List<CourseTakenDetail> courseTakenDetails;

    public class CourseTakenViewHolder extends RecyclerView.ViewHolder {
        public TextView courseName, grade, semester, isTA;

        public CourseTakenViewHolder(View itemView) {
            super(itemView);
            courseName = (TextView) itemView.findViewById(R.id.course_taken_card_name);
            grade = (TextView) itemView.findViewById(R.id.course_taken_card_grade);
            semester = (TextView) itemView.findViewById(R.id.course_taken_card_semester);
            isTA = (TextView) itemView.findViewById(R.id.course_taken_card_TA);
        }
    }

    public CourseTakenAdapter(Context mContext, List<CourseTakenDetail> courseTakenDetails) {
        this.mContext = mContext;
        this.courseTakenDetails = courseTakenDetails;
    }

    @Override
    public CourseTakenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_course_taken_card, parent, false);
        return new CourseTakenViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CourseTakenViewHolder holder, int position) {
        CourseTakenDetail courseTakenDetail = courseTakenDetails.get(position);
        Resources res = mContext.getResources();
        String grade = String.format(res.getString(R.string.course_taken_grade_v2), courseTakenDetail.mGrade);
        String semester = String.format(res.getString(R.string.course_taken_semester_v2), courseTakenDetail.mYearTaken);
        String isTA = String.format(res.getString(R.string.course_taken_isTA_v2), courseTakenDetail.mWasTA);

        holder.courseName.setText(courseTakenDetail.mName);
        holder.grade.setText(grade);
        holder.semester.setText(semester);
        holder.isTA.setText(isTA);
    }

    @Override
    public int getItemCount() {
        return courseTakenDetails.size();
    }
}
