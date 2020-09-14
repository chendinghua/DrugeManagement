package com.example.druge.sqlite.Dao;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.sqlite.DBHelper;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Administrator on 2019/3/1/001.
 */

public class AddInfoTaskDao {
    private static final String TAG = "OrdersDao";

    // 列定义
    private final String[] ORDER_COLUMNS = new String[] {"Id", "CustomName","OrderPrice","Country"};

    private Context context;
    private DBHelper dbHelper;

    public AddInfoTaskDao(Context context) {
        this.context = context;

        dbHelper = DBHelper.getInstance(context);
    }

    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist(){
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            // select count(Id) from Orders
            cursor = db.query(dbHelper.ADD_INFO_TASK, new String[]{"COUNT(Id)"}, null, null, null, null, null);

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
            if (db != null) {
                db.close();
            }
        }
        return false;
    }


    public void foreachAddData(List<TaskListInfoItem> taskListInfoItems){
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            for (int i =0;i<taskListInfoItems.size();i++){
                String sql ="insert into " + dbHelper.ADD_INFO_TASK + " " +
                "( TaDeId ,StorageId, RfidNo ,SerialNo ,AssestId,AssestName,AssestTypeName,StorageStatus,StorageStatusName,StatusName) values('"+
                       taskListInfoItems.get(i).getTaDeId()+"','"+
                        taskListInfoItems.get(i).getStorageId()+"','"+
                        taskListInfoItems.get(i).getRfidNo()+"','"+
                        taskListInfoItems.get(i).getSerialNo()+"','"+
                        taskListInfoItems.get(i).getAssestId()+"','"+
                        taskListInfoItems.get(i).getAssestName()+"','"+
                        taskListInfoItems.get(i).getAssestTypeName()+"','"+
                        taskListInfoItems.get(i).getStorageStatus()+"','"+
                        taskListInfoItems.get(i).getStorageStatusName()+"','"+
                        taskListInfoItems.get(i).getStatusName()+"')";

                Log.d(TAG, "foreachAddData: "+sql);
                db.execSQL(sql);
            }
          //  db.execSQL("insert into " + dbHelper.TABLE_NAME + " ( rfid, productName, modelNumber,productSize,productColor,productUnit,productStatus,putOnShelves,putOffShelves) values ('E28011700000020C450F6C4B',1,1,1,1,1,1,'IN201903021057','')");
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }




    /**
     * 查询数据库中所有数据
     */
    public List<TaskListInfoItem> getAllDate(){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<TaskListInfoItem> taskListInfoItems=null;
        try {
            db = dbHelper.getReadableDatabase();
            // select * from Orders

         String sql ="select TaDeId ,StorageId, RfidNo ,SerialNo ,AssestId,AssestName,AssestTypeName,StorageStatus,StorageStatusName,StatusName from "+dbHelper.ADD_INFO_TASK;
            Log.d("selectSql", ""+sql);
            cursor = db.rawQuery(sql,null);
            taskListInfoItems = new ArrayList<>(cursor.getCount());
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    Log.d("testMysql", "getAllDate: "+cursor.getCount());
                    taskListInfoItems.add(parseOrder(cursor));
                }
                return taskListInfoItems;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return taskListInfoItems;
    }
    public boolean deleteOrder() {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            // delete from Orders where Id = 7
            db.delete(dbHelper.ADD_INFO_TASK,null,null);
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }





    /**
     * 将查找到的数据转换成Order类
     */
    private TaskListInfoItem parseOrder(Cursor cursor){
        TaskListInfoItem taskListInfoItem = new TaskListInfoItem();
        taskListInfoItem.setTaDeId(cursor.getInt(cursor.getColumnIndex("TaDeId")));
        taskListInfoItem.setStorageId(cursor.getInt(cursor.getColumnIndex("StorageId")));
        taskListInfoItem.setRfidNo(cursor.getString(cursor.getColumnIndex("RfidNo")));
        taskListInfoItem.setSerialNo(cursor.getString(cursor.getColumnIndex("SerialNo")));
        taskListInfoItem.setAssestId(cursor.getInt(cursor.getColumnIndex("AssestId")));
        taskListInfoItem.setAssestName(cursor.getString(cursor.getColumnIndex("AssestName")));
        taskListInfoItem.setAssestTypeName(cursor.getString(cursor.getColumnIndex("AssestTypeName")));
        taskListInfoItem.setStorageStatus(cursor.getInt(cursor.getColumnIndex("StorageStatus")));
        taskListInfoItem.setStorageStatusName(cursor.getString(cursor.getColumnIndex("StorageStatusName")));
        taskListInfoItem.setStatusName(cursor.getString(cursor.getColumnIndex("StatusName")));
      /*
*/
        return taskListInfoItem;
    }
}
