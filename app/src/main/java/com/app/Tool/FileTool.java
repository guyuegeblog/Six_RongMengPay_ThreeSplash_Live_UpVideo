package com.app.Tool;

import android.os.Environment;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ASUS on 2016/12/6.
 */
public class FileTool {

    //创建文件夹
    public static void createFileDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                //按照指定的路径创建文件夹
                file.mkdirs();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }


    //创建文件
    public static void createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                //在指定的文件夹中创建文件
                file.createNewFile();
            } catch (Exception e) {
            }
        }
    }


    /**
     * 递归删除文件和文件夹
     *
     * @param
     */
    public static boolean deleteFile(String path) {
        if (StringTool.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }


    //写入文件
    public static void writeFileToSDFile(String fileName, String text) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.mkdir();
            }
            FileOutputStream outputStream = new FileOutputStream(fileName);
            byte[] bytes = text.getBytes();
            outputStream.write(bytes);
            outputStream.close();//关闭写人流
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    //读取文件
    public static String readFileToSDFile(String fileName) {
        String res = "";
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            int length = inputStream.available();//获取值的长度
            byte[] bytes = new byte[length];//接收读取的值
            inputStream.read(bytes);

            res = EncodingUtils.getString(bytes, "UTF-8");
            inputStream.close();//关闭输入流
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
        }

        return res;
    }

    //获取Sd卡根路径
    public static String getSDCardPath() {
        String path = "";
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            path = Environment.getRootDirectory().getPath();//system
        }
        return path;
    }

    /**
     * 快速清空一个超大的文件
     */
    public static boolean cleanFile(File file) {
        FileWriter fw = null;
        try {
            fw=new FileWriter(file);
            fw.write("");
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try {
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 复制文件
     */
    public static boolean copy(String resourcePath, String targetPath) {
        File file = new File(resourcePath);
        return copy(file, targetPath);
    }

    /**
     * 复制文件
     *      通过该方式复制文件文件越大速度越是明显
     */
    private static Integer BUFFER_SIZE = 1024 * 1024 * 10;
    public static boolean copy(File file, String targetFile) {
        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
            fin = new FileInputStream(file);
            fout = new FileOutputStream(new File(targetFile));
            FileChannel in = fin.getChannel();
            FileChannel out = fout.getChannel();
            //设定缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (in.read(buffer) != -1) {
                buffer.flip();//准备写入，防止其他读取，锁住文件
                out.write(buffer);
                buffer.clear();//准备读取。将缓冲区清理完毕，移动文件内部指针
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                fout.close();
                fin.close();
            } catch (Exception e) {

            }
        }
        return false;
    }

    /**
     * 创建多级目录
     *
     * @param paths
     * @return
     */
    public static void createPaths(String paths) {
        File dir = new File(paths);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

}
