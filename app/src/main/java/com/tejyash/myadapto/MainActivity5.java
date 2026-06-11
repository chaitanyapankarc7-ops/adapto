package com.tejyash.myadapto;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity5 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main5);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            ArrayList<String> result =
                    data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);

            if (result != null && !result.isEmpty()) {
                String command = result.get(0).toLowerCase();
                processCommand(command);
            }
        }
    }

    private void processCommand(String command) {

        if (command.contains("instagram")) {

            Intent launchIntent =
                    getPackageManager()
                            .getLaunchIntentForPackage(
                                    "com.instagram.android");

            if (launchIntent != null) {
                startActivity(launchIntent);
            }

        } else if (command.contains("whatsapp")) {

            Intent launchIntent =
                    getPackageManager()
                            .getLaunchIntentForPackage(
                                    "com.whatsapp");

            if (launchIntent != null) {
                startActivity(launchIntent);
            }

        } else if (command.contains("settings")) {

            Intent intent = new Intent(
                    android.provider.Settings.ACTION_SETTINGS);

            startActivity(intent);

        } else {

            Toast.makeText(
                    this,
                    "Command not recognized",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}

