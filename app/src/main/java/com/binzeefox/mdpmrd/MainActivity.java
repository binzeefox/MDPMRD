package com.binzeefox.mdpmrd;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.binzeefox.mdpmrd.db.User;
import com.binzeefox.mdpmrd.util.CommonUtil;
import com.binzeefox.mdpmrd.util.UserUtil;
import com.bumptech.glide.Glide;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /* UI标签 */
    private int uiTag;
    private static final int STATE_LOGIN = 0;
    private static final int STATE_REGISTER = 1;

    private CollapsingToolbarLayout toolbarLayout;
    private TextInputLayout tilUserName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPsd;
    private TextInputLayout tilPsdCheck;
    private FrameLayout focusHolder;
    private TextView loginError;
    private ImageView userImage;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* 按钮注册 */
        fab = (FloatingActionButton) findViewById(R.id.main_fab);
        FloatingActionButton fab_commit = (FloatingActionButton) findViewById(R.id.fab_commit);
        /* 其他控件注册 */
        userImage = (ImageView) findViewById(R.id.user_image); //用户自定义头背景
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        tilUserName = (TextInputLayout) findViewById(R.id.user_name);
        tilEmail = (TextInputLayout) findViewById(R.id.user_email);
        tilPsd = (TextInputLayout) findViewById(R.id.user_psd);
        tilPsdCheck = (TextInputLayout) findViewById(R.id.user_psd_check);
        focusHolder = (FrameLayout) findViewById(R.id.main_focus_holder);
        loginError = (TextView) findViewById(R.id.error_login_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /* 显示UI */
        uiTag = STATE_LOGIN;
        toolbarLayout.setTitle("请登录");
        fab.setOnClickListener(this);
        fab_commit.setOnClickListener(this);
        userImage.setOnClickListener(this);
        SharedPreferences pref = getSharedPreferences("userData", MODE_PRIVATE);
        String lastUserName = pref.getString("lastUserName", null);
        tilUserName.getEditText().setText(lastUserName);

        setSupportActionBar(toolbar);
        String url_string = pref.getString("userImageUrl", "");
        if (!url_string.isEmpty()) {
            Glide.with(this).load(url_string).into(userImage);
            TextView hint1 = (TextView) findViewById(R.id.image_hint);
            hint1.setVisibility(View.GONE);
        }
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {

        String userName = tilUserName.getEditText().getText().toString();   // 获取用户名
        if (CommonUtil.isChinese(userName)){
            tilUserName.setError("不支持中文用户名");
            return;
        }
        switch (v.getId()){
            case R.id.main_fab: // 点击注册按钮
                uiSwitch();
                if (uiTag == STATE_LOGIN) {
                    uiTag = STATE_REGISTER;
                } else if (uiTag == STATE_REGISTER){
                    uiTag = STATE_LOGIN;
                }
                break;

            case R.id.fab_commit:
                if (uiTag == STATE_LOGIN){
                    if (UserUtil.checkLogin(tilUserName, tilPsd) == 1){
                        // TODO 登陆成功，跳转
                        int id = UserUtil.getUserId(userName);
                        jumpToUser(id);
                    } else if (UserUtil.checkLogin(tilUserName, tilPsd) == -1){
                        loginError.setVisibility(View.VISIBLE);
                    }
                } else if (uiTag == STATE_REGISTER){
                    if (UserUtil.checkRegister(tilUserName, tilEmail, tilPsd, tilPsdCheck)){
                        int id = UserUtil.getUserId(userName);
                        CommonUtil.showToast(this, "注册成功");
                        jumpToUser(id);
                    }
                }
                break;

            case R.id.user_image:   // 点击图片
                // 相册选取图片并获得url
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        openAlbum();
                    }
                } else {
                    openAlbum();
                }
            default:
                break;
        }
    }

    /**
     * 登陆跳转
     * @param id 获取到的用户数据库ID
     */
    private void jumpToUser(int id) {
        Intent intent = new Intent(MainActivity.this, UserActivity.class);
        User user = DataSupport.find(User.class, id);
        intent.putExtra("user",user );

        SharedPreferences.Editor editor = getSharedPreferences("userData", MODE_PRIVATE).edit();
        editor.putString("lastUserName", tilUserName.getEditText().getText().toString());
        editor.apply();
        startActivity(intent);
        finish();
    }

    /**
     * UI判断与显示
     */
    private void uiSwitch(){
        switch (uiTag){
            case STATE_REGISTER:
                // TODO 写入开启登陆界面算法
                resetEditView();
                toolbarLayout.setTitle("请登陆");
                fab.setImageResource(R.mipmap.ic_playlist_add_black_24dp);
                tilPsdCheck.setVisibility(View.GONE);
                tilEmail.setVisibility(View.GONE);
                break;

            case STATE_LOGIN:
                // TODO 写入开启注册界面算法
                resetEditView();
                toolbarLayout.setTitle("请注册");
                fab.setImageResource(R.mipmap.ic_arrow_back_black_24dp);
                tilEmail.setVisibility(View.VISIBLE);
                tilPsdCheck.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 重置文本框
     */
    private void resetEditView(){

        tilUserName.setError("");
        List<TextInputLayout> inputLayouts = new ArrayList<>();
        inputLayouts.add(tilEmail);
        inputLayouts.add(tilPsd);
        inputLayouts.add(tilPsdCheck);
        for (TextInputLayout inputLayout : inputLayouts){
            if (inputLayout.getVisibility() == View.VISIBLE){
                inputLayout.setError("");
                inputLayout.getEditText().setText("");
            }
        }
        loginError.setVisibility(View.GONE);
        focusHolder.setFocusable(true);
        focusHolder.requestFocus();
    }

    /**
     * 开启相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }


    /**
     * 动态权限判断
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    CommonUtil.showToast(this,"获取权限失败");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理相册获得的data
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImage(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 存储从相册得到的url并设置图片
     * @param data
     */
    private void handleImage(Intent data){
        Uri uri = data.getData();
        Glide.with(this).load(uri).into(userImage);
        SharedPreferences.Editor editor = getSharedPreferences("userData", MODE_PRIVATE).edit();
        editor.putString("userImageUrl", uri.toString());
        editor.apply();
        TextView hint = (TextView) findViewById(R.id.image_hint);
        if (hint.getVisibility() == View.VISIBLE){
            hint.setVisibility(View.GONE);
        }
    }
}
