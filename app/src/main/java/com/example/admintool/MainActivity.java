package com.example.admintool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<WiFiData> WiFiList = new ArrayList<>();
    DataAdapter myAdapter;

    DatabaseReference m_Database; // 파이어베이스 리얼 타임 디비

    WifiManager wifiManager;
    private String mSpinner1Value = "";
    private String mSpinner2Value = "";

    String[] floor = {"4", "5"};
    String[] class_ = {"01호", "02호", "03호", "04호", "05호", "06호", "07호", "08호", "09호"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android10 이상부터 권한 요청
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("permission","checkSelfPermission");
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Log.d("permission","shouldShowRequestPermissionRationale");
                // 사용자에게 설명을 보여줍니다.
                // 권한 요청을 다시 시도합니다.

            } else {
                // 권한요청

                Log.d("permission","권한 요청");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_STATE},
                        1000);

            }
        }

        //권한 요청 코드 끝

        // ListView
        ListView listView = (ListView)findViewById(R.id.listView);
        myAdapter = new DataAdapter(this,WiFiList);
        listView.setAdapter(myAdapter);
        //ListView 끝

        // wifi 가져오는 부분
        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                    Log.e("wifi","scanSuccess !!!!!!!!!!!!!!!");
                } else {
                    // scan failure handling
                    scanFailure();
                    Log.e("wifi","scanFailure ..............");
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);

        //Spinner 부분

        Spinner spinner1 = (Spinner) findViewById(R.id.left);
        Spinner spinner2 = (Spinner) findViewById(R.id.right);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, floor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
                mSpinner1Value = adapterView.getItemAtPosition(position).toString(); // 첫번째 스피너 값 가져오기 층수
            }
            @Override
            public void onNothingSelected(AdapterView adapterView){

            }
        });

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, class_);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter1);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
                mSpinner2Value = adapterView.getItemAtPosition(position).toString(); // 두번째스피너에 들어있는 값 가져오기 ex) 01호
            }
            @Override
            public void onNothingSelected(AdapterView adapterView){

            }
        });

        // Spinner 끝

        Button button1 = (Button) findViewById(R.id.btn1) ;
        button1.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                String spinner1Value = mSpinner1Value;
                String spinner2Value = mSpinner2Value;

                boolean success = wifiManager.startScan();
                if (!success) {
                    // scan failure handling
                    scanFailure();
                    Log.e("wifi","scanFailure ..............");
                }

            }
        });

        // 읽어온 와이파이 정보를 데이터베이스에 올리기
        Button button2 = findViewById(R.id.btn2);
        m_Database = FirebaseDatabase.getInstance().getReference();
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < myAdapter.getCount(); i++) // 리스트뷰에 있는 만큼
                {   // 데이터 디비에 등록하기
                    m_Database.child(mSpinner1Value + "층").child(mSpinner2Value).child(myAdapter.getItem(i).getBSSID()).push().setValue(myAdapter.getItem(i).getRssi())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 저장 성공 시 처리
                        Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 저장 실패 시 처리
                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                            }
                        });;
                }
            }
        });
    }

    private void scanSuccess() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        WiFiList.clear(); // 모델 초기화
        List<ScanResult> results = wifiManager.getScanResults(); // 스캔 완료한 결과 가져오기
        for( ScanResult result: results) {
            String ssid = result.SSID; // SSID값 가져오기
            if(ssid.equalsIgnoreCase("gc_free_wifi") || ssid.equalsIgnoreCase("eduroam")) {
                int rssi = result.level; // RSSI값 가져오기
                long timestamp = result.timestamp; // 타임스탬프 값 가져오기
                String bssid = result.BSSID; // BSSID값 가져오기
                String temp = ssid + " " + rssi + " " + timestamp + " " + bssid; // 시리얼 출력용


                WiFiList.add(new WiFiData(ssid, bssid, timestamp, rssi)); // 와이파이 리스트에 추가
            }
//            System.out.println(temp); // 스캔한 결과 출력
        }
        myAdapter.notifyDataSetChanged(); // 리스트 추가한거 반영

    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<ScanResult> results = wifiManager.getScanResults();
    }
}