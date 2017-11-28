package fr.mdta.mdta.SignaturesScanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.mdta.mdta.R;

public class SignaturesScannerActivity extends AppCompatActivity {

    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signatures_scanner);

        mResultTextView = (TextView) findViewById(R.id.displaySignatureScanner);

        final Button launchButton = (Button) findViewById(R.id.launchButton);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultTextView.setText("This is not already implemented");
            }
        });
    }
}
