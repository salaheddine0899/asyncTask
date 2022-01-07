package com.example.asyntask;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView result;
    //EditText urlInput;
    ListView lv1;
    ArrayList arr=new ArrayList();
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result=(TextView) findViewById(R.id.data);
        lv1=(ListView)findViewById(R.id.lv1);
        MyAsyncTask asyncTask =new MyAsyncTask();
        asyncTask.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=10/xml");
    }

    public void buLoad(View view) {

    }

      class MyAsyncTask extends AsyncTask<String, String, String> {
        String newData="";
        @Override
        protected void onPostExecute(String s) {
            //result.setText(newData);
            lv1.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,arr));

        }

        @Override
        protected String doInBackground(String... strings) {
            publishProgress("Open connection.");
            String s="";
            try {
                URL url=new URL(strings[0]);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.connect();
                //publishProgress("start reading!!");
                InputStream in=new BufferedInputStream(urlConnection.getInputStream());
                newData=Stream2String(in);
                in.close();
                //publishProgress(newData);
                StringReader fis = new StringReader(newData);
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(fis);
                parser.next();
                parser.require(XmlPullParser.START_TAG, null, "feed");


                while ((parser.next()) != XmlPullParser.END_DOCUMENT) {
                    if(parser.getName()!=null) {
                        if(parser.getName().equals("entry")) {
                            if(parser.getEventType()!=XmlPullParser.START_TAG)    continue;
                            while(parser.next()!=XmlPullParser.END_TAG) {
                                //parser.next();
                                //while((parser.getName()==null)){parser.next();}
                                if (parser.getName() != null) {
                                    if(parser.getName().equals("id")){
                                        while(parser.next()!=XmlPullParser.END_TAG) ;
                                    }
                                    if (parser.getName().equals("title")) {
                                        //parser.require(XmlPullParser.START_TAG,null,"firstName");
                                        parser.next();
                                        s += parser.getText();
                                        parser.next();
                                        parser.require(XmlPullParser.END_TAG, null, "title");
                                        arr.add(s);
                                        s = "";
                                    }
                                }
                            }
                                /*if (parser.getName().equals("title")) {
                            //parser.require(XmlPullParser.START_TAG,null,"firstName");
                            parser.next();
                            s += "\t" + parser.getText();
                            parser.next();
                            parser.require(XmlPullParser.END_TAG, null, "title");
                            arr.add(s);
                            s = "";
                        }*/
                        }
                    }
                }

            }catch (Exception exp){
                publishProgress("cannot connect to server");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            result.setText(values[0]);
        }

        @SuppressLint("WrongThread")
        @Override
        protected void onPreExecute() {
            newData="";
            publishProgress("connecting attempt ongoing please wait !.");
        }
        public String Stream2String(InputStream in){
            BufferedReader buReader=new BufferedReader(new InputStreamReader(in));
            String text="",line;
            try {
                while ((line=buReader.readLine())!=null){
                    text+=line;
                }
            }catch (Exception exp){}
            return text;
        }
    }
}