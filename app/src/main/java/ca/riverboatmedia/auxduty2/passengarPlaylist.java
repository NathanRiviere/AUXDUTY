package ca.riverboatmedia.auxduty2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


public class passengarPlaylist extends AppCompatActivity {
    ImageView fireball;
    RotateAnimation rotate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ca.riverboatmedia.auxduty2.R.layout.passengar_playlist);
        fireball = (ImageView) findViewById(ca.riverboatmedia.auxduty2.R.id.fire);
        rotate = new RotateAnimation(0, 360000, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000000);
        rotate.setInterpolator(new LinearInterpolator());
        fireball.startAnimation(rotate);
    }

    public void leaveClicked(View view) {
        rotate.cancel();
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }
}
