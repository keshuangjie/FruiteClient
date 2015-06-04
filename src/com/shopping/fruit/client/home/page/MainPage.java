package com.shopping.fruit.client.home.page;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.CommonPage;
import com.shopping.fruit.client.manager.LocData;
import com.shopping.fruit.client.manager.LocationManager;
import com.shopping.fruit.client.order.page.OrderListPage;
import com.shopping.fruit.client.usercenter.page.AddrssListPage;
import com.shopping.fruit.client.usercenter.page.UserCenterPage;
import com.shopping.fruit.client.widget.TabGroupView;

import java.util.Vector;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-22 14:56
 */
public class MainPage extends CommonPage {

    public static final int PAGE_SHOPLIST_INDEX = 0;
    public static final int PAGE_ORDER_INDEX = 1;
    public static final int PAGE_USERCENTER_INDEX = 2;

    public static final String KEY_INDEX = "index";

    private int mCurrentIndex = 0;

    private TabGroupView mTabGroupView;
    private Vector<CommonPage> mFragments;
    private FragmentManager mFragmentManager;

    private final String[] tabNames = {"定位中...", "订单", "我的"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getPageArguments();
        if (bundle != null){
            mCurrentIndex = bundle.getInt(KEY_INDEX);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setDisplayHomeAsUpEnabled(false);
        setTitle(mCurrentIndex);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected View onCreatePageContent(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main, container, false);
}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mTabGroupView == null) {
            mTabGroupView = (TabGroupView) view.findViewById(R.id.item_tab_group);
            mFragments = new Vector<CommonPage>();
            CommonPage fragment;
            Bundle bundle;

            fragment = new ShopListPage();
            bundle = new Bundle();
            fragment.setArguments(bundle);
            mFragments.add(fragment);

            fragment = new OrderListPage();
            mFragments.add(fragment);

            fragment = new UserCenterPage();
            mFragments.add(fragment);

            mFragmentManager = getActivity().getSupportFragmentManager();

            mTabGroupView.setContainerViewId(R.id.fl_content);
            mTabGroupView.setFragmentList(mFragments);
            mTabGroupView.setFragmentManager(mFragmentManager);
            mTabGroupView.setTabChangeListener(new TabGroupView.TabChangeListener() {
                @Override
                public void onChange(int position) {
                    mCurrentIndex = position;
                    setTitle(mCurrentIndex);
                }
            });
        }
        mTabGroupView.setSelected(mCurrentIndex);
        setTitle(mCurrentIndex);
    }

    private void setTitle(int position) {
        if (position == PAGE_SHOPLIST_INDEX) {
            LocData locData = LocationManager.getInstance().getLocData();
            if (locData != null && !TextUtils.isEmpty(locData.mDetailAddr)) {
                tabNames[PAGE_SHOPLIST_INDEX] = locData.mDetailAddr;
            }
        }
        setTitle(tabNames[mCurrentIndex]);
    }
}
