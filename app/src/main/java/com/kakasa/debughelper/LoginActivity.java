package com.kakasa.debughelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import com.kakasa.debug_helper.DebugHelper;


/**
 * Created by Seng on 2018/4/27.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText mEtUserName;
    private EditText mEtPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEtUserName = findViewById(R.id.et_username);
        mEtPassword = findViewById(R.id.et_password);

        initDebugInfo();
    }

    private void initDebugInfo() {
        if (BuildConfig.DEBUG) {
            Pair<String, String> user = DebugHelper.getInstance().getCurrentUser();
            if (user != null) {
                mEtUserName.setText(user.first);
                mEtPassword.setText(user.second);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initDebugInfo();
    }

    public void login(View view) {
        String username = mEtUserName.getText().toString();
        String password = mEtPassword.getText().toString();

        if (BuildConfig.DEBUG) {
            DebugHelper.getInstance().saveUserInfo(username, password);
        }
        startActivity(new Intent(this,MainActivity.class));
    }
}
