package activity;

/**
 * Created by Loso on 2015-11-09.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.anesa.test.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import camera.C_Activity;
import helper.SQLiteHandler;
import helper.SessionManager;



public class RegisterRecept extends Activity {
    private static final String TAG = RegisterRecept.class.getSimpleName();

    private EditText inputReceptNamn;
    //private EditText inputTyp;
    private EditText inputTid;
    //private EditText inputPortioner;
    private EditText inputBeskrivning;
    private EditText inputTillagning;
    private EditText inputIngredienser;


    private Spinner inputTyp;
    private Spinner inputPortioner;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private TextView txtName;
    private TextView txtUID;



    private Button btnSparaRecept;
    private Button btnTaBild;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lagg_till_recept);


        //Spinner metod
        Spinner();

        btnSparaRecept = (Button) findViewById(R.id.btn_recept_SparaRecept);
        btnTaBild = (Button) findViewById(R.id.btn_recept_ta_bild);

        inputReceptNamn = (EditText) findViewById(R.id.editText_recept_namn);
       // inputTyp = (EditText) findViewById(R.id.editText_recept_typ);
        inputTid = (EditText) findViewById(R.id.editText_recept_tid);
       // inputPortioner = (EditText) findViewById(R.id.editText_recept_portioner);
        inputBeskrivning = (EditText) findViewById(R.id.editText_recept_beskrining);
        inputTillagning = (EditText) findViewById(R.id.editText_recept_tillagning);
        inputIngredienser = (EditText) findViewById(R.id.editText_recept_ingredienser);

        inputTyp = (Spinner) findViewById(R.id.spinner_recept_typ);
        inputPortioner = (Spinner) findViewById(R.id.spinner_recept_portioner);


        db = new SQLiteHandler(getApplicationContext());

        session = new SessionManager(getApplicationContext());

        txtName = (TextView) findViewById(R.id.textView_hamta_Name);
        txtUID = (TextView) findViewById(R.id.textView_hamta_Uid);

        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String uid = user.get("uid");

        txtName.setText(name);
        txtUID.setText(uid);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Spara Button Click event
        btnSparaRecept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = txtName.getText().toString().trim();
                String receptNamn = inputReceptNamn.getText().toString().trim();
                //String typ = inputTyp.getText().toString().trim();
                String typ = inputTyp.getSelectedItem().toString();

                String tid = inputTid.getText().toString().trim();

                //String portioner = inputPortioner.getText().toString().trim();
                String portioner = inputPortioner.getSelectedItem().toString();

                String beskrivning = inputBeskrivning.getText().toString().trim();
                String tillagning = inputTillagning.getText().toString().trim();
                String ingredienser = inputIngredienser.getText().toString().trim();
                String uid = txtUID.getText().toString().trim();


                if (!receptNamn.isEmpty() && !typ.isEmpty() && !tid.isEmpty() && !portioner.isEmpty() && !beskrivning.isEmpty() && !tillagning.isEmpty()
                        && !ingredienser.isEmpty()) {
                    registerRecept(name, receptNamn, typ, tid, portioner, beskrivning, tillagning, ingredienser, uid);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Du måste fylla i alla fält för att kunna ladda spara receptet!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Spara Button Click event
        btnTaBild.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent myIntent = new Intent(RegisterRecept.this, C_Activity.class);

                RegisterRecept.this.startActivity(myIntent);

            }

        });

    }

    /**
     * Metod som lagrar användare i databas
     * parametrar namn, email, lösenord
     * */
    private void registerRecept(final String name, final String receptNamn, final String typ, final String tid, final String portioner,
                                final String beskrivning, final String tillagning, final String ingredienser, final String uid) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RECEPT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                //försöker skapa ett JSON objekt
                try {
                    JSONObject jObjR = new JSONObject(response);
                    boolean error = jObjR.getBoolean("error");
                    if (!error) {

                        //String rid = jObjR.getString("rid");

                        JSONObject recept = jObjR.getJSONObject("recept");
                        String name = recept.getString("name");
                        String receptNamn = recept.getString("receptNamn");

                        String typ = recept.getString("typ");
                        String tid = recept.getString("tid");
                        String portioner = recept.getString("portioner");
                        String beskrivning = recept.getString("beskrivning");
                        String tillagning = recept.getString("tillagning");
                        String ingredienser = recept.getString("ingredienser");
                        String uid = recept.getString("uid");

                        // lägger till rad med användare i databas
                        db.addRecept(name, receptNamn, typ, tid, portioner, beskrivning, tillagning, ingredienser, uid);
                        Toast.makeText(getApplicationContext(), "Recept har lagts till", Toast.LENGTH_LONG).show();


                    } else {

                        // fel i registrationen. hämtar felmeddelande
                        String errorMsg = jObjR.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("receptNamn", receptNamn);
                params.put("typ", typ);
                params.put("tid", tid);
                params.put("portioner", portioner);
                params.put("beskrivning", beskrivning);
                params.put("tillagning", tillagning);
                params.put("ingredienser", ingredienser);
                params.put("uid", uid);

                return params;
            }

        };

        // Lägger till request i kön
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void Spinner (){

        Spinner Spinner_typ = (Spinner) findViewById(R.id.spinner_recept_typ); //

        // Skapar en ArrayAdapter
        ArrayAdapter<CharSequence> typAdapter = ArrayAdapter
                .createFromResource(this, R.array.array_typ_maltid,
                        android.R.layout.simple_spinner_item);

        //vanlig layout används
        typAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Sätter adapter till spinner
        Spinner_typ.setAdapter(typAdapter);

        Spinner Spinner_portioner = (Spinner) findViewById(R.id.spinner_recept_portioner);

        String[] items = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);

        Spinner_portioner.setAdapter(adapter);

        Spinner_portioner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }


    }
