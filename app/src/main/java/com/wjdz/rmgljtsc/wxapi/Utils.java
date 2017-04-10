package com.wjdz.rmgljtsc.wxapi;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	/**
	 * // 生成订单流水号（日期+随机数）
	 * 
	 * @return
	 */
	public static String getOutTradeNo() {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = format.format(date);

		String reptime = time.replace("-", " ");

		System.out.println(reptime.toString());

		String reptimes = reptime.replace(":", " ");

		System.out.println(reptimes.trim().toString());

		String reptimetrim = reptimes.replace(" ", "");//去掉后最终结果

		System.out.println(reptimetrim);

		int ran = (int) (Math.random() * 99999 + 1);

		System.out.println(ran);

		String orderNum = reptimetrim + ran;

		return orderNum;
	}

	/**
	 * 生成订单流水号（以日期格式）
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String getOutTradeNoTwo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date date = new Date();
		String key = format.format(date);

		java.util.Random r = new java.util.Random();
		key += r.nextInt();
		key = key.substring(0, 15);
		return key;
	}
	
	public static String formatIsToString(InputStream is) throws UnsupportedEncodingException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		try {
			while( (len=is.read(buf)) != -1){
				baos.write(buf, 0, len);
			}
			baos.flush();
			baos.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(baos.toByteArray(),"UTF-8");
	}
}
