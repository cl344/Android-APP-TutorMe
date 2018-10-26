package compsci290.edu.duke.tutor;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.im.v2.AVIMClient;

import cn.leancloud.chatkit.LCChatKit;
import compsci290.edu.duke.tutor.chat.CustomUserProvider;
import compsci290.edu.duke.tutor.model.Course;
import compsci290.edu.duke.tutor.model.CourseTaken;
import compsci290.edu.duke.tutor.model.Request;
import compsci290.edu.duke.tutor.model.Session;

/**
 * MyLeanCloudApp:
 * This class sets up the connection to online database LeanCloud
 *
 * @author  Mitchell Berger, Cheng Lyu, Jia Zeng, Linda Zhou
 * @version 1.0
 * @since   2017-04-15
 */


public class MyLeanCloudApp extends Application {


    private final String APP_ID = "gPyzAE6Fgqgqeg4xY6sf5MR0-gzGzoHsz";
    private final String APP_KEY = "Mg9W4tqijHAxcOnTm46Idi4U";


    @Override
    public void onCreate() {
        super.onCreate();





        AVObject.registerSubclass(Request.class);
        AVObject.registerSubclass(Session.class);
        AVObject.registerSubclass(Course.class);
        AVObject.registerSubclass(CourseTaken.class);
        // initialize connection with parameters for this, AppId, AppKey
        AVOSCloud.initialize(this,"gPyzAE6Fgqgqeg4xY6sf5MR0-gzGzoHsz","Mg9W4tqijHAxcOnTm46Idi4U");




        LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance());
        AVOSCloud.setDebugLogEnabled(true);
        LCChatKit.getInstance().init(getApplicationContext(), APP_ID, APP_KEY);
        AVIMClient.setAutoOpen(false);
    }
}