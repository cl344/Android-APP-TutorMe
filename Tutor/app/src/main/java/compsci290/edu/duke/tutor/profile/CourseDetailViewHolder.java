package compsci290.edu.duke.tutor.profile;


import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import compsci290.edu.duke.tutor.R;


public class CourseDetailViewHolder extends ChildViewHolder {

    private TextView mCourseTakenSemester;
    private TextView mGrade;

    public CourseDetailViewHolder(View itemView) {
        super(itemView);
        mCourseTakenSemester = (TextView) itemView.findViewById(R.id.course_taken_detail_semester);
        mGrade = (TextView) itemView.findViewById(R.id.course_taken_detail_grade);
    }

    public void setCourseDetail(CourseTakenDetail courseTakenDetail) {
        mCourseTakenSemester.setText(courseTakenDetail.getSemester());
        mGrade.setText(courseTakenDetail.getGrade());
    }
}

