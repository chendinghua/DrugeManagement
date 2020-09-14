package com.example.druge.sqlite.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.druge.entry.RightInfo;
import com.example.druge.entry.UserInfo.User;
import com.example.druge.sqlite.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 16486 on 2020/8/26.
 */

public class RightInfoDao {

    private static final String TAG = "RightInfoDao";


    private Context context;

    public RightInfoDao(Context context) {
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
            cursor = db.query(DBHelper.RIGHTINFO, new String[]{"COUNT(Id)"}, null, null, null, null, null);

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
     * 判断用户id是否存在
     */
    public boolean isUserInfoExist( SQLiteDatabase db,int userId ){
        int count = 0;

        Cursor cursor = null;

        try {
            String[] args = {String.valueOf(userId)};
            // select count(Id) from Orders
            cursor = db.query(DBHelper.RIGHTINFO, new String[]{"COUNT(Id)"}, "UserId=?", args, null, null, null);

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
     * 添加批量数据
     * @param db
     * @param rightInfos 权限集合
     * @param userId 用户id
     * @throws Exception 抛出的异常
     */
    public void addForcahData(SQLiteDatabase db , int userId, List<RightInfo> rightInfos) throws  Exception{

          /*  *//*String sql ="insert into "+dbHelper.USERINFO+" ( userId,userName,password,name"+")" +
                    "values ('"+userId+"','"+userName+"','"+password+"','"+name+"')";*//*
         //   db.execSQL(sql);
        ContentValues cv  = new ContentValues();
        cv.put("userId",userId);
        cv.put("userName",userName);
        cv.put("password",password);
        cv.put("name",name);

            db.insert(dbHelper.RIGHTINFO,null,cv);*/
        if(rightInfos.size()>0) {
            StringBuffer rightBuffer = new StringBuffer(" insert into " + DBHelper.RIGHTINFO + "( name,RightType,Platform,SortNo,ActionForm,ActionBtn,ActionUrl,Remark,UserId) values");
            for (int i = 0; i < rightInfos.size(); i++) {
                RightInfo rightInfo = rightInfos.get(i);

                rightBuffer.append("(" +
                        " '" + rightInfo.getName() + "'," +
                        " '" + rightInfo.getRightType() + "'," +
                        " '" + rightInfo.getPlatform() + "'," +
                        " '" + rightInfo.getSortNo() + "'," +
                        "'" + rightInfo.getActionForm() + "'," +
                        "'" + rightInfo.getActionBtn() + "'," +
                        "'" + rightInfo.getActionUrl() + "'," +
                        "'" + rightInfo.getRemark() + "','" + userId + "')");

                if (rightInfos.size() - 1 != i) {
                    rightBuffer.append(",");
                }
            }
            db.execSQL(rightBuffer.toString());
        }

    }
    //根据用户id删除权限
    public void deleteRightInfo(SQLiteDatabase db , int userId){
        String []args = {String.valueOf(userId)};
        db.delete( DBHelper.RIGHTINFO,"UserId=?",args);

    }





    /**
     * 获取权限数据
     */
    public List<RightInfo> getRightInfo(SQLiteDatabase db ,int userID) throws Exception{
      List<RightInfo> rightInfos = new ArrayList<>();
        Cursor cursor = null;

            try {
                String[] args = {String.valueOf(userID)};
                // select count(Id) from Orders
                cursor = db.query(DBHelper.RIGHTINFO, new String[]{"Name", "RightType", "Platform", "ActionForm", "ActionBtn", "ActionUrl", "Remark"}, "UserId=? ", args, null, null, "SortNo");


                while (cursor.moveToNext()) {
                    RightInfo rightInfo = new RightInfo();
                    rightInfo.setName(cursor.getString(cursor.getColumnIndex("Name")));


                    rightInfo.setRightType(cursor.getInt(cursor.getColumnIndex("RightType")));

                    rightInfo.setPlatform(cursor.getInt(cursor.getColumnIndex("Platform")));
                    rightInfo.setActionForm(cursor.getString(cursor.getColumnIndex("ActionForm")));
                    rightInfo.setActionBtn(cursor.getString(cursor.getColumnIndex("ActionBtn")));
                    rightInfo.setActionUrl(cursor.getString(cursor.getColumnIndex("ActionUrl")));
                    rightInfo.setRemark(cursor.getString(cursor.getColumnIndex("Remark")));
                    rightInfos.add(rightInfo);

                }
            }catch (Exception e){
                throw e;
            }finally {
                if(cursor!=null)
                    cursor.close();
            }


        return rightInfos;
    }


}
