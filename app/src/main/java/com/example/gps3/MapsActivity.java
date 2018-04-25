package com.example.gps3;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.Calendar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.content.Context;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,SensorEventListener{
    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final long MILLIS_BETWEEN_LOCATIONS = TimeUnit.SECONDS.toMillis(3);
    protected final static double RAD2DEG = 180 / Math.PI;
    protected final static float[] attitude = new float[3];
    protected final static float[] mOrientation = new float[9];
    protected  static float[] rotationMatrix = new float[16];
    protected  static float[] mRotationMatrix = new float[16];
    protected  static float[] gravity = new float[3];
    protected  static float[] geomagnetic = new float[3];
    protected  static float[] orientationValues= new float[3];
    private float mHeading;
    private GeomagneticField mGeomagneticField;
    private boolean mHasIterference;

    private GoogleMap mMap = null;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private List<LatLng> mRunList = new ArrayList<LatLng>();
    private double groundSpeed = 0.0;
    private double mMeter = 0.0;
    private boolean mStart = false;
    private boolean mFirst = false;
    public double lat = 0;
    public double lon = 0;
    private double direction = 0;
    private LatLng latlng;
    private int width, height;
    public double AZIMUTH=0;
    public double azimuth = 0;
    public double  roll=0;
    public  double pitch=0;
    public double  locate=0;
    public long  time=0;
    public double altitude=0;
    public double x =0;
    public double y = 0;
    public double z = 0;
    private ImageView sisei;
    private ImageView sisei2;
    //private double latitude, longitude;
    //SDカードの場所を取得
    private String sdPath = Environment.getExternalStorageDirectory().getPath() + "/log.txt";
    private DatabaseReference messageRef;

    SensorManager sensorManager;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
// Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Write a message to the database
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");

        Log.d(TAG,"Firebase");

        // Read from the database

        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               String key = dataSnapshot.getKey();
                String foo = dataSnapshot.getValue(String.class);
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " );
                Log.d("Firebase", String.format("sender:%s, body:%s", foo, value));
                String title = (String) dataSnapshot.child("message").getValue();
                Boolean isDone = (Boolean) dataSnapshot.child("isDone").getValue();

                String str2 = value;
                int result3 = str2.length();
                Log.d("Firebase", String.format("sender:%s, body:%s", str2, value));

                if (result3 == 3) {
                    Log.d(TAG, "fight" );
                   // textView.setText(String.valueOf(foo));
                    Log.d(TAG, "postTransaction:onComplete:");
                }

                // 追加されたTodoのkey、title、isDoneが取得できているので、
                // 保持しているデータの更新や描画処理を行う。
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                String foo = dataSnapshot.getValue(String.class);
                String str2 = value;
                int result3 = str2.length();
                // textView.setText(String.valueOf(foo));
                if (result3 == 1) {
                    Log.d(TAG, "fight" );
                    //textView.setText(String.valueOf(foo));
                    x = 135.962644;
                    y = 34.983480 ;
                    z = 1;
                    Log.d(TAG, "Value is: " +z);
                }
                if (result3 == 2) {
                    Log.d(TAG, "fight" );
                    //textView.setText(String.valueOf(foo));
                    x = 135.962841 ;
                    y = 34.982220;
                    z = 2;
                    Log.d(TAG, "Value is: " +z);
                }if (result3 == 3) {
                    Log.d(TAG, "fight" );
                    //textView.setText(String.valueOf(foo));
                    x = 135.965223;
                    y = 34.979729;
                    z = 3;
                    Log.d(TAG, "Value is: " +z);
                    Log.d("Firebase22", String.format("sender:%s, body:%s", foo, value));
                }if (result3 == 4) {
                    Log.d(TAG, "fight" );
                    //textView.setText(String.valueOf(foo));
                    x = 135.947439;
                    y = 35.003532;
                    z = 4;
                    Log.d(TAG, "Value is: " +z);
                }
                Log.d("Firebase", String.format("sender:%s, body:%s", foo, value));
                // Changed
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Removed
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // Moved
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error
                Log.d(TAG,"MISS");
            }
        });


        initSensor();
        verifyStoragePermissions(this);

        final Button button1 = (Button) findViewById(R.id.toast1Button);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == button1) {
                    x = 135.962644;
                    y = 34.983480 ;
                    z = 1;
                    DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");
                    messageRef.child("v").setValue("t");
                }
            }
        });
        final Button button2 = (Button) findViewById(R.id.toast2Button);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == button2) {
                    x = 135.962841 ;
                    y = 34.982220;
                    z = 2;
                    DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");
                    messageRef.child("v").setValue("ta");
                }
            }
        });
        final Button button3 = (Button) findViewById(R.id.toast3Button);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == button3) {
                    x = 135.965223;
                    y = 34.979729;
                    z = 3;
                    DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");
                    messageRef.child("v").setValue("tak");
                }
            }
        });
        final Button button4 = (Button) findViewById(R.id.toast4Button);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == button4) {
                    x = 135.947439;
                    y = 35.003532;
                    z = 4;
                    DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");
                    messageRef.child("v").setValue("taka");
                }
            }
        });
        //画面スリープにしない
        /*final Button button5 = (Button) findViewById(R.id.button2);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == button5) {
                    // Sub 画面を起動
                    Intent intent = new Intent();
                    intent.setClassName("com.example.gps3", "com.example.gps3.MainActivity");
                    startActivity(intent);
                }
            }
        });*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }
        //ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton);
        //tb.setChecked(false);

        //tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        //public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //if (isChecked) {
        //startChronometer();
        mStart = true;
        mFirst = true;
        //mStop = false;
        mMeter = 0.0;
        groundSpeed = 0.0;
        mRunList.clear();
        //} else {
        //stopChronome
        //
        // ter();
        //mStop = true;
        //calcSpeed();
        //saveConfirmDialog();
        mStart = false;

        // }
        // }
        //});

        }



    public void onResume() {
        super.onResume();
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_UI);
    }

    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

//yest

    protected void initSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            mHasIterference = (accuracy<SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        }

    }


    @SuppressWarnings("deprecation")
    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] outR = new float[16];
        float[] I = new float[16];

        if(event.sensor.getType()==Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(mRotationMatrix,event.values);
            SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, mRotationMatrix);//axisx axiszかもしれない

            SensorManager.getOrientation(
                    mRotationMatrix,mOrientation);

            float magneticHeading = (float)Math.toDegrees(mOrientation[0]);
            mHeading =computeTrueNorth(magneticHeading)
                    -6;


        }

        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values.clone();
                break;
            case Sensor.TYPE_ORIENTATION:
                orientationValues = event.values.clone();
                break;
        }

       if (geomagnetic != null && gravity != null) {

            SensorManager.getRotationMatrix(
                    rotationMatrix, I,
                    gravity, geomagnetic);

            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);//axisx axiszかもしれない

            SensorManager.getOrientation(
                    outR,
                    attitude);


            //AZIMUTH = mHeading;
            //azimuth = attitude[0] *  RAD2DEG ;
           pitch=attitude[1]*RAD2DEG;
            roll = attitude[2] * RAD2DEG;
           /*String Pitch = String.valueOf(pitch);
           DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");
           messageRef.child("pitch").setValue(Pitch);*/
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                sisei = (ImageView) findViewById(R.id.sisei);
                sisei.setImageResource(R.drawable.bou6);
                sisei2 = (ImageView) findViewById(R.id.sisei2);
                sisei2.setImageResource(R.drawable.bou7);

                //getDrawableメソッドで取り戻したものを、BitmapDrawable形式にキャストする
                BitmapDrawable bd = (BitmapDrawable) sisei.getDrawable();
               BitmapDrawable bd2 = (BitmapDrawable) sisei2.getDrawable();
                //getBitmapメソッドでビットマップファイルを取り出す
                Bitmap bmp = bd.getBitmap();
               Bitmap bmp3 = bd2.getBitmap();
                //回転させる
                //Matrix matrix = new Matrix();
                //Matrix matrix2 = new Matrix();
               // matrix.postRotate((float) roll / 3);
               // matrix.postRotate((float) 40);
               // matrix2.postTranslate(0,(float)roll);
                Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, 3250, 150, false);
               //Bitmap bmp4 = Bitmap.createScaledBitmap(bmp3, 3000, 150, false);
                //Bitmap回転させる
                //Bitmap flippedBmp = Bitmap.createBitmap(bmp2, 0, 0, bmp2.getWidth(), bmp2.getHeight(), matrix, false);
                Bitmap flippedBmp = Bitmap.createBitmap(bmp3,0,0,bmp3.getWidth(),bmp3.getHeight());
                Bitmap flippedBmp2 = Bitmap.createBitmap(bmp2,0,0,bmp2.getWidth(),bmp2.getHeight());
                //加工したBitmapを元のImageViewにセットする
               // sisei2.setImageDrawable(new BitmapDrawable(flippedBmp));
                sisei.setImageDrawable(new BitmapDrawable(flippedBmp2));
                sisei.setTranslationY((float) pitch*7);
                sisei2.setBackgroundDrawable(new BitmapDrawable(flippedBmp));
                sisei2.setTranslationX((float) roll*7);
                Calendar calendar = Calendar.getInstance();

                // 0:AM, 1:PMの取得
                String am_pm = null;
                switch(calendar.get(Calendar.AM_PM)){
                    case 0:
                        am_pm = "AM";
                        break;
                    case 1:
                        am_pm = "PM";
                        break;
                }

                // 時間の取得（24時間単位）
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                // 分の取得
                int min = calendar.get(Calendar.MINUTE);

                // 秒の取得
                int sec = calendar.get(Calendar.SECOND);


                // ミリ秒の取得
                int msec = calendar.get(Calendar.MILLISECOND);
                if(msec==0) {
                    //firebaseデータの書き込み
                    DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");
       /* messageRef.child("lat").setValue(Lat);
        messageRef.child("lon").setValue(Lon);
        messageRef.child("roll").setValue(Roll);
        messageRef.child("pitch").setValue(Pitch);*/
                    //下ので試す
                    messageRef.child("data").setValue("ロール" + roll + "ピッチ" + pitch);

                    //SDカードの状態を取得
                    String sdCardState = Environment.getExternalStorageState();
                    //上で取得したSDカードの状態毎に処理を分離
                    //書き込み処理が可能な場合
                    if (sdCardState.equals(Environment.MEDIA_MOUNTED)) {
                        String str = "ロール角" + String.valueOf(roll) + "ピッチ角" + String.valueOf(pitch);
                        try {
                            FileOutputStream fos = new FileOutputStream(sdPath, true);
                            //trueを帰して追記可能に
                            OutputStreamWriter osw = new OutputStreamWriter(fos);
                            BufferedWriter bw = new BufferedWriter(osw);
                            bw.write(str.toCharArray());
                            //http://www.c-lang.net/java-lang/2016/06/02/%E3%83%90%E3%83%83%E3%83%95%E3%82%A1%E5%8C%96%E3%81%97%E3%81%A6%E5%8A%B9%E7%8E%87char%E6%9B%B8%E3%81%8D%E8%BE%BC%E3%81%BF/
                            bw.newLine();
                            bw.flush();
                            bw.close();

                            //Toast.makeText(MapsActivity.this, str + "\n登録しました。", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(MapsActivity.this, "登録できませんでした。SDカードを確認してください。", Toast.LENGTH_LONG).show();
                        }
                        //SDカードが読取専用の場合
                    } else if (sdCardState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                        Toast.makeText(MapsActivity.this, "このSDカードは読取専用です。", Toast.LENGTH_LONG).show();
                        //SDカードが挿入されていない場合
                    } else if (sdCardState.equals(Environment.MEDIA_REMOVED)) {
                        Toast.makeText(MapsActivity.this, "SDカードが挿入されていません。", Toast.LENGTH_LONG).show();
                        //SDカードがマウントされていない場合
                    } else if (sdCardState.equals(Environment.MEDIA_UNMOUNTED)) {
                        Toast.makeText(MapsActivity.this, "SDカードがマウントされていません。", Toast.LENGTH_LONG).show();
                        //その他の場合
                    } else {
                        Toast.makeText(MapsActivity.this, "SDカードを確認してください。", Toast.LENGTH_LONG).show();
                    }
                }



            }


        }


    }




