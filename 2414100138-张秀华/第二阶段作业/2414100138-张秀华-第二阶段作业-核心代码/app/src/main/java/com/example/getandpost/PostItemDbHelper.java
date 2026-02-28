package com.example.getandpost;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PostItemDbHelper extends SQLiteOpenHelper {
    private static final String db_name = "post_database.db";
    private static final String table_name = "post_items";
    private static final int version = 1;
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_BODY = "body";
    private static final String create_table_sql =
            "CREATE TABLE " + table_name + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_BODY + " TEXT" +
                    ")";

    public PostItemDbHelper(Context context) {
        super(context, db_name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreate(db);
    }

    // 清空数据
    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table_name, null, null);
        db.close();
    }

    // 插入数据
    public void insertAll(List<PostItemData> items) {
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < items.size(); i++) {
            PostItemData item = items.get(i);
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, item.getId());
            values.put(COLUMN_USER_ID, item.getUserId());
            values.put(COLUMN_TITLE, item.getTitle());
            values.put(COLUMN_BODY, item.getBody());
            db.insert(table_name, null, values);
        }
        db.close();
    }

    // 查询数据
    public List<PostItemData> getAllItems() {
        SQLiteDatabase db = getReadableDatabase();
        List<PostItemData> postItemList = new ArrayList<>();

        Cursor cursor = db.query(
                table_name,
                null,
                null,
                null,
                null,
                null,
                null,
                "20"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BODY));

                PostItemData item = new PostItemData(id, userId, title, body);
                postItemList.add(item);
            }
            cursor.close();
        }
        db.close();
        return postItemList;
    }

}