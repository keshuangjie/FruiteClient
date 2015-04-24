package com.shopping.fruit.client.base.pagestack;

import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * 页面生成工厂类
 * <p>
 * 生成并维护page的实例，page实例依据类名和tag标识
 * </p>
 * 
 */
class PageFactoryImpl implements PageFactory {

	private static final boolean DEBUG = false;
	private static final String TAG = "PageFactory";

	private static class PageFactoryHolder {
		static final PageFactory sInstance = new PageFactoryImpl();
	}

	public static PageFactory getInstance() {
		return PageFactoryHolder.sInstance;
	}

	private PageLruCache mLruCache;

	private static final int MAX_LRU_SIZE = 16; // the max size of our PageCache

	private PageFactoryImpl() {
		mLruCache = new PageLruCache(MAX_LRU_SIZE);
	}

	@Override
	public BasePage getBasePageInstance(String pageClsName) {
		return getBasePageInstance(pageClsName, DEFAULT_PAGE_TAG);
	}

	/**
	 * <p>
	 * 获取页面对象
	 * </p>
	 * <p/>
	 * 首先从LRUcache中查找，没有再从softcache中查找
	 * 
	 * @param pageClsName
	 *            页面类名
	 * @param pageTagString
	 *            页面标签，用于区分多实例
	 * @return 页面对象
	 */
	@Override
	public BasePage getBasePageInstance(String pageClsName, String pageTagString) {

		if (pageClsName == null) {
			return null;
			// throw new NullPointerException("pageClsName == null");
		}

		String pageCacheKey = pageTagString == null ? pageClsName + "@"
				+ DEFAULT_PAGE_TAG : pageClsName + "@" + pageTagString;

		BasePage page;

		synchronized (mLruCache) {
			page = mLruCache.get(pageCacheKey);
			if (null == page) {
				page = newBasePageInstance(getDefaultClassLoader(), pageClsName);
				mLruCache.put(pageCacheKey, page);
			}
		}
		return page;
	}

	@Override
	public void clearCache() {
		mLruCache.evictAll();
	}

	@Override
	public void removePage(BasePage page) {
		String pageTag = page.getPageTag();
		String name = ((Object) page).getClass().getName();
		String pageCacheKey = pageTag == null ? name + "@" + DEFAULT_PAGE_TAG
				: name + "@" + pageTag;
		if (DEBUG) {
			Log.i(TAG, "RemovePage:" + pageCacheKey);
		}
		synchronized (mLruCache) {
			mLruCache.remove(pageCacheKey);
		}
	}

	/**
	 * 获取新的页面实例
	 * 
	 * @param classLoader
	 *            class loader
	 * @param pageClsName
	 *            页面类名
	 * @return
	 */
	private BasePage newBasePageInstance(ClassLoader classLoader,
			String pageClsName) {

		Class<?> pgCls;
		BasePage page = null;
		try {
			pgCls = getPageClassByName(classLoader, pageClsName);
			if (pgCls != null) {
				page = (BasePage) pgCls.newInstance();
			}
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "exception", e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, "exception", e);
		} catch (InstantiationException e) {
			Log.e(TAG, "exception", e);
		} catch (Exception e) {
			Log.e(TAG, "exception", e);
		}

		return page;
	}

	private Class<?> getPageClassByName(ClassLoader classLoader,
			String pageClsName) throws ClassNotFoundException {
		Class<?> pageClass = null;
		if (classLoader != null)
			return classLoader.loadClass(pageClsName);
		else {
			// default class loader
			pageClass = Class.forName(pageClsName);
		}
		return pageClass;
	}

	private ClassLoader getDefaultClassLoader() {
		return this.getClass().getClassLoader();
	}

	/**
	 * 页面缓存内部类。
	 */
	static class PageLruCache extends LruCache<String, BasePage> {

		/**
		 * @param maxSize
		 *            for caches that do not override {@link #sizeOf}, this is
		 *            the maximum number of entries in the cache. For all other
		 *            caches, this is the maximum sum of the sizes of the
		 *            entries in this cache.
		 */
		public PageLruCache(int maxSize) {
			super(maxSize);
		}

		@Override
		protected void entryRemoved(boolean evicted, String key,
				BasePage oldValue, BasePage newValue) {
			super.entryRemoved(evicted, key, oldValue, newValue);
			if (oldValue != null) {
				if (DEBUG) {
					Log.i(TAG, "LruCache entryRemoved key:" + key);
					Log.i(TAG, "LruCache entryRemoved size:" + this.size());
				}
			}
		}
	}

}
