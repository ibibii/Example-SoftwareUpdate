package com.qf.example_softwareupdate.entity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;

import com.qf.example_softwareupdate.utils.APIUtils;
import com.qf.example_softwareupdate.utils.DialogUtil;


public class UpdateTask extends AsyncTask<Void, Void, Update> {

	private static final String FILE_PATH = "//data//data//com.qf.example_softwareupdate//download//";
	private static final String APK_NAME = "latest.apk";
	private static final String TAG = "UpdateTask";

	private Context context;

	public UpdateTask(Context context) {
		this.context = context;
	}

	@Override
	protected Update doInBackground(Void... params) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			UpdateHandler handler = new UpdateHandler();
			parser.parse(APIUtils.URL, handler);
			Update update = handler.getUpdate();
			return update;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(final Update result) {
		if (result != null) {
			try {
				int version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
				if (result.getVersion() > version) {
					String title = String.format("%s%s", result.getTitle(), "\r\n");
					String desc = result.getDesc().replace(";", "\r\n");

					DialogUtil.confirm(context, String.format("%s%s", title, desc), new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							MyAsyncTask task = new MyAsyncTask();
							task.execute(result.getUrl());
						}
					});
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	class MyAsyncTask extends AsyncTask<String, Integer, File> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(context);
			progressDialog.setTitle("正在下载...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMax(100);
			progressDialog.show();
		}

		@Override
		protected File doInBackground(String... params) {
			File downloadFile = null;
			InputStream is = null;
			FileOutputStream fos = null;

			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpParams httpParams = httpClient.getParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
				HttpGet httpGet = new HttpGet(params[0]);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity httpEntity = httpResponse.getEntity();
					if (httpEntity != null) {
						long length = httpEntity.getContentLength();
						is = httpEntity.getContent();

						File filePath = new File(FILE_PATH);
						if (!filePath.exists()) {
							filePath.mkdir();
						}

						downloadFile = new File(FILE_PATH + APK_NAME);
						fos = new FileOutputStream(downloadFile);

						byte[] buf = new byte[1024];
						int len = 0, count = 0, result = 0;
						while ((len = is.read(buf)) != -1) {
							fos.write(buf, 0, len);
							count += len;
							result = (int) ((count * 100) / length);
							publishProgress(result);
							fos.flush();
						}
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}

					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return downloadFile;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			progressDialog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(File result) {
			progressDialog.dismiss();

			if (result != null) {
				// 设置文件夹的权限
				String[] args1 = { "chmod", "705", FILE_PATH };
				exec(args1);
				// 设置文件的权限
				String[] args2 = { "chmod", "604", FILE_PATH + APK_NAME };
				exec(args2);
				
				// 调用安装文件
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + FILE_PATH + APK_NAME), "application/vnd.android.package-archive");
				context.startActivity(intent);
			}
		}

		// 设置权限的方法
		public String exec(String[] args) {
			String result = "";
			ProcessBuilder processBuilder = new ProcessBuilder(args);
			Process process = null;
			InputStream errIs = null;
			InputStream inIs = null;
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int read = -1;
				process = processBuilder.start();
				errIs = process.getErrorStream();
				while ((read = errIs.read()) != -1) {
					baos.write(read);
				}
				baos.write('\n');
				inIs = process.getInputStream();
				while ((read = inIs.read()) != -1) {
					baos.write(read);
				}
				byte[] data = baos.toByteArray();
				result = new String(data);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (errIs != null) {
						errIs.close();
					}
					if (inIs != null) {
						inIs.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (process != null) {
					process.destroy();
				}
			}
			return result;
		}

	}

}

