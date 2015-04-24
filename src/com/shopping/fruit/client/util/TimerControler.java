/**
 *
 * Copyright 2014 YunRang Technology Co. Ltd., Inc. All rights reserved.
 *
 * @Author : jishu
 *
 * @Description :
 *
 */

package com.shopping.fruit.client.util;

import android.os.Handler;
import android.os.Looper;

/**
 * 倒计时使用
 *
 */
public class TimerControler implements Runnable {
  private long mDuration = -1l;
  private boolean mIsStop = false;
  private static Handler mHandler;
  private OnChangeDurationLinter mOnChangeDurationLinter;
  private long mDelay = 1000;
  private static TimerControler sTimerControler;
  private boolean isTimeOut = false;
  private int mIndex = -1;
  private String mScoreMe;
  private String mScoreOther;

  public static TimerControler getInstance() {
    if (sTimerControler == null) {
      sTimerControler = new TimerControler();
    }
    sTimerControler.stop();
    return sTimerControler;
  }

  /**
   * 毫秒
   *
   * @param mDuration
   */
  private TimerControler() {
    if (mHandler == null) {
      if (Looper.myLooper() != Looper.getMainLooper()) {
        throw new RuntimeException("please init in main ui thread !");
      }
      mHandler = new Handler();
    }
  }

  /**
   * 获取剩余时间
   *
   * @return
   */
  public long getDuration() {
    return this.mDuration;
  }

  /**
   * 设置剩余时间
   *
   * @param mDuration
   */
  public void setDuration(long mDuration) {
    this.mDuration = mDuration;
  }

  public void start() {
    this.mIsStop = false;
    this.isTimeOut=false;
    mHandler.removeCallbacks(this);
    mHandler.postDelayed(this, this.mDelay);
  }

  public void setDelayTime(long delay) {
    this.mDelay = delay;
  }

  @Override
  public void run() {
    if (!this.mIsStop) {
      this.modifyDuration();
    } else if (this.mOnChangeDurationLinter != null) {
      this.mOnChangeDurationLinter.stop();
    }
  }

  private void modifyDuration() {
    this.mDuration -= this.mDelay;
    if (this.mDuration >= 0) {
      mHandler.postDelayed(this, this.mDelay);
      if (this.mOnChangeDurationLinter != null) {
        this.mOnChangeDurationLinter.changeDuration(this.mDuration);
      }
    } else if (this.mOnChangeDurationLinter != null) {
      this.isTimeOut = true;
      this.mOnChangeDurationLinter.timeOut();
    }
  }

  public static void releaseInstance() {
    if (sTimerControler != null) {
      sTimerControler.stop();
      sTimerControler = null;
    }
  }

  public boolean isTimeOut() {
    return this.isTimeOut;
  }

  public int getIndex() {
    return this.mIndex;
  }

  public void setIndex(int mIndex) {
    this.mIndex = mIndex;
  }

  public String getScoreMe() {
    return this.mScoreMe;
  }

  public void setScoreMe(String mScoreMe) {
    this.mScoreMe = mScoreMe;
  }

  public String getScoreOther() {
    return this.mScoreOther;
  }

  public void setScoreOther(String mScoreOther) {
    this.mScoreOther = mScoreOther;
  }

  public void stop() {
    this.mIsStop = true;
  }

  public static interface OnChangeDurationLinter {
    public void changeDuration(long duration);

    public void stop();

    public void timeOut();
  }

  public void setOnChangeDurationLinter(OnChangeDurationLinter mOnChangeDurationLinter) {
    this.mOnChangeDurationLinter = mOnChangeDurationLinter;
  }

}
