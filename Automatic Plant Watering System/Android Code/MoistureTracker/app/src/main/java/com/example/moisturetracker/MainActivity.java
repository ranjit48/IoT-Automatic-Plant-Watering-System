package com.example.moisturetracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private ImageView pumpStatusImageView, ledStatus;
    private final int greenLed = R.drawable.greenled;
    private final int redLed = R.drawable.redled;
    private Vibrator vibrator;
    private Button onButton;
    private Button offButton;
    private final int onDrawable = R.drawable.on;
    private final int offDrawable = R.drawable.off;
    private TextView moistureLevelText;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ProgressBar and TextView
        progressBar = findViewById(R.id.circularProgressBar);
        moistureLevelText = findViewById(R.id.moistureLevelText);

        // Initialize WebView
        // webView = findViewById(R.id.webview);

        // For vibration
        onButton = findViewById(R.id.button1);
        offButton = findViewById(R.id.button2);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Enable JavaScript
        // WebSettings webSettings = webView.getSettings();
        // webSettings.setJavaScriptEnabled(true);

        // Load a web page
        // webView.setWebViewClient(new WebViewClient());
        // webView.loadUrl("https://thingspeak.com/channels/2510392/charts/1?bgcolor=%23fffff&color=%23d62020&dynamic=true&results=60&type=line");

        // Optional: handle page navigation within the WebView
        // webView.setWebViewClient(new WebViewClient() {
        //     @Override
        //     public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //         view.loadUrl(url);
        //         return true;
        //     }
        // });

        // LED status
        ledStatus = findViewById(R.id.ledstatus);

        // Initialize Firebase and ImageView
        pumpStatusImageView = findViewById(R.id.pump_status);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("pumpStatus");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("pumpForce");
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("ledStatus");
        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("sensorValue");

        // Add the listener to get the pump status changes
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pumpStatus = dataSnapshot.getValue(String.class);
                if (pumpStatus != null) {
                    updatePumpStatusUI(pumpStatus);
                } else {
                    Toast.makeText(MainActivity.this, "Pump status is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to read pump status", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ledStatus = snapshot.getValue(String.class);
                if (ledStatus != null) {
                    updateLedStatusUI(ledStatus);
                } else {
                    Toast.makeText(MainActivity.this, "LED status is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to read LED status", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the listener to get the sensor value
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer sensorValue = dataSnapshot.getValue(Integer.class);
                if (sensorValue != null) {
                    updateProgressBar(sensorValue);
                } else {
                    Toast.makeText(MainActivity.this, "Sensor value is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to read sensor value", Toast.LENGTH_SHORT).show();
            }
        });

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference1.setValue("on");
                vibrate();
            }
        });
        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference1.setValue("off");
                vibrate();
            }
        });
    }

    private void updatePumpStatusUI(String status) {
        if (status.equals("on")) {
            pumpStatusImageView.setImageResource(onDrawable);
        } else {
            pumpStatusImageView.setImageResource(offDrawable);
        }
    }

    private void updateLedStatusUI(String status) {
        if (status.equals("on")) {
            ledStatus.setImageResource(greenLed);
        } else {
            ledStatus.setImageResource(redLed);
        }
    }

    private void updateProgressBar(int value) {
        if (value > 45) {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circlered));
        } else {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circle));
        }
        progressBar.setProgress(value);
        moistureLevelText.setText("Moisture Level: " + value + "%");
    }


    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(100); // Vibrate for 100 milliseconds
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
