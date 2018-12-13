package www.cvit.leafrecognizer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.TextView;

public class ActivityInstructions extends AppCompatActivity {

    private TextView card1;
    private TextView card2;
    private TextView card3;
    private TextView card4;
    private TextView card5;
    private TextView card6;
    private TextView card7;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        card1 = (TextView)findViewById(R.id.inst1).findViewById(R.id.cardview_text);
        card1.setText(getString(R.string.intro_inst1));

        card2 = (TextView)findViewById(R.id.inst2).findViewById(R.id.cardview_text);
        card2.setText(getString(R.string.intro_inst2));

        card3 = (TextView)findViewById(R.id.inst3).findViewById(R.id.cardview_text);
        card3.setText(getString(R.string.intro_inst3));

        card4 = (TextView)findViewById(R.id.inst4).findViewById(R.id.cardview_text);
        card4.setText(getString(R.string.intro_inst4));

        card5 = (TextView)findViewById(R.id.inst5).findViewById(R.id.cardview_text);
        card5.setText(getString(R.string.intro_inst5));

        card6 = (TextView)findViewById(R.id.inst6).findViewById(R.id.cardview_text);
        card6.setText(getString(R.string.intro_inst6));

        card7 = (TextView)findViewById(R.id.inst7).findViewById(R.id.cardview_text);
        card7.setText(getString(R.string.intro_inst7));


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
