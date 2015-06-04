package com.shopping.fruit.client.home;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentManager;

import com.shopping.fruit.client.base.BaseActivity;
import com.shopping.fruit.client.base.CommonPage;
import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.pagestack.HistoryRecord;
import com.shopping.fruit.client.base.pagestack.TaskManager;
import com.shopping.fruit.client.base.pagestack.TaskManagerFactory;
import com.shopping.fruit.client.home.page.MainPage;
import com.shopping.fruit.client.home.page.ShopListPage;
import com.shopping.fruit.client.usercenter.page.AddrssListPage;
import com.shopping.fruit.client.usercenter.page.UserCenterPage;
import com.shopping.fruit.client.widget.TabGroupView;

import java.util.Vector;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_stack);

        setRootRecord();
        gotoDefaultPage();
    }

    private void setRootRecord() {
        TaskManagerFactory.getTaskManager().registerRootTask(MainActivity.class.getName());
        // set root page record
        final TaskManager taskManager = TaskManagerFactory.getTaskManager();
        taskManager.attach(this);
        HistoryRecord record = new HistoryRecord(MainActivity.class.getName(), MainPage.class.getName());
        record.taskSignature = HistoryRecord.genSignature(this);
        taskManager.setRootRecord(record);
    }

    private void gotoDefaultPage() {
        navigateTo(MainPage.class.getName(), "", null);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
