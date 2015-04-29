package com.muevetuweb.batteryalert;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    private TextView batteryTxt,lblStatus;
    private String mHealth;
    private int level,scale;
    private boolean isCharging,usbCharge,acCharge;
    private Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    private Ringtone ringtoneAlarm;
    private ProgressBar pgBar;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                         status == BatteryManager.BATTERY_STATUS_FULL;

            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            int mBatteryHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            mHealth = "Anonymous";
            switch(mBatteryHealth) {
                case BatteryManager.BATTERY_HEALTH_COLD : mHealth = "Cold"; break;
                case BatteryManager.BATTERY_HEALTH_DEAD : mHealth = "Dead"; break;
                case BatteryManager.BATTERY_HEALTH_GOOD : mHealth = "Good"; break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE : mHealth = "Over Voltage"; break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT : mHealth = "OverHeat"; break;
                case BatteryManager.BATTERY_HEALTH_UNKNOWN : mHealth = "Unknown"; break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE : mHealth = "Unspecified Failure"; break;
            }

            if(usbCharge || acCharge){
                if(level == 100 && !ringtoneAlarm.isPlaying()){
                    ringtoneAlarm.play();
                    Toast.makeText(getApplicationContext(),"Carga Completa",Toast.LENGTH_SHORT).show();
                    lblStatus.setText(R.string.statusCharged);
                }else{
                    lblStatus.setText(R.string.statusLoading);
                }
            }else{
                if(ringtoneAlarm.isPlaying()){
                    ringtoneAlarm.stop();
                    Toast.makeText(getApplicationContext(),"Desconectado",Toast.LENGTH_SHORT).show();
                }
            }

            pgBar.setProgress(level);

            batteryTxt.setText(String.valueOf(level) + "%");
            batteryTxt.setText(batteryTxt.getText().toString() + "\n isCharging :" + isCharging);
            batteryTxt.setText(batteryTxt.getText().toString() + "\n usbCharge :" + usbCharge);
            batteryTxt.setText(batteryTxt.getText().toString() + "\n acCharge :" + acCharge);
            batteryTxt.setText(batteryTxt.getText().toString() + "\n Battery Health :" + mHealth);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ringtoneAlarm = RingtoneManager.getRingtone(getApplicationContext(), alarmTone);

        batteryTxt = (TextView) this.findViewById(R.id.batteryTxt);
        lblStatus = (TextView) this.findViewById(R.id.lblStatus);
        pgBar = (ProgressBar) this.findViewById(R.id.progressBar);

        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
