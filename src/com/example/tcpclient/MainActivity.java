package com.example.tcpclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity
{
    private ListView mList;
    private ArrayList<String> arrayList;
    private MyCustomAdapter mAdapter;
    private TCPClient mTcpClient;
    // Location of saved files on SD Card
    private static final String FILE_DIR = "/TCPClient/";
    private File sdCard = Environment.getExternalStorageDirectory();
    private File dir = new File (sdCard.getAbsolutePath() + FILE_DIR);
    private String filename="";
    private String extension=".txt";
    private EditText ipAddressInput;
    private EditText portInput;
    private Button send;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<String>();

        final EditText editText = (EditText) findViewById(R.id.editText);
        send = (Button)findViewById(R.id.send_button);
        send.setEnabled(false);
        Button connect = (Button)findViewById(R.id.connect_button);
        
        ipAddressInput = (EditText)findViewById(R.id.ip_address);
        portInput =  (EditText)findViewById(R.id.port);
        //relate the listView from java to the one created in xml
        mList = (ListView)findViewById(R.id.list);
        mAdapter = new MyCustomAdapter(this, arrayList);
        mList.setAdapter(mAdapter);

        connect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new connectTask().execute("");	
				send.setEnabled(true);
			}
		});
        // connect to the server        
        
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = editText.getText().toString();

                //add the text in the arrayList
                arrayList.add("c: " + message);

                //sends the message to the server
                if (mTcpClient != null) {
                    mTcpClient.sendMessage(message);
                }

                //refresh the list
                mAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        });

    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {    	
    	
        @Override
        protected TCPClient doInBackground(String... message) {        	
        	// create file pointer only once            
            dir.mkdirs();            
            filename = Long.toString(System.currentTimeMillis());
        	        	
            //we create a TCPClient object and
            mTcpClient = new TCPClient(ipAddressInput.getText().toString(),
            						   Integer.valueOf(portInput.getText().toString()),
            						   new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                    //write message to file on device
                    //TODO: This may need to be buffered to prevent
                    //accessing file to often
                    writeToFile(message);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //in the arrayList we add the messaged received from server
            arrayList.add(values[0]);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
        }
        
        protected void writeToFile(String message) {        	
        	File data = new File(dir.getAbsolutePath()+"/"+filename+extension);
        	if (!data.exists())	{
        		try {
        			data.createNewFile();
        		} 
        	    catch (IOException e) {
        	    	// TODO Auto-generated catch block
        	    	e.printStackTrace();
        	    }
        	}
        	   	
        	try {
        		//BufferedWriter for performance, true to set append to file flag
        		BufferedWriter buf = new BufferedWriter(new FileWriter(data, true)); 
        		buf.append(message);
        		buf.newLine();
        		buf.flush();
        		buf.close();
        	}
        	catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}        	
        }
    }
}
