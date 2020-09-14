package com.example.druge.sqlite.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.druge.entry.InventoryCommitData;
import com.example.druge.entry.InventoryList;
import com.example.druge.entry.SynTempInfoData.SynTempInfoPDA;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.sqlite.DBHelper;
import com.example.druge.tools.UserConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 中间表
 * Created by 16486 on 2020/8/21.
 */
public class SynTempInfoDao {

    private static final String TAG = "SynTempInfoDao";


    private Context context;

    public SynTempInfoDao(Context context) {
        this.context = context;
    }


    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist( SQLiteDatabase db,int id ){
        int count = 0;

        Cursor cursor = null;

        try {
            String[] args = {String.valueOf(id)};
            // select count(Id) from Orders
            cursor = db.query(DBHelper.TABLE_SYN_TEMP_INFO, new String[]{"COUNT(Id)"}, "synId=?", args, null, null, null);

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

    public List<InventoryList> getInentoryDataList(SQLiteDatabase db,int synId)throws Exception{
        List<InventoryList> inventoryLists = new ArrayList<>();

        String[] args = {String.valueOf(synId)};
        String sql = "\n" +
                "select tempInfo.Id as ID,tempInfo.ProductId as ProductID,\n" +
                "tempInfo.DrugTypeId as AssetsTypeID,\n" +
                "tempInfo.RfidNo as RfidNo,\n" +
                "tempInfo.SerialNo as SerialNo,\n" +
                "synTemp.TaskTypeId as TaskType,\n" +
                "tempInfo.productName as ProductName,\n" +
                "tempInfo.DrugTypeName as  AsstypeName,\n" +
                "tempInfo.StockName as StockName,\n" +
                "tempInfo.StatusName as StatusName,\n" +
                "tempInfo.StorageId as StorageId ," +
                "tempInfo.StockId as StockId ," +
                "tempInfo.StorageStatus as StorageStatus,OpStatus" +
                "  from "+DBHelper.TABLE_SYN_TEMP_INFO+" as tempInfo inner join "+DBHelper.SYNTEMPPDA+" synTemp on tempInfo.synId=synTemp.id where synTemp.TaskTypeId = 8  and  synTemp.id=?";
        Cursor cursor=null;
        try {
       cursor = db.rawQuery(sql, args);

            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    inventoryLists.add(parseInventroyOrder(cursor));

                }
            }
        }catch (Exception e){
            throw  e;
        }finally {
          if(cursor!=null)
              cursor.close();
        }

        return inventoryLists;

    }
    public boolean deleteData(SQLiteDatabase db, int id ){

        String[] args = {String.valueOf(id)};


        return db.delete(DBHelper.TABLE_SYN_TEMP_INFO,"synId=?",args)>0;

    }


