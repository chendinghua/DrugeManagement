package com.example.druge.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2019/3/1/001.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "druge.db";
    //新增
    public static final String ADD_INFO_TASK = "addInfoTask";
    //盘点
    public static final String TABLE_INVENTORY="inventory";
    //同步数据详情表
    public static final String TABLE_SYN_TEMP_INFO="SynTempInfoPDA";



    //字典表
    public static final String TABL_DIC_INFO="DicInfo";
    //同步数据表
    public static final String SYNTEMPPDA="SynTempPDA";

    //用户表
    public static final String USERINFO="userInfo";

    //权限表
    public static final String RIGHTINFO="rightInfo";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static DBHelper instance;
    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("DBHelper", "onCreate: ");
        sqLiteDatabase.beginTransaction();
        try {
            //创建新增入库表
            String sql = "create table if not exists " + ADD_INFO_TASK + " (Id integer primary key," +
                    " TaDeId int, " +                  //任务详情id
                    "StorageId int," +               //库存id
                    " RfidNo text," +              //rfid
                    "SerialNo varchar(200)," +              //序列号
                    "AssestId int," +             //资产名称id
                    "AssestName varchar(200)," +              //产品名称
                    "AssestTypeName varchar(200)," +            //资产类型
                    "StorageStatus int," +           //资产状态
                    "StorageStatusName varchar(200)," +         //资产状态名称
                    "StatusName varchar(200))" ;                 //库存状态
            sqLiteDatabase.execSQL(sql);
                //盘点任务表
                String inventorySql = "create table if not exists " + TABLE_INVENTORY + "(" +
             " ID int ,"+                //id
             "ProductID int ,"+           //产品id
             "AssetsTypeID int ,"+        //资产类型id
             "RfidNo text,"+              //RFID
             "SerialNo text,"+            //序列号
             "Remark text,"+              //描述
             "Status int ,"+              //状态
             "TaskType int ,"+            //
             "TaskId int ,"+              //当前任务状态id
             "QcType int ,"+             //QC验证
             "ProductName text,"+         //产品名称
             "AsstypeName text,"+         //资产类型名称
            "stockid int ,"+              //药箱id
             "StockName text,"+              //药箱名称
             "StatusName  text)";          //库存状态名称)
            Log.d("DBHelper", "onCreate: "+inventorySql);
            sqLiteDatabase.execSQL(inventorySql);

            //中间详情表
            String synTempListSql = "create table  "+TABLE_SYN_TEMP_INFO+"\n" +
                    "(\n" +
                    "       Id                integer primary key,\n" +         //编号
                    "       StorageId         INTEGER  ,\n" +           //库存id
                    "       RfidNo            VARCHAR(4000),\n" +               //RFID
                    "       SerialNo          VARCHAR(4000),\n" +               //序列号
                    "       DrugTypeName      VARCHAR(4000),\n" +               //产品类型名称
                    "       DrugTypeId      INTEGER,\n" +                       //产品类型名称id
                    "       ProductId         INTEGER,\n" +                     //产品id
                    "       StockId           INTEGER,\n" +                     //库位id
                    "       StockName      VARCHAR(4000),\n" +                  //库位名
                    "       synId           INTEGER,\n" +                       //同步表id
                    "        productName       VARCHAR(4000)," +                //产品名称
                    "        OpStatus       INTEGER,  " +                       //操作状态
                    "        StatusName      VARCHAR(4000)," +
                    "       StorageStatus    INTEGER"+                     //库存状态
                    ");\n";

            Log.d("createTable", "onCreate: "+synTempListSql);
            sqLiteDatabase.execSQL(synTempListSql);

            String dicInfoSql ="create table  "+TABL_DIC_INFO+"\n" +
                    "(\n" +
                    "       ID                INTEGER  primary key,\n" +
                    "       Name              VARCHAR(4000),\n" +
                    "       Value             VARCHAR(4000),\n" +
                    "       GroupName         VARCHAR(4000),\n" +
                    "       statues           INTEGER\n" +
                    ");";

            Log.d("createTable", "onCreate: "+dicInfoSql);
            sqLiteDatabase.execSQL(dicInfoSql);
            String synTempSql="create table  \n"+SYNTEMPPDA +
                    "(\n" +
                    "       Id                INTEGER  primary key ,\n" +
                    "       TaskType          VARCHAR(4000),\n" +
                    "       TaskTypeId          INTEGER,\n" +
                    "       TaskID            INTEGER not null,\n" +
                    "       OpUser            VARCHAR(4000),\n" +
                    "       OpUserID          INTEGER,\n" +
                    "       OpTime            VARCHAR(4000),\n" +
                    "       remark            VARCHAR(4000),\n" +
                    "       Content           VARCHAR(2000),\n" +
                    "       imageFile         VARCHAR(2000),\n" +
                    "       pickNo             VARCHAR(2000),\n " +
                    "       Status             INTEGER," +
                    "       endTime            VARCHAR(2000)," +
                    "        DataID            INTEGER ," +
                    "        submitUserID       INTEGER," +
                    "        submitUser         VARCHAR(2000)" +
                    ")";
            Log.d("createTable", "onCreate: "+synTempSql);

            sqLiteDatabase.execSQL(synTempSql);
            //用户表
            String userInfoSql = "create table "+ USERINFO+
                    " (\n" +
                    "       id                INTEGER primary key,\n" +
                    "       userId            VARCHAR(4000),\n" +
                    "       userName          VARCHAR(4000),\n" +
                    "       password          VARCHAR(4000),\n" +
                    "       name              VARCHAR(4000)\n" +
                    ")\n";
            sqLiteDatabase.execSQL(userInfoSql);
            //权限表
            String rightInfoSql ="create table "+RIGHTINFO+
                    " (\n" +
                    "       id               INTEGER primary key,\n" +
                    "       Name              VARCHAR(50) not null,\n" +
                    "       RightType         INTEGER not null,\n" +
                    "       Platform          INTEGER not null,\n" +
                    "       SortNo            INTEGER not null,\n" +
                    "       ActionForm        VARCHAR(100),\n" +
                    "       ActionBtn         VARCHAR(100),\n" +
                    "       ActionUrl         VARCHAR(1000),\n" +
                    "       Remark            VARCHAR(300),\n" +
                    "       UserId            INTEGER\n" +
                    ")";
            sqLiteDatabase.execSQL(rightInfoSql);

            sqLiteDatabase.setTransactionSuccessful();
        }catch (SQLException e){

        }finally {
            sqLiteDatabase.endTransaction();
        }
       /* String dicSql = "create table if not exists " + TABLE_DIC_NAME + "(Id integer primary key," +
                "name Text," +
                "value int," +
                "groupName text)";
        sqLiteDatabase.execSQL(dicSql);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.beginTransaction();
        try {
            String sql = "DROP TABLE IF EXISTS " + ADD_INFO_TASK;
            sqLiteDatabase.execSQL(sql);
            String inventorySql = "DROP TABLE IF EXISTS "+TABLE_INVENTORY;
            sqLiteDatabase.execSQL(inventorySql);
            String synTempListSql = "DROP TABLE IF EXISTS "+TABLE_SYN_TEMP_INFO;
            sqLiteDatabase.execSQL(synTempListSql);
            String dicInfoSql = "DROP TABLE IF EXISTS "+TABL_DIC_INFO;
            sqLiteDatabase.execSQL(dicInfoSql);
            String synTempSql ="DROP TABLE IF EXISTS "+SYNTEMPPDA;
            sqLiteDatabase.execSQL(synTempSql);

            String userInfoql ="DROP TABLE IF EXISTS " + USERINFO;
            sqLiteDatabase.execSQL(userInfoql);
            String rightInfoSql ="DROP TABLE IF EXISTS "+ RIGHTINFO;
            sqLiteDatabase.execSQL(rightInfoSql);
            sqLiteDatabase.setTransactionSuccessful();
        }catch (SQLException e){

        }finally {
            sqLiteDatabase.endTransaction();
        }

    }

    public static synchronized boolean isCursorEmptyOrNotPrepared(Cursor cursor) {
        if (cursor == null)
            return true;

        if (cursor.isClosed())
            return true;

        if (cursor.getCount() == 0) // HERE IT CRASHES
            return true;

        return false;
    }
}
