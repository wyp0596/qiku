package cc.sinex.qiku.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import cc.sinex.qiku.entity.SportData;


/**
 * Created by sinex on 16/4/20.
 */
public class SportDataServcie {
    Context context;

    public SportDataServcie(Context context) {

        this.context = context;
    }

    /**
     * 保存
     * @param
     */
    public void saveObject(SportData sportData) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            //序号问题
            Dbhelper dbhelper = Dbhelper.getInstance(context);
            SQLiteDatabase database = dbhelper.getWritableDatabase();
            String countQuery = "SELECT  * FROM " + "sportData";
            Cursor cursor = database.rawQuery(countQuery, null);
            int cnt = cursor.getCount();
            cursor.close();

            sportData.setNum(cnt);



            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(sportData);
            objectOutputStream.flush();
            byte data[] = arrayOutputStream.toByteArray();
            objectOutputStream.close();
            arrayOutputStream.close();





            database.execSQL("insert into sportData values(null,?,?)", new Object[] { data,cnt });
            database.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ArrayList<SportData> getObject() {
        SportData sportData = null;
        ArrayList<SportData> sportDatas = new ArrayList<SportData>();

        Dbhelper dbhelper = Dbhelper.getInstance(context);
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from sportData", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                byte data[] = cursor.getBlob(cursor.getColumnIndex("sport_data"));
                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
                try {
                    ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
                    sportData = (SportData) inputStream.readObject();
                    inputStream.close();
                    arrayInputStream.close();

                    sportDatas.add(sportData);//添加一条记录

                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
            cursor.close();
        }
        return sportDatas;

    }
    public void deletData(int num){
        Dbhelper dbhelper = Dbhelper.getInstance(context);
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        database.delete("sportData","num=?",new String[]{num+""});
        database.close();
    }

}