private float computeTrueNorth(float heading){
    if(mGeomagneticField != null ){
        return heading + mGeomagneticField.getDeclination();
    } else{
        return heading;
    }
}


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // 皇居近辺の緯度経度

        latlng = new LatLng(lat, lon);

        // 標準のマーカー
        //setMarker(latitude, longitude);

    }

    private void locationStart() {
        Log.d("debug", "locationStart()");

        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "gpsEnable, startActivity");
        } else {
            Log.d("debug", "gpsEnabled");
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,

                100, //通知するための最小時間間隔（ミリ秒）　今1秒置き
                1, //通知するための最小距離間隔（メートル）　今1ｍ
                this);

        updateGeomagneticField();
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "checkSelfPermission true");

                locationStart();
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {


       // double lat1=Math.toRadians(lat);
        //double lat2=Math.toRadians(y);
       // double lon1=Math.toRadians(lon);
      //  double lon2=Math.toRadians(x);

        Calendar calendar = Calendar.getInstance();

        // 0:AM, 1:PMの取得
        String am_pm = null;
         switch(calendar.get(Calendar.AM_PM)){
            case 0:
                am_pm = "AM";
                break;
            case 1:
                am_pm = "PM";
                break;
        }

        // 時間の取得（24時間単位）
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // 分の取得
        int min = calendar.get(Calendar.MINUTE);

        // 秒の取得
        int sec = calendar.get(Calendar.SECOND);

        // ミリ秒の取得
        int msec = calendar.get(Calendar.MILLISECOND);
        altitude = location.getAltitude();
        time = location.getTime();
        updateGeomagneticField();
        AZIMUTH= location.getBearing();


        // 緯度の表示

        //小数第8位まで表示される。地上で0.9ｍ動くと第8位の数字が変化する。
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        //latlng=new LatLng(location.getLatitude(),location.getLongitude());
        lat = location.getLatitude();
        lon = location.getLongitude();
        azimuth = attitude[0] *  RAD2DEG ;
        roll = attitude[2] *RAD2DEG;
        //locate = 90-Math.atan2(Math.sin(lon2-lon1)*Math.cos(lat2),Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*RAD2DEG;
        locate = 90 - Math.atan2(y-lat,x-lon) *RAD2DEG;
        direction =locate -AZIMUTH;

        //LatLng loc = new LatLng(lat, lon);
        CameraPosition cameraPos = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(19)
                .bearing(0).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        mMap.clear();
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions();
        options.position(latlng)
                .rotation((float)direction)
                .anchor(0.5f,0.5f);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.yajirushi2);
        //画像
        options.icon(icon);
        mMap.addMarker(options);

        String Lat = String.valueOf(lat);
        String Lon = String.valueOf(lon);
        String Roll = String.valueOf(roll);
        String Pitch = String.valueOf(pitch);
        //firebaseデータの書き込み
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");
       /* messageRef.child("lat").setValue(Lat);
        messageRef.child("lon").setValue(Lon);
        messageRef.child("roll").setValue(Roll);
        messageRef.child("pitch").setValue(Pitch);*/
        //下ので試す
        messageRef.child("data").setValue("緯度"+Lat+"経度"+Lon+"ロール"+Roll+"ピッチ"+Pitch);

        if (mStart) {
            Bundle args = new Bundle();
            args.putDouble("lat", location.getLatitude());
            args.putDouble("lon", location.getLongitude());

            //getLoaderManager().restartLoader(ADDRESSLOADER_ID, args, this); これ住所取得するやつ。いらへん。
            mFirst = !mFirst;
        } else {
            drawTrace(latlng);
            sumDistance();
        }


        //SDカードの状態を取得
        String sdCardState = Environment.getExternalStorageState();
        //上で取得したSDカードの状態毎に処理を分離
        //書き込み処理が可能な場合
        if (sdCardState.equals(Environment.MEDIA_MOUNTED)) {
            String str = "現在時刻:" +String.valueOf(am_pm) +String.valueOf(hour)  +String.valueOf(min) +String.valueOf(sec)+  ",x=" +String.valueOf(x)  + ",y=" +String.valueOf(y)+ ",z=" +String.valueOf(z)  + ",x座標" + String.valueOf(lon) + ",y座標" + String.valueOf(lat)+ ",方向" + String.valueOf(AZIMUTH) + ",目的の方向" + String.valueOf(direction)+ ",位置" + String.valueOf(locate)+  ",標高=" +String.valueOf(altitude)+"ロール角" + String.valueOf(roll);
            try {
                FileOutputStream fos = new FileOutputStream(sdPath,true);
                //trueを帰して追記可能に
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(str.toCharArray());
                //http://www.c-lang.net/java-lang/2016/06/02/%E3%83%90%E3%83%83%E3%83%95%E3%82%A1%E5%8C%96%E3%81%97%E3%81%A6%E5%8A%B9%E7%8E%87char%E6%9B%B8%E3%81%8D%E8%BE%BC%E3%81%BF/
                bw.newLine();
                bw.flush();
                bw.close();

                //Toast.makeText(MapsActivity.this, str + "\n登録しました。", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MapsActivity.this, "登録できませんでした。SDカードを確認してください。", Toast.LENGTH_LONG).show();
            }
            //SDカードが読取専用の場合
        } else if (sdCardState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(MapsActivity.this, "このSDカードは読取専用です。", Toast.LENGTH_LONG).show();
            //SDカードが挿入されていない場合
        } else if (sdCardState.equals(Environment.MEDIA_REMOVED)) {
            Toast.makeText(MapsActivity.this, "SDカードが挿入されていません。", Toast.LENGTH_LONG).show();
            //SDカードがマウントされていない場合
        } else if (sdCardState.equals(Environment.MEDIA_UNMOUNTED)) {
            Toast.makeText(MapsActivity.this, "SDカードがマウントされていません。", Toast.LENGTH_LONG).show();
            //その他の場合
        } else {
            Toast.makeText(MapsActivity.this, "SDカードを確認してください。", Toast.LENGTH_LONG).show();
        }


    }



    private void updateGeomagneticField(){
        mGeomagneticField =new GeomagneticField((float)lat,(float)lon,(float)altitude,time);
    }



    private void drawTrace(LatLng latlng) {
        mRunList.add(latlng);
        if (mRunList.size() > 2) {
            PolylineOptions polylineOptions = new PolylineOptions();
            for (LatLng polyLatLng : mRunList) {
                polylineOptions.add(polyLatLng);
            }
            polylineOptions.color(Color.BLUE);
            polylineOptions.width(3);
            polylineOptions.geodesic(false);
            mMap.addPolyline(polylineOptions);
        }
    }

    private void sumDistance() {
        if (mRunList.size() < 2) {
            return;
        }
        //mMeter=0;
        //groundSpeed=0;
        float[] results = new float[3];
        int i = 1;
        while (i < mRunList.size()) {
            results[0] = 0;
            Location.distanceBetween(mRunList.get(i - 1).latitude,
                    mRunList.get(i - 1).longitude,
                    mRunList.get(i).latitude,
                    mRunList.get(i).longitude,
                    results);
            groundSpeed = results[0];
            mMeter += results[0];
            i++;
        }
        double disMeter = mMeter;
        //TextView disText = (TextView) findViewById(R.id.disText);
       // disText.setText(String.format("飛行距離" + "%.2f" + "m", disMeter));
        double mSpeeed = groundSpeed;
       // TextView speed = (TextView) findViewById(R.id.speed);
        //speed.setText(String.format("対地速度" + "%.2f" + "m/s", mSpeeed));
    }


    private void zoomMap(double lat, double lon) {
        // 表示する東西南北の緯度経度を設定
        double south = lat * (1 - 0.00005);
        double west = lon * (1 - 0.00005);
        double north = lat * (1 + 0.00005);
        double east = lon * (1 + 0.00005);

        // LatLngBounds (LatLng southwest, LatLng northeast)
        LatLngBounds bounds = LatLngBounds.builder()
                .include(new LatLng(south, west))
                .include(new LatLng(north, east))
                .build();

        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;

        // static CameraUpdate.newLatLngBounds(LatLngBounds bounds,
        // int width, int height, int padding)
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0));

    }

    @Override
    public void onProviderEnabled(String provider) {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);

    }

    @Override
    public void onProviderDisabled(String provider) {

    }}

