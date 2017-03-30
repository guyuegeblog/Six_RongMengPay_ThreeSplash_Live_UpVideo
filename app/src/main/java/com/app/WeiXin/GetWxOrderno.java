package com.app.WeiXin;


import android.app.Activity;
import android.util.Log;

import com.app.Tool.NetTool;
import com.app.View.T;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetWxOrderno {
//    public static DefaultHttpClient httpclient;
//
//    static {
//        httpclient = new DefaultHttpClient();
//        httpclient = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient);
//    }


    /**
     * description:获取预支付id
     *
     * @param url
     * @param xmlParam
     * @return
     * @author ex_yangxiaoyi
     * @see
     */
    public static String getPayNo(String url, String xmlParam, Activity mContext) {
//        DefaultHttpClient client = new DefaultHttpClient();
//        client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
//        HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
//        String prepay_id = "";
//        try {
//            httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
//            HttpResponse response = httpclient.execute(httpost);
//            String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
//            if (jsonStr.indexOf("FAIL") != -1) {
//                return prepay_id;
//            }
//            Map map = doXMLParse(jsonStr);
//            prepay_id = (String) map.get("prepay_id");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return prepay_id;


        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络");
            return "";
        }
        String prepay_id = "";
        RequestBody body = RequestBody.create(MediaType.parse("text/xml;charset=UTF-8"), url + "" + xmlParam);
        Request request = new Request.Builder().url(url)
                .post(body).build();
        Call call = new OkHttpClient().newCall(request);
        try {
            Response response = call.execute();
            Log.d("zgx", "response=====" + response.body().string());
            response.body().close();
            String jsonStr = response.body().string();
            if (jsonStr.indexOf("FAIL") != -1) {
                return prepay_id;
            }
            Map map = null;
            try {
//                map = doXMLParse(jsonStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            prepay_id = (String) map.get("prepay_id");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prepay_id;
    }

    /**
     *description:获取扫码支付连接
     *@param url
     *@param xmlParam
     *@return
     * @author ex_yangxiaoyi
     * @see
     */
//  public static String getCodeUrl(String url,String xmlParam){
//	  DefaultHttpClient client = new DefaultHttpClient();
//	  client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
//	  HttpPost httpost= HttpClientConnectionManager.getPostMethod(url);
//	  String code_url = "";
//     try {
//		 httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
//		 HttpResponse response = httpclient.execute(httpost);
//	     String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
//	    if(jsonStr.indexOf("FAIL")!=-1){
//	    	return code_url;
//	    }
//	    Map map = doXMLParse(jsonStr);
//	    code_url  = (String) map.get("code_url");
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	return code_url;
//  }

    /**
     *
     * @Title: getFind
     * @Description: 获取查询订单返回数据方法
     * @param @param url
     * @param @param xmlParam
     * @param @return    设定文件
     * @return String    返回类型
     * @throws
     */
//  public static String getFind(String url,String xmlParam){
//	  DefaultHttpClient client = new DefaultHttpClient();
//	  client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
//	  HttpPost httpost= HttpClientConnectionManager.getPostMethod(url);
//	  String find_result_xml = "";
//     try {
//		 httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
//		 HttpResponse response = httpclient.execute(httpost);
//	     find_result_xml = EntityUtils.toString(response.getEntity(), "UTF-8");
//     	} catch (Exception e) {
//     		System.out.println("查询订单获取返回异常"+e.getMessage());
//		e.printStackTrace();
//     	}
//	return find_result_xml;
//  }


    /**
     *
     * @Title: getFind
     * @Description: 获取查询订单返回数据方法
     * @param @param url
     * @param @param xmlParam
     * @param @return    设定文件
     * @return String    返回类型
     * @throws
     */
//  public static String getInit(String url,String xmlParam){
//	  DefaultHttpClient client = new DefaultHttpClient();
//	  client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
//	  HttpPost httpost= HttpClientConnectionManager.getPostMethod(url);
//	  String find_result_xml = "";
//     try {
//		 httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
//		 HttpResponse response = httpclient.execute(httpost);
//	     find_result_xml = EntityUtils.toString(response.getEntity(), "UTF-8");
//     	} catch (Exception e) {
//     		System.out.println("获取app支付返回信息异常"+e.getMessage());
//		e.printStackTrace();
//     	}
//	return find_result_xml;
//  }


    /**
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws FileNotFoundException
     * @throws KeyStoreException
     *
     * @Title: Refund
     * @Description: 申请退款获取返回数据访法
     * @param @param url
     * @param @param xmlParam
     * @param @return    设定文件
     * @return String    返回类型
     * @throws
     */
//  public static String doRefund(String url,String xmlParam) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException{
//
//	  KeyStore keyStore  = KeyStore.getInstance("PKCS12");
//      FileInputStream instream = new FileInputStream(new File("F:/advance/微信-资料/微信支付/微信支付证书/cert/apiclient_cert.p12"));//P12文件目录
//      try {
//          keyStore.load(instream, "1273703201".toCharArray());//这里写密码..默认是你的MCHID
//      } finally {
//          instream.close();
//      }
//
//      // Trust own CA and all self-signed certs
//      SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, "1273703201".toCharArray()).build();//这里也是写密码的
//      // Allow TLSv1 protocol only
//      SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,new String[] { "TLSv1" },null,SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//      CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
//
//	  DefaultHttpClient client = new DefaultHttpClient();
//	  client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
//	  HttpPost httpost= HttpClientConnectionManager.getPostMethod(url);
//	  String find_result_xml = "";
//     try {
//		 httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
//		 HttpResponse response = httpclient.execute(httpost);
//	     find_result_xml = EntityUtils.toString(response.getEntity(), "UTF-8");
//     	} catch (Exception e) {
//     		System.out.println("查询订单获取返回异常"+e.getMessage());
//     	}
//     	httpclient.close();
//		return find_result_xml;
//  	}


    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     *
     * @param
     * @return
     * @throws
     * @throws IOException
     */
//    public static Map doXMLParse(String strxml) throws Exception {
//        if (null == strxml || "".equals(strxml)) {
//            return null;
//        }
//
//        Map m = new HashMap();
//        InputStream in = String2Inputstream(strxml);
//        SAXBuilder builder = new SAXBuilder();
//        Document doc = builder.build(in);
//        Element root = doc.getRootElement();
//        List list = root.getChildren();
//        Iterator it = list.iterator();
//        while (it.hasNext()) {
//            Element e = (Element) it.next();
//            String k = e.getName();
//            String v = "";
//            List children = e.getChildren();
//            if (children.isEmpty()) {
//                v = e.getTextNormalize();
//            } else {
//                v = getChildrenText(children);
//            }
//
//            m.put(k, v);
//        }
//
//        //关闭流
//        in.close();
//
//        return m;
//    }
//
//    /**
//     * 获取子结点的xml
//     *
//     * @param children
//     * @return String
//     */
//    public static String getChildrenText(List children) {
//        StringBuffer sb = new StringBuffer();
//        if (!children.isEmpty()) {
//            Iterator it = children.iterator();
//            while (it.hasNext()) {
//                Element e = (Element) it.next();
//                String name = e.getName();
//                String value = e.getTextNormalize();
//                List list = e.getChildren();
//                sb.append("<" + name + ">");
//                if (!list.isEmpty()) {
//                    sb.append(getChildrenText(list));
//                }
//                sb.append(value);
//                sb.append("</" + name + ">");
//            }
//        }
//
//        return sb.toString();
//    }
//
//    public static InputStream String2Inputstream(String str) {
//        return new ByteArrayInputStream(str.getBytes());
//    }
//    public static Map doXMLParse(String resultXml) {
//        //解析返回的xml字符串，生成document对象
//        Document document = null;
//        try {
//            document = DocumentHelper.parseText(resultXml);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
////根节点
//        Element root = document.getRootElement();
////子节点
//        List<Element> childElements = root.elements();
//
//        Map<String, Object> mapEle = new HashMap<String, Object>();
////遍历子节点
//        mapEle = getAllElements(childElements, mapEle);
//        return mapEle;
//    }
//
//    public static Map<String, Object> getAllElements(List<Element> childElements,Map<String,Object> mapEle) {
//        for (Element ele : childElements) {
//            mapEle.put(ele.getName(), ele.getText());
//            if(ele.elements().size()>0){
//                mapEle = getAllElements(ele.elements(), mapEle);
//            }
//        }
//        return mapEle;
//    }

}