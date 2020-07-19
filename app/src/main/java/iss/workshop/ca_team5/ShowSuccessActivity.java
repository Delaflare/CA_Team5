package iss.workshop.ca_team5;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class ShowSuccessActivity extends AppCompatActivity {
LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_success);


        Intent intent = getIntent();
        String msg=intent.getStringExtra("showMsg");
        TextView msgText=findViewById(R.id.msg);
        if(msgText!=null)
            msgText.setText(msg);

        lottieAnimationView=(LottieAnimationView)findViewById(R.id.animationView);
        startCheckAnimation();


        Button play_again=findViewById(R.id.play_again);
        play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result = new Intent();
                setResult(RESULT_OK, result);
                finish();
            }
        });

    }

    private  void startCheckAnimation()
    {
        final ValueAnimator animator=ValueAnimator.ofFloat(0f,1f).setDuration(800);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lottieAnimationView.setProgress((Float)animator.getAnimatedValue());
            }
        });
        if(lottieAnimationView.getProgress()==0f){
            animator.setStartDelay(1500);
            animator.start();
        }
        else{
            lottieAnimationView.setProgress(0f);
        }
    }
}