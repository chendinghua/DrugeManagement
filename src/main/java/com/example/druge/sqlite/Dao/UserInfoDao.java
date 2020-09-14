package com.example.druge.sqlite.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.druge.entry.UserInfo.User;
import com.example.druge.sqlite.DBHelper;


/**
 * Created by 16486 on 2020/8/26.
 */

public class UserInfoDao  {

    private static final String TAG = "UserInfoDao";


    private Context context;

    public UserInfoDao(Context context) {
        this.context = context;
    }
    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist( SQLiteDatabase db){
        int count = 0;

        Cursor cursor = null;

        try {
            // select count(Id) from Orders
            cursor = db.query(DBHelper.USERINFO, new String[]{"COUNT(Id)"}, null, null, null, null, null);

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
    public boolean isUserInfoExist(  SQLiteDatabase db,int userId ){
        int count = 0;

        Cursor cursor = null;

        try {
            String[] args = {String.valueOf(userId)};
            // select count(Id) from Orders
            cursor = db.query(DBHelper.USERINFO, new String[]{"COUNT(Id)"}, "userId=?", args, null, null, null);

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
     *
     * @param db
     * @param userName 登陆名
     * @param password 登陆密码
     * @param userId 用户id
     * @param name 用户名
     * @throws Exception 抛出的异常
     */
    public void addData(SQLiteDatabase db ,String userName,String password,int userId,String name) throws  Exception{

            /*String sql ="insert into "+dbHelper.USERINFO+" ( userId,userName,password,name"+")" +
                    "values ('"+userId+"','"+userName+"','"+password+"','"+name+"')";*/
         //   db.execSQL(sql);
        ContentValues cv  = new ContentValues();
        cv.put("userId",userId);
        cv.put("userName",userName);
        cv.put("password",password);
        cv.put("name",name);

            db.insert(DBHelper.USERINFO,null,cv);

        }
    //修改原有数据
     public void updateData(SQLiteDatabase db ,String userName,String password,int userId,String name) throws  Exception{
       //  String sql="update "+dbHelper.USERINFO+" set "
         ContentValues cv  = new ContentValues();
         cv.put("userName",userName);
         cv.put("password",password);
         cv.put("name",name);
         String[] args = {String.valueOf(userId)};
        db.update(DBHelper.USERINFO,cv,"userId=?",args);

     }


    /**
     * 用户登陆接口
     */
    public User getUserInfo(SQLiteDatabase db ,String userName,String password){
        User user = null;
        Cursor cursor = null;

        try {
            String[] args = {String.valueOf(userName),String.valueOf(password)};
            // select count(Id) from Orders
            cursor = db.query(DBHelper.USERINFO, new String[]{"userId","userName","name"}, "userName=? and password=?", args, null, null, null);

            if (cursor.moveToFirst()) {
                user = new User();
                user.setUserId(cursor.getInt(cursor.getColumnIndex("userId")));
                user.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
                user.setName(cursor.getString(cursor.getColumnIndex("name")));
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
        return user;
    }



}
