package compsci290.edu.duke.tutor.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.model.Request;


public class RequestSentRecyclerViewAdapter extends RecyclerView.Adapter<RequestSentRecyclerViewAdapter.RequestSentViewHolder>{

    private Context mContext;
    private List<Request> mRequestSentViewModelList;

    public class RequestSentViewHolder extends RecyclerView.ViewHolder {
        public TextView tutorName, sessionDuration, sentTime, status, comment, date;

        public RequestSentViewHolder(View itemView) {
            super(itemView);
            tutorName = (TextView) itemView.findViewById(R.id.request_sent_card_tutor_name);
            sessionDuration = (TextView) itemView.findViewById(R.id.request_sent_card_session_duration);
            sentTime = (TextView) itemView.findViewById(R.id.request_sent_card_sent_time);
            status = (TextView) itemView.findViewById(R.id.request_sent_card_is_accpeted);
            comment = (TextView) itemView.findViewById(R.id.request_sent_card_comment_input);
        }
    }

    public RequestSentRecyclerViewAdapter(Context mContext, List<Request> requestSentViewModelList) {
        this.mContext = mContext;
        this.mRequestSentViewModelList = requestSentViewModelList;
    }

    @Override
    public RequestSentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_sent_card, parent, false);
        return new RequestSentRecyclerViewAdapter.RequestSentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RequestSentViewHolder holder, int position) {
        final Request request = mRequestSentViewModelList.get(position);
        Resources res = mContext.getResources();

        String duration = String.format(res.getString(R.string.request_sent_session_duration), request.getSessionDuration());
        String status = String.format(res.getString(R.string.request_sent_status), request.getState());
        String createdAt = String.format(res.getString(R.string.request_sent_time), request.getSentTime());

        holder.status.setText(status);
        holder.sentTime.setText(createdAt);
        holder.sessionDuration.setText(duration);
        holder.tutorName.setText(request.getTutorName());
        holder.comment.setText(request.getRequestFormComment());

    }

    @Override
    public int getItemCount() {
        return mRequestSentViewModelList.size();
    }



}
