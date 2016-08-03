package cc.sinex.qiku.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import cc.sinex.qiku.entity.Locate;


/**
 * Created by sinex on 16/4/20.
 */
public class RideDataServcie {
    Context context;

    public RideDataServcie(Context context) {

        this.context = context;
    }

    /**
     * 保存
     * @param
     */
    public void saveObject(List<Locate> locates) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(locates);
            objectOutputStream.flush();
            byte data[] = arrayOutputStream.toByteArray();
            objectOutputStream.close();
            arrayOutputStream.close();


            Dbhelper dbhelper = Dbhelper.getInstance(context);
            SQLiteDatabase database = dbhelper.getWritableDatabase();

            String countQuery = "SELECT  * FROM " + "rideData";

            Cursor cursor = database.rawQuery(countQuery, null);
            int cnt = cursor.getCount();
            cursor.close();

            database.execSQL("insert into rideData values(null,?,?)", new Object[]{data, cnt});
            database.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ArrayList<List<Locate>> getObject() {
        ArrayList<List<Locate>> list = new ArrayList<List<Locate>>();


        List<Locate> locates = null;
        Dbhelper dbhelper = Dbhelper.getInstance(context);
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from rideData", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                byte data[] = cursor.getBlob(cursor.getColumnIndex("ride_data"));
                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
                try {
                    ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
                    locates = (List<Locate>) inputStream.readObject();
                    inputStream.close();
                    arrayInputStream.close();

                    list.add(locates);
                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
        }
        return list;

    }
    public List<Locate> getObject(int position) {

        List<Locate> locates = null;
        Dbhelper dbhelper = Dbhelper.getInstance(context);
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from rideData", null);

        if (cursor != null) {

            cursor.moveToPosition(position);

            byte data[] = cursor.getBlob(cursor.getColumnIndex("ride_data"));
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
            try {
                ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
                locates = (List<Locate>) inputStream.readObject();
                inputStream.close();
                arrayInputStream.close();

            } catch (Exception e) {

                e.printStackTrace();
            }
            cursor.close();


        }
        return locates;

    }
    public void deletData(int num){
        Dbhelper dbhelper = Dbhelper.getInstance(context);
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        database.delete("rideData","num=?",new String[]{num+""});
        database.close();
    }


}