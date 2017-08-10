package com.mehrdadpy.com.metronome;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.InvalidPropertiesFormatException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView bpmText;
    private SeekBar bpmSeek;
    private Button starButton;
    private Button changeSoundButton;
    private EditText noteValueText;
    private EditText beatsText;
    private final short START_SEEK = 10;
    private int bpmValue = 90;
    private int noteValue = 4;
    private int beats = 6;
    private double[] beatSound = {261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 392, 415.3, 440, 466.16, 493.88, 523.25, 554.37,
            587.33, 622.25, 659.25, 698.46, 739.99, 783.99, 830.61, 880, 932.33, 987.77, 1046.5, 1108.73, 1174.66, 1244.51, 1318.51, 1396.91,
            1479.98, 1567.98, 1661.22, 1760, 1864.66, 1975.53, 2093, 2217.46, 2349.32, 2489.02, 2637.02, 2793.83, 2959.96, 3135.96, 3322.44,
            3520, 3729.31, 3951.07, 4186.01, 4434.92, 4698.63, 4978.03, 5274.04, 5587.65, 5919.91, 6271.93, 6644.88, 7040, 7458.62, 7902.13};
    private double beatSoundG;
    private double[] sound = {261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 392, 415.3, 440, 466.16, 493.88, 523.25, 554.37,
            587.33, 622.25, 659.25, 698.46, 739.99, 783.99, 830.61, 880, 932.33, 987.77, 1046.5, 1108.73, 1174.66, 1244.51, 1318.51, 1396.91,
            1479.98, 1567.98, 1661.22, 1760, 1864.66, 1975.53, 2093, 2217.46, 2349.32, 2489.02, 2637.02, 2793.83, 2959.96, 3135.96, 3322.44,
            3520, 3729.31, 3951.07, 4186.01, 4434.92, 4698.63, 4978.03, 5274.04, 5587.65, 5919.91, 6271.93, 6644.88, 7040, 7458.62, 7902.13};
    private double soundG;
    private AudioManager audio;
    private MetronomeAsyncTask metroTask;
    Random rand = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        metroTask = new MetronomeAsyncTask();

        bpmText = (TextView) findViewById(R.id.bpmText);
        bpmSeek = (SeekBar) findViewById(R.id.bpmSeek);
        starButton = (Button) findViewById(R.id.star);
        changeSoundButton = (Button) findViewById(R.id.changeSound);
        noteValueText = (EditText) findViewById(R.id.beatValueId);
        beatsText = (EditText) findViewById(R.id.beatsId);

        bpmSeek.setProgress(bpmValue);
        bpmText.setText(Integer.toString(bpmSeek.getProgress()+START_SEEK));
        int i = rand.nextInt(beatSound.length);
        beatSoundG = beatSound[i];
        int j = rand.nextInt(beatSound.length);
        while (i == j)
            j = rand.nextInt(beatSound.length);
        soundG = sound[j];

        bpmSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bpmValue = progress+START_SEEK;
                bpmText.setText(Integer.toString(bpmValue));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                metroTask.setBpm(bpmValue);
            }
        });

        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStarStopClick(v);
            }
        });

        changeSoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int i = rand.nextInt(beatSound.length);
                beatSoundG = beatSound[i];
                int j = rand.nextInt(beatSound.length);
                while (i == j)
                    j = rand.nextInt(beatSound.length);
                soundG = sound[j];

                metroTask.setSound(soundG);
                metroTask.setBeatSound(beatSoundG);
            }
        });

        noteValueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    noteValue = Integer.parseInt(noteValueText.getText().toString());

                    metroTask.setNoteValue(noteValue);
                }catch (Exception e){

                }


            }
        });

        beatsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    beats = Integer.parseInt(beatsText.getText().toString());

                    metroTask.setBeat(beats);
                }catch (Exception e){

                }

            }
        });

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public synchronized void onStarStopClick(View view){

        String buttonText = starButton.getText().toString().toUpperCase();
        if (buttonText.equalsIgnoreCase("START")){
            starButton.setText(R.string.stop);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                metroTask.executeOnExecutor (AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
            else
                metroTask.execute();

        }else {
            starButton.setText(R.string.start);
            metroTask.stop();
            metroTask = new MetronomeAsyncTask();
            Runtime.getRuntime().gc();

        }
    }

    private class MetronomeAsyncTask extends AsyncTask<Void,Void,String>{
        Metronome metronome = new Metronome();


        @Override
        protected String doInBackground(Void... params) {
            metronome.setBeat(beats);
            metronome.setNoteValue(noteValue);
            metronome.setBpm(bpmValue);
            metronome.setBeatSound(beatSoundG);
            metronome.setSound(soundG);

            metronome.play();

            return null;
        }

        public void stop(){
            metronome.stop();
            metronome = null;

        }

        public void setBpm(int bpm){
            metronome.setBpm(bpm);
            metronome.calcSilence();
        }

        public void setBeat(int beat){
            if (metronome != null)
                metronome.setBeat(beat);
        }

        public void setNoteValue(int NoteValue){
            if (metronome != null)
                metronome.setNoteValue(NoteValue);
        }

        public void setSound(double sound){
            if (metronome != null) {
                metronome.setSound(sound);
                metronome.calcSilence();
            }
        }

        public void setBeatSound(double beatSound){
            if (metronome != null) {
                metronome.setBeatSound(beatSound);
                metronome.calcSilence();
            }
        }
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
