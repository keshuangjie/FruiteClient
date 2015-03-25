package com.shopping.fruit.client.home;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentManager;

import com.shopping.fruit.client.base.BaseActivity;
import com.shopping.fruit.client.base.BaseFragment;
import com.shopping.fruit.client.R;
import com.shopping.fruit.client.home.page.ShopListPage;
import com.shopping.fruit.client.shop.ShopDetailPage;
import com.shopping.fruit.client.widget.TabGroupView;

import java.util.Vector;

public class MainActivity extends BaseActivity {

    private TabGroupView mTabGroupView;
    private Vector<BaseFragment> mFragments;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTabGroupView = (TabGroupView) findViewById(R.id.item_tab_group);
        mFragments = new Vector<BaseFragment>();
        BaseFragment fragment;
        Bundle bundle;

        for(int i=0; i<2; i++){
            fragment = new ShopListPage();
            bundle = new Bundle();
            fragment.setArguments(bundle);
            mFragments.add(fragment);
        }

        fragment = new ShopDetailPage();
        mFragments.add(fragment);

        mFragmentManager = getSupportFragmentManager();

        mTabGroupView.setContainerViewId(R.id.fl_content);
        mTabGroupView.setFragmentList(mFragments);
        mTabGroupView.setFragmentManager(mFragmentManager);
        mTabGroupView.setSelected(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
