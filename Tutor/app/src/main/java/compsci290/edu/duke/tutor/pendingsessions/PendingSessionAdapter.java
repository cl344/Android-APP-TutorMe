package compsci290.edu.duke.tutor.pendingsessions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetDataCallback;

import java.util.List;

import compsci290.edu.duke.tutor.loginregistration.LoginType;
import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.model.Session;
import compsci290.edu.duke.tutor.profile.ProfileActivity;
import de.hdodenhof.circleimageview.CircleImageView;


public class PendingSessionAdapter extends RecyclerView.Adapter<PendingSessionAdapter.SessionViewHolder> {

    private List<Session> mSessionList;
    private Context mContext;
    private LoginType mMode;

    public class SessionViewHolder extends RecyclerView.ViewHolder {
        public TextView tutorName;
        public TextView status;
        public View cardview_row;
        public CircleImageView profilePic;

        public SessionViewHolder(View view) {
            super(view);
            tutorName = (TextView) view.findViewById(R.id.firstName);
            status = (TextView) view.findViewById(R.id.list_desc);
            cardview_row = view.findViewById(R.id.card_view);
            profilePic = (CircleImageView) view.findViewById(R.id.list_avatar);
        }
    }

    public PendingSessionAdapter(Context context, List<Session> sessionList, LoginType mode) {
        this.mContext = context;
        this.mSessionList = sessionList;
        this.mMode = mode;
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_row, parent, false);

        View infoView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_info_card, parent, false);

        return new PendingSessionAdapter.SessionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
        final Session session = mSessionList.get(position);
        holder.tutorName.setText(session.getTuteeName());

        // FIXME: 4/28/17
        if (!session.getIsEnded()) {
            holder.status.setText("Current");
        } else {
            holder.status.setText("Ended");
        }


        // OnClick pop out dialog to present detail information about session
        holder.cardview_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                        .positiveText("View Profile")
                        .negativeText("Cancel")
                        .contentColor(Color.WHITE) // notice no 'res' postfix for literal color
                        .positiveColorRes(R.color.material_red_400)
                        .neutralColorRes(R.color.material_red_400)
                        .negativeColorRes(R.color.material_red_400)
                        .widgetColorRes(R.color.material_red_400)
                        .buttonRippleColorRes(R.color.material_red_400)
                        .title("Session Information")
                        .customView(R.layout.session_info_card, true)
                        .cancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Toast.makeText(mContext, "User cancel", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent intent = new Intent(mContext, ProfileActivity.class);
                                Bundle b = new Bundle();
                                b.putString("tutorID", session.getTuteeId());
                                b.putString("courseID", session.getCourseId());
                                intent.putExtras(b);
                                mContext.startActivity(intent);
                            }
                        })
                        .build();
                View view = dialog.getCustomView();

                // Set rating bar value on the dialog
                RatingBar mRatingBar = (RatingBar) view.findViewById(R.id.rating);
                mRatingBar.setRating(session.getRating());

                // Set tutor name for the dialog
                TextView mTutorName = (TextView) view.findViewById(R.id.tutor_session_name_input);
                mTutorName.setText(session.getTuteeName());

                // Set comment for the dialog
                TextView mComment = (TextView) view.findViewById(R.id.tutor_session_additional_comment_input);
                mComment.setText(session.getComment());

                TextView title = (TextView)  view.findViewById(R.id.tutor_session_name);
                title.setText("Tutee Name");

                dialog.show();
            }
        });

        loadProfilePicture(holder, session);
    }

    @Override
    public int getItemCount() {
        return mSessionList.size();
    }

    /*
    * load profile picture given returned AVObject that contains a field called column
    * */
    public void loadProfilePicture(final SessionViewHolder holder, AVObject avObject) {

        final String TAG = "PendingRequestAdap";

        Log.d(TAG, "try to load profile pic");
        // load user profile picture on background thread
        AVObject tutee = avObject.getAVObject("tuteeID");
        AVFile pic = (AVFile) tutee.get("picture");

        // safety check if user didn't set profile picture
        if (pic != null) {
            Log.d(TAG, "profile pic is not null");
            pic.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    if (e == null) {
                        Log.d(TAG, "load profile picture success");
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.profilePic.setImageBitmap(Bitmap.createBitmap(bmp));
                    } else {
                        Log.d(TAG, "load profile picture error with " + e.toString());
                    }
                }
            });
        }

    }

}
