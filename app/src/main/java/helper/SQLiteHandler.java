package helper;

/**
 * Created by Loso on 2015-11-09.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();


    // databas Version
    private static final int DATABASE_VERSION = 1;

    // database Namn
    private static final String DATABASE_NAME = "android_api";

    // login tabell namn
    private static final String TABLE_USER = "user";
    private static final String TABLE_RECEPT = "recept";

    // login tabell kolumn namn
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    //recept tabell kolumn namn
    private static final String KEY_RID = "rid";
    private static final String KEY_RECEPT_NAMN = "receptNamn";
    private static final String KEY_TYP = "typ";
    private static final String KEY_TID = "tid";
    private static final String KEY_PORTIONER = "portioner";
    private static final String KEY_BESKRIVNING = "beskrivning";
    private static final String KEY_TILLAGNING = "tillagning";
    private static final String KEY_INGREDIENSER = "ingredienser";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Skapar databas
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "skapat användare tabell");

        //recept

        String CREATE_RECEPT_TABLE = "CREATE TABLE " + TABLE_RECEPT + "("
                + KEY_RID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_RECEPT_NAMN + " TEXT," +  KEY_TYP + " TEXT," + KEY_TID + " TEXT,"
                + KEY_PORTIONER + " TEXT," + KEY_BESKRIVNING + " TEXT,"
                + KEY_TILLAGNING + " TEXT," + KEY_INGREDIENSER + " TEXT," + KEY_UID + " TEXT" + ")";
        db.execSQL(CREATE_RECEPT_TABLE);

        Log.d(TAG, "Skapat recept tabell");

    }

    // uppgraderar databas
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop äldre tabel om den existerar
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER + "," + TABLE_RECEPT);


                // skapar tabeller igen
                onCreate(db);

    }

    /**
     * Metod som lägger till användare i databas,
     * */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_UID, uid);
        values.put(KEY_CREATED_AT, created_at);

        // Lägger till rad
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Stänger DB connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Metod som lägger till användare i databas,
     * */
    public void addRecept(String name, String receptNamn, String typ, String tid, String portioner,
                          String beskrivning, String tillagning, String ingredienser, String uid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, name);
        values.put(KEY_RECEPT_NAMN , receptNamn);
        values.put(KEY_TYP, typ);
        values.put(KEY_TID , tid);
        values.put(KEY_PORTIONER, portioner);
        values.put(KEY_BESKRIVNING , beskrivning);
        values.put(KEY_TILLAGNING, tillagning);
        values.put(KEY_INGREDIENSER, ingredienser);
        values.put(KEY_UID, uid);


        // Lägger till rad
        long rid = db.insert(TABLE_RECEPT, null, values);
        db.close(); // Stänger DB connection

        Log.d(TAG, "New recept inserted into sqlite: " + rid);
    }


    //Hämtar användare från databas
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Flyttar till första rad
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // returnar user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    //Hämtar recept från databas
    public HashMap<String, String> getReceptDetails(String uid) { //TODO Ej kan ej hämta recept igen om användare loggar ut. Funktion för att uppdatera lokal databas med server behövs??
        HashMap<String, String> recept = new HashMap<String, String>();
        //String selectQuery = "SELECT  * FROM " + TABLE_RECEPT;


        System.out.println("DEEEEEEEEENNNN KÖÖÖÖÖÖÖRS");

        String selectQuery = "SELECT *FROM " + TABLE_RECEPT + " WHERE uid" + "=\"" + uid + "\"";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Flyttar till första rad
        cursor.moveToFirst();
        Log.d(TAG, "CursorCount: " + cursor.getCount());
        if (cursor.getCount() > 0) {
            recept.put("name", cursor.getString(1));
            recept.put("receptNamn", cursor.getString(2));
            recept.put("typ", cursor.getString(3));
            recept.put("tid", cursor.getString(4));
            recept.put("portioner", cursor.getString(5));
            recept.put("beskrivning", cursor.getString(6));
            recept.put("tillagning", cursor.getString(7));
            recept.put("ingredienser", cursor.getString(8));
            System.out.println("DEEEEEEEEENNNN KÖÖÖÖÖÖÖRS HÄR NÄR DET FUNKAR");

        }
        cursor.close();
        db.close();
        // returnar recept
        Log.d(TAG, "Fetching recept from Sqlite: " + recept.toString());

        return recept;
    }

    //TODO Hämta Specifikt recept från användare



    // Ta bort alla rader i databas
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    // Ta bort alla rader i databas
    public void deleteRecept() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECEPT, null, null);
        db.close();

        Log.d(TAG, "Deleted all recept info from sqlite");
    }

}