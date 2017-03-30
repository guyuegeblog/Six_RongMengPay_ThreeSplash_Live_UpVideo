package com.app.WeiXin;

/**
 * 
* @ClassName: Convert
* @Description: 获取随机字符串，将元转换为分，解析微信返回的XML，获取时间戳
* @author A18ccms a18ccms_gmail_com
* @date 2015年11月11日 下午4:28:46
*
 */
@SuppressWarnings("unused")
public class Convert {
	
	/**
	 * 
	* @Title: getNonceStr 
	* @Description: 获取随机字符串
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String getNonceStr(){
		//随机数
		String currTime = TenpayUtil.getCurrTime();
		//8位日期
		String strTime = currTime.substring(8,currTime.length());
		//四位随机数
		String strRandom = TenpayUtil.buildRandom(4) + "";
		//十位序列号，可以自行调整
		return strTime + strRandom;
	}
	
	/**
	 * 
	* @Title: getMoney 
	* @Description: 将元转换成分
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String getMoney(String amount){
		if(amount == null){
			return "";
		}
		
		//将金额转换为分为单位
		String currency = amount.replaceAll("\\$|\\￥|\\,", "");
		int index = currency.indexOf(".");
		int length = currency.length();
		Long amLong = 0l; 
		if(index == -1){
			amLong = Long.valueOf(currency+"00");
		}else if(length - index >= 3){
			amLong = Long.valueOf((currency.substring(0, index+3)).replace(".", ""));  
        }else if(length - index == 2){  
            amLong = Long.valueOf((currency.substring(0, index+2)).replace(".", "")+0);  
        }else{  
            amLong = Long.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");  
        }  
			return amLong.toString(); 
		}
	
	/**
	 * 
	* @Title: parseXmlToList2 
	* @Description: 解析微信返回的xml
	* @param @return    设定文件 
	* @return Map    返回类型 
	* @throws
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public static Map parseXmlToList2(String xml){
//		Map retMap =  new HashMap();
//		try {
//			StringReader read = new StringReader(xml);
//			//创建新的输入员SAX 解析器将使用InputSource对象确定如何读取XML 输入
//			InputSource source = new InputSource(read);
//			//创建一个新的SAXBuilder
//			SAXBuilder sb = new SAXBuilder();
//			//通过输入源构造一个Document
//			Document doc = (Document) sb.build(source);
//			//指向根节点
//			Element root = doc.getRootElement();
//			List<Element> es = root.getChildren();
//
//			if(es != null && es.size() != 0){
//				for (Element element : es) {
//					retMap.put(element.getName(), element.getValue());
//				}
//			}
//		} catch (Exception e) {
//			System.out.println("解析XML异常"+e.getMessage());
//		}
//		return retMap;
//	}
	
	/**
	 * 
	* @Title: getTimeStamp 
	* @Description:获取时间戳
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String getTimeStamp(){
		long timestamps = System.currentTimeMillis();
		String timestamp2 = timestamps + "";
		String timestamp = timestamp2.substring(0, 9);
		return timestamp;
	}
	
	public static String getOrderNum(){
		//日期
		long timestamp = System.currentTimeMillis();
		//随机数 4位
		String strRandom = TenpayUtil.buildRandom(4) + "";
		return  timestamp + strRandom;
	}
}
