package compsci290.edu.duke.tutor.mainfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import compsci290.edu.duke.tutor.loginregistration.LoginType;
import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.Utility;
import de.hdodenhof.circleimageview.CircleImageView;

//tutee profile
public class TuteeProfileFragment extends Fragment{

    public static String TAG = "TuteeProfileFragment";

    public String mTutorId;
    public LoginType mMode;

    protected CircleImageView profilePicture;
    protected ImageView backgroundImage;
    protected TextView name, bio;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect;
    private ImageView ivImage;
    private String userChoosenTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMode = (LoginType) this.getArguments().get("mode");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tuteeprofile, container, false);
        profilePicture = (CircleImageView) view.findViewById(R.id.profile_image);
        backgroundImage = (ImageView) view.findViewById(R.id.header_cover_image);
        name = (TextView) view.findViewById(R.id.user_profile_name_v2);
        bio = (TextView) view.findViewById(R.id.user_profile_short_bio);
        btnSelect = (Button) view.findViewById(R.id.upload_pic);
        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView) view.findViewById(R.id.profile_image);

        loadTutor();
        return view;
    }

    public void loadTutor() {
        String objectId = AVUser.getCurrentUser().getObjectId();

        AVObject userInfo = AVObject.createWithoutData(getResources().getString(R.string.database_table_user), objectId);
        AVQuery query = new AVQuery<>(getResources().getString(R.string.database_table_userinfo));
        query.whereEqualTo("uid", userInfo);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d(TAG, "Fetch tutor successfully");
                    if (list.size() > 1 || list.size() < 1) {
                        Log.d(TAG, "error fetching user info with size "+list.size());
                    } else {
                        AVObject tutorObject = list.get(0);
                        onTutorLoadingReady(tutorObject);
                    }
                } else {
                    Log.d(TAG, "fail to fetch tutor with "+ e.toString());
                }
            }
        });
    }

    public void onTutorLoadingReady(AVObject avObject) {
        name.setText(avObject.getString(getResources().getString(R.string.database_table_user_username)));
        if (avObject.getString(getResources().getString(R.string.database_table_tutor_bio)) == null) {
            bio.setVisibility(View.GONE);
        } else {
            bio.setText(avObject.getString(getResources().getString(R.string.database_table_tutor_bio)));
        }
        Log.d(TAG, "check userinfo id + " + avObject.getObjectId());

        // load user profile picture on background thread
        AVFile pic = (AVFile) avObject.get("picture");
        // safety check if user didn't set profile picture
        if (pic != null) {
            Log.d(TAG, "try to load profile picture");
            pic.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    if (e == null) {
                        Log.d(TAG, "load profile picture success");
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        profilePicture.setImageBitmap(Bitmap.createBitmap(bmp));
                    } else {
                        Log.d(TAG, "load profile picture error with " + e.toString());
                    }
                }
            });
        }
        mTutorId = avObject.getObjectId();
    }

    //popup to get permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Camera"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Gallery"))
                        galleryIntent();
                } else {
                    //code for deny
                    Toast.makeText(getContext(), "Please choose yes in order to access your photos", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //give a choice of camera or gallery picture upload
    private void selectImage() {
        final CharSequence[] items = { "Camera", "Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update your profile picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(getActivity());

                if (items[item].equals("Camera")) {
                    userChoosenTask ="Camera";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Gallery")) {
                    userChoosenTask ="Gallery";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //intents depending on which (gallery or camera) upload the user chooses
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    //open the camera or gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    //get the picture from camera or gallery
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // save profile picture under table UserInfo
        AVObject userInfo = AVObject.createWithoutData("UserInfo", mTutorId);
        AVFile newProfilePic = new AVFile("profilepic", bytes.toByteArray());
        userInfo.put("picture", newProfilePic);
        userInfo.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d(TAG, "save profile pic success");
                } else {
                    Log.d(TAG, "save profile pic fail with " + e.toString());
                }
            }
        });
        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }

}