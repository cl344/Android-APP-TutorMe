package compsci290.edu.duke.tutor.profile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.Review;


public class ProfileReviewAdapter extends RecyclerView.Adapter<ProfileReviewAdapter.ProfileReviewViewHolder> {

    private Context mContext;
    private List<ReviewViewModel> mReviewViewModelList;

    public class ProfileReviewViewHolder extends RecyclerView.ViewHolder {
        public TextView reviewerName, review;
        public Button comment;

        public ProfileReviewViewHolder(View itemView) {
            super(itemView);
            reviewerName = (TextView) itemView.findViewById(R.id.profile_review_card_reviewer);
            review = (TextView) itemView.findViewById(R.id.profile_review_card_review_string);
            comment = (Button) itemView.findViewById(R.id.profile_review_card_comment_button);

        }
    }

    public ProfileReviewAdapter(Context mContext, List<ReviewViewModel> reviewViewModelList) {
        this.mContext = mContext;
        this.mReviewViewModelList = reviewViewModelList;
    }

    @Override
    public ProfileReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_review_card, parent, false);
        return new ProfileReviewAdapter.ProfileReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProfileReviewViewHolder holder, int position) {
        ReviewViewModel review = mReviewViewModelList.get(position);

        holder.reviewerName.setText(review.mReviewerName);
        holder.review.setText(review.mComment);
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: pop out a form that could enter comment strings
                //create a new Review object, show on the comment list and send it to database
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviewViewModelList.size();
    }
}
