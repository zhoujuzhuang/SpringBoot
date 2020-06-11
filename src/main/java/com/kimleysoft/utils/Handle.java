package com.kimleysoft.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.mysql.jdbc.StringUtils;

@Component
public class Handle {

	@Autowired
    private ReloadableResourceBundleMessageSource messageSource;
	
	public static MessageSource ms;
	
	@PostConstruct
    public void init(){
        ms = this.messageSource;
	}
	
	public static String getLanguageName(String key,String lankey,String defaultKey) {
		String message = "";
		
		try {
			if(key == null) {
				return defaultKey;
			}
			Locale locale;
			if(lankey == null || lankey.split("_").length < 2) {
				locale = LocaleContextHolder.getLocale();
			}else {
				String[] lan = lankey.split("_");
				locale = new Locale(lan[0],lan[1]);
			}
			message = ms.getMessage(key, null, locale);
		} catch (Exception e) {
			return defaultKey;
		}
		return (message == null || message.equals("")) ? defaultKey : message;
	}
	
	public static String getLanguageName(String key,String lankey) {
		String message = "";
		try {
			if(key == null) {
				return "";
			}
			Locale locale;
			if(lankey == null || lankey.split("_").length < 2) {
				locale = LocaleContextHolder.getLocale();
			}else {
				String[] lan = lankey.split("_");
				locale = new Locale(lan[0],lan[1]);
			}
			message = ms.getMessage(key, null, locale);
		} catch (NoSuchMessageException e) {
			return key;
		}
		return  (message == null || message.equals("")) ? key : message;
	}
	
	// 分割字符串
	public static String splitString(String source, String split, String lankey) {
		String languageString = "";
		if (source != null && !source.equals("")) {
			if (source.indexOf("*") != -1) {
				String[] str = source.split(split);
				for (String string : str) {
					if (languageString.equals("")) {
						languageString = getLanguageName(string, lankey);
					} else {
						languageString += "*" + getLanguageName(string, lankey);
					}
				}
				return languageString;
			} else {
				return languageString = getLanguageName(source, lankey);
			}
		}
		return null;
	}

	public static String il8nStringGet(String il8nStr, String lankey) {
		if (il8nStr != null) {
			List<String> il8nLs = new ArrayList<String>();
			Pattern pattern = Pattern.compile("(?<=\\{)(.*?)(?=\\})");
			Matcher matcher = pattern.matcher(il8nStr);
			while (matcher.find()) {
				il8nLs.add(matcher.group());
			}

			if (il8nLs != null && il8nLs.size() > 0) {
				for (String l : il8nLs) {
					if (!l.trim().equals("")) {
						il8nStr = il8nStr.replace("{" + l + "}", getLanguageName(l, lankey,null) == null ? "" : getLanguageName(l, lankey,null));
					}
				}
			}
		}
		return il8nStr;
	}
	
	public static String getFileSuffix(String fileName) {
		if (fileName != null) {
			int index = fileName.lastIndexOf(".");
			if (index != -1) {
				return fileName.substring(index, fileName.length());
			}
		}

		return "";
	}

	public static Boolean isUUID(String uuid) {
		if (isNull(uuid)) {
			return false;
		}
		String reg = "^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$";
		String reg2 = "^[0-9]{16}$";
		return uuid.matches(reg) || uuid.matches(reg2);
	}

	public static Boolean isKeyID(String id) {
		if (isNull(id)) {
			return false;
		}
		// 14067 93422 37112 5
		String reg = "^[0-9]{16}$";
		return id.matches(reg);
	}

	/**
	 * @方法說明:删除List中重复元素
	 * @author chenJian E-mail: test_t@163.COM
	 * @createTime 創建時間：2013/12/4 下午2:50:08
	 * @copyright e-print
	 * @param list
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List removeDuplicate(List list) {
		HashSet h = new HashSet(list);
		list.clear();
		list.addAll(h);
		return list;
	}

	/**
	 * @方法說明: 删除List中重复元素，保持顺序
	 * @author chenJian E-mail: test_t@163.COM
	 * @createTime 創建時間：2013/12/4 下午2:51:13
	 * @copyright e-print
	 * @param list
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List removeDuplicateWithOrder(List list) {
		Set set = new HashSet();
		List newList = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		list.clear();
		list.addAll(newList);
		return list;
	}

	/**
	 * @方法說明: 获取最大接近数，如果集合中没有值大于num，则返回集合中最大的值
	 * @author chenJian E-mail: test_t@163.COM
	 * @createTime 創建時間：2013/11/30 下午3:22:51
	 * @copyright e-print
	 * @param list
	 * @param num
	 * @return
	 */
	public static Integer maxNearList(List<Integer> list, int num) {
		List<Integer> _list = new ArrayList<Integer>();
		for (Integer integer : list) {
			if (integer != null && integer >= num) {
				_list.add(integer);
			}
		}
		if (_list.size() > 0) {
			return Collections.min(_list);
		}
		return Collections.max(list);
	}

	/**
	 * @方法說明: 获取最小接近数，如果集合中没有值小于num，则返回集合中最小的值
	 * @author chenJian E-mail: test_t@163.COM
	 * @createTime 創建時間：2013/11/30 下午3:23:49
	 * @copyright e-print
	 * @param list
	 * @param num
	 * @return
	 */
	public static Integer minNearList(List<Integer> list, int num) {
		List<Integer> _list = new ArrayList<Integer>();
		for (Integer integer : list) {
			if (integer != null && integer <= num) {
				_list.add(integer);
			}
		}
		if (_list.size() > 0) {
			return Collections.max(_list);
		}
		return Collections.min(list);
	}

	/**
	 * @方法說明: 获取最大接近数，如果集合中没有值大于num，则返回集合中最大的值
	 * @author chenJian E-mail: test_t@163.COM
	 * @createTime 創建時間：2013/11/30 下午3:22:51
	 * @copyright e-print
	 * @param list
	 * @param num
	 * @return
	 */
	public static Float maxNearList2(List<Float> list, float num) {
		List<Float> _list = new ArrayList<Float>();
		for (Float integer : list) {
			if (integer != null && integer >= num) {
				_list.add(integer);
			}
		}
		if (_list.size() > 0) {
			return Collections.min(_list);
		}
		return Collections.max(list);
	}

	/**
	 * @方法說明: 获取最小接近数，如果集合中没有值小于num，则返回集合中最小的值
	 * @author chenJian E-mail: test_t@163.COM
	 * @createTime 創建時間：2013/11/30 下午3:23:49
	 * @copyright e-print
	 * @param list
	 * @param num
	 * @return
	 */
	public static Float minNearList2(List<Float> list, float num) {
		List<Float> _list = new ArrayList<Float>();
		for (Float integer : list) {
			if (integer != null && integer <= num) {
				_list.add(integer);
			}
		}
		if (_list.size() > 0) {
			return Collections.max(_list);
		}
		return Collections.min(list);
	}

