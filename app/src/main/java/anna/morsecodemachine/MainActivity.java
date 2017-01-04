package anna.morsecodemachine;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import static java.lang.Math.abs;


public class MainActivity extends AppCompatActivity {



    // initialize variables
    Button button_tone, button_clear;
    ToneGenerator toneGenerator;

    String filename = "myfile";
    String string = "Hello world!";
    FileOutputStream outputStream;

    // flags: have we gotten to the first pause?
    // 0 if the user is still pressing the button
    // and has not pressed it a second time
    // 1 if the user has pressed the button a second time
    // therefore we know how long he waited between presses
    // clear sets this back to 0
    int start_flag = 0;

    String dit_dah_conversion = "";
    String dit_dah_conversion_final = "";

    String start_time_string = "";
    String end_time_string = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set title for toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Morse Code Machine");


        // start up the tone generator
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);

        // start up button
        button_tone = (Button) findViewById(R.id.button_tone);

        // clear button
        button_clear = (Button) findViewById(R.id.button_clear);




        // start up textview
        // first text box is phone time (milliseconds)
        // when you press the morse button initially
        // second text box is phone time (milliseconds)
        // when you release the morse button
        final TextView text_box_start = (TextView)findViewById(R.id.text_start_time);
        final TextView text_box_end = (TextView)findViewById(R.id.text_end_time);

        // edit text box is the number of milliseconds that you pressed the morse button
        // it's the values of first text box minus values of second box
        final TextView text_box_press = (TextView)findViewById(R.id.absolute_millis);


        // edit text box is the number of milliseconds since you last pressed the morse button
        // it's the number of the last text box minus the new first box
        final TextView text_box_pause = (TextView)findViewById(R.id.pause_millis);

        // edit text box is the conversation of dits and dahs to letters
        // hard coded <150 ms is a dit
        // >150 ms is a dah
        final TextView text_box_ditdah = (TextView)findViewById(R.id.ditdah);


        // text box is the record of all the word dits and dahs
        final TextView text_box_morse_letter = (TextView)findViewById(R.id.morse_sentence);

        // box is the translation of dit-dahs to letters and numbers
        final TextView text_box_sentence = (TextView)findViewById(R.id.sentence);

        // this text box shows in real time what the current dit dah is
        final TextView text_box_real_time_letter = (TextView)findViewById(R.id.realtime_letter);

        final String FILE_NAME_start_time = "start_time.txt";
        final String FILE_NAME_end_time = "end_time.txt";
        final String FILE_NAME_absolute_change = "absolute_change.txt";
        final String FILE_NAME_pause = "pause.txt";

        final String FILE_NAME_dit_dah = "dit_dah.txt";
        final String FILE_NAME_dit_dah_final = "dit_dah_final.txt";


        try {
            // Create file for final ditdah
            FileOutputStream fos = openFileOutput(FILE_NAME_dit_dah_final, MODE_PRIVATE);
            fos.write("".getBytes());
            fos.close();

        } catch (Exception error) {
            // Exception
        }


        try {
            // Create file for final ditdah
            FileOutputStream fos = openFileOutput(FILE_NAME_dit_dah, MODE_PRIVATE);
            fos.write("".getBytes());
            fos.close();

        } catch (Exception error) {
            // Exception
        }










        // set the on touch listener for the morse button
        button_tone.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // if button is pressed down
                // if button is released
                switch (event.getAction()) {

                    // when button is pressed down
                    case MotionEvent.ACTION_DOWN: {

                        // you generate a beeeeep sound
                        toneGenerator.startTone(ToneGenerator.TONE_DTMF_D);

                        // keep track of when you start pressing the button
                        long start_time = 0;
                        long millis_start = System.currentTimeMillis() - start_time;

                        // put the start time in the first text box
                        // and save it

                        // write the string value of the smart time to the file for start time
                        try {
                            // Create file for the start time
                            FileOutputStream fos = openFileOutput(FILE_NAME_start_time, MODE_PRIVATE);
                            fos.write(String.valueOf(millis_start).getBytes());
                            fos.close();

                        } catch (Exception error) {
                            // Exception
                        }

                        // calculate the pause
                        // if the start flag is true, ie, the first pause is reached
                        // then you can display the first pause
                        if (start_flag == 1) {

                            // to calculate the pause, we need to get the end time
                            // initialize the file input string
                            String FILE_INPUT_end_time_newtext = "";

                            // now calculate how long you were pressing that button
                            try {

                                FileInputStream FILE_INPUT_end_time = openFileInput(FILE_NAME_end_time);
                                int size;

                                // read inside if it is not null (-1 means empty)
                                while ((size = FILE_INPUT_end_time.read()) != -1) {
                                    // add & append content
                                    FILE_INPUT_end_time_newtext += Character.toString((char) size);
                                }

                            } catch (Exception error) {
                                // Exception
                            }

                            // get the starting time
                            end_time_string = FILE_INPUT_end_time_newtext;
                            long number_end = Long.valueOf(end_time_string);

                            // get difference between the two numbers
                            // needs to be absolute since it will be negative
                            long pause_number = abs(number_end - millis_start);

                            // convert to string so we can put display it
                            String string_pause_number = String.valueOf(pause_number);

                            // set it to the total press text box
                            text_box_pause.setText(string_pause_number);


                            // write the string value of the pause
                            try {
                                // Create file for the start time
                                FileOutputStream fos = openFileOutput(FILE_NAME_pause, MODE_PRIVATE);
                                fos.write(String.valueOf(string_pause_number).getBytes());
                                fos.close();

                            } catch (Exception error) {
                                // Exception
                            }


                        }
                        break;
                    }


                    // when button is released
                    case MotionEvent.ACTION_UP: {

                        // stop the noise
                        toneGenerator.stopTone();

                        // we at starting the first pause
                        start_flag = 1;

                        // we record when we stopped pressing the button
                        long end_time = 0;
                        long millis_end = System.currentTimeMillis() - end_time;

                        // put the end time in the second text box
                        // text_box_end.setText(String.valueOf(millis_end));

                        // Create file for the end time
                        try {
                            FileOutputStream fos = openFileOutput(FILE_NAME_end_time, MODE_PRIVATE);
                            fos.write(String.valueOf(millis_end).getBytes());
                            fos.close();

                        } catch (Exception error) {
                            // Exception
                        }

                        // now calculate how long you were pressing that button
                        // first, we need to get the start time
                        // initialize the file input string
                        String FILE_INPUT_start_time_newtext = "";

                        try {

                            FileInputStream FILE_INPUT_start_time = openFileInput(FILE_NAME_start_time);
                            int size;

                            // read inside if it is not null (-1 means empty)
                            while ((size = FILE_INPUT_start_time.read()) != -1) {
                                // add & append content
                                FILE_INPUT_start_time_newtext += Character.toString((char) size);
                            }

                        } catch (Exception error) {
                            // Exception
                        }

                        // get the starting time
                        start_time_string = FILE_INPUT_start_time_newtext;
                        long number_start = Long.valueOf(start_time_string);

                        // get difference between the two numbers
                        long absolute_change = millis_end - number_start;

                        // convert to string so we can put display it
                        String string_absolute_change = String.valueOf(absolute_change);

                        // set it to the total press text box
                        text_box_press.setText(string_absolute_change);

                        // save the absolute change string to a file
                        // umm because
                        try {
                            // Create file for the start time
                            FileOutputStream fos = openFileOutput(FILE_NAME_absolute_change, MODE_PRIVATE);
                            fos.write(string_absolute_change.getBytes());
                            fos.close();

                        } catch (Exception error) {
                            // Exception
                        }


                        // now that we know how long the button was pressed
                        // algorithm for determining whether a press is a
                        // dit or a dah
                        if (absolute_change < 155) {
                            dit_dah_conversion = ".";
                            Log.e("ditdah is ", " a period");
                        } else {
                            dit_dah_conversion = "-";
                            Log.e("ditdah is ", " a dash");
                        }

                        // write the string of the dit dah
                        try {
                            // Create file for the start time
                            FileOutputStream fos = openFileOutput(FILE_NAME_dit_dah, MODE_PRIVATE);
                            fos.write(dit_dah_conversion.getBytes());
                            fos.close();

                        } catch (Exception error) {
                            // Exception
                        }


                        text_box_ditdah.setText(dit_dah_conversion);



                        int dit_dah_length = dit_dah_conversion_final.length();
                        // String display = Integer.toString(dit_dah_length);
                        // Log.e("the length of anna is ", display);



                        // now get the pause time
                        long absolute_pause_number = 20;
                        String FILE_INPUT_pause_newtext = "";
                        try {

                            FileInputStream FILE_INPUT_pause = openFileInput(FILE_NAME_pause);
                            int size;

                            // read inside if it is not null (-1 means empty)
                            while ((size = FILE_INPUT_pause.read()) != -1) {
                                // add & append content
                                FILE_INPUT_pause_newtext += Character.toString((char) size);
                                absolute_pause_number = Long.parseLong(FILE_INPUT_pause_newtext);
                            }

                        } catch (Exception error) {
                            // Exception
                        }
                        Log.e("the pause length is ", String.valueOf(absolute_pause_number));


                        // if the length of the dit dahs is less than 6
                        // then the user is possibly still typing the word
                        // and you need to figure out if they are done or not
                        if (dit_dah_length < 6) {

                            // if the pause is short, then the user is still typing
                            if (absolute_pause_number < 500 ) {

                                dit_dah_conversion_final = dit_dah_conversion_final + dit_dah_conversion;
                                text_box_ditdah.setText(dit_dah_conversion_final);
                            }

                            else {

                                // first, show the full display of dit dah in the edit box
                                text_box_morse_letter.append(dit_dah_conversion_final + " ");

                                // now is a good time to commit this to memory
                                // write the string of the dit dah
                                try {
                                    // Create file for final ditdah
                                    FileOutputStream fos = openFileOutput(FILE_NAME_dit_dah_final, MODE_PRIVATE);
                                    fos.write(dit_dah_conversion_final.getBytes());
                                    fos.close();

                                } catch (Exception error) {
                                    // Exception
                                }




                                if (start_flag == 1) {

                                    // get the current ditdah
                                    String dit_dah_final = "";
                                    String FILE_INPUT_dit_dah_final_newtext = "";
                                    try {

                                        FileInputStream FILE_INPUT_dit_dah_final = openFileInput(FILE_NAME_dit_dah_final);
                                        int size;

                                        // read inside if it is not null (-1 means empty)
                                        while ((size = FILE_INPUT_dit_dah_final.read()) != -1) {
                                            // add & append content
                                            FILE_INPUT_dit_dah_final_newtext += Character.toString((char) size);
                                        }

                                    } catch (Exception error) {
                                        // Exception
                                    }

                                    dit_dah_final = FILE_INPUT_dit_dah_final_newtext;
                                    Log.e("what it is ", dit_dah_final);

                                    // a method to convert dit dahs to english letters
                                    // input: full dit dahs
                                    // output: english letters
                                    String input_to_conversion_method = dit_dah_final;
                                    String output_conversion = DitDahConversion.convert_ditdah_to_letters(
                                            input_to_conversion_method);

                                    text_box_sentence.append(output_conversion);



                                    if (absolute_pause_number > 1000) {
                                        Log.e("the pause length is ", String.valueOf(absolute_pause_number));
                                        text_box_sentence.append("  ");
                                    }


                                } // start flag



                                // then clear the ditdahs
                                dit_dah_conversion_final = "";
                                dit_dah_conversion_final = dit_dah_conversion;

                                // lastly, display the dit dah input
                                text_box_ditdah.setText(dit_dah_conversion_final);
                            }



                        }


                        // if the length of the dit dahs is 6
                        // then the user is definitely done typing the word
                        // and you can start the conversion to english letters
                        else {

                            // add the final dit dahs to the morse edit table
                            text_box_morse_letter.append(dit_dah_conversion_final + " ");

                            // now is a good time to commit this to memory
                            // write the string of the dit dah
                            try {
                                // Create file for final ditdah
                                FileOutputStream fos = openFileOutput(FILE_NAME_dit_dah_final, MODE_PRIVATE);
                                fos.write(dit_dah_conversion_final.getBytes());
                                fos.close();

                            } catch (Exception error) {
                                // Exception
                            }







                            if (start_flag == 1) {

                                // get the current ditdah
                                String dit_dah_final = "";
                                String FILE_INPUT_dit_dah_final_newtext = "";
                                try {

                                    FileInputStream FILE_INPUT_dit_dah_final = openFileInput(FILE_NAME_dit_dah_final);
                                    int size;

                                    // read inside if it is not null (-1 means empty)
                                    while ((size = FILE_INPUT_dit_dah_final.read()) != -1) {
                                        // add & append content
                                        FILE_INPUT_dit_dah_final_newtext += Character.toString((char) size);
                                    }

                                } catch (Exception error) {
                                    // Exception
                                }

                                dit_dah_final = FILE_INPUT_dit_dah_final_newtext;
                                Log.e("what it is ", dit_dah_final);

                                // a method to convert dit dahs to english letters
                                // input: full dit dahs
                                // output: english letters
                                String input_to_conversion_method = dit_dah_final;
                                String output_conversion = DitDahConversion.convert_ditdah_to_letters(
                                        input_to_conversion_method);
                                Log.e("this is a " , output_conversion);

                                text_box_sentence.append(output_conversion);



                                Log.e("the pause length is ", String.valueOf(absolute_pause_number));

                                if (absolute_pause_number > 1000) {
                                    text_box_sentence.append("  ");
                                }


                            } // start flag



                            // then clear the ditdahs
                            dit_dah_conversion_final = "";
                            dit_dah_conversion_final = dit_dah_conversion;

                            // lastly, display the dit dah input
                            text_box_ditdah.setText(dit_dah_conversion_final);

                        }

                }
                        break;

                }
                return false;
            }


            // when the morse letter changes, automatically convert this to a letter
            // and display it
        }); // on Touch listener for button
        text_box_ditdah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (text_box_real_time_letter != null) {

                    // a method to convert dit dahs to english letters
                    // input: full dit dahs
                    // output: english letters
                    String input_real_time = text_box_ditdah.getText().toString();
                    String output_real_time = DitDahConversion.convert_ditdah_to_letters(
                            input_real_time);
                    Log.e("this is a " , output_real_time);

                    text_box_real_time_letter.setText(output_real_time);

                }


            }
        });



        // set the clear button
        // it should clear:

        button_clear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                // set start flag to 0
                start_flag = 0;

                dit_dah_conversion_final = "";
                dit_dah_conversion = "";

                try {
                    // Create file for final ditdah
                    FileOutputStream fos = openFileOutput(FILE_NAME_dit_dah_final, MODE_PRIVATE);
                    fos.write(dit_dah_conversion_final.getBytes());
                    fos.close();

                } catch (Exception error) {
                    // Exception
                }


                try {
                    // Create file for final ditdah
                    FileOutputStream fos = openFileOutput(FILE_NAME_dit_dah, MODE_PRIVATE);
                    fos.write(dit_dah_conversion.getBytes());
                    fos.close();

                } catch (Exception error) {
                    // Exception
                }

                // set the start and end times to 0 or null
                text_box_start.setText(null);
                text_box_end.setText(null);

                // set the total diff to 0 or null
                text_box_press.setText(null);

                // set the total pause to 0 or null
                text_box_pause.setText(null);

                //  erase the editbox with ditdahs
                text_box_ditdah.setText("");

                /// set the morse code line to empty
                text_box_morse_letter.setText("");

                // set the sentence line to empty
                text_box_sentence.setText("");

                // set the real time conversion to empty
                text_box_real_time_letter.setText("");

                return false;
            }
        });

    }




    // this is a class called Dit Dah Conversion
    // so far, the only subroutine is convert_ditdah_to_letters

    public static class DitDahConversion {

        public static String convert_ditdah_to_letters(String input_ditdah) {
            // input: full dit dahs
            // output: english letters


            String what_letter_is_this = input_ditdah;

            String final_letter_is = "";
            // you can translate from morse code to letters now
            // now convert the dit-dah to dit dit dah or dah dit dah to letters
            // basically string them together

            //}
            switch (what_letter_is_this) {
                case ".-":
                    final_letter_is = "a";
                    break;
                case "-...":
                    final_letter_is = "b";
                    break;
                case "-.-.":
                    final_letter_is = "c";
                    break;
                case "-..":
                    final_letter_is = "d";
                    break;
                case ".":
                    final_letter_is = "e";
                    break;
                case "..-.":
                    final_letter_is = "f";
                    break;
                case "--.":
                    final_letter_is = "g";
                    break;
                case "....":
                    final_letter_is = "h";
                    break;
                case "..":
                    final_letter_is = "i";
                    break;
                case ".---":
                    final_letter_is = "j";
                    break;
                case "-.-":
                    final_letter_is = "k";
                    break;
                case ".-..":
                    final_letter_is = "l";
                    break;
                case "--":
                    final_letter_is = "m";
                    break;
                case "-.":
                    final_letter_is = "n";
                    break;
                case "---":
                    final_letter_is = "o";
                    break;
                case ".--.":
                    final_letter_is = "p";
                    break;
                case "--.-":
                    final_letter_is = "q";
                    break;
                case ".-.":
                    final_letter_is = "r";
                    break;
                case "...":
                    final_letter_is = "s";
                    break;
                case "-":
                    final_letter_is = "t";
                    break;
                case "..-":
                    final_letter_is = "u";
                    break;
                case "...-":
                    final_letter_is = "v";
                    break;
                case ".--":
                    final_letter_is = "w";
                    break;
                case "-..-":
                    final_letter_is = "x";
                    break;
                case "-.--":
                    final_letter_is = "y";
                    break;
                case "--..":
                    final_letter_is = "z";
                    break;
                case "-----":
                    final_letter_is = "0";
                    break;
                case ".----":
                    final_letter_is = "1";
                    break;
                case "..---":
                    final_letter_is = "2";
                    break;
                case "...--":
                    final_letter_is = "3";
                    break;
                case "....-":
                    final_letter_is = "4";
                    break;
                case ".....":
                    final_letter_is = "5";
                    break;
                case "-....":
                    final_letter_is = "6";
                    break;
                case "--...":
                    final_letter_is = "7";
                    break;
                case "---..":
                    final_letter_is = "8";
                    break;
                case "----.":
                    final_letter_is = "9";
                    break;
                case ".-.-":
                    final_letter_is = "ä";
                    break;
                case "---.-":
                    final_letter_is = "á";
                    break;
                case ".--.-":
                    final_letter_is = "å";
                    break;
                case "----":
                    final_letter_is = "ch";
                    break;
                case "..-..":
                    final_letter_is = "é";
                    break;
                case "--.--":
                    final_letter_is = "ñ";
                    break;
                case "---.":
                    final_letter_is = "ö";
                    break;
                case "..--":
                    final_letter_is = "ü";
                    break;
                case ".-.-.-":
                    final_letter_is = ".";
                    break;
                case "--..--":
                    final_letter_is = ",";
                    break;
                case "---...":
                    final_letter_is = ":";
                    break;
                case "..--..":
                    final_letter_is = "?";
                    break;
                case ".----.":
                    final_letter_is = "'";
                    break;
                case "-....-":
                    final_letter_is = "-";
                    break;
                case "-..-.":
                    final_letter_is = "/";
                    break;
                case "-.--.-":
                    final_letter_is = "(";
                    break;
                case ".-..-.":
                    final_letter_is = "'";
                    break;
                case ".--.-.":
                    final_letter_is = "@";
                    break;
                case "-...-":
                    final_letter_is = "=";
                    break;

                case "......":
                    final_letter_is = "nothing!";
                    break;

                default:
                    final_letter_is = "that isn't a real letter!";
                    break;
            }


            return final_letter_is;

        }

    }


}