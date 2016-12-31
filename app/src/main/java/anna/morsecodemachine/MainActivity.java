package anna.morsecodemachine;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.Objects;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {

    // initialize variables
    Button button_tone, button_clear;
    ToneGenerator toneGenerator;
    TextView text_box_start, text_box_end;
    EditText absolute_millis, alphanum_letter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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
        final TextView textbox_time_start = (TextView)findViewById(R.id.text_start_time);
        final TextView textbox_time_end = (TextView)findViewById(R.id.text_end_time);

        // edit text box is the number of milliseconds that you pressed the morse button
        // it's the values of first text box minus values of second box
        final EditText editbox_absolute = (EditText)findViewById(R.id.absolute_millis);
        // edit text box is the number of milliseconds since you last pressed the morse button
        // it's the number of the last text box minus the new first box
        final EditText editbox_pause = (EditText)findViewById(R.id.pause_millis);

        // edit text box is the conversation of dits and dahs to letters
        // hard coded <150 ms is a dit
        // >150 ms is a dah
        final EditText editbox_alphamum = (EditText)findViewById(R.id.alphanum_letter);

        // edit text box is the record of all the word dits and dahs
        final EditText editbox_morse_letter = (EditText)findViewById(R.id.morse_sentence);

        // edit box is the translation of dit-dahs to letters and numbers
        final EditText editbox_sentence = (EditText)findViewById(R.id.sentence);


        // set up context for toasts
        // this is just in the initial phases
        final Context context = getApplicationContext();
        final int toast_duration = Toast.LENGTH_SHORT;


        // have we gotten to the first pause yet
        final int[] start_flag = {0};

        // set the on touch listener for the morse button
        button_tone.setOnTouchListener(new View.OnTouchListener() {




            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // if button is pressed down
                // if button is released
                switch (event.getAction()) {

                    // when button is pressed down
                    case MotionEvent.ACTION_DOWN: {

                        // this toast confirms that the button works
                        //CharSequence text = "you pressed the button!";
                        //Toast toast = Toast.makeText(context, text, toast_duration);
                        //toast.show();

                        // you generate a beeeeep sound
                        toneGenerator.startTone(ToneGenerator.TONE_DTMF_D);


                        // keep track of when you start pressing the button
                        // maybe I could change the start time so that the system time
                        // isn't like 983164838417981
                        double start_time = 0;
                        double millis_start = System.currentTimeMillis() - start_time;


                        // put the start time in the first text box
                        // I do this because I want to show how long something is pressed
                        // but also because I don't know how to use internal storage
                        // to store values ....
                        // meep
                        if (textbox_time_start != null) {
                            textbox_time_start.setText(String.valueOf(millis_start));
                        }


                        // if the start flag is true, ie, the first pause is reached
                        // then you can display the first pause
                        if (start_flag[0] == 1) {


                            // get the last ending number
                            String string_end_time = textbox_time_end.getText().toString();
                            double number_end = Double.parseDouble(string_end_time);

                            // subtract that from the start time of the press
                            // it will be an absolute number so you have to use math class
                            double pause_number =  abs(number_end - millis_start);

                            // then subtract however long it took you to press the key
                            String length_of_press = editbox_absolute.getText().toString();
                            //double press_number =   Double.parseDouble(length_of_press);


                            //double real_pause =  pause_number;

                            editbox_pause.setText(String.valueOf(pause_number));

                            // you also should clear the morses letter edit box
                            //editbox_morse_letter.setText("");


                        };


                        break;
                    }

                    // when button is released
                    case MotionEvent.ACTION_UP: {

                        // stop the noise
                        toneGenerator.stopTone();

                        // we at starting the first pause
                        start_flag[0] = 1;

                        // we record when we stopped pressing the button
                        double end_time = 0;
                        double millis_end = System.currentTimeMillis() - end_time;


                        // put the end time in the second text box
                        textbox_time_end.setText(String.valueOf(millis_end));

                        // algorithm for determining whether a press is a
                        // dit or a dah
                        //if (millis_end - millis_start)

                        break;
                    }

                }

                return false;
            }

        });


        // set the clear button
        // it should clear:

        button_clear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                /// set the morse code line to empty
                editbox_morse_letter.setText("");
                editbox_sentence.setText("");
                // set the sentence line to empty

                return false;
            }
        });


        // calculate how long we had pressed the button down
        // we have to check if the text box actually has value in it ...
        if (textbox_time_end != null) {
            textbox_time_end.addTextChangedListener(new TextWatcher() {

                // default methods
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                // default methods
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                // after the text on the release text box is changed
                // look at the start text box
                // and calculate the difference between them
                @Override
                public void afterTextChanged(Editable s) {

                    // get the string values from the two boxes
                    // don't need to parse because I didn't put prose text yay
                    //they're long numbers, so I have to convert to String
                    String start_time_string = textbox_time_start.getText().toString();
                    String string_end_time = textbox_time_end.getText().toString();

                    // convert both those numbers to double
                    // so we can do subtraction with them
                    double number_start = Double.parseDouble(start_time_string);
                    double number_end = Double.parseDouble(string_end_time);

                    // get difference between the two numbers
                    double absolute_change = number_end - number_start;

                    // convert to string so we can put display it
                    String string_absolute_change = Double.toString(absolute_change);

                    // display the amount of time (milliseconds) that morse button
                    // was pressed
                    editbox_absolute.setText(string_absolute_change);

                }
            });
        }

        // convert the duration of button press to dits and dahs
        // <150 ms is dit
        // >150 ms is dah
        if (editbox_absolute != null) {
            editbox_absolute.addTextChangedListener(new TextWatcher() {

                String dit_dah_conversion_final = "";

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                // get the absolute difference of ms from the absolute difference textbox
                // convert to dits .
                // or convert to dahs -
                // display
                @Override
                public void afterTextChanged(Editable s) {


                    // first get the time that the button is pressed
                    // this is the absolute difference
                    Log.e("alpha numeric thing ", " is pressed");
                    String get_editbox_absolute_diff = editbox_absolute.getText().toString();
                    double absolute_diff_number = Double.parseDouble(get_editbox_absolute_diff);

                    // second, get the time that the user waits between presses
                    // this is the pause difference
                    // but before the second letter press, there will be nothing there
                    // so we have to make sure this only runs if the second letter has been pressed

                    boolean start_flag = false;
                    double absolute_pause_number = 20;

                    String get_editbox_pause_diff = editbox_pause.getText().toString();

                    if (get_editbox_pause_diff.equals("Pause milliseconds")) {

                        start_flag = false;
                    }
                    else {

                        start_flag= true;

                        absolute_pause_number = Double.parseDouble(get_editbox_pause_diff);

                    }


                    //Log.e("and it's ", what);

                    String dit_dah_conversion = "";



                    // Les Kerr says that the threshold for distingishing a dit from a dah
                    // is 1.9. I think a dit is 100 ms so a dah should be
                    // 190 ms
                    // note to self: set this in SharedPreferences
                    if (absolute_diff_number < 244) {
                        dit_dah_conversion = ".";
                        Log.e("ditdah is ", " a period");
                    }
                    else {
                        dit_dah_conversion = "-";
                        Log.e("ditdah is ", " a dash");
                    };

                    // okay now let me think this out
                    //


                    int dit_dah_length = dit_dah_conversion_final.length();
                    String display = Integer.toString(dit_dah_length);
                    Log.e("the length of anna is " , display);

                    if (editbox_alphamum != null) {

                        // if the length of the dit dahs is less than 6
                        // then the user is possibly still typing the word
                        // and you need to figure out if they are done or not
                        if (dit_dah_length < 6) {


                            // if the pause is short, then the user is still typing
                            if (absolute_pause_number < 244 ) {

                                dit_dah_conversion_final = dit_dah_conversion_final + dit_dah_conversion;
                                editbox_alphamum.setText(dit_dah_conversion_final);
                                }

                            // and the pause is really really really long
                            // then you finished the word
                            // and it is ready to be converted
                            else {


                                // add the final dit dahs to the morse edit table
                                editbox_morse_letter.append(dit_dah_conversion_final + "  ");


                                // you can translate from morse code to letters now
                                // now convert the dit-dah to dit dit dah or dah dit dah to letters
                                // basically string them together

                                //}
                                switch (dit_dah_conversion_final) {
                                    case ".-":
                                        editbox_sentence.append("a");
                                        break;
                                    case "-...":
                                        editbox_sentence.append("b");
                                        break;
                                    case "-.-.":
                                        editbox_sentence.append("c");
                                        break;
                                    case "-..":
                                        editbox_sentence.append("d");
                                        break;
                                    case ".":
                                        editbox_sentence.append("e");
                                        break;
                                    case "..-.":
                                        editbox_sentence.append("f");
                                        break;
                                    case "--.":
                                        editbox_sentence.append("g");
                                        break;
                                    case "....":
                                        editbox_sentence.append("h");
                                        break;
                                    case "..":
                                        editbox_sentence.append("i");
                                        break;
                                    case ".---":
                                        editbox_sentence.append("j");
                                        break;
                                    case "-.-":
                                        editbox_sentence.append("k");
                                        break;
                                    case ".-..":
                                        editbox_sentence.append("l");
                                        break;
                                    case "--":
                                        editbox_sentence.append("m");
                                        break;
                                    case "-.":
                                        editbox_sentence.append("n");
                                        break;
                                    case "---":
                                        editbox_sentence.append("o");
                                        break;
                                    case ".--.":
                                        editbox_sentence.append("p");
                                        break;
                                    case "--.-":
                                        editbox_sentence.append("q");
                                        break;
                                    case ".-.":
                                        editbox_sentence.append("r");
                                        break;
                                    case "...":
                                        editbox_sentence.append("s");
                                        break;
                                    case "-":
                                        editbox_sentence.append("t");
                                        break;
                                    case "..-":
                                        editbox_sentence.append("u");
                                        break;
                                    case "...-":
                                        editbox_sentence.append("v");
                                        break;
                                    case ".--":
                                        editbox_sentence.append("w");
                                        break;
                                    case "-..-":
                                        editbox_sentence.append("x");
                                        break;
                                    case "-.--":
                                        editbox_sentence.append("y");
                                        break;
                                    case "--..":
                                        editbox_sentence.append("z");
                                        break;
                                    case "-----":
                                        editbox_sentence.append("0");
                                        break;
                                    case ".----":
                                        editbox_sentence.append("1");
                                        break;
                                    case "..---":
                                        editbox_sentence.append("2");
                                        break;
                                    case "...--":
                                        editbox_sentence.append("3");
                                        break;
                                    case "....-":
                                        editbox_sentence.append("4");
                                        break;
                                    case ".....":
                                        editbox_sentence.append("5");
                                        break;
                                    case "-....":
                                        editbox_sentence.append("6");
                                        break;
                                    case "--...":
                                        editbox_sentence.append("7");
                                        break;
                                    case "---..":
                                        editbox_sentence.append("8");
                                        break;
                                    case "----.":
                                        editbox_sentence.append("9");
                                        break;
                                    case ".-.-":
                                        editbox_sentence.append("ä");
                                        break;
                                    case "---.-":
                                        editbox_sentence.append("á");
                                        break;
                                    case ".--.-":
                                        editbox_sentence.append("å");
                                        break;
                                    case "----":
                                        editbox_sentence.append("ch");
                                        break;
                                    case "..-..":
                                        editbox_sentence.append("é");
                                        break;
                                    case "--.--":
                                        editbox_sentence.append("ñ");
                                        break;
                                    case "---.":
                                        editbox_sentence.append("ö");
                                        break;
                                    case "..--":
                                        editbox_sentence.append("ü");
                                        break;
                                    case ".-.-.-":
                                        editbox_sentence.append(".");
                                        break;
                                    case "--..--":
                                        editbox_sentence.append(",");
                                        break;
                                    case "---...":
                                        editbox_sentence.append(":");
                                        break;
                                    case "..--..":
                                        editbox_sentence.append("?");
                                        break;
                                    case ".----.":
                                        editbox_sentence.append("'");
                                        break;
                                    case "-....-":
                                        editbox_sentence.append("-");
                                        break;
                                    case "-..-.":
                                        editbox_sentence.append("/");
                                        break;
                                    case "-.--.-":
                                        editbox_sentence.append("(");
                                        break;
                                    case ".-..-.":
                                        editbox_sentence.append("'");
                                        break;
                                    case ".--.-.":
                                        editbox_sentence.append("@");
                                        break;
                                    case "-...-":
                                        editbox_sentence.append("=");
                                        break;

                                    default:
                                        editbox_sentence.append("ERROR");
                                        break;
                                }
                                ;



                                dit_dah_conversion_final = "";
                                dit_dah_conversion_final = dit_dah_conversion;
                                editbox_alphamum.setText(dit_dah_conversion_final);


                            }




                        }

                        // the maximum amount of dit dahs is 6, I think
                        // the user is definitely done making the word
                        // you can translate from morse code to letters now
                        else {

                            // first, add the dit dah to the morse code edit box
                            editbox_morse_letter.append(dit_dah_conversion_final + "  ");

                            // then add the translated letter to the English letter edit box
                            // but first
                            // determine what the letter is
                            switch (dit_dah_conversion_final) {
                                case ".-":
                                    editbox_sentence.append("a");
                                    break;
                                case "-...":
                                    editbox_sentence.append("b");
                                    break;
                                case "-.-.":
                                    editbox_sentence.append("c");
                                    break;
                                case "-..":
                                    editbox_sentence.append("d");
                                    break;
                                case ".":
                                    editbox_sentence.append("e");
                                    break;
                                case "..-.":
                                    editbox_sentence.append("f");
                                    break;
                                case "--.":
                                    editbox_sentence.append("g");
                                    break;
                                case "....":
                                    editbox_sentence.append("h");
                                    break;
                                case "..":
                                    editbox_sentence.append("i");
                                    break;
                                case ".---":
                                    editbox_sentence.append("j");
                                    break;
                                case "-.-":
                                    editbox_sentence.append("k");
                                    break;
                                case ".-..":
                                    editbox_sentence.append("l");
                                    break;
                                case "--":
                                    editbox_sentence.append("m");
                                    break;
                                case "-.":
                                    editbox_sentence.append("n");
                                    break;
                                case "---":
                                    editbox_sentence.append("o");
                                    break;
                                case ".--.":
                                    editbox_sentence.append("p");
                                    break;
                                case "--.-":
                                    editbox_sentence.append("q");
                                    break;
                                case ".-.":
                                    editbox_sentence.append("r");
                                    break;
                                case "...":
                                    editbox_sentence.append("s");
                                    break;
                                case "-":
                                    editbox_sentence.append("t");
                                    break;
                                case "..-":
                                    editbox_sentence.append("u");
                                    break;
                                case "...-":
                                    editbox_sentence.append("v");
                                    break;
                                case ".--":
                                    editbox_sentence.append("w");
                                    break;
                                case "-..-":
                                    editbox_sentence.append("x");
                                    break;
                                case "-.--":
                                    editbox_sentence.append("y");
                                    break;
                                case "--..":
                                    editbox_sentence.append("z");
                                    break;
                                case "-----":
                                    editbox_sentence.append("0");
                                    break;
                                case ".----":
                                    editbox_sentence.append("1");
                                    break;
                                case "..---":
                                    editbox_sentence.append("2");
                                    break;
                                case "...--":
                                    editbox_sentence.append("3");
                                    break;
                                case "....-":
                                    editbox_sentence.append("4");
                                    break;
                                case ".....":
                                    editbox_sentence.append("5");
                                    break;
                                case "-....":
                                    editbox_sentence.append("6");
                                    break;
                                case "--...":
                                    editbox_sentence.append("7");
                                    break;
                                case "---..":
                                    editbox_sentence.append("8");
                                    break;
                                case "----.":
                                    editbox_sentence.append("9");
                                    break;
                                case ".-.-":
                                    editbox_sentence.append("ä");
                                    break;
                                case "---.-":
                                    editbox_sentence.append("á");
                                    break;
                                case ".--.-":
                                    editbox_sentence.append("å");
                                    break;
                                case "----":
                                    editbox_sentence.append("ch");
                                    break;
                                case "..-..":
                                    editbox_sentence.append("é");
                                    break;
                                case "--.--":
                                    editbox_sentence.append("ñ");
                                    break;
                                case "---.":
                                    editbox_sentence.append("ö");
                                    break;
                                case "..--":
                                    editbox_sentence.append("ü");
                                    break;
                                case ".-.-.-":
                                    editbox_sentence.append(".");
                                    break;
                                case "--..--":
                                    editbox_sentence.append(",");
                                    break;
                                case "---...":
                                    editbox_sentence.append(":");
                                    break;
                                case "..--..":
                                    editbox_sentence.append("?");
                                    break;
                                case ".----.":
                                    editbox_sentence.append("'");
                                    break;
                                case "-....-":
                                    editbox_sentence.append("-");
                                    break;
                                case "-..-.":
                                    editbox_sentence.append("/");
                                    break;
                                case "-.--.-":
                                    editbox_sentence.append("(");
                                    break;
                                case ".-..-.":
                                    editbox_sentence.append("'");
                                    break;
                                case ".--.-.":
                                    editbox_sentence.append("@");
                                    break;
                                case "-...-":
                                    editbox_sentence.append("=");
                                    break;

                                default:
                                    editbox_sentence.append("ERROR");
                                    break;
                            }


                            // put the letter into alpha numberic sentence editbox
                            // then clear the ditdahs
                            editbox_alphamum.setText(dit_dah_conversion_final);
                            dit_dah_conversion_final = "";
                            dit_dah_conversion_final = dit_dah_conversion;

                        }




                    }

                }
            });
        }







    }

}