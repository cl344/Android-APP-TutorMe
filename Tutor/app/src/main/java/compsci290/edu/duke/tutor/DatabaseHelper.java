package compsci290.edu.duke.tutor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Dtabase:
 * This class builds local database to store info for offline usage
 *
 * @author  Mitchell Berger, Cheng Lyu, Jia Zeng, Linda Zhou
 * @version 1.0
 * @since   2017-04-15
 */


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "tutorclass.db";

    public static final String TABLE_NAME = "tutorclass";
    public static final String COL_1 = "objectId";
    public static final String COL_2 = "name";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table" + TABLE_NAME + "(objectID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT )"  );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS"+TABLE_NAME);
        onCreate(db);


    }

    public boolean insertDATA(String ObjectID, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(COL_1, ObjectID);
        c.put(COL_2, name);
        long result = db.insert(TABLE_NAME,null,c);
        if(result == -1) return false;
        return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from" + TABLE_NAME,null);
        return res;

    }

    public boolean updateData(String ObjectID, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(COL_1, ObjectID);
        c.put(COL_2,name);
        db.update(TABLE_NAME, c, "ObjectID =?", new String[]{ObjectID});
        return true;
    }

    public Integer deleteData(String ObjectID){
        SQLiteDatabase db = this.getWritableDatabase();
        return(db.delete(TABLE_NAME, "ObjectID =?", new String[]{ObjectID}));

    }


}
