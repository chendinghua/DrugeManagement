package com.example.druge.sqlite.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.druge.entry.InventoryList;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.sqlite.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/3/1/001.
 */

public class InventoryDao {
    private static final String TAG = "InventoryDao";


    private Context context;
    private DBHelper dbHelper;

    public InventoryDao(Context context) {
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
            cursor = db.query(dbHelper.TABLE_INVENTORY, new String[]{"COUNT(Id)"}, null, null, null, null, null);

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


    public void foreachAddData(List<InventoryList> inventorys){
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            for (int i =0;i<inventorys.size();i++){
                String sql ="insert into " + dbHelper.TABLE_INVENTORY + " " +
                "(   ID,ProductID,AssetsTypeID,RfidNo,SerialNo,Remark,Status ,TaskType,TaskId,QcType,ProductName,AsstypeName,stockid,StockName,StatusName) values('"+
                        inventorys.get(i).getID()+"','"+
                        inventorys.get(i).getProductID()+"','"+
                        inventorys.get(i).getAssetsTypeID()+"','"+
                        inventorys.get(i).getRfidNo()+"','"+
                        inventorys.get(i).getSerialNo()+"','"+
                        inventorys.get(i).getRemark()+"','"+
                        inventorys.get(i).getStatus()+"','"+
                        inventorys.get(i).getTaskType()+"','"+
                        inventorys.get(i).getTaskId()+"','"+
                        inventorys.get(i).getQcType()+"','" +
                        inventorys.get(i).getProductName()+"','" +
                        inventorys.get(i).getAsstypeName()+"','" +
                        inventorys.get(i).getStockid()+"','" +
                        inventorys.get(i).getStockName()+"','" +
                        inventorys.get(i).getStatusName()+"')";

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
    public List<InventoryList> getAllDate(){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<InventoryList> inventoryLists=null;
        try {
            db = dbHelper.getReadableDatabase();
            // select * from Orders

         String sql ="select ID,ProductID,AssetsTypeID,RfidNo,SerialNo,Remark,Status ,TaskType,TaskId,QcType,ProductName,AsstypeName,stockid,StockName,StatusName from "+dbHelper.TABLE_INVENTORY;
            Log.d("selectSql", ""+sql);
            cursor = db.rawQuery(sql,null);
            inventoryLists = new ArrayList<>(cursor.getCount());
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    Log.d("testMysql", "getAllDate: "+cursor.getCount());
                    inventoryLists.add(parseOrder(cursor));
                }
                return inventoryLists;
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

        return inventoryLists;
    }
    public boolean deleteOrder() {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            // delete from Orders where Id = 7
            db.delete(dbHelper.TABLE_INVENTORY,null,null);
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
    private InventoryList parseOrder(Cursor cursor){
        InventoryList inventoryList = new InventoryList();
        inventoryList.setID(cursor.getInt(cursor.getColumnIndex("ID")));
        inventoryList.setProductID(cursor.getInt(cursor.getColumnIndex("ProductID")));
        inventoryList.setAssetsTypeID(cursor.getInt(cursor.getColumnIndex("AssetsTypeID")));
        inventoryList.setRfidNo(cursor.getString(cursor.getColumnIndex("RfidNo")));
        inventoryList.setSerialNo(cursor.getString(cursor.getColumnIndex("SerialNo")));
        inventoryList.setStatus(cursor.getInt(cursor.getColumnIndex("Status")));
        inventoryList.setTaskType(cursor.getInt(cursor.getColumnIndex("TaskType")));
        inventoryList.setTaskId(cursor.getInt(cursor.getColumnIndex("TaskId")));
        inventoryList.setQcType(cursor.getInt(cursor.getColumnIndex("QcType")));
        inventoryList.setProductName(cursor.getString(cursor.getColumnIndex("ProductName")));

        inventoryList.setAsstypeName(cursor.getString(cursor.getColumnIndex("AsstypeName")));
        inventoryList.setStockid(cursor.getInt(cursor.getColumnIndex("stockid")));
        inventoryList.setStockName(cursor.getString(cursor.getColumnIndex("StockName")));
        inventoryList.setStatusName(cursor.getString(cursor.getColumnIndex("StatusName")));


      /*
*/
        return inventoryList;
    }
}
