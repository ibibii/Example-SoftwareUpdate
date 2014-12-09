package com.qf.example_softwareupdate.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class DialogUtil {
	/**
	 * 确认对话框
	 * @param context
	 * @param message 消息
	 * @param listener 确认按钮点击事件
	 */
	public static void confirm(Context context, String message, OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("温馨提示");
		builder.setMessage(message);
		builder.setPositiveButton("确认", listener);
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}
}