    public List<TaskListInfoItem> getTaskDataList(SQLiteDatabase db, int synId,Integer[]taskTypeList)throws Exception{
        List<TaskListInfoItem> taskListInfoItems = new ArrayList<>();
        StringBuffer taskTypeStr =new StringBuffer("");

        for (int i = 0;i<taskTypeList.length;i++){

            taskTypeStr.append(""+taskTypeList[i]);
            if(taskTypeList.length-1!=i){
                taskTypeStr.append(",");
            }

        }
        //1,2,6

        String[] args = {String.valueOf(synId)};
        String sql = "\n" +
                "select tempInfo.Id as ID,tempInfo.ProductId as ProductID,\n" +
                "tempInfo.DrugTypeId as AssetsTypeID,\n" +
                "tempInfo.RfidNo as RfidNo,\n" +
                "tempInfo.SerialNo as SerialNo,\n" +
                "synTemp.TaskTypeId as TaskType,\n" +
                "tempInfo.productName as ProductName,\n" +
                "tempInfo.DrugTypeName as  AsstypeName,\n" +
                "tempInfo.StockName as StockName,\n" +
                "tempInfo.StatusName as StatusName,\n" +
                "tempInfo.StorageId as StorageId ," +
                "tempInfo.StockId as StockId ," +
                "tempInfo.StorageStatus as StorageStatus,OpStatus" +
                "  from "+DBHelper.TABLE_SYN_TEMP_INFO+" as tempInfo inner join "+DBHelper.SYNTEMPPDA+" synTemp on tempInfo.synId=synTemp.id where synTemp.TaskTypeId in ("+taskTypeStr.toString()+") and  synTemp.id=?";

        Log.d("getTaskDataList", "getTaskDataList: "+sql);
        Cursor cursor=null;
        try {
            cursor = db.rawQuery(sql, args);

            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    taskListInfoItems.add(parseTaskOrder(cursor));

                }
            }
        }catch (Exception e){
            throw  e;
        }finally {
            if(cursor!=null)
                cursor.close();
        }

        return taskListInfoItems;

    }

    //提交盘点结果 此处是单独执行的语句，所以会在里面直接db.close()
    public boolean addIventoryCommit(SQLiteDatabase db , HashMap<String,Object> map){
        boolean isSuccess = true;
        int dataId =Integer.parseInt(map.get("ID").toString());
        String endTime = map.get("EndTime").toString();
        String result = map.get("Result").toString();
        List<InventoryCommitData> datas =(List<InventoryCommitData>) map.get("DetailList");
        String sql;

        try {
            db.beginTransaction();
            for (int i = 0; i < datas.size(); i++) {
                InventoryCommitData data = datas.get(i);

                if (data.getId() != 0) {
                    sql = "update " + DBHelper.TABLE_SYN_TEMP_INFO + " set OpStatus=? where id=?";
                    db.execSQL(sql, new Object[]{data.getStatus(), data.getId()});
                } else {
                    sql = "insert into  SynTempinfoPDA(RfidNo,synid,OpStatus) values(?,?,?)";
                    db.execSQL(sql, new Object[]{data.getRfidNo(), dataId, data.getStatus()});
                }
            }

            sql = "update SynTempPDA set remark=? , endtime=? ,Status=? ,submitUserID=?,submitUser=?  where id=?";

            db.execSQL(sql, new Object[]{result, endTime,2, UserConfig.UserId,UserConfig.UserName, dataId});
            db.setTransactionSuccessful();
        }catch (Exception e){
            isSuccess=false;
        }finally {
            if(db!=null) {
             db.endTransaction();
                db.close();
            }
        }





        return isSuccess;
    }



    public void foreachAddData(SQLiteDatabase db ,List<SynTempInfoPDA> synTempInfoPDAs) throws  Exception{

            for (int i =0;i<synTempInfoPDAs.size();i++){
                String sql ="insert into " + DBHelper.TABLE_SYN_TEMP_INFO + " " +
                        "(  StorageId,RfidNo,SerialNo,DrugTypeName,ProductId,StockId,productName,synId,StockName,DrugTypeId,OpStatus,StatusName,StorageStatus) values("+
                        "'"+synTempInfoPDAs.get(i).getStorageId()+"'," +
                        "'"+synTempInfoPDAs.get(i).getRfidNo()+"'," +
                        "'"+synTempInfoPDAs.get(i).getSerialNo()+"'," +
                        "'"+synTempInfoPDAs.get(i).getDrugTypeName()+"'," +
                        "'"+synTempInfoPDAs.get(i).getProductId()+"'," +
                        "'"+synTempInfoPDAs.get(i).getStockId()+"'," +
                        "'"+synTempInfoPDAs.get(i).getProductName()+"'," +
                        "'"+synTempInfoPDAs.get(i).getSynId()+"'," +
                        "'"+synTempInfoPDAs.get(i).getStockName()+"'," +
                        "'"+synTempInfoPDAs.get(i).getDrugTypeId()+"'," +
                        "'"+synTempInfoPDAs.get(i).getOpStatus()+"'," +
                        "'"+synTempInfoPDAs.get(i).getStatusName()+"'," +
                        "'"+synTempInfoPDAs.get(i).getStorageStatus()+"')";
                Log.d(TAG, "foreachAddData: "+sql);
                db.execSQL(sql);
            }

    }




    /**
     * 查询数据库中所有数据
     */
    public List<SynTempInfoPDA> getAllDate(  SQLiteDatabase db ){
        Cursor cursor = null;
        List<SynTempInfoPDA> synTempInfoPDAs=null;
        try {

            String sql ="select ID,StorageId,RfidNo,SerialNo,DrugTypeName,ProductId,StockId,productName,synId,StockName,DrugTypeId,OpStatus from "+DBHelper.TABLE_SYN_TEMP_INFO;
            Log.d("selectSql", ""+sql);
            cursor = db.rawQuery(sql,null);
            synTempInfoPDAs = new ArrayList<>(cursor.getCount());
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    Log.d("testMysql", "getAllDate: "+cursor.getCount());
                    synTempInfoPDAs.add(parseOrder(cursor));
                }
                return synTempInfoPDAs;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return synTempInfoPDAs;
    }
    /**
     * 将查找到的数据转换成Order类
     */
    private SynTempInfoPDA parseOrder(Cursor cursor){
        SynTempInfoPDA synTempInfoPDA = new SynTempInfoPDA();
        synTempInfoPDA.setId(cursor.getInt(cursor.getColumnIndex("ID")));
        synTempInfoPDA.setStorageId(cursor.getInt(cursor.getColumnIndex("StorageId")));
        synTempInfoPDA.setRfidNo(cursor.getString(cursor.getColumnIndex("RfidNo")));
        synTempInfoPDA.setSerialNo(cursor.getString(cursor.getColumnIndex("SerialNo")));
        synTempInfoPDA.setDrugTypeName(cursor.getString(cursor.getColumnIndex("DrugTypeName")));
        synTempInfoPDA.setProductId(cursor.getInt(cursor.getColumnIndex("ProductId")));
        synTempInfoPDA.setStockId(cursor.getInt(cursor.getColumnIndex("StockId")));
        synTempInfoPDA.setProductName(cursor.getString(cursor.getColumnIndex("productName")));
        synTempInfoPDA.setDrugTypeId(cursor.getInt(cursor.getColumnIndex("DrugTypeId")));
        synTempInfoPDA.setStockName(cursor.getString(cursor.getColumnIndex("StockName")));
        synTempInfoPDA.setSynId(cursor.getInt(cursor.getColumnIndex("synId")));
        synTempInfoPDA.setOpStatus(cursor.getInt(cursor.getColumnIndex("OpStatus")));
        return synTempInfoPDA;
    }

    /**
     * 将查找到的数据转换成Order类
     */
    private InventoryList parseInventroyOrder(Cursor cursor){
        InventoryList inventoryList = new InventoryList();
        inventoryList.setID(cursor.getInt(cursor.getColumnIndex("ID")));
        inventoryList.setProductID(cursor.getInt(cursor.getColumnIndex("ProductID")));
        inventoryList.setAssetsTypeID(cursor.getInt(cursor.getColumnIndex("AssetsTypeID")));


        inventoryList.setRfidNo(cursor.getString(cursor.getColumnIndex("RfidNo")));
        inventoryList.setSerialNo(cursor.getString(cursor.getColumnIndex("SerialNo")));
        inventoryList.setTaskType(cursor.getInt(cursor.getColumnIndex("TaskType")));

        inventoryList.setProductName(cursor.getString(cursor.getColumnIndex("ProductName")));
        inventoryList.setAsstypeName(cursor.getString(cursor.getColumnIndex("AsstypeName")));
        inventoryList.setStockName(cursor.getString(cursor.getColumnIndex("StockName")));
        inventoryList.setStatusName(cursor.getString(cursor.getColumnIndex("StatusName")));
        inventoryList.setStorageId(cursor.getInt(cursor.getColumnIndex("StorageId")));
        inventoryList.setStockid(cursor.getInt(cursor.getColumnIndex("StockId")));
        inventoryList.setStorageStatus(cursor.getInt(cursor.getColumnIndex("StorageStatus")));
        inventoryList.setOpStatus(cursor.getInt(cursor.getColumnIndex("OpStatus")));
        return inventoryList;
    }


    private TaskListInfoItem parseTaskOrder(Cursor cursor){
        TaskListInfoItem taskListInfoItem = new TaskListInfoItem();
        taskListInfoItem.setTaDeId(cursor.getInt(cursor.getColumnIndex("ID")));
        taskListInfoItem.setAssestId(cursor.getInt(cursor.getColumnIndex("ProductID")));
        taskListInfoItem.setAssestTypeId(cursor.getInt(cursor.getColumnIndex("AssetsTypeID")));


        taskListInfoItem.setRfidNo(cursor.getString(cursor.getColumnIndex("RfidNo")));
        taskListInfoItem.setSerialNo(cursor.getString(cursor.getColumnIndex("SerialNo")));
      //  taskListInfoItem.setTaskType(cursor.getInt(cursor.getColumnIndex("TaskType")));

        taskListInfoItem.setAssestName(cursor.getString(cursor.getColumnIndex("ProductName")));
        taskListInfoItem.setAssestTypeName(cursor.getString(cursor.getColumnIndex("AsstypeName")));
        taskListInfoItem.setStockName(cursor.getString(cursor.getColumnIndex("StockName")));
        taskListInfoItem.setStatusName(cursor.getString(cursor.getColumnIndex("StatusName")));
        taskListInfoItem.setStorageId(cursor.getInt(cursor.getColumnIndex("StorageId")));
        taskListInfoItem.setStockid(cursor.getInt(cursor.getColumnIndex("StockId")));
        taskListInfoItem.setStorageStatus(cursor.getInt(cursor.getColumnIndex("StorageStatus")));
        taskListInfoItem.setStatus(cursor.getInt(cursor.getColumnIndex("OpStatus")));
        return taskListInfoItem;
    }

}
