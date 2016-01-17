package activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.anesa.test.R;
import app.AppConfig;
import helper.ConnectImg;
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

    private ImageView imageView;
    private helper.ConnectImg ConnectImg;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser
    JSONParser jsonParser = new JSONParser();


    //JSON Objekt
    static JSONObject receptObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visa_recept_layout);

        imageView = (ImageView) findViewById(R.id.imageView_mina_recept);

        // Hämtar recept från intent
        Intent i = getIntent();

        ConnectImg = new ConnectImg();

        //Hämtar rid från intent dvs det unika recept id
        rid = i.getStringExtra(AppConfig.TAG_RID);

        new GetRecept().execute();

        getImage();

    }

    //Förstorar Bild i en popup ruta
    private void popUpBild(ImageView imageView, int width, int height) {

        ImageView tempImageView = imageView;


        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.mall_dialog_bild,
                (ViewGroup) findViewById(R.id.layout_grund));
        ImageView image = (ImageView) layout.findViewById(R.id.storBild);
        image.setImageDrawable(tempImageView.getDrawable());
        imageDialog.setView(layout);
        imageDialog.setPositiveButton(R.string.stäng_dialog_box, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });


        imageDialog.create();
        imageDialog.show();
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

                            receptObj = (JSONObject) json.get(AppConfig.TAG_RECEPT);
                            Log.i("JSONArray: ", receptObj.toString());

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
                txtName.setText("Skapad av: " + receptObj.getString(AppConfig.TAG_NAME));
                txtReceptNamn.setText(receptObj.getString(AppConfig.TAG_RECEPT_NAME));
                txtBeskrivning.setText(receptObj.getString(AppConfig.TAG_BESKRIVNING));
                txtTyp.setText(receptObj.getString(AppConfig.TAG_TYP));
                txtTid.setText(receptObj.getString(AppConfig.TAG_TID));
                txtPortioner.setText(receptObj.getString(AppConfig.TAG_PORTIONER));


                txtIngredienser.setText(receptObj.getString(AppConfig.TAG_INGREDIENSER));

                txtTillagning.setText(receptObj.getString(AppConfig.TAG_TILLAGNING));



                // stänger ner pdialog när allt har laddats
                pDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void getImage() {

        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ReceptAct.this, "Uploading...", null,true,true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                loading.dismiss();
                imageView.setImageBitmap(b);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popUpBild(imageView, 250, 250);
                    }
                });

            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String id = params[0];
               // String add = "http://eatwit.se/android_pictures/getImage.php?id="+id;
                String add = AppConfig.URL_GET_IMG+id;
                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(add);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }
        }

        GetImage gi = new GetImage();
        gi.execute(rid);
    }

}
