package com.tacuna.android;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.example.tcpclient.R;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ToggleButton;

/**
 * The applications main activity.
 * 
 */
public class MainActivity extends AppMenuActivity
{
    private ListView mList;
    private ArrayList<String> arrayList;
    private MyCustomAdapter mAdapter;
    private ListViewUpdater listUpdater;
    // Location of saved files on SD Card
    private static final String FILE_DIR = "/TCPClient/";
    private File sdCard = Environment.getExternalStorageDirectory();
    private File dir = new File (sdCard.getAbsolutePath() + FILE_DIR);
    private String filename="";
    private String extension=".txt";         
    
    private LogFileWriter fileWriter;
    private ToggleButton record;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<String>();

        record = (ToggleButton)findViewById(R.id.toggleRecord);
                
        //relate the listView from java to the one created in xml
        mList = (ListView)findViewById(R.id.list);
        mAdapter = new MyCustomAdapter(this, arrayList);
                
        mList.setAdapter(mAdapter);
        
        // Create and register the device message consumer.
        listUpdater = new ListViewUpdater(this, mAdapter);
        ConnectionManager.INSTANCE.getConnectionMessageProducer().registerObserver(listUpdater);       
        

	record.setOnClickListener(new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
	        if(record.isChecked()){
	        	//Create and register the log file writer
	        	fileWriter = new LogFileWriter(dir.getAbsolutePath(),extension);
	        	ConnectionManager.INSTANCE.getConnectionMessageProducer().registerObserver(fileWriter);
	        
	        	dir.mkdirs();
	        	filename = Long.toString(System.currentTimeMillis());
	        	try {
	        		fileWriter.startNewFile(filename);
	        	} catch (IOException err) {
	        		Log.e(LogFileWriter.TAG,
	        				"Could not open log file. No data log will be created.",
	        				err);
	        	}
	        } else {
	            ConnectionManager.INSTANCE.getConnectionMessageProducer().removeObserver(fileWriter);	        	
	        }
		}
	});	
    }
}