// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FileUtil.java

package com.android.music.cardview;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;


public class FileUtil {

    private static final String TAG = "FileUtil";

    public FileUtil() {
    }

    public static String getFileName(String path) {
        if (path == null)
            return null;
        String retStr = "";
        if (path.indexOf(File.separator) > 0)
            retStr = path.substring(path.lastIndexOf(File.separator) + 1);
        else
            retStr = path;
        return retStr;
    }

    public static String getFileNameNoPostfix(String path) {
        if (path == null)
            return null;
        else
            return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    public static String getExtension(String uri) {
        if (uri == null)
            return null;
        int extensionIndex = uri.lastIndexOf('.');
        int lastUnixIndex = uri.lastIndexOf('/');
        int lastWindowsIndex = uri.lastIndexOf('\\');
        int index = Math.max(lastUnixIndex, lastWindowsIndex);
        if (index > extensionIndex || extensionIndex < 0)
            return null;
        else
            return uri.substring(extensionIndex);
    }
    
    public static String getDirectory(String fullPath) {
        if (fullPath == null) {
            return null;
        }
//        int extensionIndex = fullPath.lastIndexOf('.');
        int lastUnixIndex = fullPath.lastIndexOf('/');
//        int lastWindowsIndex = fullPath.lastIndexOf('\\');
//        int index = Math.max(lastUnixIndex, lastWindowsIndex);
//        if (index > extensionIndex || extensionIndex < 0)
//            return null;
//        else
        return fullPath.substring(0,lastUnixIndex);
    }


    public static String byteCountToDisplaySize(long size) {
        String displaySize;
        if (size / 0x40000000L > 0L)
            displaySize = (new StringBuilder(String.valueOf(String
                    .valueOf(size / 0x40000000L)))).append(" GB").toString();
        else if (size / 0x100000L > 0L)
            displaySize = (new StringBuilder(String.valueOf(String
                    .valueOf(size / 0x100000L)))).append(" MB").toString();
        else if (size / 1024L > 0L)
            displaySize = (new StringBuilder(String.valueOf(String
                    .valueOf(size / 1024L)))).append(" KB").toString();
        else
            displaySize = (new StringBuilder(String.valueOf(String
                    .valueOf(size)))).append(" bytes").toString();
        return displaySize;
    }


    public static boolean isDirectory(File file) {
        return file.exists() && file.isDirectory();
    }

    public static boolean isFile(File file) {
        return file.exists() && file.isFile();
    }

    public static boolean createNewDirectory(String path) {
        return createNewDirectory(new File(path));
    }

    public static boolean createNewDirectory(File file) {
        if (file.exists() && file.isDirectory())
            return true;
        else
            return file.mkdirs();
    }
    
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.length() < 1)
            return true;
        File file = new File(filePath);
        if (!file.exists())
            return true;
        boolean flag = false;
        if (file.isFile())
            flag = file.delete();
        return flag;
    }

    public static void delDirectory(String directoryPath) {
        try {
            delAllFile(directoryPath);
            String filePath = directoryPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if(!myFilePath.delete()) {
                Log.i(TAG,"delDirectory");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
    public static void delAllAssignFuffixFile(String directoryPath , String postfix){
         try {
            String path;
             path = directoryPath;
             File mfile = new File(path);
             File[] files = mfile.listFiles();
             for (int i = 0; i < files.length; i++) {
                  File file = files[i];
                  String FileEnd = file.getPath().substring(file.getPath().lastIndexOf(".") + 1,
                          file.getPath().length()).toLowerCase();
                  if (FileEnd.equals(postfix)){
                      deleteFile(file.getPath());
                  }
             }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }    
    }
    public static File getPopOpenfile(String directoryPath,String name){
        String path;
        File file = null;
         path = directoryPath;
        try {
             File mfile = new File(path);
             File[] files = mfile.listFiles();
             for (int i = 0; i < files.length; i++) {
                  file = files[i];
                  String FileEnd = file.getPath().toString();
                  if (FileEnd.contains(name)){
                      Log.d("liuj", "name == "+name);
                      return file ;
                  }else{
                      file = null;  
                  }
             }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
        return file;
        
    }
    public static boolean isPopApkfile(String directoryPath,String name){
        String path;
        File file = null;
        boolean flag = false;
         path = directoryPath;
        try {
             File mfile = new File(path);
             File[] files = mfile.listFiles();
             for (int i = 0; i < files.length; i++) {
                  file = files[i];
                  String FileEnd = file.getPath().toString();
                  if (FileEnd.contains(name)){
                      flag = true ;
                  }
             }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
        return flag;
        
    }
    
    public static void delAllAssignFuffixFile(String directoryPath){
         String path;
         path = directoryPath;
         File mfile = new File(path);
         File[] files = mfile.listFiles();
         for (int i = 0; i < files.length; i++) {
              File file = files[i];
              String FileEnd = file.getPath().substring(file.getPath().lastIndexOf(".") + 1,
                      file.getPath().length()).toLowerCase();
              if (FileEnd.equals("tmp")){
                  deleteFile(file.getPath());
              }
         }
         
    }
    private static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists())
            return flag;
        if (!file.isDirectory())
            return flag;
        String tempList[] = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator))
                temp = new File((new StringBuilder(String.valueOf(path)))
                        .append(tempList[i]).toString());
            else
                temp = new File((new StringBuilder(String.valueOf(path)))
                        .append(File.separator).append(tempList[i]).toString());
            if (temp.isFile()) {
                if(!temp.delete()) {
                    Log.i(TAG,"delAllFile");
                }
            }
            if (temp.isDirectory()) {
                delAllFile((new StringBuilder(String.valueOf(path)))
                        .append("/").append(tempList[i]).toString());
                delDirectory((new StringBuilder(String.valueOf(path)))
                        .append("/").append(tempList[i]).toString());
                flag = true;
            }
        }

        return flag;
    }



    public static boolean isLosslessSupported(File f) {
        String s = f.toString();
        if (s.endsWith(".flac") || s.endsWith(".FLAC"))
            return true;
        if (s.endsWith(".ape") || s.endsWith(".APE"))
            return true;
        if (s.endsWith(".wav") || s.endsWith(".WAV"))
            return true;
        if (s.endsWith(".wv") || s.endsWith(".WV"))
            return true;
        if (s.endsWith(".mpc") || s.endsWith(".MPC"))
            return true;
        return s.endsWith(".m4a") || s.endsWith(".M4A");
    }

    public static boolean isLosslessSupported(String s) {
        return false;
    }

    public static void clearFiles(String dirPath, String suffix) {
        if (TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(suffix))
            return;
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory())
            return;
        String filename = null;
        File afile[];
        int j = (afile = dir.listFiles()).length;
        for (int i = 0; i < j; i++) {
            File file = afile[i];
            filename = file.getName();
            if (filename.endsWith(suffix))
                if(!file.delete()) {
                    Log.i(TAG,"clearFiles");
                }
        }
        return;
    }
    public static boolean isCollectionEmpty(Collection collection)
    {
        return collection == null || collection.isEmpty();
    }

    public static boolean isMapEmpty(Map map)
    {
        return map == null || map.isEmpty();
    }
    
    public static void clearFiles(ArrayList list, String suffix) {
        if (isCollectionEmpty(list))
            return;
        File file = null;
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            String path = (String) iterator.next();
            if (!TextUtils.isEmpty(path)) {
                String tmp = getExtension(path);
                if (!TextUtils.isEmpty(tmp) && tmp.equals(suffix)) {
                    file = new File(path);
                    if (file.exists() && file.isFile())
                        if(!file.delete()) {
                            Log.i(TAG,"clearFiles");
                        }
                }
            }
        }
        return;
    }

    public static String writeToFile(Context context, InputStream is,
            String filename) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        if (is == null || context == null || TextUtils.isEmpty(filename))
            return null;
        try {
            in = new BufferedInputStream(is);
            out = new BufferedOutputStream(context.openFileOutput(filename, 0));
            byte buffer[] = new byte[1024];
            int l;
            while ((l = in.read(buffer)) != -1) {
                out.write(buffer, 0, l);
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                    if(in != null) {
                        in.close();
                    }
                    is.close();
                    if(out != null) {
                        out.flush();
                        out.close();
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (new StringBuilder()).append(context.getFilesDir())
                .append(File.separator).append(filename).toString();
    }

    public static String writeToFile(InputStream is, String filepath)
            throws IOException {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        if (is == null || TextUtils.isEmpty(filepath))
            return null;
        try {
            Log.d("FileUtil", (new StringBuilder("write to file : "))
                    .append(filepath).toString());
            File file = new File(filepath);
            checkDir(file.getParent());
            in = new BufferedInputStream(is);
            out = new BufferedOutputStream(new FileOutputStream(filepath));
            byte buffer[] = new byte[1024];
            int l;
            while ((l = in.read(buffer)) != -1) {
                out.write(buffer, 0, l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                is.close();
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
        }
        return filepath;
    }

    public static synchronized String writeToFileSync(InputStream is,
            String filepath) throws IOException {
        return writeToFile(is, filepath);
    }

//    public static boolean writeStringToFile(File file, String string, boolean isAppend)
//    {
//        boolean isWriteOk;
//        FileWriter fw = null;
//        isWriteOk = false;
//        if (file == null || string == null)
//            return isWriteOk;
//        try
//        {
//            fw = new FileWriter(file, isAppend);
//            fw.write(string, 0, string.length());
//            fw.flush();
//            isWriteOk = true;
//        }
//        catch (Exception e)
//        {
//            isWriteOk = false;
//            e.printStackTrace();
//        }
//        if (fw != null)
//            try
//            {
//                fw.close();
//            }
//            catch (IOException e)
//            {
//                isWriteOk = false;
//                e.printStackTrace();
//            }
//
//        if (fw != null)
//            try
//            {
//                fw.close();
//            }
//            catch (IOException e)
//            {
//                isWriteOk = false;
//                e.printStackTrace();
//            }
//
//        if (fw != null)
//            try
//            {
//                fw.close();
//            }
//            catch (IOException e)
//            {
//                isWriteOk = false;
//                e.printStackTrace();
//            }
//        return isWriteOk;
//    }

    public static String readFileToString(File file)
    {
        FileInputStream fileInput;
        StringBuffer strBuf;
        if (file == null)
            return "";
        fileInput = null;
        strBuf = new StringBuffer();
        try{
            fileInput = new FileInputStream(file);
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
        try{
            if(fileInput != null) {
                for (byte buf[] = new byte[1024]; fileInput.read(buf) != -1; buf = new byte[1024])
                    strBuf.append(new String(buf, "UTF-8"));
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        if (fileInput != null)
            try
            {
                fileInput.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        if (fileInput != null)
            try
            {
                fileInput.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        if (fileInput != null)
            try
            {
                fileInput.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        if (fileInput != null)
            try
            {
                fileInput.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        if (fileInput != null)
            try
            {
                fileInput.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        return strBuf.toString();
    }

    public static File[] listFiles(String path)
    {
        if(path == null){
            return new File[0];
        }
        File dir;
        dir = new File(path);
        if (!dir.exists())
            if(!dir.mkdir()) {
                Log.i(TAG,"listFiles");
            }
        return dir.listFiles();
    }

//    public static int getFileLines(File file)
//    {
//        BufferedReader bufReader;
//        int count;
//        if (file == null)
//            return 0;
//        bufReader = null;
//        count = 0;
//        try{
//            for (bufReader = new BufferedReader(new FileReader(file)); bufReader.readLine() != null;)
//                count++;
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//        if (bufReader != null)
//            try
//            {
//                bufReader.close();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//
//        if (bufReader != null)
//            try
//            {
//                bufReader.close();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//
//        if (bufReader != null)
//            try
//            {
//                bufReader.close();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//
//        if (bufReader != null)
//            try
//            {
//                bufReader.close();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        return count;
//    }

    public static boolean checkDir(String dirPath) {
        if (TextUtils.isEmpty(dirPath))
            return false;
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory())
            return true;
        if (dir.exists()) {
            if(!dir.delete()) {        
                Log.i(TAG,"checkDir");
            }
        }
        return dir.mkdirs();
    }

    public static long getDirLength(String dirPath) {
        if (TextUtils.isEmpty(dirPath))
            return 0L;
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory())
            return 0L;
        long length = 0L;
        File afile[];
        int j = (afile = dir.listFiles()).length;
        for (int i = 0; i < j; i++) {
            File file = afile[i];
            if (file.isFile())
                length += file.length();
            else if (file.isDirectory())
                length += getDirLength(file.getAbsolutePath());
        }

        return length;
    }

    public static int removeOldFiles(String dirPath, long length) {
        if (TextUtils.isEmpty(dirPath) || length <= 0L)
            return 0;
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory())
            return 0;
        File files[] = getFilesByLastModified(dir);
        long l = 0L;
        int count = 0;
        File afile[];
        int j = (afile = files).length;
        for (int i = 0; i < j; i++) {
            File file = afile[i];
            l = file.length();
            if (!file.delete())
                continue;
            count++;
            length -= l;
            if (length <= 0L)
                break;
        }

        return count;
    }

    public static File[] getFilesByLastModified(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return null;
        } else {
            File files[] = dir.listFiles();
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            Arrays.sort(files, new Comparator() {

                public int compare(File lhs, File rhs) {
                    return (int) (lhs.lastModified() - rhs.lastModified());
                }

                public synchronized int compare(Object obj, Object obj1) {
                    return compare((File) obj, (File) obj1);
                }

            });
            return files;
        }
    }

    public static boolean isSDCardAvailable() {
        String status = Environment.getExternalStorageState();
        return status.equals("mounted");
    }

    public static boolean clearDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory())
            return false;
        File afile[];
        int j = (afile = dir.listFiles()).length;
        for (int i = 0; i < j; i++) {
            File file = afile[i];
            if (file.exists()) {
                if (file.isFile()) {
                    if(!file.delete()) {
                        Log.i(TAG,"clearDir");
                    }
                }
                if (file.isDirectory())
                    clearDir(file.getAbsolutePath());
            }
        }

        File files[] = dir.listFiles();
        return files == null || files.length == 0;
    }

    public static boolean checkFile(File file) throws IOException {
        if (file == null)
            return false;
        if (file.isFile() && file.exists())
            return true;
        if (file.exists() && !file.isFile()) {
            if(!file.delete()) {
                Log.i(TAG,"checkFile");
            }
        }
        if(!file.getParentFile().mkdirs()) {
            Log.i(TAG,"checkFile11");
        }
        return file.createNewFile();
    }

    public static String filterFileName(String filename) {
        if (TextUtils.isEmpty(filename)) {
            return filename;
        } else {
            filename = filename.replace(' ', '_');
            filename = filename.replace('"', '_');
            filename = filename.replace('\'', '_');
            filename = filename.replace('\\', '_');
            filename = filename.replace('/', '_');
            filename = filename.replace('<', '_');
            filename = filename.replace('>', '_');
            filename = filename.replace('|', '_');
            filename = filename.replace('?', '_');
            filename = filename.replace(':', '_');
            filename = filename.replace(',', '_');
            filename = filename.replace('*', '_');
            return filename;
        }
    }

    public static long getSDCardAvailableSpace() {
        File file = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(file.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static boolean checkSDCardHasEnoughSpace(long size) {
        return getSDCardAvailableSpace() > size;
    }

    public static boolean checkPathInSDcard(String path) {
        if (TextUtils.isEmpty(path))
            return false;
        String sdcardPrefix = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        return path.startsWith("/sdcard") || path.startsWith(sdcardPrefix);
    }
    
    public static final long sLimitSpace = 20 * 1024 * 1024;
    public static boolean isSDCardSpaceEnough() {
        File sdcard = Environment.getExternalStorageDirectory();
        return isSpaceEnoughByPath(sdcard.getPath());
    }
    
    public static boolean isSpaceEnoughByPath(String path) {
        boolean res = true;
        try {
            StatFs statFs = new StatFs(path);
            long blocSize = statFs.getBlockSize();
            long blocks = statFs.getAvailableBlocks();
            if (blocks * blocSize > sLimitSpace) {
                res = true;
            } else {
                res = false;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            res = false;
        }
        return res;
    }
    
//    public static String[] getStorageVolumePaths(Context context){
//        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//        String [] s = storageManager.getVolumePaths();
//        return s;
//    }

    public static int getFatVolumeId(String sdpath){
        int volumeId = -1;
        try{
            Class<?> fileUtils = Class.forName("android.os.FileUtils");
            Method method = null;
            if(android.os.Build.VERSION.SDK_INT < 19){
                method = fileUtils.getDeclaredMethod("getFatVolumeId", String.class);
            }else{
                method = fileUtils.getDeclaredMethod("getUid", String.class);
            }
            volumeId = (Integer) method.invoke(fileUtils, sdpath);
        }catch(Exception e){
            Log.i(TAG, "getFatVolumeId e="+e.toString());
        }
        return volumeId;
    }

//    public static String getVolumeIds(Context c){
//        StringBuffer volumeIdBuffer = new StringBuffer();
//        String [] sds = getStorageVolumePaths(c);
//        for(int i=0;i< sds.length;i++){
//            int volumeID = getFatVolumeId(sds[i]);
//            volumeIdBuffer.append(volumeID).append("&");
//        }
//        return volumeIdBuffer.toString();
//    }
    
    public static boolean isExist(String filePath){
        boolean res = false;
        if(filePath == null){
            return res;
        }
        File f = new File(filePath);
        if(f != null){
            if(f.exists())
                res = true;
        }
        return res;
    }
    
    public static boolean writeStringToFile(String str, String filePath) {
        boolean res = false;
        File file = new File(filePath);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(str.getBytes("UTF-8"));
            res = true;
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }
    
    public static void createNullFile(String path) {
        try {
            File file = new File(path);
            if(file.exists()) {
                if(!file.delete()) {
                    Log.i(TAG,"createNullFile");
                }
            }
            if(!file.createNewFile()) {
                Log.i(TAG,"createNullFile11");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
    
    public static void copyFile(String oldPath, String newPath) {
        File file = new File(oldPath);
        File fileNew = new File(newPath);
        if(fileNew.exists()) {
            if(!fileNew.delete()) {
                Log.i(TAG,"copyFile");
            }
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;
        try{
            fis = new FileInputStream(file);
            fos = new FileOutputStream(fileNew);
            in = fis.getChannel();
            out = fos.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (Exception e) {
            Log.i(TAG, "copyFile: " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                    if(fis != null) {
                        fis.close();
                    }
                    if(fos != null) {
                        fos.close();
                    }
                    if(in != null) {
                        in.close();    
                    }
                    if(out != null) {
                        out.close();
                    }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return;
    }
    
    public static void renameFile(String oldname, String newname) {
        try {
            File oldFile = new File(oldname);
            File newFile = new File(newname);
            if(!oldFile.renameTo(newFile)) {
            	CardViewUtils.copyFile(oldFile, newFile);
                if(!oldFile.delete()) {
                    Log.i(TAG,"DownloadThread");
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"renameFile e"+e.toString());
        }
    }

    /**
     * 获取指定文件夹
     * 
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 获取指定文件大小
     * 
     * @param file
     * @return 文件的大小
     * @throws Exception
     */
    private static long getFileSize(File file) {
        long size = 0;
        FileInputStream fis = null;
        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                size = fis.available();
            }
        } catch (Exception e) {
            Log.i(TAG, "getFileSize: " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     * 
     * @param deleteThisPath
     *            是否删除文件夹下的文件
     * @param filepath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        if(file.delete()) {
                            Log.i(TAG,"delete file succussfully");
                        }
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            if(file.delete()) {
                                Log.i(TAG,"delete file succussfully");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /** Gionee jingcl add for cover picture 141B end */
    
    /**
     * copy assets 
     */
    public static boolean transAssetsApk(Context ctx,String apkName,String filePath) {
        boolean res = false;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = ctx.getAssets().open(apkName);
            File file = new File(filePath);
            if(file.delete()) {
                Log.i(TAG,"delete file succussfully");
            }
            if(file.createNewFile()) {
                Log.i(TAG,"create file succussfully");
            }
            fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            res = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return res;
    }
    
    /**
     * 转换文件大小
     * @param sizeType 
     * @param fileSize
     * @return
     */
    
    public static String FormetFileSize(long dirsize) {
        String fileSizeStrting = "";
        double fileSize;
        String wrongSizeString = "0.0M";
        if(dirsize==0){
            fileSizeStrting = wrongSizeString;
        }else if(dirsize<1024){
            fileSizeStrting=Double.valueOf(dirsize)+"B";
        }else if(dirsize<1048576){
            fileSize=dirsize /(double)1024;
            fileSizeStrting = (double)(Math.round(fileSize*10)/10.0)+"K";
        }else if(dirsize<1073741824){
            fileSize=dirsize /(double)1048576;
            fileSizeStrting = (double)(Math.round(fileSize*10)/10.0)+"M";
        }
        return fileSizeStrting;
    }
    
    public static String[] getFilesInDir(String directoryPath) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            File file = new File(directoryPath);
            if (!file.exists())
                return null;
            if (!file.isDirectory())
                return null;
            File[] fs = file.listFiles();
            for (int i = 0; i < fs.length; i++) {
                File f = fs[i];
                if(f!= null && f.isFile()){
                    /*String s = f.getAbsolutePath().toLowerCase();
                    if(endWithType(s)){
                        res++;
                    }*/
                    list.add(f.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] res = new String[list.size()];
        for(int i=0;i<list.size();i++){
            res[i] = list.get(i);
        }
        return res;
    }
    
    public static String transferUnit(double number) {
        if(number<10000) {
            return (int)number+"";
        }else {
            double num = number/10000;
            
            return Math.round(num*10)/10.0 + "万";
        }
        
    }

    
}
