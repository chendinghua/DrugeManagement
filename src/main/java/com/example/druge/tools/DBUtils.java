

/**
 * Created by 16486 on 2020/8/19.
 */
/*

public class DBUtils {
    private String drive = "net.sourceforge.jtds.jdbc.Driver";
    // SQL连接字符串，格式是 jdbc:jtds:sqlserver://服务器IP:端口号/数据库名称
    // 端口号默认为1433，如果不是，可以打开SQL Server配置管理器设定，
    // 如果你的SQL Server不是默认实例，需添加连接字符串为
    // jdbc:jtds:sqlserver://服务器IP:端口号/数据库名称;instance=实例名，不过博主没有试验过，你可以百度一下。
    private static String connStr = "jdbc:jtds:sqlserver://192.168.1.200:1433/HSJ.Drug";
    // 用户名和密码，则是对应的数据库的帐号，博主使用sa进行说明，如果你用的不是sa，记得在数据库表中打开你的帐号的权限。
    private static  String uid = "sa";
    private static String pwd = "123";
    // 连接对象，相当于C#中的SqlConnection
    private static Connection con = null;

    static {
        try {
            con = DriverManager.getConnection(connStr, uid, pwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
*/
package com.example.druge.tools;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * 数据库工具类
 * 说明：不支持事务。。。。。。。。
 * @author zhang
 * @version 0.3
 * @createTime 2020-07-24
 * 2020-7-30 第一次修改
 * 2020-8-01 第二次修改
 */
public class DBUtils {
    private static String drive = "net.sourceforge.jtds.jdbc.Driver";
    // SQL连接字符串，格式是 jdbc:jtds:sqlserver://服务器IP:端口号/数据库名称
    // 端口号默认为1433，如果不是，可以打开SQL Server配置管理器设定，
    // 如果你的SQL Server不是默认实例，需添加连接字符串为
    // jdbc:jtds:sqlserver://服务器IP:端口号/数据库名称;instance=实例名，不过博主没有试验过，你可以百度一下。
    private static String url = "jdbc:jtds:sqlserver://";
    private static String dataBaseName =":1433/HSJ.Drug";
    // 用户名和密码，则是对应的数据库的帐号，博主使用sa进行说明，如果你用的不是sa，记得在数据库表中打开你的帐号的权限。
    private static  String user = "sa";
    private static String password = "a123456@";




    static Connection conn;

    static PreparedStatement ps;

    static ResultSet rs;
    static {
        //加载驱动。。。。把Driver加载进内存，，。。。
        try {
            Class.forName(drive);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 获取连接
     */
    public static Connection connection (Context context)
    {
        try {
         SharedPreferences sp = context.getSharedPreferences("setting_action_url_config", Context.MODE_PRIVATE);
 //        String actionUrl = sp.getString("actionUrl","http://192.168.1.95:9006/api");
   //         String ip =  actionUrl.substring(actionUrl.indexOf("//")+2,actionUrl.lastIndexOf(":"));
            String ip="192.168.1.200";
            String newUrl = url+ip+dataBaseName;
            Log.d("newUrl", "connection: "+newUrl);
            conn = DriverManager.getConnection(newUrl, user, password);

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭连接
     */
    private static void close ()
    {

        if (rs != null)
        {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps != null)
        {
            try {
                ps.close();
            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
      /*  if (conn != null)
        {
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/
    }
    public static void closeConnect(Connection connection){

        if (connection != null)
        {
            try {
                connection.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }



    /**
     * 增、删、改操作
     * @param sql sql
     * @param params sql中的参数
     * @return 受影响行数
     */
    public static int executeUpdate (Connection connection, String sql, Object... params)
    {

        try {
         /*   //获取连接。。。。
            connection();*/
            //获取句柄
            ps = connection.prepareStatement(sql);

            //绑定参数
            if (params != null)
            {
                for (int i=0; i<params.length; i++)
                {
                    ps.setObject(i+1, params[i]);
                }
            }
            //执行sql
            return ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            //关闭连接。。。。
            close();
        }
        return 0;
    }


    /**
     * 添加数据并返回自增的主键
     * @param sql
     * @param params
     * @return 自增的id
     */
    public static int addAndReturnKey(Connection connection, String sql, Object... params){

        try {
        /*    //获取连接。。。。
            connection();*/
            //获取句柄
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //绑定参数
            if (params != null)
            {
                for (int i=0; i<params.length; i++)
                {
                    ps.setObject(i+1, params[i]);
                }
            }
            //执行sql
            ps.executeUpdate();
            //获取插入后的自增主键
            ResultSet res= ps.getGeneratedKeys();
            if(res.next()){
                return res.getInt(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            //关闭连接。。。。
            close();
        }
        return 0;
    }



    /**
     * 查询单行内容
     * @param <T> 封装的返回值对象类型
     * @param clazz 返回值class对象
     * @param sql sql语句
     * @param params sql参数
     * @return 查询结果
     */
    public static <T> T executeQryOne (Connection connection, Class<T> clazz, String sql, Object... params) throws InvocationTargetException, SQLException, InstantiationException, NoSuchMethodException, IllegalAccessException {

        List<T> arr = executeQryList(connection,clazz, sql, params);

        return arr.size()>0 ? arr.get(0) : null;
    }

    /**
     * 查询多行内容
     * @param <T> 封装的返回值对象类型
     * @param clazz 返回值class对象
     * @param sql sql语句
     * @param params sql参数
     * @return 查询结果List
     */
    public static <T> List<T> executeQryList (Connection connection,Class<T> clazz, String sql, Object... params) throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        List<T> arr = new ArrayList<T>();

     /*   //获取连接。。。。。
        connection();*/
        //获取句柄
            ps = connection.prepareStatement(sql);
            //绑定参数
            if (params != null)
            {
                for (int i=0; i<params.length; i++)
                {
                    ps.setObject(i, params[i]);
                }
            }
            //查询
            rs = ps.executeQuery();

            while ( rs.next() )
            {
                //获取到类的属性（对应数据库表中的列名）
                Field[] fs = clazz.getDeclaredFields();
                T t = clazz.newInstance();
                //将数据封装到 对象中
                for (Field f : fs)
                {
                    //类的属性名
                    String colName = f.getName();



                    //2020-08-01  查询列是否存在 - 张 - begin

                    if (!hasCol(colName,rs))
                    {
                        continue;
                    }
                    //2020-08-01  查询列是否存在 -张- end






                    Object colVal = rs.getObject(colName);

                    String setMethodName = "set"
                            + colName.substring(0, 1).toUpperCase()
                            + colName.substring(1);
                    //获取改属性的set方法
                    Method m = clazz.getDeclaredMethod(setMethodName, f.getType());
                    //执行set方法
                    m.invoke(t, colVal);
                }
                arr.add(t);
            }



        return arr;
    }



    public static int qryCount(Connection connection,String sql,Object...params){
     //   connection();
        //获取句柄
        try {
            ps = connection.prepareStatement(sql);
            //绑定参数
            if (params != null)
            {
                for (int i=0; i<params.length; i++)
                {
                    ps.setObject(i+1, params[i]);
                }
            }
            //查询
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            //关闭连接。。。。
            close();
        }
        return 0;
    }


    /**
     * 2020-08-01
     * 数据库中判断是否有该列
     * @param colName 列的名称
     * @param rs 数据库列名的集合
     * @return true 有
     * 			false 没有
     */
    private static boolean hasCol(String colName,ResultSet rs)
    {
        try {
            rs.findColumn(colName);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }
}