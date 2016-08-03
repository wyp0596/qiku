package cc.sinex.qiku.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sinex on 16/4/20.
 */
public class Dbhelper  extends SQLiteOpenHelper {

    private static Dbhelper dbhelper = null;

    public static Dbhelper getInstance(Context context) {
        if (dbhelper == null) {
            dbhelper = new Dbhelper(context);
        }
        return dbhelper;
    }

    private Dbhelper(Context context) {
        super(context, "datebase.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        //这张表采用二进制文件存储对象注意第二个字段我们将对象存取在这里面
        String sql_class_table="create table if not exists rideData(" +
                "_id integer primary key autoincrement,"+
                                "ride_data text,num integer)";
        db.execSQL(sql_class_table);

        //这张表采用二进制文件存储对象注意第二个字段我们将对象存取在这里面
        sql_class_table="create table if not exists sportData(" +
                "_id integer primary key autoincrement,"+
                "sport_data text,num integer)";
        db.execSQL(sql_class_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {

    }

}