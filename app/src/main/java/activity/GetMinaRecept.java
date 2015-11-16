package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.anesa.test.R;
import com.example.anesa.test.sida3;

import java.util.HashMap;

import camera.C_Activity;
import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by Loso on 2015-11-16.
 */
public class GetMinaRecept extends Activity {

    private TextView txtName;
    private TextView txtUID;

    private TextView txtReceptNamn;
    private TextView txtBeskrivning;
    private TextView txtTyp;
    private TextView txtTid;
    private TextView txtPortioner;
    private TextView txtIngredienser;
    private TextView txtTillagning;


    private SessionManager session;
    private SQLiteHandler db;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mina_recept);

        db = new SQLiteHandler(getApplicationContext());

        txtName = (TextView) findViewById(R.id.textV_mina_recept_name);
        txtUID = (TextView) findViewById(R.id.textV_mina_recept_uid);

        txtReceptNamn = (TextView) findViewById(R.id.textV_mina_recept_recept_namn);
        txtBeskrivning = (TextView) findViewById(R.id.textV_mina_recept_beskrivning);
        txtTyp = (TextView) findViewById(R.id.textV_mina_recept_typ);
        txtTid = (TextView) findViewById(R.id.textV_mina_recept_tid);
        txtPortioner = (TextView) findViewById(R.id.textV_mina_recept_portioner);
        txtIngredienser = (TextView) findViewById(R.id.textV_mina_recept_ingredienser);
        txtTillagning = (TextView) findViewById(R.id.textV_mina_recept_tillagning);


        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();




        String name = user.get("name");
        String uid = user.get("uid");





        HashMap<String, String> recept = db.getReceptDetails(uid);

        String receptNamn = recept.get("receptNamn");
        String beskrivning = recept.get("beskrivning");
        String typ = recept.get("typ");
        String tid = recept.get("tid");
        String portioner = recept.get("portioner");
        String ingredienser = recept.get("ingredienser");
        String tillagning = recept.get("tillagning");


        txtName.setText(name);
        txtUID.setText(uid);

        txtReceptNamn.setText(receptNamn);
        txtBeskrivning.setText(beskrivning);
        txtTyp.setText(typ);
        txtTid.setText(tid);
        txtPortioner.setText(portioner);
        txtIngredienser.setText(ingredienser);
        txtTillagning.setText(tillagning);




    }




}