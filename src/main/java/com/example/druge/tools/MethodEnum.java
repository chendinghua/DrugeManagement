package com.example.druge.tools;

/**
 * Created by Administrator on 2019/1/5/005.
 */

public class MethodEnum {
    //登录路径
    public static String LOGINSIGNIN="/login/signin";
    //退出路径
    public static String LOGINSIGNOUT="/login/signout";

    //文件上传
    public static  final String UPLOADFILE="/upload/uploadfile?userID=";
    //修改密码
    public static String RESETPWD="/user/resetpwd";
    //任务列表
    public static final String GETTASKLIST="/task/gettasklist";
    //获取巡检任务列表
    public static final String GETPOLLLIST="/Polling/GetPollList";




    //获取任务详情  (只有已处理和已作废状态显示)
    public  static final  String  GETTASKINFO="/task/GetTaskInfo";

    //获取字典信息
    public static final String GETDICBYGROUPNAME="/common/getdiclistbygroupname";





    //心跳监控接口
    public static final String HEARTBEAT="/common/heartbeat";
    //获取消息通知列表
    public static final String GETNOTIFYLIST="/common/getnotifylist";
    //修改消息通知状态
    public static final String UPDATENOTIFYSTATUS = "/common/UpdateNotifyStatus";




    //异常信息记录
    public static final String ADDERRORINFO="/common/AddErrorInfo";

    //获取资产信息
    public static final String GETASSESTINFOLIST= "/Product/GetAssestInfoList";

    //扫描查询资产信息
    public static final String GETASSESTINFO="/Product/GetAssestInfo";
    //提交新增任务
    public static final String UPDATEADD="/AddAssets/UpdateAdd";


    //提交归还和新增数据
    public static final String GETRETURNAFFIRM="/Return/GetReturnAffirm";

    //获取药箱和药品数据
    public static final String GETQELSTOCKDRUG="/Receive/GetQelStockDrug";


    //获取盘点列表信息
    public static final String GETINVENTORTLIST="/Inventory/GetInventoryList";

    //记录盘点信息
    public static final String ADDINVENTORYINFO ="/Inventory/AddInventoryInfo";

    //调拨任务
    public static final String GETALLOTLIST="/Allot/GetAllotList";

    //调拨入库提交
    public static final String POSTSUMMITIN="/Allot/PostSummitIn";
    //提交借出
    public static final String POSTRECEADD="/Receive/PostReceAdd";
    //提交归还
    public static final String POSTPRODUCTRETUN="/Return/PostProductReturn";
    //根据药品类型查询药品名称
    public static final String GETPRODUCTLIST="/Product/GetProductList";
    //综合查询列表信息
    public static final String GETDRUGEINFO="/Product/GetDrugInfo";
    //提交出库
    public static final String UPDATEOUTSTORAGE="/AddAssets/UpdateOutStorage";
    //药品检查信息提交
    public static final String ADDCHECK="/Product/AddCheck";
    //提交替换数据
    public static final String ADDREPLACE="/Product/AddReplace";
    //同步接取数据
    public  static final String SYNGET="/Task/SynGet";
    //同步提交数据
    public static final String SYNSUBMIT="/Task/SynSubmit";
    //同步放弃数据
    public static final String SYNDEL="/Task/SynDel";

}
