package com.happy.testonly;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        go();
        run();
    }

    private void go() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(800);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tv = findViewById(R.id.text);
                                tv.setText("");
                            }
                        });
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(600);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tv = findViewById(R.id.text);
                                tv.setText(working());
                            }
                        });
                        Thread.sleep(390);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String shaEncode(String inStr) throws Exception {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    public static int ord(String s) {
        return s.length() > 0 ? (s.getBytes(StandardCharsets.UTF_8)[0] & 0xff) : 0;
    }

    public static int ord(char c) {
        return c < 0x80 ? c : ord(Character.toString(c));
    }

    private String working() {
        String origin = "B08020D0FACFDAF81DB46890E4040EDBB8613DA5ABF038F8B86BD44525D2E27B26E22ACD06388112D8467FD688C79CC7EA83F27440577350E8168C2560368616";
        String result = "";
        try {
            result = shaEncode(origin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.d("TestThread", "working: "+result);
        // 25596746a8fa78a9344a265ad3bef5a3
        // flag{Th1s_S33M3_40R_T3sT_0N1Y}
        char[] table = {85, 95, 5, 83, 75, 96, 94, 0, 17, 61, 102, 87, 80, 123, 4, 105, 85, 83, 101, 109, 55, 85, 23, 48, 106, 1, 40, 7, 97, 31};
        for (int i = 0; i < table.length; i++) {
            table[i] ^= ord(result.charAt(i));
        }
        //Log.d("TestThread", "working string: "+String.copyValueOf(table));
        return String.copyValueOf(table).replace("flag","minil");
    }
}
