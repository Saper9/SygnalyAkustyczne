package com.example.kodmorsa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordButton=findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //co ten guzik robi
            }
        });
    }
}