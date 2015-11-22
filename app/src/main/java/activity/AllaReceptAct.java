package activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.anesa.test.R;

import app.AppConfig;
import helper.JSONParser;

public class AllaReceptAct extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON Parser
    JSONParser jParser = new JSONParser();

    //Receptlista
    ArrayList<HashMap<String, String>> receptList;

    // recept JSONArray
    JSONArray recepts = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alla_recept_lista);

        // Hashmap för receptList
        receptList = new ArrayList<HashMap<String, String>>();

        // Laddar in recept i bakgrund Thread
        new LoadAllRecept().execute();

        ListView lv = getListView();

        // om ett specifikt recept väljs så startas vyn för att kolla recept
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // hämtar värdet från listview
                String rid = ((TextView) view.findViewById(R.id.rid)).getText()
                        .toString();

                // När det har hämtas så startas ny intent
                Intent intent = new Intent(getApplicationContext(),
                        ReceptAct.class);
                // skickar rid till nästa intent
                intent.putExtra(AppConfig.TAG_RID, rid);

                // Startar aktivitet och väntar på respons från onActivity 100 är värdet den vill ha
                startActivityForResult(intent, 100);
            }
        });

    }

    // Respons
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    /**
     * Bakgrund Async Task för att ladda alla recept från server genom HTTP Request
     * */
    class LoadAllRecept extends AsyncTask<String, String, String> {

        /**
         * innan bakgrundsaktivitet startar visar den Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllaReceptAct.this);
            pDialog.setMessage("Laddar recept. Vänligen vänta...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * hämtar alla recept i bakgrund
         * */
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Hämtar JSON string från angivet url
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_HAMTA_ALLA_RECEPT, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Alla Recept: ", json.toString());

            try {

                int success = json.getInt(AppConfig.TAG_SUCCESS);

                if (success == 1) {
                    // om recept har hittats, Hämtas Arrayen av recept
                    recepts = json.getJSONArray(AppConfig.TAG_RECEPT);

                    // loopar genom alla recept i arrayen
                    for (int i = 0; i < recepts.length(); i++) {
                        JSONObject j = recepts.getJSONObject(i);

                        // Sparar de värden man vill ha från json objektet
                        String rid = j.getString(AppConfig.TAG_RID);
                        String receptName = j.getString(AppConfig.TAG_RECEPT_NAME);

                        // skapar hashmap där hämtade värden läggs till
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(AppConfig.TAG_RID, rid);
                        map.put(AppConfig.TAG_RECEPT_NAME, receptName);

                        // lägger till HashListen map till ArrayListen receptList
                        receptList.add(map);
                    }
                } else {
                    // om inga recept hittas kör skapa nytt recept
                    Intent i = new Intent(getApplicationContext(),
                            NyttReceptAct.class);
                    // Stänger aktiviteter
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Stänger ner progress dialog
         * **/
        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
            //
            runOnUiThread(new Runnable() {
                public void run() {
                     // uppdaterar json till ListView
                    ListAdapter adapter = new SimpleAdapter(
                            AllaReceptAct.this, receptList,
                            R.layout.list_item, new String[] {AppConfig.TAG_RID, AppConfig.TAG_RECEPT_NAME},
                            new int[] { R.id.rid, R.id.recept_name});
                    // uppdaterar listview
                    setListAdapter(adapter);
                }
            });

        }

    }
}