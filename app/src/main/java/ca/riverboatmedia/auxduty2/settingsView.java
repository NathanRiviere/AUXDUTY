package ca.riverboatmedia.auxduty2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Nathan on 2017-12-11.
 */

public class settingsView extends Activity {
    private int _playlistSize;
    private Intent intent;
    private String _def_key;
    private int curr_button;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ca.riverboatmedia.auxduty2.R.layout.activity_settings_session);
        intent = getIntent();
        _def_key = intent.getStringExtra("session_key");
        curr_button = _playlistSize = intent.getIntExtra("playlist_size", 10);
        et = (EditText) findViewById(ca.riverboatmedia.auxduty2.R.id.EditText1);
        if (_def_key.equals("null")) {
            et.setHint("Enter Default Session Key");
        } else {
            et.setHint(_def_key);
        }
        switch(_playlistSize){
            case 5:
                findViewById(ca.riverboatmedia.auxduty2.R.id.Button1).setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 10:
                findViewById(ca.riverboatmedia.auxduty2.R.id.Button2).setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 15:
                findViewById(ca.riverboatmedia.auxduty2.R.id.Button3).setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 20:
                findViewById(ca.riverboatmedia.auxduty2.R.id.Button4).setBackgroundColor(Color.parseColor("#ffffff"));
                break;
        }
    }

    public void playlist_size_clicked(View view) {
        Button _b = (Button) view;
        _b.setBackgroundColor(Color.parseColor("#ffffff"));
        String t = (String) _b.getText();

        switch (t) {
            case "5":
                _playlistSize = 5;
                break;
            case "10":
                _playlistSize = 10;
                break;
            case "15":
                _playlistSize = 15;
                break;
            case "20":
                _playlistSize = 20;
                break;
        }

        if (t.equals("" + curr_button)) {
        } else {
            switch (curr_button) {
                case 5:
                    findViewById(ca.riverboatmedia.auxduty2.R.id.Button1).setBackgroundColor(Color.parseColor("#D3D3D3"));
                    break;
                case 10:
                    findViewById(ca.riverboatmedia.auxduty2.R.id.Button2).setBackgroundColor(Color.parseColor("#D3D3D3"));
                    break;
                case 15:
                    findViewById(ca.riverboatmedia.auxduty2.R.id.Button3).setBackgroundColor(Color.parseColor("#D3D3D3"));
                    break;
                case 20:
                    findViewById(ca.riverboatmedia.auxduty2.R.id.Button4).setBackgroundColor(Color.parseColor("#D3D3D3"));
                    break;
            }
        }
        curr_button = _playlistSize;
    }

    public void back_clicked(View view) {
        Editable temp = new Editable.Factory().newEditable("");
        Intent retIntent = new Intent(this, MainScreen.class);

        if(et.getText().toString().equals(temp.toString())) {
            if(et.getHint().equals("Enter Default Session Key")) {
                retIntent.putExtra("retKey", "null");
            }
            else {
                retIntent.putExtra("retKey", et.getHint());
            }
        } else {
            retIntent.putExtra("retKey", et.getText().toString());
        }

        retIntent.putExtra("playlist_size", _playlistSize);
        setResult(RESULT_OK, retIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        back_clicked(null);
    }

    public void clearKeyPressed(View view) {
        et.setText(new Editable.Factory().newEditable(""));
        et.setHint("Enter Default Session Key");
    }
}
