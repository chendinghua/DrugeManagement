package com.example.druge.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * Created by Administrator on 2019/2/26/026.
 */

public class DialogUtils {

    public static AlertDialog.Builder showAlertDialog(Context context, final AlertDialogCallBack callBack, String message, DialogInterface.OnKeyListener keyListener, View view ) {
        final AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle("系统提示").setIcon(android.R.drawable.ic_dialog_info).setView(view);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callBack.alertDialogFunction();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if(keyListener!=null){
            builder.setOnKeyListener(keyListener);
        }
        builder.setCancelable(false);
        builder.show();
        return builder;
    }


    public static AlertDialog.Builder showAlertDialog(Context context, final AlertDialogCallBack callBack,final AlertDialogNegativeCallBack negative, String message, DialogInterface.OnKeyListener keyListener, View view ) {
        final AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle("系统提示").setIcon(android.R.drawable.ic_dialog_info).setView(view);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callBack.alertDialogFunction();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(negative!=null)
                negative.alertDialogFunction();
                dialog.dismiss();
            }
        });
        if(keyListener!=null){
            builder.setOnKeyListener(keyListener);
        }

        builder.show();
        return builder;
    }


    public static AlertDialog.Builder showAlertDialog(Context context, final AlertDialogCallBack callBack,final AlertDialogNegativeCallBack negative, String message, DialogInterface.OnKeyListener keyListener, View view ,boolean isOk,boolean isCancel) {
        final AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle("系统提示").setIcon(android.R.drawable.ic_dialog_info).setView(view);
        builder.setMessage(message);
     //   builder.setCancelable(false); //不响应back按钮
        if(isOk) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(callBack!=null)
                    callBack.alertDialogFunction();
                }
            });
        }
        if(isCancel) {
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(negative!=null)
                    negative.alertDialogFunction();
                    // dialog.dismiss();
                }
            });
        }
        if(keyListener!=null){
            builder.setOnKeyListener(keyListener);
        }

        builder.create().show();

        return builder;
    }
}
