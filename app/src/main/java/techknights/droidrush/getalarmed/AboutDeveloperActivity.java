package techknights.droidrush.getalarmed;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.widget.TextView;

public class AboutDeveloperActivity extends AppCompatActivity {

    TextView aman,shivam,sameer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_developer);

        aman= (TextView) findViewById(R.id.aman);
        aman.setMovementMethod(LinkMovementMethod.getInstance());
        aman.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        aman.setLinkTextColor(Color.parseColor("#0000e6"));

        shivam= (TextView) findViewById(R.id.shivam);
        shivam.setMovementMethod(LinkMovementMethod.getInstance());
        shivam.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        shivam.setLinkTextColor(Color.parseColor("#0000e6"));

        sameer= (TextView) findViewById(R.id.sameer);
        sameer.setMovementMethod(LinkMovementMethod.getInstance());
        sameer.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        sameer.setLinkTextColor(Color.parseColor("#0000e6"));
    }
}

