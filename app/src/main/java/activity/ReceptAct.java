package activity;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.example.anesa.test.R;
import app.AppConfig;
import helper.JSONParser;

public class ReceptAct extends Activity {

    private TextView txtName;
    private TextView txtReceptNamn;
    private TextView txtBeskrivning;
    private TextView txtTyp;
    private TextView txtTid;
    private TextView txtPortioner;
    private TextView txtIngredienser;
    private TextView txtTillagning;
    String rid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser
    JSONParser jsonParser = new JSONParser();


    //JSON Objekt
    static JSONObject productObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visa_recept_layout);

        // Hämtar recept från intent
        Intent i = getIntent();

        //Hämtar rid från intent dvs det unika recept id
        rid = i.getStringExtra(AppConfig.TAG_RID);

        // Getting complete product details in background thread
        new GetRecept().execute();
    }

    /**
     * bakgrund Async Task för att hämta valt recept
     * */
    class GetRecept extends AsyncTask<String, String, String> {

        /**
         * innan bakgrunds thread startar skapas Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReceptAct.this);
            pDialog.setMessage("Laddar recept. Vänligen Vänta...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Hämtar valt recept bakgrunds thread
         * */
        protected String doInBackground(String... params) {

                    // Check for success tag
                    int success;
                    try {
                        // ArrayList och servern ska hitta rätt recept genom rid
                        List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                        params2.add(new BasicNameValuePair("rid", rid));

                        // hämtar recept genom att kontakta server genom HTTP
                        JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_HAMTA_SPECIFIKT_RECEPT, "GET", params2);

                        // För felsökning
                        Log.d("Recept Detaljer", json.toString());

                        // json success tag
                        success = json.getInt(AppConfig.TAG_SUCCESS);
                        if (success == 1) {

                            productObj = (JSONObject) json.get(AppConfig.TAG_RECEPT);
                            Log.i("JSONArray: ", productObj.toString());

                            txtName = (TextView) findViewById(R.id.textV_mina_recept_name);
                            txtReceptNamn = (TextView) findViewById(R.id.textV_mina_recept_recept_namn);
                            txtBeskrivning = (TextView) findViewById(R.id.textV_mina_recept_beskrivning);
                            txtTyp = (TextView) findViewById(R.id.textV_mina_recept_typ);
                            txtTid = (TextView) findViewById(R.id.textV_mina_recept_tid);
                            txtPortioner = (TextView) findViewById(R.id.textV_mina_recept_portioner);
                            txtIngredienser = (TextView) findViewById(R.id.textV_mina_recept_ingredienser);
                            txtTillagning = (TextView) findViewById(R.id.textV_mina_recept_tillagning);

                        }else{
                            // recept rid har inte hittats
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            return null;
        }

        /**
         * Post Execute, Sätter namn till alla textviews
         * **/
        protected void onPostExecute(String file_url) {


            // Visar information från valt recept i textView

            try {
                txtName.setText(productObj.getString(AppConfig.TAG_NAME));
                txtReceptNamn.setText(productObj.getString(AppConfig.TAG_RECEPT_NAME));
                txtBeskrivning.setText(productObj.getString(AppConfig.TAG_BESKRIVNING));
                txtTyp.setText(productObj.getString(AppConfig.TAG_TYP));
                txtTid.setText(productObj.getString(AppConfig.TAG_TID));
                txtPortioner.setText(productObj.getString(AppConfig.TAG_PORTIONER));
                txtIngredienser.setText(productObj.getString(AppConfig.TAG_INGREDIENSER));
                txtTillagning.setText(productObj.getString(AppConfig.TAG_TILLAGNING));

                // stänger ner dialog när allt har laddats
                pDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //TODO Spara och ta bort recept

}
