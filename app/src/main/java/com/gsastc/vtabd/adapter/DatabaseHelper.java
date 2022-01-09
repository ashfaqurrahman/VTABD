package com.gsastc.vtabd.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String VTABD = "vtabd.db";
    private static final String MY_CART = "my_cart";
    private static final String productId = "PRODUCT_ID";
    private static final String targetedUserId = "TARGETED_USER_ID";
    private static final String name = "NAME";
    private static final String type = "TYPE";
    private static final String checkInCheckOut = "CHECK_IN_CHECK_OUT";
    private static final String rantPerDay = "RANT_PER_DAY";
    private static final String day = "DAY";
    private static final String totalAmount = "TOTAL_AMOUNT";

    public DatabaseHelper(@Nullable Context context) {
        super(context, VTABD, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String myCart = "CREATE TABLE " + MY_CART + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " PRODUCT_ID TEXT, TARGETED_USER_ID TEXT, NAME TEXT, TYPE TEXT, CHECK_IN_CHECK_OUT TEXT, RANT_PER_DAY TEXT, DAY TEXT, TOTAL_AMOUNT TEXT)";
        db.execSQL(myCart);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MY_CART);
        onCreate(db);
    }

    public boolean addData(String _ProductId, String _TargetedUserId, String _Name, String _Type, String _CheckInCheckOut, String _RantPerDay, String _Day, String _TotalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(productId, _ProductId);
        contentValues.put(targetedUserId, _TargetedUserId);
        contentValues.put(name, _Name);
        contentValues.put(type, _Type);
        contentValues.put(checkInCheckOut, _CheckInCheckOut);
        contentValues.put(rantPerDay, _RantPerDay);
        contentValues.put(day, _Day);
        contentValues.put(totalAmount, _TotalAmount);

        long result = db.insert(MY_CART, null, contentValues);

        return result != -1;
    }

    public Cursor getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + MY_CART, null);
        return data;
    }

    public Cursor getQuantity(int _ProductId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data1 = db.rawQuery("SELECT quantity FROM " + MY_CART + " WHERE " + "PRODUCT_ID = ?" , new String[] {String.valueOf(_ProductId)});
        return data1;
    }

    public void deleteData () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + MY_CART);
    }

    public void removeItem(int name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "productId=?";
        String[] whereArgs = new String[] { String.valueOf(name) };
        db.delete(MY_CART, whereClause, whereArgs);
    }
}
