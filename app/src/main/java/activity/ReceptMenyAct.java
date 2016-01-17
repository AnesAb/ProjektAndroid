package activity;

import com.example.anesa.test.R;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class ReceptMenyAct extends ActionBarActivity {

    ImageView btnAllaRecept;
    ImageView btnMinaRecept;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recept_meny);

        // Buttons
        btnAllaRecept = (ImageView) findViewById(R.id.btn_alla_recept);

        btnMinaRecept = (ImageView) findViewById(R.id.btn_mina_recept);

        // view recepts click event
        btnAllaRecept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //  Startar alla recept
                Intent i = new Intent(getApplicationContext(), AllaReceptAct.class);
                startActivity(i);

            }
        });

        // view recepts click event
        btnMinaRecept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Startar mina recept
                Intent i = new Intent(getApplicationContext(), AllaMinaReceptAct.class);
                startActivity(i);

            }
        });

    }
}
