package com.qf.example_softwareupdate;

import com.qf.example_softwareupdate.entity.UpdateTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
/**
 * ����:û�е�������.������Ȩ������.
 * @author uaige
 *
 * 2014��12��9������8:06:32
 */
public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	public void btnClick(View view){
		Toast.makeText(MainActivity.this, "����", Toast.LENGTH_SHORT).show();
		UpdateTask task = new UpdateTask(MainActivity.this);
		task.execute();
	}
}
