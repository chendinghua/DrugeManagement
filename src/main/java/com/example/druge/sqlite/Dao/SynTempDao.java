package com.example.druge.sqlite.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.druge.entry.SynData.SynTemp;
import com.example.druge.entry.TaskListInfo;
import com.example.druge.sqlite.DBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 中间表
 * Created by 16486 on 2020/8/21.
 */
public class SynTempDao {

    private static final String TAG = "SynTempDao";


    private Context context;

    public SynTempDao(Context context) {
        this.context = context;
    }



    /**
     * 判断表中是否有数据
     */
    public int isDataExist(SQLiteDatabase db){
        int count = 0;
        Cursor cursor = null;

        try {
            // select count(Id) from Orders
            cursor = db.query(DBHelper.SYNTEMPPDA, new String[]{"COUNT(Id)"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
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
        return count;
    }


    public void foreachAddData(SQLiteDatabase db, List<SynTemp> synTemps) throws Exception{

            for (int i =0;i<synTemps.size();i++){
                String sql ="insert into " + DBHelper.SYNTEMPPDA + " " +
                        "(  TaskType,taskTypeId,TaskID,OpUser,OpUserID,OpTime,remark,Content,imageFile,pickNo,Status,DataID) values("+
                        "'"+synTemps.get(i).getTaskType()+"'," +
                        "'"+synTemps.get(i).getTaskTypeId()+"'," +
                        "'"+synTemps.get(i).getTaskID()+"'," +
                        "'"+synTemps.get(i).getOpUser()+"'," +
                        "'"+synTemps.get(i).getOpUserID()+"'," +
                        "'"+synTemps.get(i).getOpTime()+"'," +
                        "'"+synTemps.get(i).getRemark()+"'," +
                        "'"+synTemps.get(i).getContent()+"'," +
                        "'"+synTemps.get(i).getImageFile()+"'," +
                        "'"+synTemps.get(i).getPickNo()+"'," +
                        "'"+synTemps.get(i).getStatus()+"'," +
                        "'"+synTemps.get(i).getDataID()+"'  )";
                Log.d(TAG, "foreachAddData: "+sql);
                db.execSQL(sql);
            }
    }

    /**
     * 查询数据库中所有数据
     */
    public int getAllDateCount(SQLiteDatabase db ,Map<String,Object> list){
        Cursor cursor = null;
      int count =0;

        try {
            // select * from Orders

            String sql ="select count(id) from "+DBHelper.SYNTEMPPDA+" synTemp  where 1=1";
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

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
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

        return count;
    }



    /**
     * 查询数据库中所有数据
     */
    public List<SynTemp> getAllDate(SQLiteDatabase db ,Map<String,Object> list,int pageIndex,int pageSize,String choice){
        Cursor cursor = null;
        List<SynTemp> synTemps=null;

        try {
            // select * from Orders

            String sql ="select ID,TaskType,taskTypeId,TaskID,OpUser,OpUserID,OpTime,remark,Content,imageFile,pickNo,Status,DataID ,  " +
                    "(select name from "+DBHelper.TABL_DIC_INFO+"  where GroupName = 'TaskStatus' and Value= synTemp.Status) as StatusName,synTemp.endtime  as endtime ,submitUserID,submitUser" +
                    " from "+DBHelper.SYNTEMPPDA+" synTemp   ";
            String[] param = new String[list.entrySet().size()];
           int i =0;
            if(list !=null && list.size()>0){
                Set<Map.Entry<String, Object>> sets = list.entrySet();

                for (Map.Entry<String, Object> entry :sets){
                    if(i==0){
                      sql+=" where  " + entry.getKey() + " = ? ";
                    }else {

                        sql += choice + " " + entry.getKey() + " = ? ";
                    }
                    param[i]=entry.getValue().toString();
                    i++;
                }
            }
            sql+=" limit "+pageSize+" offset "+(pageIndex)*pageSize;

            Log.d("selectSql", ""+sql);
            cursor = db.rawQuery(sql,param);
            synTemps = new ArrayList<>(cursor.getCount());
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    Log.d("testMysql", "getAllDate: "+cursor.getCount());
                    synTemps.add(parseOrder(cursor));
                }
                return synTemps;
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

        return synTemps;
    }





    /**
     * 查询数据库所有任务
     */
    public List<TaskListInfo> getTaskAllDate(SQLiteDatabase db , Map<String,Object> list,String content, int pageIndex, int pageSize){
        Cursor cursor = null;
        List<TaskListInfo> taskListInfos= new ArrayList<>();

        try {
            // select * from Orders

            String sql ="select ID,TaskType,taskTypeId,TaskID,OpUser,OpUserID,OpTime,remark,Content,imageFile,pickNo,Status ," +
                    "(select name from "+DBHelper.TABL_DIC_INFO+"  where GroupName = 'TaskStatus' and Value= synTemp.Status) as StatusName" +
                    " from "+DBHelper.SYNTEMPPDA+" as  synTemp where 1=1 ";
            String[] param = new String[list.entrySet().size()+1];
            int i =0;
            Log.d("selectSql", "getTaskAllDate: "+sql);
            if(list !=null && list.size()>0){
                Set<Map.Entry<String, Object>> sets = list.entrySet();

                for (Map.Entry<String, Object> entry :sets){
                    sql+=" and "+entry.getKey()+" = ?";
                    param[i]=entry.getValue().toString();
                    i++;
                }
            }
            sql+=" and Content like ?";
            param[i] ="%"+content+"%";
            sql+=" limit "+pageSize+" offset "+(pageIndex)*pageSize;

            Log.d("selectSql", ""+sql);
            cursor = db.rawQuery(sql,param);
            taskListInfos = new ArrayList<>(cursor.getCount());
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    Log.d("testMysql", "getAllDate: "+cursor.getCount());
                    taskListInfos.add(parseTaskOrder(cursor));
                }
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

        return taskListInfos;
    }

    /**
     * 将查找到的数据转换成Order类
     */
    private TaskListInfo parseTaskOrder(Cursor cursor){
        TaskListInfo taskListInfo = new TaskListInfo();
        taskListInfo.setID(cursor.getInt(cursor.getColumnIndex("Id")));
        taskListInfo.setCreatorID(cursor.getInt(cursor.getColumnIndex("OpUserID")));
        taskListInfo.setTaskType(cursor.getInt(cursor.getColumnIndex("TaskTypeId")));
        taskListInfo.setCreatorName(cursor.getString(cursor.getColumnIndex("OpUser")));
        taskListInfo.setOpTime(cursor.getString(cursor.getColumnIndex("OpTime")));
        taskListInfo.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
        taskListInfo.setContent(cursor.getString(cursor.getColumnIndex("Content")));
        taskListInfo.setStatus(cursor.getInt(cursor.getColumnIndex("Status")));

        taskListInfo.setStatusName(cursor.getString(cursor.getColumnIndex("StatusName")));
        return taskListInfo;
    }

    public boolean deleteData(SQLiteDatabase db, int id ){

        String[] args = {String.valueOf(id)};

        return db.delete(DBHelper.SYNTEMPPDA,"id=?",args)>0;


    }
    /**
     * 将查找到的数据转换成Order类
     */
    private SynTemp parseOrder(Cursor cursor){
        SynTemp synTemp = new SynTemp();
        synTemp.setId(cursor.getInt(cursor.getColumnIndex("Id")));
        synTemp.setOpUserID(cursor.getInt(cursor.getColumnIndex("OpUserID")));
        synTemp.setTaskID(cursor.getInt(cursor.getColumnIndex("TaskID")));
        synTemp.setTaskTypeId(cursor.getInt(cursor.getColumnIndex("TaskTypeId")));
        synTemp.setTaskType(cursor.getString(cursor.getColumnIndex("TaskType")));
        synTemp.setOpUser(cursor.getString(cursor.getColumnIndex("OpUser")));
        synTemp.setOpTime(cursor.getString(cursor.getColumnIndex("OpTime")));
        synTemp.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
        synTemp.setContent(cursor.getString(cursor.getColumnIndex("Content")));
        synTemp.setImageFile(cursor.getString(cursor.getColumnIndex("imageFile")));
        synTemp.setPickNo(cursor.getString(cursor.getColumnIndex("pickNo")));
        synTemp.setStatus(cursor.getInt(cursor.getColumnIndex("Status")));

        synTemp.setStatusName(cursor.getString(cursor.getColumnIndex("StatusName")));
        synTemp.setEndtime(cursor.getString(cursor.getColumnIndex("endtime")));
        synTemp.setDataID(cursor.getInt(cursor.getColumnIndex("DataID")));

        synTemp.setSubmitUserID(cursor.getInt(cursor.getColumnIndex("submitUserID")));
        synTemp.setSubmitUser(cursor.getString(cursor.getColumnIndex("submitUser")));

        return synTemp;
    }
}
