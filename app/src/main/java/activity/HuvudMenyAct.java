package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.example.anesa.test.R;


/**
 * Created by Loso on 2015-11-09.
 */

public class HuvudMenyAct extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meny);


    }

    public void onBtnAddRecept(View view) {

        Intent getAddReceptScreenIntent = new Intent (this, NyttReceptAct.class);
        startActivity(getAddReceptScreenIntent);
    }

    public void onBtnShowRecept(View view) {

        Intent getShowReceptScreenIntent = new Intent(this, ReceptMenyAct.class);
        startActivity(getShowReceptScreenIntent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem mItem=menu.findItem(R.id.loggaUt_meny);
        mItem.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent loggaUtIntent = new Intent(HuvudMenyAct.this, LoggaUtAct.class);
                        startActivity(loggaUtIntent);
                        return false;
                    }
                });
        return true;
    }


}