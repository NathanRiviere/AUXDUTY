package nriviere97.auxduty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import nriviere97.auxduty.Adapters.playlistAdapter;
import nriviere97.auxduty.firebaseHelpers.songInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class passengarPlaylist extends AppCompatActivity {
    ImageView fireball;
    RotateAnimation rotate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passengar_playlist);
        fireball = (ImageView) findViewById(R.id.fire);
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
