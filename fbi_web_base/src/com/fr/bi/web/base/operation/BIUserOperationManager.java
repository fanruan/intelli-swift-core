package com.fr.bi.web.base.operation;

import com.fr.bi.fs.BIFileRepository;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Connery on 14-11-12.
 */
public class BIUserOperationManager {

    public static final String FILESEPARATOR = System.getProperty("file.separator");
    private static final String FOLD_NAME = "operation_record";
    long userId;
    ArrayList<BIOperationRecord> biOperationRecords;

    /**
     * 初始化
     *
     * @param userId 用户
     * @throws Exception
     */
    public BIUserOperationManager(long userId) throws Exception {
        this.userId = userId;
        biOperationRecords = new ArrayList<BIOperationRecord>();
        BIUserOperationManager.checkFold(userId);
        initialRecordsAccordingName();
    }

    /**
     * 获得userId对应的当前目录
     *
     * @param userId 用户
     * @throws Exception
     */
    public static StringBuffer getParentDirectoryPath(long userId) throws Exception {
        File parent = BIFileRepository.getInstance().getBIDirFile(userId);
        StringBuffer sb = new StringBuffer();
        sb.append(parent.getPath()).append(FILESEPARATOR).append(FOLD_NAME);
        return sb;
    }

    public static String[] getAllUserId() throws Exception {
        File current = BIFileRepository.getInstance().getBIDirFile(0);
        File parent = new File(current.getParent());
        String[] childName = parent.list();
        if (childName == null) {
            return new String[0];
        } else {
            return childName;
        }
    }

    private static ArrayList<String> getAllUserName() throws Exception {
        String[] id = BIUserOperationManager.getAllUserId();
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < id.length; i++) {
            try {
                UserControl userControl = UserControl.getInstance();
                User user = userControl.getUser(Long.parseLong(id[i]));
                names.add(user.getUsername());
            } catch (Exception ex) {

            }
        }
        return names;
    }

    /**
     * 检查userId对应的当前目录是否存在
     *
     * @param userId 用户
     * @throws Exception
     */
    public static void checkFold(long userId) throws Exception {
        StringBuffer sb = BIUserOperationManager.getParentDirectoryPath(userId);
        String directory = sb.toString();
        File recordFold = new File(directory);
        if (!recordFold.exists()) {
            StableUtils.mkdirs(recordFold);
        }
    }

    /**
     * 获得userId对应的所有子目
     *
     * @param userId 用户
     * @return
     * @throws Exception 异常
     */

    public static File[] fetchAllFiles(long userId) throws Exception {
        StringBuffer sb = BIUserOperationManager.getParentDirectoryPath(userId);
        String directory = sb.toString();
        File recordFold = new File(directory);
        return recordFold.listFiles();
    }

    /**
     * @return
     */
    public static String getCurrentDate() {
        Date d = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(d);
    }

    public static JSONObject getUserNameJson() throws Exception {
        JSONObject json = new JSONObject();
        ArrayList<String> names = BIUserOperationManager.getAllUserName();

        json.put("usernames", names);
        return json;
    }

    private ArrayList<String> fetchAllFileName() throws Exception {

        ArrayList<String> id_Names = new ArrayList<String>();
        File[] recordFiles = BIUserOperationManager.fetchAllFiles(userId);
        for (int i = 0; i < recordFiles.length; i++) {
            id_Names.add(recordFiles[i].getName());
        }
        return id_Names;
    }

    private void initialRecordsAccordingName() throws Exception {
        ArrayList<String> fileNames = fetchAllFileName();
        Iterator<String> it = fileNames.iterator();
        while (it.hasNext()) {
            String id_name = it.next();
            BIOperationRecord record;
            try {
                record = new BIOperationRecord(userId, id_name);
            } catch (Exception ex) {
                System.out.println("名为：" + id_name + " 的用户行为记录文件损坏");
                continue;
            }
            biOperationRecords.add(record);
        }
    }

    /**
     * 生成对应的json数据
     *
     * @return
     * @throws Exception
     */
    public JSONObject getReportBriefInfo() throws Exception {
        JSONObject json = new JSONObject();
        Comparator<BIOperationRecord> comparator = getDateComparator();
        java.util.Collections.sort(biOperationRecords, comparator);
        Iterator<BIOperationRecord> it = biOperationRecords.iterator();
        ArrayList<JSONObject> jsons = new ArrayList<JSONObject>();
        while (it.hasNext()) {
            jsons.add(it.next().getJsonObject());
        }
        json.put("records", jsons);
        return json;
    }

    private Comparator<BIOperationRecord> getDateComparator() {
        return new Comparator<BIOperationRecord>() {
            @Override
            public int compare(BIOperationRecord b1, BIOperationRecord b2) {

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    synchronized (this) {
                        Date date1 = formatter.parse(b1.getCreateDate());
                        Date date2 = formatter.parse(b2.getCreateDate());

                        if (date1.after(date2)) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                } catch (Exception ex) {

                }
                return 1;
            }
        };
    }
}