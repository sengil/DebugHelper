package com.kakasa.debug_helper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Seng on 2018/4/26.
 */

public class DebugActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_current_base;
    TextView btn_switch_base;
    TextView tv_current_user;
    ListView mListView;
    View layout_switch_base;
    private AccountAdapter mAccountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        tv_current_base = findViewById(R.id.tv_current_base);
        btn_switch_base = findViewById(R.id.tv_switch_base);
        tv_current_user = findViewById(R.id.tv_current_user);
        layout_switch_base = findViewById(R.id.layout_switch_base);
        mListView = findViewById(R.id.lv);

        layout_switch_base.setOnClickListener(this);
        tv_current_base.setText("当前域名" + DebugHelper.getInstance().getBaseUrl());

        if (DebugHelper.getInstance().isTestBase()) {
            btn_switch_base.setText("切换到正式");
        } else {
            btn_switch_base.setText("切换到测试");
        }

        Pair<String, String> currentUser = DebugHelper.getInstance().getCurrentUser();
        if (currentUser != null)
            tv_current_user.setText("当前用户：" + currentUser.first);
        List<Pair<String, String>> userAccoutnts = DebugHelper.getInstance().getAccounts();
        if (userAccoutnts != null) {
            mAccountAdapter = new AccountAdapter(userAccoutnts);
            mListView.setAdapter(mAccountAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Pair<String, String> item = mAccountAdapter.getItem(position);
                    DebugHelper.getInstance().saveCurrentUser(item.first, item.second);
                    DebugHelper.getInstance().switchAccount();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.layout_switch_base) {
            DebugHelper.getInstance().switchServer();
        }
//        switch (v.getId()) {
//            case R.id.layout_switch_base:
//
//                break;
//        }
    }

    private class AccountAdapter extends BaseAdapter {

        private final List<Pair<String, String>> mData;

        public AccountAdapter(List<Pair<String, String>> userAccoutnts) {
            mData = userAccoutnts;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Pair<String, String> getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView item = new TextView(DebugActivity.this);
            item.setPadding(0, 30, 0, 30);
            Pair<String, String> pair = mData.get(position);
            item.setText("账号：" + pair.first + "\n密码：" + pair.second);
            return item;
        }
    }
}
