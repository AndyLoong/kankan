package com.android.music.cardview;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class CardViewUtils {

	public static final String TAG = "CardViewUtils";

	public static ArrayList<CardViewItem> getCardViewInfo(Context context)
			throws Throwable {

		String version = getVerCode(context);
		String head = getTestOrProductAps();
		String deviceType = getPhoneVersion();
		StringBuilder sb = new StringBuilder();
		sb.append(head).append("/music/api/minusScreenGet.do?v=")
				.append(version).append("&m=").append(deviceType).append("&t=")
				.append(1);

		String jsonString = getJSONStringByHttpURLConnection(sb.toString());
		// String string2 = readParse(sb.toString());
		Log.d("liuj", "string ==  = " + jsonString);
		// Log.d("liuj", "string2 === "+string2);
		return parseJson(jsonString);
	}

	private static ArrayList<CardViewItem> parseJson(String jsonString) {
		// TODO Auto-generated method stub
		ArrayList<CardViewItem> cardviewlist = new ArrayList<CardViewItem>();
		if (jsonString == null) {
			return cardviewlist;
		}
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray("l");
			int jsonArrayLength = jsonArray.length();
			for (int index = 0; index < jsonArrayLength; index++) {
				CardViewItem cardViewItem = new CardViewItem();
				JSONObject object = jsonArray.getJSONObject(index);
				if (object.has("i")) {
					cardViewItem.setVideoId(object.getString("i"));
				}
				if (object.has("t")) {
					cardViewItem.setTitle(object.getString("t"));
				}
				if (object.has("st")) {
					cardViewItem.setSonTitle(object.getString("st"));
				}
				if (object.has("lg")) {
					cardViewItem.setPicUrl(object.getString("lg"));
				}
				cardviewlist.add(cardViewItem);
			}

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cardviewlist;

	}

	public static String getVerCode(Context context) throws Exception {
		String versionName = context.getPackageManager().getPackageInfo(
				"com.android.music", 0).versionName;
		return versionName;
	}

	public static String getPhoneVersion() {
		String dv = Build.MODEL;
		if (dv == null || dv.length() == 0)
			return "NULL";
		else
			return dv;
	}

	public static String getTestOrProductAps() {
		boolean isTureEnvironment = readCommunicationEnvironment();
		String apsServer = null;
		if (isTureEnvironment) {
			apsServer = "http://music.gionee.com";
		} else {
			apsServer = "http://t-music.gionee.com";
		}
		return apsServer;
	}

	public static boolean readCommunicationEnvironment() {
		boolean isRealEnv = true;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory(),
					"gnmusic1234567890");
			if (file.exists()) {
				isRealEnv = false;
			} else {
				isRealEnv = true;
			}
		}
		return isRealEnv;
	}

	/**
	 * 从指定的URL中获取数组
	 * 
	 * @param urlPath
	 * @return
	 * @throws Exception
	 */
	public static String readParse(String urlPath) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		URL url = new URL(urlPath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		InputStream inStream = conn.getInputStream();
		while ((len = inStream.read(data)) != -1) {
			outStream.write(data, 0, len);
		}
		inStream.close();
		return new String(outStream.toByteArray());// 通过out.Stream.toByteArray获取到写的数据
	}

	/**
	 * 访问数据库并返回JSON数据字符串
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String getJSONStringByHttpURLConnection(String url)
			throws Exception {
		String result = null;
		// 获取HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		// 新建HttpPost对象
		HttpGet httpGet = new HttpGet(url);
		// 连接超时
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
		// 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				3000);
		// 获取HttpResponse实例
		HttpResponse httpResp = httpClient.execute(httpGet);
		// 判断是够请求成功
		if (httpResp.getStatusLine().getStatusCode() == 200) {
			// 获取返回的数据
			result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
		} else {
			Log.i(TAG, "resultCode" + httpResp.getStatusLine().getStatusCode());
		}

		return result;
	}

	public static boolean downloadSingleFile(String uriString, String fileName) {
		Log.d(TAG, "downloadSingleFile fileName=" + fileName);
		String tmp = fileName + ".tmp";
		if (uriString == null) {
			return false;
		}
		boolean res = false;
		File file = new File(FileUtil.getDirectory(tmp));
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.d(TAG, "downloadSingleFile");
			}
		}
		InputStream inputStream = getInputStreamByUrl(uriString);
		if (null != inputStream) {
			Log.d(TAG, "downloadSingleFile inputStream !=null");
			if (uriString.contains("open.migu.cn")) {
				readStreamToFile(inputStream, tmp, true);
			} else {
				readStreamToFile(inputStream, tmp, false);
			}
			res = true;
			FileUtil.renameFile(tmp, fileName);
		}
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (Exception e) {
			Log.d(TAG, "downloadSingleFile e ==" + e.toString());
		}
		Log.d(TAG, "downloadSingleFile res=" + res);
		return res;
	}
	
    public static InputStream getInputStreamByUrl(String UriString){
        if(UriString == null){
            return null;
        }
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        int responseCode = 0;
        try {
            connection = (HttpURLConnection) new URL(UriString.trim()).openConnection();
            if(connection == null) {
                return null;
            }
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(90000);
            connection.setRequestMethod("GET");
            responseCode = connection.getResponseCode();
            Log.e(TAG,"responseCode =="+responseCode);
            if ((responseCode == HttpStatus.SC_OK)
                    || (responseCode == HttpStatus.SC_MOVED_TEMPORARILY)) {

                inputStream = connection.getInputStream();
            }
        } catch (Exception e) {
            Log.e(TAG,"downloadSingleFile failed");
            e.printStackTrace();
        }
        return inputStream;
    }
	 private static String readStreamToFile(InputStream ins, String filePath, boolean isCMCC) {

	        boolean isStreamGBK = false;
	        if(filePath.endsWith(".lrc") && isCMCC){
	            isStreamGBK = true;
	        }
	        File file = new File(filePath);
	        FileOutputStream fOut = null;
	        try {
	            fOut = new FileOutputStream(file);
	            Log.e(TAG,"readStreamToFile isStreamGBK=="+isStreamGBK);
	            if(isStreamGBK){
	                String line = null;
	                BufferedReader br = new BufferedReader(new InputStreamReader(ins,"GBK"));
	                while((line = br.readLine()) != null){
	                    line = line + "\r\n";
	                    fOut.write(line.getBytes("UTF-8"), 0, line.getBytes("UTF-8").length);
	                }
	            }else{
	                byte[] buffer = new byte[1024];
	                int length = -1;
	                while ((length = ins.read(buffer)) != -1) {
	                    fOut.write(buffer, 0, length);
	                }
	            }
	            fOut.close();
	        } catch (IOException e) {
	            Log.d(TAG,"readStreamToFile error");
	            e.printStackTrace();
	            if (file.exists() && file.canWrite()) {
	                if(!file.delete()) {
	                    Log.i(TAG,"readStreamToFile");
	                }
	                return null;
	            }
	        }finally {
	            try{
	                if(fOut != null) {
	                    fOut.close();
	                }
	            }catch(Exception e) {
	                e.printStackTrace();
	            }
	        }
	        return file.getAbsolutePath();
	    }
	    

	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		// 关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}
	
}
