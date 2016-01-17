package activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.anesa.test.R;
import app.AppConfig;
import camera.C_Activity;
import helper.JSONParser;
import helper.SQLiteHandler;

public class NyttReceptAct extends Activity implements AdapterView.OnItemSelectedListener {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    private EditText inputReceptNamn;
    private EditText inputBeskrivning;
    private EditText inputTillagning;
    private AutoCompleteTextView inputIngredienser;
    private Spinner inputTyp;
    private Spinner inputPortioner;
    private Spinner inputMangd;
    private Spinner inputMatt;
    private Spinner inputTid;
    private TextView txtName;
    private TextView txtUID;
    private SQLiteHandler db;
    private Button btnSparaRecept;
    private ImageButton btnTaBild;
    private EditText editTextReceptIngredienser;
    private ListView listViewIngredientList;
    private TextView ing;
    private static String allt;
    public static int bildKoll = 0;
    private static boolean faltKontroll;

    // autocomplete
    InputStream is=null;
    String result=null;
    String line=null;

    // Progress Dialog
    private ProgressDialog pDialogAuto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lagg_till_recept);

        editTextReceptIngredienser= (EditText) findViewById(R.id.autocomp_recept_ingredienser);
        listViewIngredientList= (ListView) findViewById(R.id.ingredient_list);

        //Gör det möjligt att ha en scroll i listview när man har en scroll på hela sidan
        listViewIngredientList.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        final ArrayList<String> ingredientItems = new ArrayList<>();
        final  ArrayAdapter<String> aa;

        aa= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ingredientItems);

        listViewIngredientList.setAdapter(aa);


        //Startar Spinners
        Spinner();

        //autocomplete
        // Hämtar recept från intent
        Intent i = getIntent();

        // Getting complete product details in background thread
        new GetIngrediens().execute();


        //Knappar
        btnSparaRecept = (Button) findViewById(R.id.btn_recept_SparaRecept);
        btnTaBild = (ImageButton) findViewById(R.id.btn_recept_ta_bild);

        inputReceptNamn = (EditText) findViewById(R.id.editText_recept_namn);
        inputTid = (Spinner) findViewById(R.id.spinner_recept_tid);
        inputBeskrivning = (EditText) findViewById(R.id.editText_recept_beskrining);
        inputTillagning = (EditText) findViewById(R.id.editText_recept_tillagning);
        inputIngredienser = (AutoCompleteTextView) findViewById(R.id.autocomp_recept_ingredienser);

        inputTyp = (Spinner) findViewById(R.id.spinner_recept_typ);
        inputPortioner = (Spinner) findViewById(R.id.spinner_recept_portioner);

        inputMangd = (Spinner) findViewById(R.id.amount_spinner);
        inputMatt = (Spinner) findViewById(R.id.measure_spinner);


        ing = (TextView) findViewById(R.id.editText_ing);

        //Hämta namn och uid från användare tabell

        db = new SQLiteHandler(getApplicationContext());

        txtName = (TextView) findViewById(R.id.textView_hamta_Name);
        txtUID = (TextView) findViewById(R.id.textView_hamta_Uid);


        HashMap<String, String> user = db.getUserDetails();


        String name = user.get("name");
        String uid = user.get("uid");
        txtName.setText(name);
        txtUID.setText(uid);

        Intent intent = getIntent();
        bildKoll = intent.getIntExtra("bildKoll", 0);

        if (bildKoll ==1) {
            btnTaBild.setImageResource(R.drawable.bild_tagen);
            btnTaBild.setMaxHeight(70);
            btnTaBild.setMaxWidth(95);
        }


        // button click event
        btnSparaRecept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Skapar ny bakgrundsthread
                if (bildKoll == 1 && !isEmpty(inputReceptNamn,ing,inputBeskrivning,inputTillagning)){
                    new skapaRecept().execute();
                }else {
                    Toast.makeText(getApplicationContext(),"Du har glömt att fylla i ett fält eller så har du glömt ta en bild", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnTaBild.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), C_Activity.class);
                startActivity(myIntent);
                //bildKoll = 1;
                // btnTaBild.setImageResource(R.drawable.bild_tagen);
                // btnTaBild.setMaxHeight(70);
                // btnTaBild.setMaxWidth(95);

            }

        });

    }

    private boolean isEmpty(EditText r, TextView i, EditText b, EditText t) {

        if (r.getText().toString().trim().length() > 0 && i.getText().toString().trim().length() > 0 && b.getText().toString().trim().length() > 0 && t.getText().toString().trim().length() > 0 ) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * Async Task för att skapa recept
     * */
    class skapaRecept extends AsyncTask<String, String, String> {

        /**
         * Innan aktivitet i bakgrund sker så ska Progress Dialog visas
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NyttReceptAct.this);
            pDialog.setMessage("Skapar Recept..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Skapar recept i bakgrunden
         * */
        protected String doInBackground(String... args) {

            String name = txtName.getText().toString().trim();
            String receptNamn = inputReceptNamn.getText().toString().trim();
            String typ = inputTyp.getSelectedItem().toString();
            String tid = inputTid.getSelectedItem().toString().trim();
            String portioner = inputPortioner.getSelectedItem().toString();
            String beskrivning = inputBeskrivning.getText().toString().trim();
            String tillagning = inputTillagning.getText().toString().trim();

            String uid = txtUID.getText().toString().trim();

            String matt = inputMatt.getSelectedItem().toString();
            String mangd = inputMangd.getSelectedItem().toString();

           // String ingredienser = inputMangd.getSelectedItem().toString() + " " + inputMatt.getSelectedItem().toString() + "" + ing.getText().toString();


            String ingredienser = ing.getText().toString();



            // ArrayList
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            //Lägger till parametrar till ArrayList
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("receptNamn", receptNamn));
            params.add(new BasicNameValuePair("typ", typ));
            params.add(new BasicNameValuePair("tid", tid));
            params.add(new BasicNameValuePair("portioner", portioner));
            params.add(new BasicNameValuePair("beskrivning", beskrivning));
            params.add(new BasicNameValuePair("tillagning", tillagning));
            params.add(new BasicNameValuePair("ingredienser", ingredienser));
            params.add(new BasicNameValuePair("uid", uid));

            // Skickar förfrågan till server genom att använda JSON
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_SKAPA_RECEPT, "POST", params);
            Log.d("Create Response", json.toString());

            // Kollar om respons lyckades
            try {
                int success = json.getInt(AppConfig.TAG_SUCCESS);

                if (success == 1) {
                    // Recept har skapats
                    Intent i = new Intent(getApplicationContext(), AllaReceptAct.class);
                    startActivity(i);

                    // Stänger denna aktivitet
                    finish();
                } else {
                    // TODO felmeddelande om recept inte lyckas
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // Dialogen stängs ner
            pDialog.dismiss();
        }
    }

    private void Spinner (){

        Spinner Spinner_typ = (Spinner) findViewById(R.id.spinner_recept_typ);
        Spinner amount_spinner = (Spinner) findViewById(R.id.amount_spinner);
        Spinner measure_spinner = (Spinner) findViewById(R.id.measure_spinner);
        Spinner Spinner_tid = (Spinner) findViewById(R.id.spinner_recept_tid);


        // Skapar en ArrayAdapter
        ArrayAdapter<CharSequence> typAdapter = ArrayAdapter
                .createFromResource(this, R.array.array_typ_maltid,
                        android.R.layout.simple_spinner_item);

        //ArrayAdapter för mängd dropdown.
        ArrayAdapter amount_adapter = ArrayAdapter
                .createFromResource(this, R.array.amount_spinner,
                        android.R.layout.simple_spinner_item);

        //ArrayAdapter för mått dropdown.
        ArrayAdapter measure_adapter = ArrayAdapter
                .createFromResource(this, R.array.measure_spinner,
                        android.R.layout.simple_spinner_item);


        //vanlig layout används
        typAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //TId

        ArrayAdapter<CharSequence> tidAdapter = ArrayAdapter
                .createFromResource(this, R.array.tid_spinner,
                        android.R.layout.simple_spinner_item);


        //Sätter adapter till spinner

        Spinner_typ.setAdapter(typAdapter);
        amount_spinner.setAdapter(amount_adapter);
        measure_spinner.setAdapter(measure_adapter);
        Spinner_tid.setAdapter(tidAdapter);


        amount_spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        measure_spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        Spinner_typ.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        Spinner_tid.setOnItemSelectedListener((AdapterView.OnItemSelectedListener)this);

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

    //Metod för att visa meddelande när användare klockar på ett värde i spinner.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }

    //Metod för att lägga till ingredient från editText_recept_ingredienser till ingredient_list
    public void onBtnClickAddIngredient(View v){

        int k= listViewIngredientList.getCount();
        String a1[]= new String[k+1];
        a1[0] =  inputMangd.getSelectedItem().toString() + " " + inputMatt.getSelectedItem().toString() + " " +  editTextReceptIngredienser.getText().toString();

        // ing.setText(editTextReceptIngredienser.getText().toString());

        editTextReceptIngredienser.setText("");
        for (int i = 0; i < k; i++){
            a1[i + 1] = listViewIngredientList.getItemAtPosition(i).toString();
            //ing.setText(listViewIngredientList.getItemAtPosition(i).toString());
        }
        allt = Arrays.toString(a1);
        ing.setText(allt);
        ArrayAdapter<String> aan = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, a1);
        listViewIngredientList.setAdapter(aan);

    }

    /**
     * bakgrund Async Task för att hämta valt recept
     * */
    class GetIngrediens extends AsyncTask<String, String, String> {

        /**
         * innan bakgrunds thread startar skapas Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialogAuto = new ProgressDialog(NyttReceptAct.this);
            pDialogAuto.setMessage("Laddar. Vänligen Vänta...");
            pDialogAuto.setIndeterminate(false);
            pDialogAuto.setCancelable(true);
            pDialogAuto.show();
        }

        /**
         * Hämtar valt recept bakgrunds thread
         * */

        protected String doInBackground(String... params) {

            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(AppConfig.URL_AUTO_COMP);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                Log.e("Pass 1", "connection success ");
            }
            catch(Exception e)
            {
                Log.e("Fail 1", e.toString());
                Toast.makeText(getApplicationContext(), "Invalid IP Address",
                        Toast.LENGTH_LONG).show();
            }

            try {
                BufferedReader reader = new BufferedReader
                        (new InputStreamReader(is,"UTF-8"));
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                is.close();
                result = sb.toString();
                Log.e("Pass 2", "connection success ");
            }
            catch(Exception e)
            {
                Log.e("Fail 2", e.toString());
            }

            return null;
        }

        /**
         * Post Execute, Sätter namn till alla textviews
         * **/
        protected void onPostExecute(String file_url) {

            try
            {
                JSONArray JA=new JSONArray(result);
                JSONObject json= null;
                final String[] str1 = new String[JA.length()];

                for(int i=0;i<JA.length();i++)
                {
                    json=JA.getJSONObject(i);
                    str1[i]=json.getString("Livsmedelsnamn");
                }

                final AutoCompleteTextView text = (AutoCompleteTextView)
                        findViewById(R.id.autocomp_recept_ingredienser);
                final List<String> list = new ArrayList<String>();

                for(int i=0;i<str1.length;i++)
                {
                    list.add(str1[i]);
                }

                Collections.sort(list);

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                        (getApplicationContext(), android.R.layout.simple_spinner_item, list);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                text.setThreshold(1);
                text.setAdapter(dataAdapter);
                text.setTextColor(getResources().getColor(R.color.black));
                text.setDropDownBackgroundResource(R.color.color_green_logo);

                text.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                        Toast.makeText(getBaseContext(), list.get(arg2).toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(Exception e)
            {
                Log.e("Fail 3", e.toString());
            }
            pDialogAuto.dismiss();


        }

    }
}