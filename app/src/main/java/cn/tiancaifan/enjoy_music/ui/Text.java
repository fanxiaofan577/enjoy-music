package cn.tiancaifan.enjoy_music.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.ui.widget.AttachmentRingView;
import cn.tiancaifan.enjoy_music.utils.VisualizerHelper;

public class Text extends AppCompatActivity {
    private AttachmentRingView ring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teext);

        ring = findViewById(R.id.ring);
        VisualizerHelper.getInstance().addCallBack(ring);
        ((RadioButton) findViewById(R.id.checkbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring.setRotate(isChecked);
            }
        });
        ((RadioButton) findViewById(R.id.checkbox2)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring.setRandom(isChecked);
            }
        });
        ((SeekBar) findViewById(R.id.seek_bar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress == 0) {
                        return;
                    }
                    ring.setScope(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        ((SeekBar) findViewById(R.id.seek_bar2)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress == 0) {
                        return;
                    }
                    ring.setStart(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((RadioButton) findViewById(R.id.radiobutton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ring.setColumnar(true);
            }
        });

        ((RadioButton) findViewById(R.id.radiobutton2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ring.setBomb(true);
            }
        });
        ((RadioButton) findViewById(R.id.radiobutton3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ring.setWave(true);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        VisualizerHelper.getInstance().removeCallBack(ring);
    }
}