	/**
	 * @方法說明: list降序排序
	 * @author chenJian E-mail: test_t@163.COM
	 * @createTime 創建時間：2013/11/30 上午11:38:27
	 * @copyright e-print
	 * @param list
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void descList(List<Integer> list) {
		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				Integer a1 = (Integer) o1;
				Integer a2 = (Integer) o2;
				if (a1 > a2) {
					return 0;
				}
				return 1;
			}
		});
	}

	/**
	 * @方法說明: list降序排序
	 * @author chenJian E-mail: test_t@163.COM
	 * @createTime 創建時間：2013/11/30 上午11:38:27
	 * @copyright e-print
	 * @param list
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void descList2(List<Float> list) {
		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				Float a1 = (Float) o1;
				Float a2 = (Float) o2;
				if (a1 > a2) {
					return 0;
				}
				return 1;
			}
		});
	}

	/**
	 * @方法說明: list 升序排序
	 * @author chenJian E-mail: test_t@163.COM
	 * @createTime 創建時間：2013/11/30 上午11:39:07
	 * @copyright e-print
	 * @param list
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void ascList(List<Integer> list) {
		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				Integer a1 = (Integer) o1;
				Integer a2 = (Integer) o2;
				if (a1 > a2) {
					return 1;
				}
				return 0;
			}
		});
	}


	/**
	 * 統一path路徑"\"
	 * 
	 * @param path
	 * @return
	 */
	public static String passpath2(String path) {
		boolean flag = true;
		path = passpath(path);
		while (flag) {
			path = path.replace("/", "\\");
			if (path.indexOf("/") == -1 && path.indexOf("//") == -1) {
				flag = false;
			}
		}
		return path;
	}

	/**
	 * 統一path路徑"/"
	 * 
	 * @param path
	 * @return
	 */
	public static String passpath(String path) {
		boolean flag = true;
		while (flag) {
			path = path.replace("\t", "/t");
			path = path.replace("\\", "/");
			path = path.replace("//", "/");
			if (path.indexOf("\\") == -1 && path.indexOf("//") == -1) {
				flag = false;
			}
		}
		return path;
	}

	/**
	 * 統一文件路徑，只可用於文件路徑，其他路徑不適用
	 * 
	 * @param path
	 * @return
	 */
	public static String formatPath(String path) {
		// 將所有"\"替換為"/"
		path = path.replaceAll("\\\\", "/");
		// 將所有重複"/"替換為單"/"
		path = path.replaceAll("(\\/{2,})+", "/");
		// 去掉路徑最後的"/"
		if (path.endsWith("/")) {
			path = path.substring(0, path.lastIndexOf("/"));
		}

		return path;
	}


	public static long getLong(String str) {
		str = trim(str);
		try {
			return Long.valueOf(str);
		} catch (Exception e) {
			return 0;
		}
	}

	public static long getLong2(Integer num) {
		try {
			return Long.valueOf(num);
		} catch (Exception e) {
			return 0;
		}
	}


	/** 為空時返回true */
	public static boolean isNull(String arg) {
		if (arg == null)
			return true;
		if ("".equals(arg))
			return true;
		if ("".equals(arg.trim()))
			return true;
		if ("null".equals(arg.trim().toLowerCase()))
			return true;
		return false;
	}

	/** 去空格. */
	public static String trim(String arg) {
		return isNull(arg) ? null : arg.trim();
	}

	public static String getString2(Object obj) {
		if (obj != null && !obj.equals("") && !obj.equals("null")) {
			return obj.toString().trim();
		}
		return "";
	}

	public static String getString(String str) {
		if (str != null && !str.equals("") && !str.equals("null")) {
			return str.trim();
		}
		return "";
	}

	public static String getString3(String str) {
		if (str != null && !str.equals("") && !str.equals("null")) {
			return str.trim();
		}
		return "0";
	}

	public static String getString1(String str) {
		if (str != null && !str.equals("") && !str.equals("null")) {
			return str.trim();
		}
		return null;
	}

	public static String getInteger1(int id) {
		try {
			if (id == 0) {
				return "";
			} else {
				return id + "";
			}
		} catch (Exception e) {
		}
		return "";
	}

	public static String getFloat1(float value) {
		try {
			if (value == 0f) {
				return "";
			} else {
				return value + "";
			}
		} catch (Exception e) {
		}
		return "";
	}

	public static String getUploadFileName(String name) {
		try {
			Date date = new Date();
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyyMMddHHmmss");
			String str = (Math.random() * 10000) + "";
			str = str.substring(0, 4);
			name = name.substring(name.lastIndexOf("."), name.length());
			return simpleDF.format(date).toString() + str + name;
		} catch (Exception e) {
		}

		return name;
	}


