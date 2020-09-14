package com.example.druge.sqlite.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.druge.entry.DicInfo;
import com.example.druge.sqlite.DBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 中间表
 * Created by 16486 on 2020/8/21.
 */
public class DicInfoDao {

    private static final String TAG = "DicInfoDao";


    private Context context;

    public DicInfoDao(Context context) {
        this.context = context;
    }



    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist(SQLiteDatabase db){
        int count = 0;

        Cursor cursor = null;

        try {
            // select count(Id) from Orders
            cursor = db.query(DBHelper.TABL_DIC_INFO, new String[]{"COUNT(Id)"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0) return true;
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        return false;
    }



    /**
     * 删除一条数据  此处删除Id为7的数据
     */

    public void deleteDicInfoAll(SQLiteDatabase db )throws Exception{
            db.delete(DBHelper.TABL_DIC_INFO, null, null);


    }

    public void foreachAddData( SQLiteDatabase db,List<DicInfo> dicInfos)throws Exception{
            for (int i =0;i<dicInfos.size();i++){
                String sql ="insert into " + DBHelper.TABL_DIC_INFO + " " +
                        "(  Name,Value,GroupName,statues) values("+
                        "'"+dicInfos.get(i).getName()+"'," +
                        "'"+dicInfos.get(i).getValue()+"'," +
                        "'"+dicInfos.get(i).getGroupName()+"'," +
                        "'"+dicInfos.get(i).getState()+"')";
                Log.d(TAG, "foreachAddData: "+sql);
                db.execSQL(sql);
            }
    }




    /**
     * 查询数据库中所有数据
     */
    public List<DicInfo> getAllDate(  SQLiteDatabase db,Map<String,Object> list){

        Cursor cursor = null;
        List<DicInfo> dicInfos= new ArrayList<>();
        try {
            // select * from Orders

            String sql ="select ID,Name,Value,GroupName,statues from "+DBHelper.TABL_DIC_INFO+"  where 1=1  ";


            String[] param = new String[list.entrySet().size()];
            int i =0;
            if(list !=null && list.size()>0){
                Set<Map.Entry<String, Object>> sets = list.entrySet();

                for (Map.Entry<String, Object> entry :sets){
                    sql+=" and "+entry.getKey()+" = ?";
                    param[i]=entry.getValue().toString();
                    i++;
                }
            }

            Log.d("selectSql", ""+sql);



            cursor = db.rawQuery(sql,param);
            dicInfos = new ArrayList<>(cursor.getCount());
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    Log.d("testMysql", "getAllDate: "+cursor.getCount());
                    dicInfos.add(parseOrder(cursor));
                }
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (!DBHelper.isCursorEmptyOrNotPrepared(cursor)) {
                cursor.close();
            }
        }

        return dicInfos;
    }
    /**
     * 将查找到的数据转换成Order类
     */
    private DicInfo parseOrder(Cursor cursor){
        DicInfo dicInfo = new DicInfo();
        dicInfo.setID(cursor.getInt(cursor.getColumnIndex("ID")));
        dicInfo.setState(cursor.getInt(cursor.getColumnIndex("statues")));
        dicInfo.setName(cursor.getString(cursor.getColumnIndex("Name")));
        dicInfo.setValue(cursor.getString(cursor.getColumnIndex("Value")));
        dicInfo.setGroupName(cursor.getString(cursor.getColumnIndex("GroupName")));
        return dicInfo;
    }
}
