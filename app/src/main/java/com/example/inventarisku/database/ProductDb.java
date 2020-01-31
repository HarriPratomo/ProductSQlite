package com.example.inventarisku.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.inventarisku.model.ProductObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDb extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "product";

    public ProductDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProductObject.CREATE_TABle);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProductObject.TABLE_NAME);
        onCreate(db);
    }

    public long insertProduct(ProductObject object) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(ProductObject.COLUMN_NAME, object.getName());
        cv.put(ProductObject.COLUMN_PRICE, object.getPrice());
        cv.put(ProductObject.COLUMN_IMG, object.getImg());

        long id = db.insert(ProductObject.TABLE_NAME, null, cv);
        db.close();
        return id;
    }

    public ProductObject getProduct(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(ProductObject.TABLE_NAME, new String[]{ProductObject.COLUMN_ID, ProductObject.COLUMN_NAME, ProductObject.COLUMN_IMG,
                        ProductObject.COLUMN_PRICE, ProductObject.COLUMN_TIME}, ProductObject.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        ProductObject productObject = new ProductObject(
                cursor.getInt(cursor.getColumnIndex(ProductObject.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(ProductObject.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(ProductObject.COLUMN_PRICE)),
                cursor.getString(cursor.getColumnIndex(ProductObject.COLUMN_IMG)),
                cursor.getString(cursor.getColumnIndex(ProductObject.COLUMN_TIME))

        );
        cursor.close();
        return productObject;
    }

    public List<ProductObject> getAllProduct() {
        List<ProductObject> products = new ArrayList<>();

        String selectQuery = " SELECT * FROM " + ProductObject.TABLE_NAME + " ORDER BY " + ProductObject.COLUMN_TIME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ProductObject productObject = new ProductObject();
                productObject.setId(cursor.getInt(cursor.getColumnIndex(ProductObject.COLUMN_ID)));
                productObject.setName(cursor.getString(cursor.getColumnIndex(ProductObject.COLUMN_NAME)));
                productObject.setPrice(cursor.getString(cursor.getColumnIndex(ProductObject.COLUMN_PRICE)));
                productObject.setImg(cursor.getString(cursor.getColumnIndex(ProductObject.COLUMN_IMG)));
                productObject.setTime(cursor.getString(cursor.getColumnIndex(ProductObject.COLUMN_TIME)));


                products.add(productObject);
            } while
            (cursor.moveToNext());
        }
        db.close();
        return products;
    }

    public int getProductCount() {
        String countQuery = " SELECT * FROM " + ProductObject.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateProduct(ProductObject productObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ProductObject.COLUMN_NAME, productObject.getName());
        cv.put(ProductObject.COLUMN_PRICE, productObject.getPrice());
        cv.put(ProductObject.COLUMN_IMG, productObject.getImg());

        return db.update(ProductObject.TABLE_NAME, cv, ProductObject.COLUMN_ID + "=?", new String[]{String.valueOf(productObject.getId())});

    }

    public void deleteProduct(ProductObject productObject) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ProductObject.TABLE_NAME, ProductObject.COLUMN_ID + "=?", new String[]{String.valueOf(productObject.getId())});
        db.close();
    }
}