	public static List getElementvalueids(String strs) {
		if (strs != null && !strs.trim().equals("")) {
			List list = null;
			try {
				list = new ArrayList();
				String str[] = Handle.getArrString(strs, "_");
				for (int i = 0; i < str.length; i++) {
					if (str[i] != null && !str[i].trim().equals("")) {
						list.add(str[i]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}
		return null;
	}

	public static String filterstring(String source, String split) {
		String[] strs = source.split(split);
		Set<String> set = new HashSet<String>();
		if (strs != null) {
			for (int i = 0; i < strs.length; i++) {
				if (!"".equals(strs[i])) {
					set.add(strs[i]);
				}
			}
		}
		StringBuffer dest = new StringBuffer();
		for (String str : set) {
			dest.append(str).append(",");
		}
		return dest.toString();
	}


	public static String Replace(String s, String s1, String s2) {
		StringBuffer stringbuffer = new StringBuffer();
		int i = s2.indexOf(s);
		if (i == -1) {
			return s2;
		}
		stringbuffer.append(s2.substring(0, i) + s1);
		if (i + s.length() < s2.length()) {
			stringbuffer.append(Replace(s, s1, s2.substring(i + s.length(), s2.length())));
		}
		return stringbuffer.toString();
	}

	public static String RetrunTX(String s) {
		return Replace("\"", "\\\"", Replace("'", "\\'", Replace("\\", "\\\\", s)));
	}

	public static String replaceLign(String str, String key) {
		return Replace(key.toLowerCase(), "<font color=red>" + key + "</font>", str.toLowerCase());
	}

	public static boolean checkObjectIsNull(Object object) {
		if (object != null && !object.toString().trim().equals("")) {
			return true;
		}
		return false;
	}

	// 检查字符串是否是整形
	public static boolean checkInt(String str) {
		try {
			Integer.parseInt(str.trim());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// 检查字符串是否是长整形
	public static boolean checkLong(String str) {
		try {
			Long.parseLong(str.trim());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static Float getFloat(String str, Float defaultValue) {
		try {
			return Float.valueOf(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float getfloat(String str) {
		try {

			DecimalFormat myformat = new DecimalFormat("######0.00");
			Number strss = myformat.parse(str.trim());
			// System.out.println(strss.floatValue());

			return strss.floatValue();
			// return Float.valueOf();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0f;
	}

	public static Float getfloat2(float str) {
		try {

			DecimalFormat myformat = new DecimalFormat("######0.00");
			Number strss = myformat.parse(str + "");
			// System.out.println(strss.floatValue());

			return strss.floatValue();
			// return Float.valueOf();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1f;
	}

	public static double getfloat3(double d) {
		try {
			BigDecimal a = new BigDecimal(d);
			BigDecimal b = a.setScale(2, 2);
			return b.doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1d;
	}

	public static float getfloat4(double d) {
		try {
			// BigDecimal a = new BigDecimal(d);
			// BigDecimal b = a.setScale(2, 2);
			// return b.floatValue();
			return new BigDecimal(d).floatValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1f;
	}

	public static float getfloat5(float d) {
		try {
			BigDecimal a = new BigDecimal(d);
			BigDecimal b = a.setScale(2, 2);
			return b.floatValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1f;
	}

	public static String getfloatstr(float num) {
		double d = div(num, 1, 2);
		return d + "";
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */

	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */

	public static Float div(float v1, float v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static float add(float v1, float v2) {
		BigDecimal b1 = new BigDecimal(Float.toString(v1));
		BigDecimal b2 = new BigDecimal(Float.toString(v2));
		return b1.add(b2).floatValue();
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double bigadd(double v1, BigDecimal v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = v2;
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的减法运算。 V1- V2
	 * 
	 * @param v1
	 *            减数
	 * @param v2
	 *            被减数
	 * @return 两个参数的差 V1- V2
	 */
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的减法运算。 V1- V2
	 * 
	 * @param v1
	 *            减数
	 * @param v2
	 *            被减数
	 * @return 两个参数的差 V1- V2
	 */
	public static float sub(float v1, float v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).floatValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param v1
	 *            乘数
	 * @param v2
	 *            被乘数
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static int mul(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.multiply(b2).intValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static float mul(float v1, float v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.multiply(b2).floatValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, 10);
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 計算在指定的一個時間內完成一個指定的數量要花費的時間/實際要印刷的數量
	 * 
	 * @param costTime
	 *            指定的時間
	 * @param number
	 *            指定的數量
	 * @param printnumber
	 *            實際要印刷的數量
	 * @return
	 */

	// 將分鐘轉化為小時
	public static String minuteTOHour(int minute) {
		String hour = null;
		if (minute > 0) {
			if (minute > 60) {
				hour = (minute / 60) + "小時" + (minute % 60) + "分";
				if ((minute / 60) > 24) {
					hour = (minute / 60) / 24 + "天" + (minute / 60) % 24 + "小時" + (minute % 60) + "分";
				}
			} else {
				hour = minute + "分";
			}
		} else {
			hour = "0分";
		}

		return hour;
	}

	// 从小到大排序 int
	public static int[] sort(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = i; j < arr.length; j++) {
				if (arr[i] > arr[j]) {
					int x = arr[i];
					arr[i] = arr[j];
					arr[j] = x;
				}
			}
		}
		return arr;
	}

	// 時間的加減 +2 表示加2天，-2表示減2天
	public static String add_minus(Date date, int how) {
		if (date != null)
			return DateToStr(new Date(date.getTime() + how * 24 * 60 * 60 * 1000));
		return null;
	}

	// 時間的加減 +2 表示加2天，-2表示減2天
	public static Date add_Date(Date date, int how) {
		if (date != null)
			return new Date(date.getTime() + how * 24 * 60 * 60 * 1000);
		return null;
	}

	/**
	 * 判断是否是节假日
	 * 
	 * @param deliveryDate
	 * @return
	 */
	// public static Date compareDate(Date deliveryDate) {
	// List<BaseHolidayDetail> lbhdlist =
	// FMX.BASEHOLIDAYDETAIL_MAP.get("baseHolidayKey");
	// if (lbhdlist != null && deliveryDate != null) {
	// for (BaseHolidayDetail baseHolidayDetail : lbhdlist) {
	// String holiday = DateToStr(baseHolidayDetail.getHolidaydate());
	// String ddate = DateToStr(deliveryDate);
	// if (holiday.equals(ddate)) {
	// long dvalue = baseHolidayDetail.getHolidaydate().getTime() -
	// deliveryDate.getTime();
	// if (dvalue > 0) {
	// return getDate(new Date(baseHolidayDetail.getHolidaydate().getTime() +
	// dvalue));
	// } else {
	// return getDate(new Date(baseHolidayDetail.getHolidaydate().getTime() -
	// dvalue));
	// }
	// }
	// // if(baseHolidayDetail.getHolidaydate().getTime()-deliveryDate.getTime()
	// // >= 0 &&
	// // baseHolidayDetail.getHolidaydate().getTime()-deliveryDate.getTime()
	// // <= (24 * 60 * 60 * 1000)){
	// // return getDate(new
	// //
	// Date(baseHolidayDetail.getHolidaydate().getTime()+(baseHolidayDetail.getHolidaydate().getTime()-deliveryDate.getTime())));
	// // }
	// }
	// }
	// return null;
	// }

	// 从小到大排序 long
	public static long[] sort(long[] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = i; j < arr.length; j++) {
				if (arr[i] > arr[j]) {
					long x = arr[i];
					arr[i] = arr[j];
					arr[j] = x;
				}
			}
		}
		return arr;
	}

	/*
	 * costTime是估計時間內 number是估計時間內完成的數量 machinnumber 實際印刷的數量
	 */

	public static int costTime(int costTime, int number, int machinnumber) {
		int ct = 0;
		double b = Handle.div(costTime, number, 10);
		double c = b * machinnumber;
		double h = Handle.div(c, 1, 0); // 將需要用的時間四舍五入
		ct = (int) h;
		return ct;
	}

	public static Integer[] getIntegers(String[] str) {
		if (str != null) {
			Integer tt[] = new Integer[str.length];
			for (int i = 0; i < str.length; i++) {
				tt[i] = getInteger(str[i]);
			}
			return tt;
		}
		return null;
	}

	public static List getIntegersList(String[] str) {
		if (str != null) {
			List list = new ArrayList();
			for (int i = 0; i < str.length; i++) {
				list.add(getInteger(str[i]));
			}
			return list;
		}
		return null;
	}

	public static Integer getInteger(String str) {
		try {
			return new Integer(str.trim());
		} catch (Exception e) {
		}
		return null;
	}

	public static Integer getInteger1(String str) {
		try {
			return new Integer(str.trim());
		} catch (Exception e) {
		}
		return -1;
	}

	public static Integer getInteger2(Object obj) {
		if (obj != null && !obj.equals("") && !obj.equals("null")) {
			return new Integer(obj.toString().trim());
		}
		return null;
	}

	public static int getInteger3(Object obj) {
		if (obj != null && !obj.equals("") && !obj.equals("null")) {
			return new Integer(obj.toString().trim()).intValue();
		}
		return 0;
	}

	public static Integer getInteger(String str, Integer def) {
		Integer rint = getInteger(str);
		if (rint != null)
			return rint;
		return def;
	}

	public static Integer[] getArrInteger(String str) {
		StringTokenizer sto = new StringTokenizer(str, ",");
		Integer rr[] = new Integer[sto.countTokens()];
		int i = 0;
		while (sto.hasMoreTokens()) {
			rr[i] = getInteger(sto.nextToken());
			i++;
		}
		return rr;
	}

	public static String strDistinct(String str) {
		String[] strArr = str.split(",");
		String strAim = ",";
		for (int i = 0; i < strArr.length; i++) {
			if (strArr[i].equals(""))
				continue;
			if (strAim.indexOf("," + strArr[i] + ",") == -1) {
				strAim = strAim + strArr[i] + ",";
			}
		}
		if (!strAim.equals(","))
			strAim = strAim.substring(1, strAim.length() - 1);
		else
			strAim = "";
		return strAim;
	}

	public static String replace(String arg) {
		if (arg == null) {
			return "";
		} else {
			byte[] bs = arg.getBytes();
			int j = 0;
			byte[] temp = new byte[bs.length];
			for (int i = 0; i < bs.length; i++) {
				if (0 < bs[i] && bs[i] <= 32) {
					// bs[i]=0;
				} else if (bs[i] == 127) {
					// bs[i]=0;
				} else if (bs[i] >= 128) {
					// bs[i]=0;
				} else {
					temp[j] = bs[i];
					// System.out.println(bs[i]);
					j++;
				}
			}
			if (j > 0) {
				return new String(temp, 0, j);
			} else {
				return "";
			}
		}
	}

	public static String[] getArrString(String str, String split) {
		StringTokenizer sto = new StringTokenizer(str, split);
		String rr[] = new String[sto.countTokens()];
		int i = 0;
		while (sto.hasMoreTokens()) {
			rr[i] = sto.nextToken();
			i++;
		}
		List list = new ArrayList();
		for (int j = 0; j < rr.length; j++) {
			list.add(rr[j]);
		}
		for (int j = 0; j < list.size(); j++) {
			for (int k = 0; k < j; k++) {
				if (list.get(j).toString().equalsIgnoreCase(list.get(k).toString())) {
					list.remove(j);
					j--;
					break;
				}
			}
		}
		String kkr[] = new String[list.size()];
		list.toArray(kkr);
		return kkr;
	}

	public static List<String> getArrList(String str) {
		List<String> list = new ArrayList();
		if (str != null) {
			if (str.indexOf(",") != -1) {
				String rr[] = str.split(",");
				for (int i = 0; i < rr.length; i++) {
					list.add(trim(rr[i]));
				}
			} else {
				list.add(str);
			}
		}
		return list;
	}

	public static String[] getArrString(String str) {
		if (trim(str) == null) {
			String[] strnull = { null };
			return strnull;
		}
		return getArrString(str, ",");
	}

	public static boolean[] getArrboolean(String str) {
		StringTokenizer sto = new StringTokenizer(str, ",");
		boolean rr[] = new boolean[sto.countTokens()];
		int i = 0;

		while (sto.hasMoreTokens()) {
			rr[i] = StrToBoolean(sto.nextToken()).booleanValue();
			i++;
		}
		return rr;
	}

	public static Short getShort2(String str) {
		try {
			return new Short(str);
		} catch (Exception e) {
		}
		return new Short("0");
	}

	public static short getShort(String str) {
		return getShort2(str).shortValue();
	}

	public static Byte getByte3(String str) {
		try {
			return new Byte(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static Byte getByte2(String str) {
		try {
			return new Byte(str);
		} catch (Exception e) {

		}
		return new Byte("0");
	}

	public static byte getByte(String str) {
		try {
			return new Byte(str).byteValue();
		} catch (Exception e) {
		}
		return new Byte("0").byteValue();
	}

	public static boolean isDate(String str) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd");

			Date d = (Date) simpleDF.parse(str);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Date getDate4(String str) {
		try {
			if (str.length() > 19) {
				str = str.substring(0, 19);
			}
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return (Date) simpleDF.parse(str);
		} catch (Exception e) {
			return getDate2(str);
		}
	}

	public static Date getDate3(String str) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return (Date) simpleDF.parse(str);
		} catch (Exception e) {
			return getDate2(str);
		}
	}

	/**
	 * 格式化date类型日期
	 * 
	 * @author oyxl
	 * @param date
	 * @return date
	 */
	public static Date getDate(Date date) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datetime = simpleDF.format(date);
			return simpleDF.parse(datetime);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * @方法说明:
	 * @author oyxl
	 * @createtime 2015年10月7日 上午10:09:27
	 * @param date
	 * @return
	 */
	public static Date getDate2(Date date) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd");
			String datetime = simpleDF.format(date);
			return simpleDF.parse(datetime);
		} catch (Exception e) {
		}
		return null;
	}

	// 2008/12/4 上午 12:00:00
	public static Date getDate31_(String str) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			return (Date) simpleDF.parse(str);
		} catch (Exception e) {
			return getDate2(str);
		}
	}

	public static Date getDate31(String str) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return (Date) simpleDF.parse(str);
		} catch (Exception e) {
			return getDate2(str);
		}
	}

	public static Date getDate2(String str) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd");
			return (Date) simpleDF.parse(str);
		} catch (Exception e) {
			return new Date();
		}
	}

	public static Date getDate2_(String str) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy/MM/dd");

			return (Date) simpleDF.parse(str);
		} catch (Exception e) {
		}
		return null;
	}

	public static Date getDate(String str) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd");

			return (Date) simpleDF.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date(System.currentTimeMillis());
	}

	public static String getYMD(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy年MM月dd日");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}

		return "";
	}

	public static String getYMD1(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}

		return "";
	}

	public static String getYMDHHMM(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}

		return "";
	}

	public static String DateToStr3(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return simpleDF.format(d).toString();
		} catch (Exception e) {

		}
		return "";
	}

	public static String getMMDDHHMM(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("HH:mm");
			return simpleDF.format(d).toString();
		} catch (Exception e) {

		}
		return "";
	}

	public static String getHHMM(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("HH:mm");
			return simpleDF.format(d).toString();
		} catch (Exception e) {

		}
		return "";
	}

	public static String getHHMMSS(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("HH:mm:ss");
			return simpleDF.format(d).toString();
		} catch (Exception e) {

		}
		return "";
	}

	public static String DateToStr(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}

		return "";
	}

