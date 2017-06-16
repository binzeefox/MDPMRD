package com.binzeefox.mdpmrd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.binzeefox.mdpmrd.db.User;
import org.litepal.crud.DataSupport;

public class UserActivity extends AppCompatActivity {

    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        user = (User) getIntent().getSerializableExtra("user");
    }
}
