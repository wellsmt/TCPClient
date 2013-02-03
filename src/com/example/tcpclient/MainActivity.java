package com.example.tcpclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
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

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.lp.io.DataInterpreter;
import com.lp.io.DeviceMessageInterpretor;
import com.lp.io.Message;
import com.lp.io.MessageConsumer;
import com.lp.io.SimpleDeviceMessage;
import com.lp.io.SocketConnector;
import com.lp.test_app.SensorDataSeries;

public class MainActivity extends Activity
{
	/**
	 * The device message consumer class is used to receive messages
	 *  from the message interpretor and updates the UI accordingly.
	 * @author marc
	 */
	public class DeviceMessageConsumer implements MessageConsumer, Runnable{
		private SensorDataSeries series;
		private Activity activity;
		XYPlot dataPlot;
		
		DeviceMessageConsumer(SensorDataSeries series, Activity activity, XYPlot dataPlot){
			this.series = series;
			this.activity = activity;
			this.dataPlot = dataPlot;
		}
		
		@Override
		public void onMessage(Message msg) {
			SimpleDeviceMessage deviceMsg = (SimpleDeviceMessage)msg;
			series.addLast(deviceMsg.getTimestamp(), deviceMsg.getValue());
			
        	
            //in the arrayList we add the messaged received from server
            arrayList.add(deviceMsg.getData());
			
			// Now update the UI using this classes run method.
			activity.runOnUiThread(this);
		}
		
		@Override
		public void run(){
			// The run method is used to provide thread safe updates to the UI.

        	double range = dataSeries.getRange();
        	double rangeAdder = 0.05*range;
        	if(rangeAdder < 0.0001){
        		rangeAdder = 0.1;
        	}
        	dataPlot.setRangeBoundaries(dataSeries.getMinimum().doubleValue() - rangeAdder, dataSeries.getMaximum().doubleValue() + rangeAdder, BoundaryMode.FIXED);
        	dataPlot.redraw();
        	
        	//TODO: Need to pass these into the constructor
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
		}
	}
	
    private ListView mList;
    private ArrayList<String> arrayList;
    private MyCustomAdapter mAdapter;
    //private TCPClient mTcpClient;
    private SocketConnector connection;
    // Location of saved files on SD Card
    private static final String FILE_DIR = "/TCPClient/";
    private File sdCard = Environment.getExternalStorageDirectory();
    private File dir = new File (sdCard.getAbsolutePath() + FILE_DIR);
    private String filename="";
    private String extension=".txt";
    private EditText ipAddressInput;
    private EditText portInput;
    
    private DataInterpreter dataInterpretor;
    
    private LogFileWriter fileWriter;
    private DeviceMessageConsumer messageConsumer;
    private SensorDataSeries dataSeries;
    
    private XYPlot dynamicPlot;


    @SuppressWarnings("deprecation")
	protected void onCreatePlot(){
        // get handles to our View defined in layout.xml:
        dynamicPlot = (XYPlot) findViewById(R.id.dynamicPlot);
        dynamicPlot.setTitle("Data Plot");
        dataSeries = new SensorDataSeries("Channel 0", 30);

        // only display whole numbers in domain labels
        dynamicPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
        dynamicPlot.setBackgroundColor(Color.BLACK);
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
        LineAndPointFormatter f1 = new LineAndPointFormatter(Color.rgb(51, 51, 255), Color.rgb(51, 153, 255), null);
        
        f1.getLinePaint().setStyle(Paint.Style.STROKE);
        f1.getLinePaint().setStrokeWidth(2);
        dynamicPlot.addSeries(dataSeries, f1);//new LineAndPointFormatter(Color.rgb(51, 51, 255),null,null, FillDirection.BOTTOM));
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

    }
    
    private Button send;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<String>();

        final EditText editText = (EditText) findViewById(R.id.editText);
        send = (Button)findViewById(R.id.send_button);
        //send.setEnabled(false);
        Button connect = (Button)findViewById(R.id.connect_button);
        
        ipAddressInput = (EditText)findViewById(R.id.ip_address);
        portInput =  (EditText)findViewById(R.id.port);
        //relate the listView from java to the one created in xml
        mList = (ListView)findViewById(R.id.list);
        mAdapter = new MyCustomAdapter(this, arrayList);

    	onCreatePlot();
        
        
        mList.setAdapter(mAdapter);
        
        dataInterpretor = new DeviceMessageInterpretor();
        // Create and register the device message consumer.
        messageConsumer = new DeviceMessageConsumer(dataSeries, this,dynamicPlot);
        dataInterpretor.registerObserver(messageConsumer);
        
        //Create and register the log file writer

        	fileWriter = new LogFileWriter(dir.getAbsolutePath(),extension);
        	dataInterpretor.registerObserver(fileWriter);
        //}catch(IOException err){
       // 	Log.e(LogFileWriter.TAG, "Could not open log file. No data log will be created.", err);
       // }
        
        connect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				new connectTask().execute("");	
				send.setEnabled(true);
			}
		});
        // connect to the server        
        
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					String message = editText.getText().toString();

					// add the text in the arrayList
					arrayList.add("c: " + message);

					// sends the message to the server
					if (connection != null) {
						connection.send(message.getBytes());// sendMessage(message);
					}

					// refresh the list
					mAdapter.notifyDataSetChanged();
					editText.setText("");
				} catch (Exception err) {
					Log.e("TCP CLIENT",
							"An error occured while trying to send data to peer.",
							err);
				}
			}
		});

	}
    
    public class connectTask extends AsyncTask<String,String,TCPClient> {    	
    	
        @Override
        protected TCPClient doInBackground(String... message) {        	
        	// create file pointer only once            
			dir.mkdirs();
			filename = Long.toString(System.currentTimeMillis());
			int port = Integer.valueOf(portInput.getText().toString());
			String host = ipAddressInput.getText().toString();
			connection = new SocketConnector(host, port, dataInterpretor);
			try {
				fileWriter.startNewFile(filename);
			} catch (IOException err) {
				Log.e(LogFileWriter.TAG,
						"Could not open log file. No data log will be created.",
						err);
			}

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
    
    public class LogFileWriter implements MessageConsumer{
    	public static final String TAG = "LOG FILE WRITER";
    	String directory;
    	String extension;
    	File data;
    	BufferedWriter buf;
    	
    	LogFileWriter(String dir, String extension){
    		this.directory = dir;
    		this.extension = extension;
    	}
    	
    	/**
    	 * Creates a new file with the specified file name to log out data.
    	 * @param filename
    	 * @throws IOException
    	 */
    	public void startNewFile(String filename) throws IOException{
    		if(buf !=null){
    			buf.close();
    		}
    		String filePath = dir+"/"+filename+extension;
    		Log.d(TAG, String.format("Attempting to open log file: %s", filePath));
    		data = new File(filePath);
        	if (!data.exists())	{
        		data.createNewFile();
        	}
        	buf = new BufferedWriter(new FileWriter(data, true)); 
    	}
    	
		@Override
		public void onMessage(Message message) {
			// If the buffer is null, do nothing.
			if(buf == null){
				return;
			}
			try{
    		buf.append(message.getData());
    		buf.newLine();
    		buf.flush();
			}catch(IOException err){
				Log.e(TAG, "Unable to append data to file.", err);
			}
		}
    	
    }
    
}
