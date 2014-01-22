package com.media.dongfeng.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;

import com.media.dongfeng.model.Content;
import com.media.dongfeng.model.Info;
import com.media.dongfeng.model.User;

public class Utils {
    
    public static final String USER_FILE = "user";
    public static final String USER_LIST_FILE = "user_list";
    
    public static ProgressDialog showProgressDialog(Context context, String title, CharSequence charSequence) {
        return ProgressDialog.show(context, title, charSequence, true, true);
    }
    
    public static User loadUser(Context ctx) {
//        User user = new User();
//        user.mid = "56";
//        return null;
        
        File file = new File(ctx.getCacheDir(), USER_FILE);
        User user = (User)load(file);
//        if (user == null) {
//            user = new User();
//        }
        return user;
    }
    
    public static void saveUser(Context ctx, User user) {
        File file = new File(ctx.getCacheDir(), USER_FILE);
        if(user == null){
        	file.delete();
        }else if (user != null) {
            save(user, file);
        }
    }
    
    public static List<Content> loadSucaiCidList(Context ctx, User user) {
        File file = new File(ctx.getCacheDir(), "sucai_"+user.mid);
        List<Content> cidlist = (List<Content>)load(file);
        if (cidlist == null) {
            cidlist = new ArrayList<Content>();
        }
        return cidlist;
    }
    
    public static void saveSucaiCidList(Context ctx, User user, List<Content> cidlist) {
        File file = new File(ctx.getCacheDir(), "sucai_"+user.mid);
        if (cidlist != null) {
            save(cidlist, file);
        }
    }
    
    public static List<Info> loadInfoCidList(Context ctx, User user) {
        File file = new File(ctx.getCacheDir(), "info_"+user.mid);
        List<Info> cidlist = (List<Info>)load(file);
        if (cidlist == null) {
            cidlist = new ArrayList<Info>();
        }
        return cidlist;
    }
    
    public static void saveInfoCidList(Context ctx, User user, List<Info> cidlist) {
        File file = new File(ctx.getCacheDir(), "info_"+user.mid);
        if (cidlist != null) {
            save(cidlist, file);
        }
    }
    
    private static Object load(File file) {
        Object obj = null;
        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                try {
                    obj = ois.readObject();
                } catch (ClassNotFoundException e) {
                }
                ois.close();
            }
        } catch (IOException e) {
        }
        return obj;
    }
    
    private static void save(Object obj, File f) {
        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
        } catch (IOException e) {
        }
    }
    
    public static void makeFilepathExsist(String filepath) {
        File file = new File(filepath);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
    }
}
