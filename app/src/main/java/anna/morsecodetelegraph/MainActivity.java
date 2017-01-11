package anna.morsecodetelegraph;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    // initialize button
    Button button_press;

    // initialize tone generators
    ToneGenerator toneGenerator;

    // initialize text box
    TextView text_box_start_time, text_box_end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up the button on the user interface
        button_press = (Button) findViewById(R.id.button_press);

        // set up the tone generator on the user interface
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);

        // set up text boxes in the user interface
        // start and end time displays
        final TextView text_box_start_time = (TextView) findViewById(R.id.text_box_start_time);
        final TextView text_box_end_time = (TextView) findViewById(R.id.text_box_end_time);
        // how long the user was pressing display
        final TextView text_box_pressed = (TextView) findViewById(R.id.text_box_pressed);
        // how long the user waited before pressing the button a second time
        final TextView text_box_paused = (TextView) findViewById(R.id.text_box_paused);


        // create string values of value names
        final String FILENAME_start_time = "FILENAME_start_time.txt";
        final String FILENAME_end_time = "FILENAME_end_time.txt";

        // flag that shows whether user has pressed and released the button once
        final int[] flag = {0};



        // this listener will detect when you press the button
        button_press.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                // if action is pressed down, the user is pressing the button
                // if the action is released, the user has stopped pressing the button
                switch (motionEvent.getAction()) {

                    // if the user is pressing the button down
                    case MotionEvent.ACTION_DOWN: {

                        // generate a beeeeeep sound
                        toneGenerator.startTone(ToneGenerator.TONE_DTMF_7);

                        // get the system time
                        long start_time = System.currentTimeMillis();


                        // display the start time in the text box start time
                        // but it only displays strings
                        // so we have to convert the long to a string
                        String start_time_string = String.valueOf(start_time);


                        // write the long system time to internal storage
                        // write the value to a file
                        // create a file
                        try {
                            FileOutputStream file_output = openFileOutput(FILENAME_start_time, MODE_PRIVATE);
                            // write to the output file
                            file_output.write(start_time_string.getBytes());
                            // close the file
                            file_output.close();
                        }
                        catch (Exception error) {
                            // if something bad happens
                            Log.e("cant write to file ", "start time");
                        }

                        text_box_start_time.setText(start_time_string);



                        // read the end time from file
                        String FILEINPUT_end_time_newtext = "";

                        // get the end time from the file
                        // read from FILENAME_end_time
                        try {
                            // read the file we just wrote to
                            FileInputStream FILEINPUT_end_time =
                                    openFileInput(FILENAME_end_time);

                            // while loop
                            // while the file is not empty, we want to read the contents to a new
                            // string
                            long size;

                            while ( (size = FILEINPUT_end_time.read()) != -1) {
                                // appending the characters to a string
                                FILEINPUT_end_time_newtext += Character.toString((char) size);
                            }

                        }
                        catch (Exception error) {
                            Log.e("could not read ", "end time");
                        }

                        // convert string end time to long
                        long end_time_long = Long.valueOf(FILEINPUT_end_time_newtext);


                        // set a flag
                        // if flag is 0, then the user has not pressed and released the button yet
                        // if the flag is 1, then user has already pressed and released the button
                        if (flag[0] == 1) {
                            // the user has completed a full press and release cycle
                            Log.e("flag if else " , "is called");

                            // calculate how long the user paused before pressing the button again
                            // start time - end time
                            // we have start time already
                            // but we don't have the end time

                            long time_paused = start_time - end_time_long;

                            // convert long to string
                            String time_paused_string = String.valueOf(time_paused);

                            // display this in a text box
                            text_box_paused.setText(time_paused_string);



                        }






                        break;
                    }


                    // if the user has released the button
                    case MotionEvent.ACTION_UP: {

                        flag[0] = 1;

                        // stop the beeeep sound
                        toneGenerator.stopTone();

                        // get the system time
                        long end_time = System.currentTimeMillis();

                        // display the end time in the text box end time
                        // but it only displays strings
                        // so we have to covert the long to a string
                        String end_time_string = String.valueOf(end_time);

                        try {
                            // create the output file
                            FileOutputStream file_output = openFileOutput(FILENAME_end_time, MODE_PRIVATE);
                            // write to the file
                            file_output.write(end_time_string.getBytes());
                            // close the file
                            file_output.close();

                        }
                        catch (Exception error) {
                            // do nothing
                            Log.e("cant write to file ", "end time");
                        }


                        text_box_end_time.setText(end_time_string);

                        // calculate how long the user has pressed the button
                        // end time - start time
                        // end time is long end_time
                        // but we have to get the start time from internal storage

                        // read the file that has the start time


                        String FILEINPUT_start_time_newtext = "";

                        try {
                            // read the file we just wrote to
                            FileInputStream FILEINPUT_start_time =
                                    openFileInput(FILENAME_start_time);

                            // while loop
                            // while the file is not empty, we want to read the contents to a new
                            // string
                            long size;

                            while ( (size = FILEINPUT_start_time.read()) != -1) {
                                // appending the characters to a string
                                FILEINPUT_start_time_newtext += Character.toString((char) size);
                            }

                        }
                        catch (Exception error) {
                            Log.e("could not read ", "start time");
                        }

                        // now that we have read from our input file
                        // we have our start time
                        // now we can calculate the amount of time the user has pressed the button
                        // end time - start time
                        // end_time: end time is already a long
                        // start time needs to be converted to a long
                        long start_time_long = Long.valueOf(FILEINPUT_start_time_newtext);
                        long time_pressed = end_time - start_time_long;

                        String time_pressed_string = String.valueOf(time_pressed);

                        // let's display it on our UI
                        text_box_pressed.setText(time_pressed_string);


                        break;
                    }




                }


                return false;
            }
        });


















    }
}
