package com.xinwei.ue.core.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.os.SystemClock;
import android.util.Log;

public class PerformanceUtil {
	
	private static final String TAG = PerformanceUtil.class.getSimpleName();
  // 单例对象
  private static PerformanceUtil instance;
  // 记录Tag,与时间段
  private Map<String, HashMap<Integer, Long>> maps;
  //
  private HashMap<String, Integer> orderNum;

  private PerformanceUtil() {
    maps = new HashMap<String, HashMap<Integer,Long>>();
    orderNum = new HashMap<String, Integer>();
  }

  public synchronized static PerformanceUtil getInstance() {
    if (instance == null) {
      instance = new PerformanceUtil();

    }
    return instance;
  }

  /**
   * 打印耗时
   * 
   * @param tag 自定义标签,用于过滤
   *
   */
  public void LogTime(String tag) {
    LogTime(tag, "");
  }
  /**
   * 打印耗时
   * 
   * @param tag 自定义标签,用于过滤
   *
   */
  public void LogTime(String tag, String secondTag) {
    // 获取时间数组
     HashMap<Integer, Long> array = maps.get(tag);
    if (array == null) {
      array = new HashMap<Integer, Long>();
    }
    int length = array.size();
    // 记录当前时间戳
    array.put(length, SystemClock.elapsedRealtime());
    maps.put(tag, array);
    // 打印时间差
    /* 判断数组大于两个输出结果 */
    if (length < 1) {
      return;
    }
    long cost = array.get(length) - array.get(length - 1);
    if(length > 100 && length % 100 == 0) {
    	long totalCost = array.get(length) - array.get(length - 100);
    	Log.i(tag, String.format("100步总耗时: %d, 平均耗时: %d!", totalCost, (totalCost / 100)));
    }
    String strLog = String.format("发生在 %s  从第%d步 到 第%d步,总共耗时:%d 毫秒 !", secondTag, length, length + 1, cost);
    Log.i(tag, strLog);
  }
  /**
   * 查看调用者相关信息
   */
  public static void getCaller() {
	  getCaller(TAG);
  }
  /**
   * 查看调用者相关信息
   * @param tag
   */
  public static void getCaller(String tag) {
	  int i;
	  StackTraceElement stack[] = new Throwable().getStackTrace();
	  for (i = 0; i < stack.length; i++) {
		StackTraceElement ste = stack[stack.length - i - 1];
		String strA = String.format("Caller's name is: %s,\tcall in : %s(...),\tThe line num :%d.",
				ste.getClassName(),
				ste.getMethodName(),
				ste.getLineNumber());
		Log.d(tag, strA);
	}
  }
  
  /**
   * 打印方法调用过程
   * @param TAG
   */
  public void LogOrderLife(String TAG) {
	  int num = (orderNum.get(TAG) == null) ? 0 : orderNum.get(TAG);
	  StackTraceElement stact[] = new Throwable().getStackTrace();
	  String log = String.format("callNum:%d,%s(),by %s,in %d line!", num, stact[1].getMethodName(), stact[1].getClassName(), stact[1].getLineNumber());
	  Log.d(TAG, log);
	  num++;
	  orderNum.put(TAG, num);
  }
  /**
   * 打印当前模式字符串
   * @param context
   * @return
   */
  public static String getModeString(Context context) {
	  AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
  	switch (am.getMode()) {
		case AudioManager.MODE_CURRENT:
			return "AudioManager.MODE_CURRENT";
		case AudioManager.MODE_IN_CALL:
			return "AudioManager.MODE_IN_CALL";
		case AudioManager.MODE_IN_COMMUNICATION:
			return "AudioManager.MODE_IN_COMMUNICATION";
		case AudioManager.MODE_INVALID:
			return "AudioManager.MODE_INVALID";
		case AudioManager.MODE_NORMAL:
			return "AudioManager.MODE_NORMAL";
		case AudioManager.MODE_RINGTONE:
			return "AudioManager.MODE_RINGTONE";
		default:
			return "none";
		}
  }
}
