package com.app.DBManager;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.DBHelper.DBHelper;
import com.app.DBHelper.DatabaseContext;
import com.app.Model.CommentInfo;
import com.app.Model.LookInfo;
import com.app.Model.RandomInfo;
import com.app.Model.VideoTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2016/11/21.
 */
public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;
    private static DBManager dbManager;
    private DatabaseContext dbContext;

    public DBManager(Context context) {
        dbContext = new DatabaseContext(context);
        helper = new DBHelper(dbContext);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();//系统默认路径data/data
    }

    public static DBManager getDBManager(Activity context) {
        return dbManager == null ? dbManager = new DBManager(context) : dbManager;
    }

    /**
     * add persons======================================================视频时间表操作start=============================================
     *
     * @param
     */
    public void addVideoTimeData(List<String> stringList) {
        db.beginTransaction();    //开始事务
        try {
            for (String person : stringList) {
                db.execSQL("INSERT INTO night_Video_Time VALUES(?,?)",
                        new String[]{null, person});
            }
            db.setTransactionSuccessful();    //设置事务成功完成
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public VideoTime queryVideoTimeById(String id) {
        VideoTime persons = null;
        Cursor c = queryVideoTimeByIdTheCursor(id);
        while (c.moveToNext()) {
            persons = new VideoTime();
            persons.video_Time = c.getString(c.getColumnIndex("video_time_random"));
            persons.id = c.getString(c.getColumnIndex("id"));
        }
        c.close();
        return persons;
    }

    public List<VideoTime> queryVideoTimeAll() {
        List<VideoTime> persons = new ArrayList<>();
        try {
            Cursor c = queryVideoTimeInfoAllTheCursor();
            while (c.moveToNext()) {
                VideoTime videoTime = new VideoTime();
                videoTime.video_Time = c.getString(c.getColumnIndex("video_time_random"));
                videoTime.id = c.getString(c.getColumnIndex("id"));
                persons.add(videoTime);
            }
            c.close();
        } catch (Exception e) {
            return persons;
        }
        return persons;
    }

    public Cursor queryVideoTimeInfoAllTheCursor() {
        //使用limit分页查询
        Cursor c = db.rawQuery("SELECT * FROM night_Video_Time", null);
        return c;
    }


    public Cursor queryVideoTimeByIdTheCursor(String id) {
        Cursor c = db.rawQuery("SELECT * FROM night_Video_Time where id=" + id + "", null);
        return c;
    }


    /**
     * add persons======================================================随机数表操作start=============================================
     *
     * @param
     */
    public void addRandomInfoData(RandomInfo randomInfo) {
        db.beginTransaction();    //开始事务
        try {
            db.execSQL("INSERT INTO RandomInfo VALUES(?)",
                    new Object[]{randomInfo.getRandom_text()});
            db.setTransactionSuccessful();    //设置事务成功完成
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public List<RandomInfo> queryRandomInfo() {
        List<RandomInfo> persons = new ArrayList<>();
        Cursor c = queryRandomInfoTheCursor();
        while (c.moveToNext()) {
            RandomInfo person = new RandomInfo();
            person.random_text = c.getString(c.getColumnIndex("random_Text"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryRandomInfoTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM RandomInfo", null);
        return c;
    }


    /**
     * add persons======================================================随机数表操作end=============================================
     *
     */


    /**
     * add persons======================================================试看无码表操作start=============================================
     *
     * @param
     */
    public void addLookCodeData(List<LookInfo> lookInfos) {
        clearLookCodeTable();
        db.beginTransaction();    //开始事务
        try {
            for (LookInfo person : lookInfos) {
                db.execSQL("INSERT INTO Look_Code VALUES(?,?, ?, ?, ?, ?, ?, ?,?)",
                        new Object[]{null, person.getName(), person.getPic(), person.getAddress_sd(), person.getAddress_hd(),
                                person.getType(), person.getPic_heng(), person.getIsvip(),person.getSpare1()});
            }
            db.setTransactionSuccessful();    //设置事务成功完成
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * update person's age
     *
     * @param
     */
    public void clearLookCodeTable() {
        db.execSQL("DELETE FROM Look_Code");
//        SQLiteDatabase.execSQL("DROP TABLE CUSTOMERS")
//        清除表中所有记录：
//        SQLiteDatabase.execSQL("DELETE FROM CUSTOMERS")
    }


    /**
     * update person's age
     *
     * @param person
     */
    public void updateLookCode_Pic(LookInfo person) {
//        ContentValues cv = new ContentValues();
//        cv.put("pic", person.getPic());//需要更新的数据
//        db.update("ThreeVideo", cv, "id = ?", new String[]{person.getId()});
    }

    /**
     * delete old person
     *
     * @param person
     */
    public void deleteLookInfoById(LookInfo person) {
//        db.delete("ThreeVideo", " id= ?", new String[]{String.valueOf(person.getId())});
    }

    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public List<LookInfo> queryLookCodeByType(String type) {
        List<LookInfo> persons = new ArrayList<>();
        Cursor c = queryLookInfoTypeTheCursor(type);
        while (c.moveToNext()) {
            LookInfo person = new LookInfo();
            person.id = c.getString(c.getColumnIndex("id"));
            person.name = c.getString(c.getColumnIndex("name"));
            person.pic = c.getString(c.getColumnIndex("pic"));
            person.address_sd = c.getString(c.getColumnIndex("address_sd"));
            person.address_hd = c.getString(c.getColumnIndex("address_hd"));
            person.type = c.getString(c.getColumnIndex("type"));
            person.pic_heng = c.getString(c.getColumnIndex("pic_heng"));
            person.isvip = c.getString(c.getColumnIndex("isvip"));
            person.spare1 = c.getString(c.getColumnIndex("spare1"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<LookInfo> queryLookCodeAll() {
        List<LookInfo> persons = new ArrayList<>();
        try {
            Cursor c = queryLookInfoAllTheCursor();
            while (c.moveToNext()) {
                LookInfo person = new LookInfo();
                person.id = c.getString(c.getColumnIndex("id"));
                person.name = c.getString(c.getColumnIndex("name"));
                person.pic = c.getString(c.getColumnIndex("pic"));
                person.address_sd = c.getString(c.getColumnIndex("address_sd"));
                person.address_hd = c.getString(c.getColumnIndex("address_hd"));
                person.type = c.getString(c.getColumnIndex("type"));
                person.pic_heng = c.getString(c.getColumnIndex("pic_heng"));
                person.isvip = c.getString(c.getColumnIndex("isvip"));
                person.spare1 = c.getString(c.getColumnIndex("spare1"));
                persons.add(person);
            }
            c.close();
        } catch (Exception e) {
            return persons;
        }
        return persons;
    }

    public Cursor queryLookInfoTypeTheCursor(String type) {
        //使用limit分页查询
        Cursor c = db.rawQuery("SELECT * FROM Look_Code  where type=" + type + "", null);
        return c;
    }

    public Cursor queryLookInfoAllTheCursor() {
        //使用limit分页查询
        Cursor c = db.rawQuery("SELECT * FROM Look_Code", null);
        return c;
    }


    /**
     * add persons======================================================试看更多表操作start=============================================
     *
     * @param
     */
    public void addLookMoreData(List<LookInfo> lookInfos) {
        clearLookMoreTable();
        db.beginTransaction();    //开始事务
        try {
            for (LookInfo person : lookInfos) {
                db.execSQL("INSERT INTO Look_More VALUES(?,?, ?, ?, ?, ?, ?, ?)",
                        new Object[]{null,person.getName(), person.getPic(), person.getAddress_sd(), person.getAddress_hd(),
                                person.getType(), person.getPic_heng(), person.getIsvip()});
            }
            db.setTransactionSuccessful();    //设置事务成功完成
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * update person's age
     *
     * @param
     */
    public void clearLookMoreTable() {
        db.execSQL("DELETE FROM Look_More");
//        SQLiteDatabase.execSQL("DROP TABLE CUSTOMERS")
//        清除表中所有记录：
//        SQLiteDatabase.execSQL("DELETE FROM CUSTOMERS")
    }


    /**
     * update person's age
     *
     * @param person
     */
    public void updateLookMore_Pic(LookInfo person) {
//        ContentValues cv = new ContentValues();
//        cv.put("pic", person.getPic());//需要更新的数据
//        db.update("ThreeVideo", cv, "id = ?", new String[]{person.getId()});
    }

    /**
     * delete old person
     *
     * @param person
     */
    public void deleteLookMoreInfoById(LookInfo person) {
//        db.delete("ThreeVideo", " id= ?", new String[]{String.valueOf(person.getId())});
    }

    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public List<LookInfo> queryLookMoreByType(String type) {
        List<LookInfo> persons = new ArrayList<>();
        try {
            Cursor c = queryLookMoreInfoTypeTheCursor(type);
            while (c.moveToNext()) {
                LookInfo person = new LookInfo();
                person.id = c.getString(c.getColumnIndex("id"));
                person.name = c.getString(c.getColumnIndex("name"));
                person.pic = c.getString(c.getColumnIndex("pic"));
                person.address_sd = c.getString(c.getColumnIndex("address_sd"));
                person.address_hd = c.getString(c.getColumnIndex("address_hd"));
                person.type = c.getString(c.getColumnIndex("type"));
                person.pic_heng = c.getString(c.getColumnIndex("pic_heng"));
                person.isvip = c.getString(c.getColumnIndex("isvip"));
                persons.add(person);
            }
            c.close();
        } catch (Exception e) {
            return persons;
        }
        return persons;
    }

    public List<LookInfo> queryLookMoreAll() {
        List<LookInfo> persons = new ArrayList<>();
        try {
            Cursor c = queryLookMoreInfoAllTheCursor();
            while (c.moveToNext()) {
                LookInfo person = new LookInfo();
                person.id = c.getString(c.getColumnIndex("id"));
                person.name = c.getString(c.getColumnIndex("name"));
                person.pic = c.getString(c.getColumnIndex("pic"));
                person.address_sd = c.getString(c.getColumnIndex("address_sd"));
                person.address_hd = c.getString(c.getColumnIndex("address_hd"));
                person.type = c.getString(c.getColumnIndex("type"));
                person.pic_heng = c.getString(c.getColumnIndex("pic_heng"));
                person.isvip = c.getString(c.getColumnIndex("isvip"));
                persons.add(person);
            }
            c.close();
        } catch (Exception e) {
            return persons;
        }
        return persons;
    }

    public Cursor queryLookMoreInfoTypeTheCursor(String type) {
        //使用limit分页查询
        Cursor c = db.rawQuery("SELECT * FROM Look_More  where type=" + type + "", null);
        return c;
    }

    public Cursor queryLookMoreInfoAllTheCursor() {
        //使用limit分页查询
        Cursor c = db.rawQuery("SELECT * FROM Look_More", null);
        return c;
    }

    /**
     * add persons======================================================试看更多表操作end=============================================
     *
     *
     */


    /**
     * add persons======================================================三级表操作start=============================================
     *
     * @param threeVideoInfoList
     */
//    public void addThreeVideoData(List<ThreeVideoInfo> threeVideoInfoList) {
//        db.beginTransaction();    //开始事务
//        try {
//            for (ThreeVideoInfo person : threeVideoInfoList) {
//                db.execSQL("INSERT INTO ThreeVideo VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
//                        new Object[]{person.getId(), person.getCreatetime(), person.getName(), person.getArea(),
//                                person.getAddress_sd(), person.getAddress_hd(), person.getPic(), person.getPic_heng(),
//                                person.getLasttime(), person.getScore(), person.getShowtime(), person.getState(),
//                                person.getIsLook()});
//            }
//            db.setTransactionSuccessful();    //设置事务成功完成
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            db.endTransaction();    //结束事务
//        }
//    }
//
//    /**
//     * update person's age
//     *
//     * @param person
//     */
//    public void updateThreeVideo_Pic(ThreeVideoInfo person) {
//        ContentValues cv = new ContentValues();
//        cv.put("pic", person.getPic());//需要更新的数据
//        db.update("ThreeVideo", cv, "id = ?", new String[]{person.getId()});
//    }
//
//    /**
//     * delete old person
//     *
//     * @param person
//     */
//    public void deleteThreeVideoById(ThreeVideoInfo person) {
//        db.delete("ThreeVideo", " id= ?", new String[]{String.valueOf(person.getId())});
//    }
//
//    /**
//     * query all persons, return list
//     *
//     * @return List<Person>
//     */
//    public List<ThreeVideoInfo> queryThreeVideoAll() {
//        List<ThreeVideoInfo> persons = new ArrayList<>();
//        Cursor c = queryThreeVideoInfoTheCursor();
//        while (c.moveToNext()) {
//            ThreeVideoInfo person = new ThreeVideoInfo();
//            person.id = c.getString(c.getColumnIndex("id"));
//            person.createtime = c.getString(c.getColumnIndex("createtime"));
//            person.name = c.getString(c.getColumnIndex("name"));
//            person.area = c.getString(c.getColumnIndex("area"));
//            person.address_sd = c.getString(c.getColumnIndex("address_sd"));
//            person.address_hd = c.getString(c.getColumnIndex("address_hd"));
//            person.pic = c.getString(c.getColumnIndex("pic"));
//            person.pic_heng = c.getString(c.getColumnIndex("pic_heng"));
//            person.lasttime = c.getString(c.getColumnIndex("lasttime"));
//            person.score = c.getString(c.getColumnIndex("score"));
//            person.showtime = c.getString(c.getColumnIndex("showtime"));
//            person.state = c.getString(c.getColumnIndex("state"));
//            person.isLook = c.getString(c.getColumnIndex("isLook"));
//            persons.add(person);
//        }
//        c.close();
//        return persons;
//    }
//
//    public List<ThreeVideoInfo> queryPagerThreeVideo(int pageSize, int page) {
//        List<ThreeVideoInfo> persons = new ArrayList<>();
//        Cursor c = queryThreeVideoPagerTheCursor(pageSize, page);
//        while (c.moveToNext()) {
//            ThreeVideoInfo person = new ThreeVideoInfo();
//            person.id = c.getString(c.getColumnIndex("id"));
//            person.createtime = c.getString(c.getColumnIndex("createtime"));
//            person.name = c.getString(c.getColumnIndex("name"));
//            person.area = c.getString(c.getColumnIndex("area"));
//            person.address_sd = c.getString(c.getColumnIndex("address_sd"));
//            person.address_hd = c.getString(c.getColumnIndex("address_hd"));
//            person.pic = c.getString(c.getColumnIndex("pic"));
//            person.pic_heng = c.getString(c.getColumnIndex("pic_heng"));
//            person.lasttime = c.getString(c.getColumnIndex("lasttime"));
//            person.score = c.getString(c.getColumnIndex("score"));
//            person.showtime = c.getString(c.getColumnIndex("showtime"));
//            person.state = c.getString(c.getColumnIndex("state"));
//            person.isLook = c.getString(c.getColumnIndex("isLook"));
//            persons.add(person);
//        }
//        c.close();
//        return persons;
//    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryThreeVideoInfoTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM ThreeVideo", null);
        return c;
    }

    public Cursor queryThreeVideoPagerTheCursor(int pageSize, int page) {
        //使用limit分页查询
        Cursor c = db.rawQuery("SELECT * FROM ThreeVideo limit " + (page - 1) * pageSize + "," + pageSize + "", null);
        return c;
    }
    /**
     * query all persons, return cursor=======================三级表操作end=====================================================
     *
     * @return Cursor
     */


    /**
     * add persons======================================================大片表操作start=============================================
     *
     * @param
     */
//    public void addBigVideoData(List<BigVideoInfo> bigVideoInfoList) {
//        db.beginTransaction();    //开始事务
//        try {
//            for (BigVideoInfo person : bigVideoInfoList) {
//                db.execSQL("INSERT INTO BigVideoInfo VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
//                        new Object[]{person.getId(), person.getCreatetime(), person.getName(), person.getArea(),
//                                person.getAddress_sd(), person.getAddress_hd(), person.getPic(), person.getPic_heng(),
//                                person.getLasttime(), person.getScore(), person.getShowtime(), person.getState(),
//                                person.getIsLook()});
//            }
//            db.setTransactionSuccessful();    //设置事务成功完成
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            db.endTransaction();    //结束事务
//        }
//    }
//
//    /**
//     * update person's age
//     *
//     * @param person
//     */
//    public void updateBigVideo_Pic(BigVideoInfo person) {
//        ContentValues cv = new ContentValues();
//        cv.put("pic", person.getPic());//需要更新的数据
//        db.update("BigVideoInfo", cv, "id = ?", new String[]{person.getId()});
//    }
//
//    /**
//     * delete old person
//     *
//     * @param person
//     */
//    public void deleteBigVideoById(BigVideoInfo person) {
//        db.delete("BigVideoInfo", " id= ?", new String[]{String.valueOf(person.getId())});
//    }
//
//    /**
//     * query all persons, return list
//     *
//     * @return List<Person>
//     */
//    public List<BigVideoInfo> queryBigVideoAll() {
//        List<BigVideoInfo> persons = new ArrayList<>();
//        Cursor c = queryBigVideoTheCursor();
//        while (c.moveToNext()) {
//            BigVideoInfo person = new BigVideoInfo();
//            person.id = c.getString(c.getColumnIndex("id"));
//            person.createtime = c.getString(c.getColumnIndex("createtime"));
//            person.name = c.getString(c.getColumnIndex("name"));
//            person.area = c.getString(c.getColumnIndex("area"));
//            person.address_sd = c.getString(c.getColumnIndex("address_sd"));
//            person.address_hd = c.getString(c.getColumnIndex("address_hd"));
//            person.pic = c.getString(c.getColumnIndex("pic"));
//            person.pic_heng = c.getString(c.getColumnIndex("pic_heng"));
//            person.lasttime = c.getString(c.getColumnIndex("lasttime"));
//            person.score = c.getString(c.getColumnIndex("score"));
//            person.showtime = c.getString(c.getColumnIndex("showtime"));
//            person.state = c.getString(c.getColumnIndex("state"));
//            person.isLook = c.getString(c.getColumnIndex("isLook"));
//            persons.add(person);
//        }
//        c.close();
//        return persons;
//    }
//
//    public List<BigVideoInfo> queryPagerBigVideo(int pageSize, int page) {
//        List<BigVideoInfo> persons = new ArrayList<>();
//        Cursor c = queryBigVideoPagerTheCursor(pageSize, page);
//        while (c.moveToNext()) {
//            BigVideoInfo person = new BigVideoInfo();
//            person.id = c.getString(c.getColumnIndex("id"));
//            person.createtime = c.getString(c.getColumnIndex("createtime"));
//            person.name = c.getString(c.getColumnIndex("name"));
//            person.area = c.getString(c.getColumnIndex("area"));
//            person.address_sd = c.getString(c.getColumnIndex("address_sd"));
//            person.address_hd = c.getString(c.getColumnIndex("address_hd"));
//            person.pic = c.getString(c.getColumnIndex("pic"));
//            person.pic_heng = c.getString(c.getColumnIndex("pic_heng"));
//            person.lasttime = c.getString(c.getColumnIndex("lasttime"));
//            person.score = c.getString(c.getColumnIndex("score"));
//            person.showtime = c.getString(c.getColumnIndex("showtime"));
//            person.state = c.getString(c.getColumnIndex("state"));
//            person.isLook = c.getString(c.getColumnIndex("isLook"));
//            persons.add(person);
//        }
//        c.close();
//        return persons;
//    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryBigVideoTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM BigVideoInfo", null);
        return c;
    }

    public Cursor queryBigVideoPagerTheCursor(int pageSize, int page) {
        //使用limit分页查询
        Cursor c = db.rawQuery("SELECT * FROM BigVideoInfo limit " + (page - 1) * pageSize + "," + pageSize + "", null);
        return c;
    }
    /**
     * query all persons, return cursor=======================大片表操作end=====================================================
     *
     * @return Cursor
     */


    /**
     * add persons======================================================评论表操作start=============================================
     *
     * @param
     */
    public void addCommentData(List<CommentInfo> commentInfoList) {
        clearCommentTable();
        db.beginTransaction();    //开始事务
        try {
            for (CommentInfo person : commentInfoList) {
                db.execSQL("INSERT INTO CommentInfo VALUES(?, ?, ?, ?)",
                        new Object[]{person.getName(), person.getPic(), person.getHand(), person.getInfo()});
            }
            db.setTransactionSuccessful();    //设置事务成功完成
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * update person's age
     *
     * @param
     */
    public void clearCommentTable() {
        db.execSQL("DELETE FROM CommentInfo");
//        SQLiteDatabase.execSQL("DROP TABLE CUSTOMERS")
//        清除表中所有记录：
//        SQLiteDatabase.execSQL("DELETE FROM CUSTOMERS")
    }

    /**
     * update person's age
     *
     * @param person
     */
    public void updateComment(CommentInfo person) {
//        ContentValues cv = new ContentValues();
//        cv.put("pic", person.getPic());//需要更新的数据
//        db.update("BigVideoInfo", cv, "id = ?", new String[]{person.getId()});
    }

    /**
     * delete old person
     *
     * @param person
     */
    public void deleteCommentById(CommentInfo person) {
//        db.delete("CommentInfo", " id= ?", new String[]{String.valueOf(person.getId())});
    }

    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public List<CommentInfo> queryCommentAll() {
        List<CommentInfo> persons = new ArrayList<>();
        Cursor c = queryCommentTheCursor();
        while (c.moveToNext()) {
            CommentInfo person = new CommentInfo();
            person.hand = c.getString(c.getColumnIndex("hand"));
            person.info = c.getString(c.getColumnIndex("info"));
            person.name = c.getString(c.getColumnIndex("name"));
            person.pic = c.getString(c.getColumnIndex("pic"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<CommentInfo> queryPagerComment(int pageSize, int page) {
        List<CommentInfo> persons = new ArrayList<>();
        Cursor c = queryCommentPagerTheCursor(pageSize, page);
        while (c.moveToNext()) {
            CommentInfo person = new CommentInfo();
            person.hand = c.getString(c.getColumnIndex("hand"));
            person.info = c.getString(c.getColumnIndex("info"));
            person.name = c.getString(c.getColumnIndex("name"));
            person.pic = c.getString(c.getColumnIndex("pic"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryCommentTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM CommentInfo", null);
        return c;
    }

    public Cursor queryCommentPagerTheCursor(int pageSize, int page) {
        //使用limit分页查询
        Cursor c = db.rawQuery("SELECT * FROM CommentInfo limit " + (page - 1) * pageSize + "," + pageSize + "", null);
        return c;
    }
    /**
     * query all persons, return cursor=======================评论表操作end=====================================================
     *
     * @return Cursor
     */


    /**
     * query all persons, return cursor=========================直播表操作start============================================
     *
     * @return Cursor
     */

//    public void addLiveData(List<LiveInfo> liveInfoList) {
//        db.beginTransaction();    //开始事务
//        try {
//            for (LiveInfo person : liveInfoList) {
//                db.execSQL("INSERT INTO LiveInfo VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
//                        new Object[]{person.getId(), person.getIsLook(), person.getLive_Url(), person.getLogo_url(),
//                                person.getRemarks(), person.getVideo_name(), person.getWonderful(),person.getTv_name(),
//                                person.getDescription(),person.getCreate_Time(),person.getClientPic_Url(),person.getClient_FirstPic_Url(),
//                                person.getClient_FirstPic_Url()});
//            }
//            db.setTransactionSuccessful();    //设置事务成功完成
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            db.endTransaction();    //结束事务
//        }
//    }
//
//    /**
//     * update person's age
//     *
//     * @param person
//     */
//    public void updateLiveInfo(LiveInfo person) {
////        ContentValues cv = new ContentValues();
////        cv.put("pic", person.getPic());//需要更新的数据
////        db.update("BigVideoInfo", cv, "id = ?", new String[]{person.getId()});
//    }
//
//    /**
//     * delete old person
//     *
//     * @param person
//     */
//    public void deleteLiveInfoById(LiveInfo person) {
//        db.delete("LiveInfo", " id= ?", new String[]{String.valueOf(person.getId())});
//    }
//
//    /**
//     * query all persons, return list
//     *
//     * @return List<Person>
//     */
//    public List<LiveInfo> queryLiveInfoAll() {
//        List<LiveInfo> persons = new ArrayList<>();
//        Cursor c = queryLiveInfoTheCursor();
//        while (c.moveToNext()) {
//            LiveInfo person = new LiveInfo();
//            person.id = c.getString(c.getColumnIndex("id"));
//            person.isLook = c.getString(c.getColumnIndex("isLook"));
//            person.live_Url = c.getString(c.getColumnIndex("live_Url"));
//            person.logo_url = c.getString(c.getColumnIndex("logo_url"));
//            person.remarks = c.getString(c.getColumnIndex("remarks"));
//            person.video_name = c.getString(c.getColumnIndex("video_name"));
//            person.wonderful = c.getString(c.getColumnIndex("wonderful"));
//            person.tv_name = c.getString(c.getColumnIndex("tv_name"));
//            person.description = c.getString(c.getColumnIndex("description"));
//            person.create_Time = c.getString(c.getColumnIndex("create_Time"));
//            person.clientPic_Url = c.getString(c.getColumnIndex("clientPic_Url"));
//            person.client_FirstPic_Url = c.getString(c.getColumnIndex("client_FirstPic_Url"));
//            person.orderBy = c.getInt(c.getColumnIndex("orderBy"));
//            persons.add(person);
//        }
//        c.close();
//        return persons;
//    }
//
//    public List<LiveInfo> queryPagerLiveInfo(int pageSize, int page) {
//        List<LiveInfo> persons = new ArrayList<>();
//        Cursor c = queryLiveInfoPagerTheCursor(pageSize, page);
//        while (c.moveToNext()) {
//            LiveInfo person = new LiveInfo();
//            person.id = c.getString(c.getColumnIndex("id"));
//            person.isLook = c.getString(c.getColumnIndex("isLook"));
//            person.live_Url = c.getString(c.getColumnIndex("live_Url"));
//            person.logo_url = c.getString(c.getColumnIndex("logo_url"));
//            person.remarks = c.getString(c.getColumnIndex("remarks"));
//            person.video_name = c.getString(c.getColumnIndex("video_name"));
//            person.wonderful = c.getString(c.getColumnIndex("wonderful"));
//            person.tv_name = c.getString(c.getColumnIndex("tv_name"));
//            person.description = c.getString(c.getColumnIndex("description"));
//            person.create_Time = c.getString(c.getColumnIndex("create_Time"));
//            person.clientPic_Url = c.getString(c.getColumnIndex("clientPic_Url"));
//            person.client_FirstPic_Url = c.getString(c.getColumnIndex("client_FirstPic_Url"));
//            person.orderBy = c.getInt(c.getColumnIndex("orderBy"));
//            persons.add(person);
//        }
//        c.close();
//        return persons;
//    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryLiveInfoTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM LiveInfo", null);
        return c;
    }

    public Cursor queryLiveInfoPagerTheCursor(int pageSize, int page) {
        //使用limit分页查询
        Cursor c = db.rawQuery("SELECT * FROM LiveInfo limit " + (page - 1) * pageSize + "," + pageSize + "", null);
        return c;
    }


    /**
     * query all persons, return cursor=======================直播表操作end=====================================================
     *
     * @return Cursor
     */


    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}