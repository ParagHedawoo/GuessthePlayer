package com.example.paraghedawoo.guesstheplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> playerNames = new ArrayList<>();
    ArrayList<String> playerURLs = new ArrayList<>();
    ArrayList<String> options = new ArrayList<>();
    ImageView imageView;
    Button button1;
    Button button2;
    Button button3;
    Button button4;

    int chosenPlayer = 0;
    int correctOption = 0;
    int otherOptions = 0;
    int tag;

    public void newQuestion(){

        Random random = new Random();

        chosenPlayer = random.nextInt(playerNames.size());
        correctOption = random.nextInt(4);

        DownloadImage imageTask = new DownloadImage();
        imageView = (ImageView) findViewById(R.id.imageView2);

        try {
            Bitmap image = imageTask.execute(playerURLs.get(chosenPlayer)).get();
            imageView.setImageBitmap(image);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        for(int i =0; i<4 ; i++){
            if (i == correctOption){
                options.add(i, playerNames.get(chosenPlayer));
            }
            else{
                otherOptions = random.nextInt(playerNames.size());
                while(otherOptions == chosenPlayer){
                    otherOptions = random.nextInt(playerNames.size());
                }
                options.add(i, playerNames.get(otherOptions));
            }
        }
        button1.setText(options.get(0));
        button2.setText(options.get(1));
        button3.setText(options.get(2));
        button4.setText(options.get(3));
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url ;
            HttpURLConnection connection;
            try{
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                Bitmap myImage = BitmapFactory.decodeStream(inputStream);
                return myImage;
            }
            catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url ;
            HttpURLConnection connection;
            try{
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public void tappedAnswer(View view){


        if (view.getTag().toString().equals(Integer.toString(correctOption))){
            Toast.makeText(getApplicationContext(), "the answer is correct", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "the answer is wrong", Toast.LENGTH_SHORT).show();
        }

        newQuestion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.one);
        button2 = (Button)findViewById(R.id.two);
        button3 = (Button)findViewById(R.id.three);
        button4 = (Button)findViewById(R.id.four);

        DownloadTask task1 = new DownloadTask();
        try {
            String webPageCode1 = task1.execute("http://www.worldblaze.in/top-20-most-famous-sports-personalities-in-india-ever/").get();
            String[] part1_1 = webPageCode1.split("alt=\"\"");
            String[] part1_2 = part1_1[1].split("\">Sushil Kumar");

            Pattern p = Pattern.compile(" alt=\"(.*?)\" w");
            Matcher m = p.matcher(part1_2[0]);

            while (m.find()){
                playerNames.add(m.group(1));
            }

            p = Pattern.compile("\" src=\"(.*?)\"");
            m = p.matcher(part1_2[0]);

            while (m.find()){
                playerURLs.add(m.group(1));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.out.println("Failed ! ");
        }
        newQuestion();
    }
}
