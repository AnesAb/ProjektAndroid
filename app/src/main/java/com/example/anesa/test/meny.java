package com.example.anesa.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import activity.GetMinaRecept;
import activity.RegisterActivity;
import activity.RegisterRecept;
import camera.C_Activity;
import helper.SQLiteHandler;
import helper.SQLiteHandlerRecept;
import helper.SessionManager;


/**
 * Created by Loso on 2015-11-09.
 */



public class meny extends Activity {


    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;

    private SQLiteHandler db;
    private SessionManager session;
    public String name;
    public String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meny);

        txtName = (TextView) findViewById(R.id.name);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // Hämtar användarinfo från databas
        HashMap<String, String> user = db.getUserDetails();

        name = user.get("name");
        uid = user.get("uid");

        // Visar användarens namn
        txtName.setText(name);

    }

    public void onBtn2(View view) {
        Intent getBookScreenIntent = new Intent (this, sida2.class);
        startActivity(getBookScreenIntent);
    }

    public void onBtnTillFlip(View view) {
        Intent getSi = new Intent (this, sida3.class);

        startActivity(getSi);

    }

    public void onBtnLogOut(View view) {
        Intent getBookScreenIntent = new Intent (this, loggaUt.class);

        startActivity(getBookScreenIntent);

    }

    public void onBtnKamera(View view) {

        Intent getBookScreenIntent = new Intent (this, C_Activity.class);
        startActivity(getBookScreenIntent);

    }

    public void onBtnAddRecept(View view) {

        Intent getBookScreenIntent = new Intent (this, RegisterRecept.class);
        startActivity(getBookScreenIntent);

    }

    public void onBtnShowRecept(View view) {

        Intent getBookScreenIntent = new Intent (this, GetMinaRecept.class);
        startActivity(getBookScreenIntent);

    }


}