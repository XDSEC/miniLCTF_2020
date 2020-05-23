package com.happy.khronos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText tv = findViewById(R.id.edittext);
        final Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String flag=tv.getText().toString().trim();
                int ans=check(flag);
                if (ans==0){
                    Toast.makeText(MainActivity.this, "Wrong flag.", Toast.LENGTH_SHORT).show();
                }else if(ans==1){
                    Toast.makeText(MainActivity.this, "Khronos is transcendental, your flag is not correct.", Toast.LENGTH_SHORT).show();
                }else if(ans==2){
                    Toast.makeText(MainActivity.this, "Khronos is mysterious, wrong flag but is almost correct.", Toast.LENGTH_SHORT).show();
                }else if (ans==3){
                    Toast.makeText(MainActivity.this, "Good job. The flag is "+flag, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public native int check(String flag);
}
