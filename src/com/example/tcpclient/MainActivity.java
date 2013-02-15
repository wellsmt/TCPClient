package com.example.tcpclient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.lp.io.DataInterpreter;
import com.lp.io.DeviceMessageInterpretor;
import com.lp.io.SocketConnector;

/**
 * The applications main activity.
 * 
 */
public class MainActivity extends Activity
{
    private ListView mList;
    private ArrayList<String> arrayList;
    private MyCustomAdapter mAdapter;
    private ListViewUpdater listUpdater;
    
    private SocketConnector connection;
    // Location of saved files on SD Card
    private static final String FILE_DIR = "/TCPClient/";
    private File sdCard = Environment.getExternalStorageDirectory();
    private File dir = new File (sdCard.getAbsolutePath() + FILE_DIR);
    private String filename="";
    private String extension=".txt";
    private EditText ipAddressInput;
    private EditText portInput;
    private Button connect;    
    
    //private DataInterpreter dataInterpretor;
    
    private LogFileWriter fileWriter;
   // private PlotUpdater messageConsumer;
   // private TableUpdater tableUpdater;
   // private SensorDataSeries[] dataSeries = new SensorDataSeries[3];
    
    
   // private XYPlot dynamicPlot;

   /* @SuppressWarnings("deprecation")
	protected void onCreatePlot(){
        // get handles to our View defined in layout.xml:
        dynamicPlot = (XYPlot) findViewById(R.id.dynamicPlot);
        dynamicPlot.setTitle("Data Plot");
        dataSeries[0] = new SensorDataSeries("Channel 0", 30);
        dataSeries[1] = new SensorDataSeries("Channel 1", 30);
        dataSeries[2] = new SensorDataSeries("Channel 2", 30);

        // only display whole numbers in domain labels
        dynamicPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
        //dynamicPlot.setBackgroundColor(Color.BLACK);
        Paint bg = new Paint();
        bg.setColor(Color.BLACK);
        dynamicPlot.getGraphWidget().setBackgroundPaint(bg);
        Paint plotBg = new Paint();
        plotBg.setColor(Color.rgb(0,26,0));
        dynamicPlot.getGraphWidget().setGridBackgroundPaint(plotBg);
        //dynamicPlot.getGraphWidget().getBorderPaint().setColor(Color.rgb(0, 153, 0));
        //dynamicPlot.getGraphWidget().getBorderPaint().setStrokeWidth(1);
        dynamicPlot.getBackgroundPaint().setColor(Color.BLACK);
        dynamicPlot.getBorderPaint().setStrokeWidth(1);
        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.rgb(0, 102, 0));
        dynamicPlot.getGraphWidget().setGridLinePaint(gridPaint);
        // getInstance and position datasets:

        // create a series using a formatter with some transparency applied:
        LineAndPointFormatter f1 = new LineAndPointFormatter(Color.rgb(255, 51, 51), Color.rgb(255, 153, 153), null);  
        f1.getLinePaint().setStyle(Paint.Style.STROKE);
        f1.getLinePaint().setStrokeWidth(2);
        dynamicPlot.addSeries(dataSeries[0], f1);
        
        LineAndPointFormatter f2 = new LineAndPointFormatter(Color.rgb(51, 51, 255), Color.rgb(51, 153, 255), null);  
        f2.getLinePaint().setStyle(Paint.Style.STROKE);
        f2.getLinePaint().setStrokeWidth(2);
        dynamicPlot.addSeries(dataSeries[1], f2);
        
        LineAndPointFormatter f3 = new LineAndPointFormatter(Color.rgb(51, 255, 51), Color.rgb(153, 255, 153), null);  
        f3.getLinePaint().setStyle(Paint.Style.STROKE);
        f3.getLinePaint().setStrokeWidth(2);
        dynamicPlot.addSeries(dataSeries[2], f3);
        //dynamicPlot.setGridPadding(5, 0, 5, 0);
        // hook up the plotUpdater to the data model:

        dynamicPlot.setDomainStepMode(XYStepMode.SUBDIVIDE);
        dynamicPlot.setDomainStepValue(10);
        // thin out domain/range tick labels so they dont overlap each other:
        dynamicPlot.setTicksPerDomainLabel(2);
        dynamicPlot.setTicksPerRangeLabel(2);
        dynamicPlot.disableAllMarkup();
        // freeze the range boundaries:
        dynamicPlot.setRangeBoundaries(-10, 10, BoundaryMode.FIXED);

    }*/
    
    //private connectTask task;
   // private Button send;
    private ToggleButton record;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<String>();

        //send.setEnabled(false);
        //connect = (Button)findViewById(R.id.connect_button);
        record = (ToggleButton)findViewById(R.id.toggleRecord);
        
        //ipAddressInput = (EditText)findViewById(R.id.ip_address);
        //portInput =  (EditText)findViewById(R.id.port);
        //relate the listView from java to the one created in xml
        mList = (ListView)findViewById(R.id.list);
        mAdapter = new MyCustomAdapter(this, arrayList);

    	//onCreatePlot();
                
        mList.setAdapter(mAdapter);
        
        //dataInterpretor = new DeviceMessageInterpretor();
        
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