package activity;

import com.example.anesa.test.R;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MinaReceptMenyAct extends ActionBarActivity {

    Button btnAllaRecept;
    Button btnNyttRecept;
    Button btnMinaRecept;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mina_recept_main);

        // Buttons
        btnAllaRecept = (Button) findViewById(R.id.btn_alla_recept);
        btnNyttRecept = (Button) findViewById(R.id.btn_nytt_recept);
        btnMinaRecept = (Button) findViewById(R.id.btn_mina_recept);

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
        btnNyttRecept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // startar nytt recept
                Intent i = new Intent(getApplicationContext(), NyttReceptAct.class);
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
