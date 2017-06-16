package com.binzeefox.mdpmrd.util;

import android.support.design.widget.TextInputLayout;
import com.binzeefox.mdpmrd.db.User;
import com.binzeefox.mdpmrd.util.CommonUtil;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Objects;

/**
 * Created by tong.xiwen on 2017/6/13.
 */
public class UserUtil {

    /**
     * 注册验证与注册
     * 以下全部为TextInputLayout实例
     * @param userName 用户名
     * @param eMail 注册邮箱
     * @param psd 密码
     * @param psd_c 确认密码
     * @return 返回是否注册成功
     */
    public static boolean checkRegister(TextInputLayout userName, TextInputLayout eMail, TextInputLayout psd, TextInputLayout psd_c){

        String mUserName;
        String mEmail;
        String mPsd;

        if (userName.getEditText().getText() == null){
            userName.setError("用户名不能为空");
            return false;
        } else {
            mUserName = userName.getEditText().getText().toString();
        }
        List<User> users = DataSupport.where("userName = ?", mUserName).find(User.class);
        if (users != null){
            userName.setError("该用户名已存在");
        }


        if (eMail.getEditText().getText() == null){
            mEmail = "";
        } else {
            mEmail = eMail.getEditText().getText().toString();
        }

        if (psd.getEditText().getText() == null){
            psd.setError("请输入密码");
            return false;
        } else {
            mPsd = psd.getEditText().getText().toString();
        }

        if (psd_c.getEditText().getText() == null){
            psd_c.setError("二次输入密码不一致");
            return false;
        } else if (psd.getEditText().getText() != psd_c.getEditText().getText()){
            psd_c.setError("二次输入密码不一致");
        }

        mPsd = CommonUtil.md5(mPsd); //获取密码的MD5码
        User user = new User();
        user.setUserName(mUserName);
        user.setEmail(mEmail);
        user.setMd5Psd(mPsd);
        user.save();  //注册成功，存入数据库
        return true;
    }

    /**
     * 验证登陆
     * @param userName userName的TextInputLayout实例
     * @param psd psd的TextInputLayout实例
     * @return -1：用户名或密码错误； 0：验证失败并报错； 1：验证成功
     */
    public static int checkLogin(TextInputLayout userName, TextInputLayout psd){
        String mUserName;
        String mPsd;

        if (userName.getEditText().getText().toString() != null || userName.getEditText().getText().toString() == ""){
            mUserName = userName.getEditText().getText().toString();
        } else {
            userName.setError("用户名不能为空");
            return 0;
        }

        if (psd.getEditText().getText().toString() != null){
            mPsd = psd.getEditText().getText().toString();
            mPsd = CommonUtil.md5(mPsd); // 获取密码的MD5码
        } else {
            psd.setError("请输入密码");
            return 0;
        }

        List<User> users = DataSupport.where("userName = ?", mUserName).find(User.class);
        if (users.isEmpty()){
            // 用户名不存在
            return -1;
        }

        User user = users.get(0);
        if (!Objects.equals(user.getMd5Psd(), mPsd)){ // 验证数据库的密码MD5码与输入的密码获取的MD5码是否一致
            // 密码错误
            return -1;
        }

        // 验证成功
        return 1;
    }

    public static int getUserId(String userName){

        List<User> users = DataSupport.where("userName = ?", userName).find(User.class);
        User user = users.get(0);
        int id = user.getId();
        return id;
    }
}
