package com.example.druge.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.druge.entry.DicInfo;
import com.example.druge.entry.InventoryData.IntegerValue;
import com.example.druge.entry.InventoryData.SelectInventoryData;
import com.example.druge.entry.InventoryList;
import com.example.druge.entry.RightInfo;
import com.example.druge.entry.SynData.SynTemp;
import com.example.druge.entry.SynTempInfoData.SynTempInfoPDA;
import com.example.druge.entry.TaskListInfo;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.entry.UserInfo.User;
import com.example.druge.entry.UserInfo.UserRightInfo;
import com.example.druge.sqlite.Dao.DicInfoDao;
import com.example.druge.sqlite.Dao.RightInfoDao;
import com.example.druge.sqlite.Dao.SynTempDao;
import com.example.druge.sqlite.Dao.SynTempInfoDao;
import com.example.druge.sqlite.Dao.UserInfoDao;
import com.example.druge.tools.DBUtils;
import com.example.druge.tools.HttpUtils;
import com.example.druge.tools.MethodEnum;
import com.example.druge.tools.UserConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** 同步数据工具表
 * Created by 16486 on 2020/8/24.
 */
public class SynUtils {
    final   String    TAG = "adddatas";

    public boolean addSynTaskInfo(Context context,Object temp){
        boolean isSuccess=true;

        List<TaskListInfo> lists = (List<TaskListInfo>)temp;
        SynTempDao synTempDao = new SynTempDao(context);
        SynTempInfoDao synTempInfoDao = new SynTempInfoDao(context);
        Connection connection = DBUtils.connection(context);

        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();





        SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");
        Date curDate =  new Date(System.currentTimeMillis());
//获取当前时间

        String pickNo ="R"+ formatter.format(curDate)+new Random().nextInt(10);
        try {
            db.beginTransaction();
            connection.setAutoCommit(false);
            //获取当前任务id的集合
            StringBuffer taskIdBuffer = new StringBuffer();
            for (int i = 0;i<lists.size();i++){
                if(lists.size()-1==i){
                    taskIdBuffer.append(lists.get(i).getID());
                }else{
                    taskIdBuffer.append(lists.get(i).getID()+",");
                }
            }
            //获取当前选中任务的详细信息
            String synTempSql="select task.id as id,(select Name from DicInfo where GroupName='taskType' and Value=task.TaskType ) as TaskType,\n" +
                    "                    task.TaskType as taskTypeId,\n" +
                    "                     task.id as TaskID,\n" +
                    "                      task.CreatorName  as OpUser,\n" +
                    "                      task.CreatorID as OpUserID,\n" +
                    "                     convert(varchar,task.CreatorTime,120)    as OpTime,\n" +
                    "                     task.Remark as remark,\n" +
                    "                     task.Content as Content\n" +
                    "                      , '' as imageFile ,\n" +
                    "                     '' as pickNo ,\n" +
                    "                       task.Status as Status,\n" +
                    "                    '' as StatusName, \n" +
                    "                    task.DataID as DataID, \n" +
                    "                     '' as  endtime, " +
                    "                    0 as submitUserID," +
                    "                    '' as  submitUser  \n" +
                    " from TaskInfo as task where ID in ("+taskIdBuffer.toString()+")";

            //根据任务的id集合获取任务详情
            List<SynTemp> synTemps =  DBUtils.executeQryList(connection, SynTemp.class,synTempSql);
            //为列表中添加批次编号
            for (int i =0;i<synTemps.size();i++){
                synTemps.get(i).setPickNo(pickNo);
            }
            //将任务详情信息添加到本地数据库
            synTempDao.foreachAddData(db,synTemps);
         /*   db.setTransactionSuccessful();
            db.beginTransaction();*/
            //根据批次编号获取数据
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("pickNo",pickNo);
            //根据批次编号查询新的数据，主要是获取到新增数据的id
            synTemps = synTempDao.getAllDate(db,map,0,synTemps.size()+1,"and");

            for(int i =0; i <synTemps.size();i++){
                List<SynTempInfoPDA> synTempInfoItems = new ArrayList<>();
                StringBuffer taskDetilSql=new StringBuffer();
                //判断任务类型不是为盘点单的情况下
                if(synTemps.get(i).getTaskTypeId()!=8) {

                    //根据任务id查询任务详情
                    taskDetilSql.append( "\n" +
                            " select detail.StorageId as StorageId,\n" +
                            "    storage.RfidNo as RfidNo,\n" +
                            "\tstorage.SerialNo as SerialNo,\n" +
                            "\tstorage.AssetsTypeID as DrugTypeId,\n" +
                            "    (select name from DicInfo where GroupName = 'DrugType' and Value = storage.AssetsTypeID) as DrugTypeName,\n" +
                            "\tstorage.StockID as StockId,\n" +
                            "\t(select name from StockInfo where id=storage.StockID) as StockName,\n" +
                            "\t(select ProductName from ProductInfo where id = storage.ProductID) as productName,\n" +
                            "\tstorage.ProductID as ProductId \n," +
                            "\t0 as id,\n" +
                            "\t0 as synId,\n" +
                            "\t0 as OpStatus,\n" +
                            "\t (select name from DicInfo where GroupName = 'StorageStatus' and Value=storage.Status) as StatusName," +
                            "  storage.Status as StorageStatus" +
                            "  from taskDetailInfo  as detail\n" +
                            "   inner join StorageInfo as storage on storage.ID = detail.StorageId where detail.TaskId = " + synTemps.get(i).getTaskID());
                    //获取数据的参数

                    //以下是盘点的情况下
                }else{
                    String inventoryIdSql="select inventory.id as id , " +
                            "inventory.DataInfo as DataInfo," +
                            "inventory.Type as type " +
                            "from TaskInfo  task " +
                            "inner join InventoryInfo inventory" +
                            " on task.DataID = inventory.ID " +
                            "and task.TaskType=8  where task.ID="+synTemps.get(i).getTaskID();
                    Log.d("inventoryIdSql", "addSynTaskInfo: "+inventoryIdSql  );

                    //获取盘点的任务id和盘点的类型
                    SelectInventoryData data =  DBUtils.executeQryOne(connection,SelectInventoryData.class,inventoryIdSql);
                    if(data!=null){
                        String [] datas =   data.getDataInfo().split(";");
                        int typeId = datas.length>0 && !"".equals(datas[0])? Integer.parseInt(datas[0]):-1;
                        int assestId = datas.length>1 && !"".equals(datas[1])? Integer.parseInt(datas[1]):-1;

                        taskDetilSql.append("\n" +
                                "select\n" +
                                "t.ID as StorageId, t.RfidNo as RfidNo, t.SerialNo as SerialNo,\n" +
                                "(select Name from DicInfo where GroupName = 'DrugType' and value=t.AssetsTypeID) as DrugTypeName,\n" +
                                "t.AssetsTypeID as DrugTypeId,t.StockID as StockId,\n" +
                                "(select name from StockInfo where id=t.StockID) as StockName,\n" +
                                "\t(select ProductName from ProductInfo where id = t.ProductID) as productName,\n" +
                                "\tt.ProductID as ProductId,\n" +
                                "\t0 as id,\n" +
                                "\t0 as synId, \n" +
                                "\t 0 as OpStatus,\n" +
                                "\t (select name from DicInfo where GroupName = 'StorageStatus' and Value=t.Status) as StatusName, t.Status as StorageStatus  from storageinfo t where 1=1 ");


                        switch (data.getType()){
                            //全盘
                            case 1:
                                taskDetilSql.append(" and  t.Status!=9");
                                break;
                            //类型盘点
                            case 2:
                                taskDetilSql.append(" and  t.AssetsTypeID="+typeId);
                                if (assestId != -1)
                                    taskDetilSql.append( " and productid="+ assestId);
                                break;
                            //根据药箱盘点
                            case 3:
                                taskDetilSql.append(" and  status=1 and StockID='"+data.getDataInfo()+"'");
                                break;
                            //在库盘点 在库物品盘点(合格商品在库)
                            case 4:
                                taskDetilSql.append(" and  status=1");
                                break;




                        }

                    }


                    //判断如何是盘点数据则需要在中间详情表添加数据,并且查询的数据必须大于0
               /*     if(synTemps.get(i).getTaskTypeId()!=8 && synTempInfoItems.size()>0) {
                        StringBuffer addSynListSql = new StringBuffer("insert into " +
                                "SynTempInfo(SynId,StorageId,RfidNo,SerialNo,DrugTypeName,ProductId,StockId,OpStatus) values");
                        for (int j=0;j<synTempInfoItems.size();j++){
                            SynTempInfoPDA tempData =  synTempInfoItems.get(j);

                            addSynListSql.append("('"+tempData.getSynId()+"'," +
                                    "'"+tempData.getStorageId()+"'," +
                                    "'"+tempData.getRfidNo()+"'," +
                                    "'"+tempData.getSerialNo()+"'," +
                                    "'"+tempData.getDrugTypeName()+"'," +
                                    "'"+tempData.getProductId()+"'," +
                                    "'"+tempData.getStockId()+"'," +
                                    "2)");

                            if(synTempInfoItems.size()-1!=j){
                                addSynListSql.append(",");
                            }
                        }
                        Log.d(TAG, "addSynTaskInfo: " + addSynListSql.toString());
                        //添加盘点详情数据
                        isSuccess = DBUtils.executeUpdate(connection,addSynListSql.toString(),null)>0 && isSuccess?true:false  ;
                    }*/
                }
                Log.d(TAG, "taskDetilSql : "+ taskDetilSql.toString());
                synTempInfoItems.addAll( DBUtils.executeQryList(connection, SynTempInfoPDA.class, taskDetilSql.toString()));
                //为当前获取的数据临时详情表的的synId赋值
                for (int j =0;j<synTempInfoItems.size();j++){
                    synTempInfoItems.get(j).setSynId(synTemps.get(i).getId());
                }
                //为临时中间详情表添加数据
                synTempInfoDao.foreachAddData(db,synTempInfoItems);

            }
            StringBuffer addSynTempTableSql = new StringBuffer("insert into SynTemp\n" +
                    "(  TaskType,TaskID,Status,OpUser,OpUserID,ReceiveTime,Mac,IsSyn,remark,ReceiveSynNo,SynTempId,DataID) values\n");
            for (int i =0;i<synTemps.size();i++){
                SynTemp synTemp =  synTemps.get(i);

                addSynTempTableSql.append("('"
                        +synTemp.getTaskTypeId()+"'," +
                        "'" +synTemp.getTaskID()+"'," +
                        "1," +
                        "'" + UserConfig.UserName+"'," +
                        "'" +UserConfig.UserId+"'," +
                        "'" + new   SimpleDateFormat   ("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))+"'," +
                        "'" + HttpUtils.getIMEI(context)+"'," +                //MAC地址
                        "1," +
                        "''," +
                        "'"+pickNo+"'," +
                        "'"+synTemp.getId()+"','"+synTemp.getDataID()+"')");
                if(synTemps.size()-1!=i){
                    addSynTempTableSql.append(",");
                }
            }
            Log.d("adddatas", "addSynTaskInfo: "+addSynTempTableSql.toString());
            //往临时表中添加数据
            isSuccess =  DBUtils.executeUpdate(connection,addSynTempTableSql.toString())>0 && isSuccess? true:false;


            if(!isSuccess){
                connection.rollback();

            }else {
                connection.commit();
                db.setTransactionSuccessful();
                HttpUtils httpUtils = new HttpUtils();
                HashMap<String,Object> synMap  = new HashMap<>();
                synMap.put("OpUserid",UserConfig.UserId);
                synMap.put("OpUser",UserConfig.UserName);
                synMap.put("SynNo",pickNo);
                //提交同步数据
                String  result  =  httpUtils.sendPostMessage(context,synMap, MethodEnum.SYNGET);
                Log.d(TAG, "resultData"+ result);
                isSuccess = JSON.parseObject(result).getBoolean("Success")&& isSuccess?true:false;
            }



        /*    if(!isSuccess){
                connection.rollback();
            }else {
                connection.commit();
                db.setTransactionSuccessful();
            }*/
        } catch (SQLException e) {
            try {
                connection.rollback();
                isSuccess=false;
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (Exception e) {
            try {
                isSuccess=false;
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DBUtils.closeConnect(connection);
            if(db!=null){
                db.endTransaction();
                db.close();
            }


        }


        return isSuccess;
    }


    public boolean saveLoaclUserInfo(Context context,String userName,String password,String name,int userID, List<RightInfo> rightList){
        boolean isSvae=true;

        UserInfoDao userInfoDao = new UserInfoDao(context);
        RightInfoDao rightInfoDao = new RightInfoDao(context);
        DicInfoDao dicInfoDao =  new DicInfoDao(context);

        Connection connection  = DBUtils.connection(context);



        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();

        db.beginTransaction();
        try {
            connection.setAutoCommit(false);
            //判断的当前的用户id是否存在
            if( userInfoDao.isUserInfoExist(db,userID)){
                //更新当前的用户信息
                userInfoDao.updateData(db,userName,password,userID,name);
                //删除当前的用户权限
                rightInfoDao.deleteRightInfo(db,userID);
                //添加最新的权限
                rightInfoDao.addForcahData(db,userID,rightList);
            }else{
                //添加用户信息
                userInfoDao.addData(db,userName,password,userID,name);
                //添加最新的权限
                rightInfoDao.addForcahData(db,userID,rightList);
            }
            String  dicInfoSql = "\t\t select ID,Name,Value,GroupName,statues as State from DicInfo where GroupName = 'TaskStatus' and platform=2 or GroupName = 'TaskStatus' and platform=3 ";
            List<DicInfo> dicInfos= DBUtils.executeQryList(connection, DicInfo.class,dicInfoSql);
            if(dicInfoDao.isDataExist(db)){
                dicInfoDao.deleteDicInfoAll(db);
            }
            dicInfoDao.foreachAddData(db,dicInfos);
            connection.commit();
            db.setTransactionSuccessful();
        }catch (Exception e){
            isSvae=false;
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }finally {
            DBUtils.closeConnect(connection);
            if(db!=null){
                db.endTransaction();
                db.close();
            }
        }
        return  isSvae;
    }
    //单机登陆
    public UserRightInfo offlineLoginUser(     SQLiteDatabase db,  Context context,String userName,String password)throws  Exception{
        UserRightInfo userRightInfo=null;
        UserInfoDao userInfoDao = new UserInfoDao(context);
        RightInfoDao rightInfoDao = new RightInfoDao(context);
        User user = null;
        if ((user = userInfoDao.getUserInfo(db, userName, password)) != null) {
            userRightInfo = new UserRightInfo();
            userRightInfo.setUser(user);
            userRightInfo.setRightInfos(rightInfoDao.getRightInfo(db, user.getUserId()));
        }


        return userRightInfo;

    }
//同步提交数据
    public boolean synCommitData( Context context,Object temp ){

        List<SynTemp> synTemps = (  List<SynTemp>) temp;
        SQLiteDatabase db =    DBHelper.getInstance(context).getReadableDatabase();
        Connection connection = DBUtils.connection(context);

        SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");
        Date curDate =  new Date(System.currentTimeMillis());
//获取当前时间

        String pickNo ="S"+ formatter.format(curDate)+new Random().nextInt(10);

        SynTempInfoDao synTempInfoDao = new SynTempInfoDao(context);
        SynTempDao synTempDao = new SynTempDao(context);

        boolean isSuccess = true;

        try {
            if(synTemps!=null){
                db.beginTransaction();
                connection.setAutoCommit(false);
                for (SynTemp synTemp :synTemps){
                    //循环的同时修改远程数据库SynTemp里面的数据包含   (optTime   isSyn   remark  SynNo  status)
                    String sql ="update SynTemp set  opTime=? ,  isSyn=? ,  remark=? ,  SubmitSynNo=?,  status=? ,OpUserID=?, OpUser=?  where SynTempid=? and ReceiveSynNo=?";

                    DBUtils.executeUpdate(connection,sql,synTemp.getEndtime(),1,
                            synTemp.getRemark(),pickNo,synTemp.getStatus(),synTemp.getSubmitUserID(),synTemp.getSubmitUser(),synTemp.getId(),synTemp.getPickNo());
                    String qryOneSynTempIdSql = "select ID from SynTemp where SynTempid='"+synTemp.getId()+"' and ReceiveSynNo='"+synTemp.getPickNo()+"'";
                    Log.d("qryOneSynTempIdSql", "synCommitData: "+qryOneSynTempIdSql);
                    int synTempId =   DBUtils.qryCount(connection,qryOneSynTempIdSql);
                    StringBuffer addSynListSql = new StringBuffer("insert into " +
                            "SynTempInfo(SynId," +
                            "StorageId," +
                            "RfidNo," +
                            "SerialNo," +
                            "DrugTypeName," +
                            "ProductId," +
                            "StockId,OpStatus,StorageStatus) values");
                    int dataLenght=0;
                    switch (synTemp.getTaskTypeId()){
                        case 8:
                            //根据本地选中的集合循环查询详情
                            List<InventoryList> inventoryLists =  synTempInfoDao.getInentoryDataList(db,synTemp.getId());
                            dataLenght=inventoryLists.size();
                            if(inventoryLists.size()>0){

                                for (int j=0;j<inventoryLists.size();j++){
                                    InventoryList tempData =  inventoryLists.get(j);

                                    addSynListSql.append("('"+synTempId+"'," +
                                            "'"+tempData.getStorageId()+"'," +
                                            "'"+tempData.getRfidNo()+"'," +
                                            "'"+tempData.getSerialNo()+"'," +
                                            "'"+tempData.getAsstypeName()+"'," +
                                            "'"+tempData.getProductID()+"'," +
                                            "'"+tempData.getStockid()+"'," +
                                            "'"+tempData.getOpStatus()+"','"+tempData.getStorageStatus()+"')");

                                    if(inventoryLists.size()-1!=j){
                                        addSynListSql.append(",");
                                    }
                                }
                                Log.d(TAG, "addSynTaskInfo: " + addSynListSql.toString());
                                //添加盘点详情数据

                            }

                            break;
                        //新增
                        case 1:
                            //调拨入库
                        case 2:
                            //出库
                        case 3:
                        case 5:
                        case 6:
                            List<TaskListInfoItem> taskListInfoItems =  synTempInfoDao.getTaskDataList(db,synTemp.getId(),new Integer[]{1,2,6,3,5});
                            dataLenght= taskListInfoItems.size();
                            if(taskListInfoItems.size()>0){

                                for (int j=0;j<taskListInfoItems.size();j++){
                                    TaskListInfoItem tempData =  taskListInfoItems.get(j);

                                    addSynListSql.append("('"+synTempId+"'," +
                                            "'"+tempData.getStorageId()+"'," +
                                            "'"+tempData.getRfidNo()+"'," +
                                            "'"+tempData.getSerialNo()+"'," +
                                            "'"+tempData.getAssestTypeName()+"'," +
                                            "'"+tempData.getAssestId()+"'," +
                                            "'"+tempData.getStockid()+"'," +
                                            "'"+tempData.getStatus()+"','"+tempData.getStorageStatus()+"')");

                                    if(taskListInfoItems.size()-1!=j){
                                        addSynListSql.append(",");
                                    }
                                }
                                Log.d(TAG, "addSynTaskInfo: " + addSynListSql.toString());
                                //添加盘点详情数据

                            }

                            break;



                    }
                    //判断详情必须要有数据
                    if(dataLenght>0){
                        isSuccess = DBUtils.executeUpdate(connection,addSynListSql.toString())>0 && isSuccess?true:false  ;
                    }


                }
                connection.commit();
                if(isSuccess) {
                    HttpUtils httpUtils = new HttpUtils();
                    HashMap<String, Object> synMap = new HashMap<>();
                    synMap.put("OpUserid", UserConfig.UserId);
                    synMap.put("OpUser", UserConfig.UserName);
                    synMap.put("SynNo", pickNo);
                    //提交同步数据
                    String result = httpUtils.sendPostMessage(context, synMap, MethodEnum.SYNSUBMIT);
                    Log.d(TAG, "resultData" + result);
                    isSuccess = JSON.parseObject(result).getBoolean("Success") && isSuccess ? true : false;
                    if(isSuccess){
                        for (SynTemp synTemp :synTemps){

                            isSuccess=synTempDao.deleteData(db,synTemp.getId()) && isSuccess ? true : false;
                            if(synTempInfoDao.isDataExist(db,synTemp.getId()))
                                isSuccess= synTempInfoDao.deleteData(db,synTemp.getId())&& isSuccess ? true : false;
                        }
                        if(isSuccess){
                            db.setTransactionSuccessful();

                        }
                    }
                }
            }
        }catch (SQLException e){
            isSuccess=false;
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            isSuccess=false;
            e.printStackTrace();
        } finally {
            DBUtils.closeConnect(connection);
            if(db!=null){
                db.endTransaction();
                db.close();
            }
        }



        return isSuccess;
    }

    //提交放弃任务
    public boolean synDelData( Context context,Object temp ){
        List<SynTemp> synTemps = (  List<SynTemp>) temp;
        SQLiteDatabase db =    DBHelper.getInstance(context).getReadableDatabase();
        Connection connection = DBUtils.connection(context);

        SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");
        Date curDate =  new Date(System.currentTimeMillis());
//获取当前时间

        String pickNo ="S"+ formatter.format(curDate)+new Random().nextInt(10);

        SynTempInfoDao synTempInfoDao = new SynTempInfoDao(context);
        SynTempDao synTempDao = new SynTempDao(context);

        boolean isSuccess = true;

        try {
            if(synTemps!=null){
                db.beginTransaction();
                connection.setAutoCommit(false);
                for (SynTemp synTemp :synTemps){
                    //循环的同时修改远程数据库SynTemp里面的数据包含   (optTime   isSyn   remark  SynNo  status)
                    String sql ="update SynTemp set  opTime=? ,  isSyn=? ,  remark=? ,  SubmitSynNo=?,  status=? ,OpUserID=?, OpUser=?  where SynTempid=? and ReceiveSynNo=?";

                    DBUtils.executeUpdate(connection,sql,synTemp.getEndtime(),1,
                            synTemp.getRemark(),pickNo,3,synTemp.getSubmitUserID(),synTemp.getSubmitUser(),synTemp.getId(),synTemp.getPickNo());
                }
                connection.commit();
                if(isSuccess) {
                    HttpUtils httpUtils = new HttpUtils();
                    HashMap<String, Object> synMap = new HashMap<>();
                    synMap.put("OpUserid", UserConfig.UserId);
                    synMap.put("OpUser", UserConfig.UserName);
                    synMap.put("SynNo", pickNo);
                    //提交同步数据
                    String result = httpUtils.sendPostMessage(context, synMap, MethodEnum.SYNDEL);
                    Log.d(TAG, "resultData" + result);
                    isSuccess = JSON.parseObject(result).getBoolean("Success") && isSuccess ? true : false;

                    if(isSuccess){
                        for (SynTemp synTemp :synTemps){

                            isSuccess=synTempDao.deleteData(db,synTemp.getId()) && isSuccess ? true : false;
                            if(synTempInfoDao.isDataExist(db,synTemp.getId()))
                            isSuccess= synTempInfoDao.deleteData(db,synTemp.getId())&& isSuccess ? true : false;
                        }
                        if(isSuccess){
                            db.setTransactionSuccessful();

                        }
                    }
                }
            }
        }catch (SQLException e){
            isSuccess=false;
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            isSuccess=false;
            e.printStackTrace();
        } finally {
            DBUtils.closeConnect(connection);
            if(db!=null){
                db.endTransaction();
                db.close();
            }
        }

        return isSuccess;
    }


}
