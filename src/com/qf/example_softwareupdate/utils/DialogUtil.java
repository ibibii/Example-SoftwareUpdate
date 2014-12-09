package com.qf.example_softwareupdate.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class DialogUtil {
	/**
	 * ȷ�϶Ի���
	 * @param context
	 * @param message ��Ϣ
	 * @param listener ȷ�ϰ�ť����¼�
	 */
	public static void confirm(Context context, String message, OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("��ܰ��ʾ");
		builder.setMessage(message);
		builder.setPositiveButton("ȷ��", listener);
		builder.setNegativeButton("ȡ��", null);
		builder.create().show();
	}
}