	public static String DateToStr4(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyyMMddHHmmss");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}

		return "";
	}

	public static String DateToStr41(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("HH-mm-ss");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}

		return "";
	}

	public static String DateToStr7(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}

		return "";
	}

	public static String DateToStr8(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}

		return "";
	}

	//
	// public static String DateToStr5(Date d) {
	// try {
	// SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// return simpleDF.format(d).toString();
	// } catch (Exception e) {
	// }
	//
	// return "";
	// }

	public static String IntToStr(Integer i) {
		try {
			return i.toString();
		} catch (Exception e) {
		}
		return "";
	}

	public static String ShortToStr(short s) {
		try {
			Short ss = new Short(s);
			return ss.toString();
		} catch (Exception e) {
		}
		return "";
	}

	public static String ByteToStr(byte b) {
		try {
			Byte bb = new Byte(b);
			return bb.toString();
		} catch (Exception e) {
		}
		return "";
	}


	public static Boolean StrToBoolean(String str) {
		try {
			if (str.equalsIgnoreCase("1")) {
				return new Boolean(true);
			} else if (str.equalsIgnoreCase("0")) {
				return new Boolean(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// asp中轉日期格式
	public static Date getAspDate(String str) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy/MM/dd");
			return (Date) simpleDF.parse(str);
		} catch (Exception e) {
			return getAspDate2(str);
		}
	}

	public static Date getAspDate2(String str) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd");
			return (Date) simpleDF.parse(str);
		} catch (Exception e) {

		}
		return null;
	}

	public static Date getAspDate3(String str) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyyMMddHH");
			return (Date) simpleDF.parse(str);
		} catch (Exception e) {

		}
		return null;
	}

	// 得到日期最大值
	public static int getmaxDay(int year, int month) {
		boolean value = Leep(year);
		int result = 0;
		if (month % 2 != 0) {
			return 31;
		} else {
			if (value) {
				if (month == 2) {
					result = 29;
				} else {
					result = 30;
				}
			} else {
				if (month == 2) {
					result = 28;
				} else {
					result = 30;
				}
			}
		}
		return result;
	}

	public static boolean Leep(int year) {
		boolean value;
		if ((year % 4) != 0)
			value = false;
		else {
			if ((year % 100) != 0)
				value = true;
			else {
				if ((year % 400) != 0)
					value = false;
				else
					value = true;
			}
		}
		return value;
	}

	public static String DateToStr(Date d, Integer mm, Integer dd) {
		try {
			int y = getInteger(getYYYY(d));
			int m = getInteger(getMM(d));
			int di = getInteger(getDD(d));
			if (mm != null) {
				m = m + mm;
			}
			if (dd != null) {
				di = di + dd;
			}
			Date tempdate = getDate2(y + "-" + m + "-" + di);
			return DateToStr(tempdate);
		} catch (Exception e) {
		}

		return "";
	}

	public static String getHH(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("HH");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}

		return "";
	}

	public static String getDD(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("dd");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}

		return "";
	}

	public static String getMM(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("MM");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}

		return "";
	}

	public static String getYYYY(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}
		return "";
	}

	public static String getMD(Date d) {
		try {
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyyMMdd");
			return simpleDF.format(d).toString();
		} catch (Exception e) {
		}
		return "";
	}

	public static void errorlog(Log log, Class clazz, Exception e) {
		if (log.isErrorEnabled()) {
			// e.printStackTrace();
			log.error(clazz + " : " + e.getMessage());
		}
	}

	public static String getIP(String path) {
		if (path != null && !path.trim().equals("")) {
			int c = path.lastIndexOf("192");

			if (c != -1) {
				String temp = path.substring(c, path.length());

				int indexOf = 10000;
				if (temp != null) {
					if (temp.indexOf("\\") != -1) {
						if (temp.indexOf("\\") < indexOf)
							indexOf = temp.indexOf("\\");
					}
					if (temp.indexOf("/") != -1) {
						if (temp.indexOf("/") < indexOf)
							indexOf = temp.indexOf("/");
					}

				}
				if (temp != null && indexOf != -1 && indexOf != 10000) {
					temp = temp.substring(0, indexOf);
				}
				return temp;
			}

		}

		return "";
	}

	/**
	 * jiang
	 * 
	 * @param temp
	 * @return
	 */
	public static String getSize(double fileSize) {
		if (fileSize > 1000) {
			fileSize = div(fileSize, 1024.00, 2);
			if (fileSize >= 1000) {
				fileSize = div(fileSize, 1024.00, 2);
				if (fileSize >= 1000) {
					fileSize = div(fileSize, 1024.00, 2);
					return fileSize + " GB";
				} else {
					return fileSize + " MB";
				}
			} else {
				return fileSize + " KB";
			}
		} else {
			return fileSize + " B";
		}
	}

	public static boolean isGOT(String publishno) {

		if (publishno.toLowerCase().lastIndexOf("gto") != -1) {
			return true;
		}
		return false;
	}


	/**
	 * 判断字符串是否是数字
	 * 
	 * @author ycw
	 * @param str
	 * @return
	 */
	public static Boolean isNumberic(String str) {
		boolean flag = false;
		Pattern pattern = Pattern.compile("[0-9]*");
		if (null != str) {
			Matcher isNum = pattern.matcher(str.trim());
			if (isNum.matches())
				flag = true;
		}

		return flag;
	}

	/**
	 * 將日期/時間值格式化為字符串
	 * 
	 * @param date
	 *            待格式化的日期/時間值
	 * @param pattern
	 *            輸出格式
	 * @return 格式化的时间字符串。
	 */
	public static String formatDate(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 將日期/時間值格式化為字符串,默認輸出格式為yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 *            待格式化的日期/時間值
	 * @return 格式化的时间字符串。
	 */
	public static String formatDate(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 计算字符串MD5值
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String getMD5String(String str) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		StringBuffer sb = new StringBuffer(32);

		md.update(str.getBytes("UTF-8"));

		byte[] result = md.digest();

		for (byte b : result) {
			int i = b & 0xff;
			if (i < 0xf) {
				sb.append("0");
			}
			sb.append(Integer.toHexString(i));
		}
		return sb.toString();
	}

	/**
	 * 獲得要添加的Id
	 * 
	 * @param ids
	 *            数据库的id
	 * @param checkids
	 *            选中的id
	 * @return
	 */
	public static List add_checkid(List ids, List checkids) {
		List<Object> lst = new ArrayList<Object>();
		if (ids == null) {
			return checkids;
		} else {
			Map<Object, Object> map = new HashMap<Object, Object>();
			for (int i = 0; i < ids.size(); i++) {
				map.put(ids.get(i), ids.get(i));
			}
			for (int i = 0; i < checkids.size(); i++) {
				if (map != null && map.get(checkids.get(i)) == null) {
					lst.add(checkids.get(i));
				}
			}
		}
		return lst;
	}

	/**
	 * 獲得要删除的Id
	 * 
	 * @param ids
	 *            数据库的id
	 * @param checkids
	 *            选中的id
	 * @return
	 */
	public static List del_checkid(List ids, List checkids) {
		List<Object> lst = new ArrayList<Object>();
		if (ids != null && checkids == null || checkids.size() == 0) {
			return ids;
		}
		if (ids == null) {
			return null;
		} else {
			Map<Object, Object> map = new HashMap<Object, Object>();
			for (int i = 0; i < ids.size(); i++) {
				map.put(ids.get(i), ids.get(i));
			}
			for (int i = 0; i < checkids.size(); i++) {
				if (map != null && map.get(checkids.get(i)) != null) {
					map.remove(checkids.get(i));
				}
			}
			if (map != null) {
				for (Iterator it = map.values().iterator(); it.hasNext();) {
					Integer id = (Integer) it.next();
					lst.add(id);
				}
			} else {
				return checkids;
			}
		}
		return lst;
	}

	/**
	 * 獲得要添加的Id
	 * 
	 * @param ids
	 *            数据库ID
	 * @param checkids
	 *            选中ID
	 * @return
	 */
	public static String getAddCheck(String ids, String[] checkids) {
		String include = "";
		ids = "," + ids;
		if (checkids != null) {
			for (int i = 0; i < checkids.length; i++) {
				if (ids.indexOf(("," + checkids[i] + ",")) < 0) {
					include = include + checkids[i] + ",";
				}
			}
		}
		if (include == null && include.equals(""))
			include = null;
		return include;
	}

	/**
	 * 獲得要刪除的Id
	 * 
	 * @param ids
	 *            数据库ID
	 * @param checkids
	 *            选中ID
	 * @return
	 */
	public static String getDelCheck(String ids, String[] checkids) {
		if (ids == null || ids.equals("")) {
			return null;
		}
		String[] checkidssplit = ids.split(",");
		String outclude = "";
		for (int i = 0; i < checkidssplit.length; i++) {
			String same = "0";
			if (!checkidssplit[i].equals("")) {
				for (int j = 0; j < checkids.length; j++) {
					if (checkids[j].equals(checkidssplit[i])) {
						same = "1";
						break;

					}
				}
			}
			if (!checkidssplit[i].equals("")) {
				if (same.equals("0")) {
					outclude = outclude + checkidssplit[i] + ",";
				}
			}
		}
		if (outclude != null && !outclude.equals(""))
			outclude = "," + outclude;
		return outclude;
	}

	// ----------------------------------------------------------------------------------------------------

	/**
	 * 保留n位小数
	 * 
	 * @param val
	 *            要操作的Double值
	 * @param retainNum
	 *            保留的位数
	 * @return
	 */
	public static String retainDecimal(Double val, int retainNum) {
		if (val == null)
			return null;
		StringBuilder builder = new StringBuilder();
		builder.append("#.");
		for (int i = 0; i < retainNum; i++) {
			builder.append("0");
		}
		DecimalFormat decimalFormat = new DecimalFormat(builder.toString());
		return decimalFormat.format(val);
	}

	/**
	 * @author oyxl 生成9位隨機密碼，前4位為大小寫字母，后4位為數字，最後一位為特殊符號
	 * @return
	 */
	public static String getRandomPassword() {
		StringBuffer password = new StringBuffer();
		Random ran = new Random();
		String enChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 0; i < 4; i++) {
			password.append(enChar.charAt(ran.nextInt(enChar.length() - 1)));
		}
		String numberChar = "0123456789";
		for (int j = 0; j < 4; j++) {
			password.append(numberChar.charAt(ran.nextInt(numberChar.length() - 1)));
		}
		String otherChar = "*?><!@#$%^&";
		password.append(otherChar.charAt(ran.nextInt(otherChar.length() - 1)));

		return password.toString();
	}

	/**
	 * @author oyxl 生成N位可能包含大小字母數字的隨機密碼
	 * @param Integer
	 *            n
	 */

	public static String getRandomPassword(Integer n) {
		StringBuffer randNum = new StringBuffer();
		Random ran = new Random();
		String enChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 0; i < n; i++) {
			randNum.append(enChar.charAt(ran.nextInt(enChar.length() - 1)));
		}
		return randNum.toString();
	}

	/**
	 * 
	 * @方法說明 生成一个四位数字的验证码，吉利一点.....
	 * @author yrf
	 * @createTime 2016/3/30 上午11:16:31
	 * @copyright kimleysoftServer
	 */
	public static String getVeriCode(Integer n) {
		String base = "01234567896886668868682367896886686";
		StringBuffer result = new StringBuffer();
		int index = 0;
		for (int i = 0; i < n; i++) {
			index = (int) (Math.random() * base.length());
			result.append(base.charAt(index));
		}
		return result.toString();
	}

	/**
	 * @author oyxl 复制版本，重新生成版本号
	 * @param versionno
	 */
	public static String getNewVersionno(String versionno) {
		String newVersionno = "";
		if (versionno.indexOf("_CP") != -1) {
			String verStr = versionno.substring(0, versionno.indexOf("_") + 3);
			Integer num = Integer.parseInt(versionno.substring(versionno.indexOf("_") + 3)) + 1;
			String numStr = String.valueOf(num);
			StringBuffer sbZero = new StringBuffer();
			for (int i = numStr.length(); i < 3; i++) {
				sbZero.append("0");
			}
			newVersionno = verStr + sbZero.toString() + numStr;
		} else {
			newVersionno = versionno + "_CP001";
		}
		return newVersionno;
	}

	public static Double getDouble(String str, Double defaultValue) {
		try {
			return Double.valueOf(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Double getDouble2(Float f) {
		try {
			return Double.valueOf(f);
		} catch (Exception e) {
			return 0d;
		}
	}

	/**
	 * @方法說明: 获取一个字符中的数字
	 * @author chenJian E-mail: test_t@163.COM
	 * @createTime 創建時間：2013/11/26 下午4:37:16
	 * @copyright e-print
	 * @param str
	 * @return
	 */
	public static List<Double> getStringOfNumber(String str) {
		List<Double> list = new ArrayList<Double>();
		if (isNull(str)) {
			return list;
		}
		String regex = "\\d*[.]\\d*|\\d*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			if (!"".equals(m.group())) {
				// System.out.println("come here:" + m.group());
				list.add(getDouble(m.group(), 0d));
			}
		}
		return list;
	}

	public static Float getFristFloatOfNumber(String str) {
		String regex = "\\d*[.]\\d*|\\d*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			if (!"".equals(m.group())) {
				return getFloat(m.group(), -1f);
			}
		}
		return -1f;
	}

	public static String getLikeString(String likeString) {
		StringBuffer sb = new StringBuffer();
		String regex = "[\\u2E80-\\u9FFF]|\\w|\\d*[.]\\d*|\\d*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(likeString);
		while (m.find()) {
			if (!"".equals(m.group())) {
				sb.append(m.group());
			}
		}
		return sb.toString();
	}

	/**
	 * @方法說明: 去掉對象中字符串的前後空格
	 * @author chenJian E-mail: test_t@163.COM
	 * @createTime 創建時間：2013-12-23 下午5:44:49
	 * @copyright e-print
	 * @param obj
	 * @return
	 */
	public static Object objTirm(Object obj) {
		try {
			java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
			for (java.lang.reflect.Field field : fields) {
				String name = field.getName();
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				String type = field.getGenericType().toString();
				if (type.equals("class java.lang.String")) {
					Method getM = obj.getClass().getMethod("get" + name);
					String value = (String) getM.invoke(obj);
					Class[] parameterTypes = new Class[1];
					parameterTypes[0] = String.class;
					if (!StringUtils.isNullOrEmpty(value)) {
						Method setM = obj.getClass().getMethod("set" + name, parameterTypes);
						setM.invoke(obj, new String[] { value.trim() });
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * @方法说明:所有類型轉成字符串類型
	 * @author oyxl
	 * @creatime 2014年8月11日上午10:09:10
	 * @param value
	 * @return
	 */
	public static String objToString(Object value) {
		if (value != null) {
			if (value instanceof Short) {
				Short aa = (Short) value;
				return aa == 0 ? "false" : "true";
			}
			return value.toString();
		}
		return "";
	}

	public static void sortMap(Map<String, String> map) {
		List<Map.Entry<String, String>> mappingList = new ArrayList<Map.Entry<String, String>>(map.entrySet());
		// 通过比较器实现比较排序
		Collections.sort(mappingList, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> mapping1, Map.Entry<String, String> mapping2) {
				return mapping1.getKey().compareTo(mapping2.getKey());
			}
		});
	}

	public static String encoderBASE64(String s) {
		if (s == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
	}

	/**
	 * 获取当前日期的前后几天,返回格式化后的日期
	 * 
	 * @author yrf
	 * @createTime 2016/4/29上午9:13:01
	 * @copyright kimleysoftserver
	 * @param day
	 * @return
	 */
	public static String beforeDateStr(int day) {
		java.util.Calendar rightNow = java.util.Calendar.getInstance();
		java.text.SimpleDateFormat sim = new java.text.SimpleDateFormat("yyyy-MM-dd");
		// rightNow.add(java.util.Calendar.DAY_OF_MONTH, -day);
		rightNow.add(java.util.Calendar.DAY_OF_MONTH, day);
		// 进行时间转换
		String date = sim.format(rightNow.getTime()) + " 00:00:00";
		return date;
	}

	/**
	 * 获取当前时间的前后几天
	 * 
	 * @author yrf
	 * @createTime 2016/5/5下午4:15:35
	 * @copyright kimleysoftserver
	 * @param day
	 * @return
	 */
	public static Date beforeDate(int day) {
		java.util.Calendar rightNow = java.util.Calendar.getInstance();
		rightNow.add(java.util.Calendar.DAY_OF_MONTH, day);
		return rightNow.getTime();
	}

	/**
	 * 获取当前时间的前后几个月
	 * 
	 * @author yrf
	 * @createTime 2016/5/28下午3:52:07
	 * @copyright kimleysoftserver
	 * @param month
	 * @return
	 */
	public static Date beforeMonth(int month) {
		java.util.Calendar rightNow = java.util.Calendar.getInstance();
		rightNow.add(java.util.Calendar.MONTH, month);
		return rightNow.getTime();
	}

	/**
	 * 获取指定日期的前后几天
	 * 
	 * @author yrf
	 * @createTime 2016/7/29下午4:25:45
	 * @copyright kimleysoftserver
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date beforeDateSel(Date d, Integer day) {
		java.util.Calendar rightNow = java.util.Calendar.getInstance();
		rightNow.setTime(d);
		rightNow.add(java.util.Calendar.DAY_OF_MONTH, day);
		return rightNow.getTime();
	}

	/**
	 * lyw 返回平年，闰年//能被4或100或400整除的是闰年
	 */
	public static boolean leapYear(int year) {
		boolean leap;
		if (year % 4 == 0) {
			if (year % 100 == 0) {
				if (year % 400 == 0)
					leap = true;
				else
					leap = false;
			} else
				leap = true;
		} else
			leap = false;
		return leap;
	}

	/**
	 * lyw 返回當前月的最後一天 2月份天數，闰年2月29天，平年2月28天
	 */
	public static String getNowMonLastDay() throws ParseException {
		String strY = null;
		String strZ = null;
		boolean leap = false;
		int x;
		int y;
		Calendar localTime = Calendar.getInstance();
		x = localTime.get(Calendar.YEAR);
		y = localTime.get(Calendar.MONTH) + 1;
		if (y == 1 || y == 3 || y == 5 || y == 7 || y == 8 || y == 10 || y == 12) {
			strZ = "31";
		}
		if (y == 4 || y == 6 || y == 9 || y == 11) {
			strZ = "30";
		}
		if (y == 2) {
			leap = leapYear(x);
			if (leap) {
				strZ = "29";
			} else {
				strZ = "28";
			}
		}
		strY = y >= 10 ? String.valueOf(y) : ("0" + y);
		return x + "-" + strY + "-" + strZ;
	}

	/**
	 * lyw 返回任意月的最後一天 2月份天數，闰年2月29天，平年2月28天
	 */
	public static String getAnyLastDay(Calendar a, int month) throws ParseException {
		String strY = null;
		String strZ = null;
		boolean leap = false;
		int x;
		int y;
		// Calendar localTime = Calendar.getInstance();
		// x = localTime.get(Calendar.YEAR);
		x = a.get(Calendar.YEAR);
		// y = localTime.get(Calendar.MONTH) + 1;
		y = month;
		if (y == 1 || y == 3 || y == 5 || y == 7 || y == 8 || y == 10 || y == 12) {
			strZ = "31";
		}
		if (y == 4 || y == 6 || y == 9 || y == 11) {
			strZ = "30";
		}
		if (y == 2) {
			leap = leapYear(x);
			if (leap) {
				strZ = "29";
			} else {
				strZ = "28";
			}
		}
		strY = y >= 10 ? String.valueOf(y) : ("0" + y);
		return x + "-" + strY + "-" + strZ + " 23:59:59";
	}

	/**
	 * 得到下個月的莫一天 lyw
	 */
	public static String getNextMonday(Integer day) throws ParseException {
		int x;
		int y;
		String time = "";
		Calendar localTime = Calendar.getInstance();
		String strY = null;
		String strD = null;
		x = localTime.get(Calendar.YEAR);
		y = localTime.get(Calendar.MONTH) + 2;
		strY = y >= 10 ? String.valueOf(y) : ("0" + y);
		strD = day >= 10 ? String.valueOf(day) : ("0" + day);
		return x + "-" + strY + "-" + strD;
	}

	/**
	 * 
	 * @author lyw
	 * @date 2017年11月8日
	 * @version 1.0.0
	 * @param date1
	 *            最后付款时间
	 * @param date2
	 *            当前时间（当前时间必须大于最后付款时间）
	 * @return
	 */
	public static int getDateDifference(Date date1, Date date2) {

		Calendar now = Calendar.getInstance();
		now.setTime(date1);

		Calendar overdue = Calendar.getInstance();
		overdue.setTime(date2);

		boolean isafter = now.before(overdue);
		if (!isafter) {
			return -1;
		}

		int day1 = overdue.get(Calendar.DAY_OF_YEAR);
		int day2 = now.get(Calendar.DAY_OF_YEAR);

		int year1 = overdue.get(Calendar.YEAR);
		int year2 = now.get(Calendar.YEAR);

		if (year1 != year2) {// 不同年份的
			int timediscount = 0;

			for (int i = year1; i < year2; i++) {
				if (i % 4 == 0 && i % 100 == 0 || i % 400 == 0) {
					timediscount += 366;
				} else {
					timediscount += 365;
				}
			}

			return timediscount + (day1 - day2);
		} else {// 相同年份的
			return day1 - day2;
		}

	}

	/**
	 * 获取类中所有的属性名
	 * 
	 * @author yrf
	 * @createTime 2016/5/19上午9:45:04
	 * @copyright kimleysoftserver
	 * @param o
	 * @return
	 */
	public static List<String> FiledNameGet(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		List<String> filedlist = new ArrayList();
		for (int i = 0; i < fields.length; i++) {
			filedlist.add(fields[i].getName());
		}
		return filedlist;
	}

	public static String replaceParamter(String templateContent, String paramters, Object paramterValues) {
		if (templateContent.indexOf(paramters) != -1) {
			templateContent = templateContent.replace(paramters, paramterValues.toString());
		}
		return templateContent;
	}

	/**
	 * 绝对值进位（正值变大,副值变小)
	 * 
	 * @param value
	 *            必须为数值型
	 * @param scale
	 * @return
	 */
	public static <T> T roundUp(T value, int scale) {
		Double val = Double.parseDouble(value.toString());
		BigDecimal bdval = new BigDecimal(val);
		return (T) bdval.setScale(scale, BigDecimal.ROUND_UP);
	}

	/**
	 * 绝对值舍位(正值变小,副值变大)
	 * 
	 * @param value
	 *            必须为数值型
	 * @param scale
	 * @return
	 */
	public static <T> T roundDown(T value, int scale) {
		Double val = Double.parseDouble(value.toString());
		BigDecimal bdval = new BigDecimal(val);
		return (T) bdval.setScale(scale, BigDecimal.ROUND_DOWN);
	}

	/**
	 * 四舍五入
	 * 
	 * @param value
	 *            必须为数值型
	 * @param scale
	 * @return
	 */
	public static <T> T roundHalfUp(T value, int scale) {
		Double val = Double.parseDouble(value.toString());
		BigDecimal bdval = new BigDecimal(val);
		return (T) bdval.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 向上进位（值变大）
	 * 
	 * @param value
	 *            必须为数值型
	 * @param scale
	 * @return
	 */
	public static <T> T roundCeiling(T value, int scale) {
		Double val = Double.parseDouble(value.toString());
		BigDecimal bdval = new BigDecimal(val);
		return (T) bdval.setScale(scale, BigDecimal.ROUND_CEILING);
	}

	/**
	 * 向下舍位(值变小)
	 * 
	 * @param value
	 *            必须为数值型
	 * @param scale
	 * @return
	 */
	public static <T> T roundFloor(T value, int scale) {
		Double val = Double.parseDouble(value.toString());
		BigDecimal bdval = new BigDecimal(val);
		return (T) bdval.setScale(scale, BigDecimal.ROUND_FLOOR);
	}

	/**
	 * 简体转为繁体
	 * 
	 * @return
	 */
	public static String toHkChinaese(String text) {
		String tradiStr = "";
		if (null != text) {
			tradiStr = ZHConverter.convert(text, ZHConverter.TRADITIONAL);// SIMPLIFIED繁转简 TRADITIONAL
		} else {
			return "";
		}
		return tradiStr;
	}
	

	public static float parse(float f) {
		float result = (int) Math.ceil(f);
		float singler = result % 10;
		if (singler >= 1) {
			result = result + 10 - singler;
		}
		return result;
	}


	/**
	 * 逗号分隔的字符串匹配
	 * 
	 * @param sourceString
	 *            源字符串
	 * @param matchString
	 *            匹配的字符串
	 * @return Boolean
	 */
	public static Boolean matchSameString(String sourceString, String matchString) {
		if (sourceString != null && matchString != null) {
			String[] sourceStrings = sourceString.split(",");
			for (String string : sourceStrings) {
				if (string.equals(matchString)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 2个日期間隔是否相差1天
	 * 
	 * @param d1
	 * @param d2
	 * @return d1 - d2 = ? 天
	 * @throws Exception
	 */

	/**
	 * 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @return
	 */
	public static String decrypt(String contentString) {
		try {
			if (contentString != null) {
				KeyGenerator kgen = KeyGenerator.getInstance("AES");// 实例化AES密钥生成器
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				secureRandom.setSeed("kimleysoft".getBytes("UTF-8"));
				kgen.init(128, secureRandom);// 根据密钥明文初始化128位密钥生成器
				SecretKey secretKey = kgen.generateKey();// 生成密钥
				byte[] enCodeFormat = secretKey.getEncoded();// 获取密钥字节信息RAW
				SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 创建密钥空间
				Cipher cipher = Cipher.getInstance("AES");// 根据AES加密类型实例化加密算法对象
				cipher.init(Cipher.DECRYPT_MODE, key);// 根据类型（加密或解密）与密钥初始化加密算法对象
				byte[] content = parseHexStr2Byte(contentString);// 将16进制密文字符串转换为字节数组
				byte[] result = cipher.doFinal(content);// 解密
				return new String(result, "UTF-8");// 转换明文字符串
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return
	 */
	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	/**
	 * 获取某月的最后一天
	 * 
	 * @Title:getLastDayOfMonth @Description: @param:@param year @param:@param
	 * month @param:@return @return:String @throws
	 */
	public static Date getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		// 设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}
	
	
	/**
	 * 设置开始时间yyyy-mm-dd HH:mm:ss
	 * 例子
	 * month 当前月份加上多少月 “+3” ：   当前月减去多少月  “-3”
	 * day 当前日加上 多少天“+3” ：   当前日减去多少天  “-3”
	 * 不传填null
	 * 最终返回  YYYY-mm-dd 00 00 00
	 */
	public static Date setBeginDate(Integer month,Integer day) throws Exception {
//		查询的开始时间(前3个月购物车)
		Calendar start = Calendar.getInstance();
		if(month != null){
			start.add(Calendar.MONTH, month);
		}
		if(day != null){
			start.add(Calendar.DATE, day);
		}
		start.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DATE), 00, 00, 00);
		
		return start.getTime();
	}
	
	public static Short changeShort(Byte obj) {
		Short data=null;
		
		if(null!=obj) {
			data=obj.shortValue();
		}
		
		return data;
	}
}
