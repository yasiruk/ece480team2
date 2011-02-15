package com.iDocent;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.widget.TextView;

//The task to be performed by the timer
public class ScanTask extends TimerTask{
    WeakReference<TextView> textView;
    //WeakReference<TextView> mapView;
    WifiManager wifi;
    WeightedScanFactory wsFactory;
    
    Handler handler = new Handler();

    public ScanTask(TextView text, TextView map, WifiManager w) {
        textView = new WeakReference<TextView>(text);
        //mapView = new WeakReference<TextView>(map);
        wifi = w;
        wsFactory = new WeightedScanFactory();
    }
    
    public void SetTextView(TextView text)
    {
    	textView = new WeakReference<TextView>(text);
    }

    public void run() 
    {
        handler.post(new Runnable() 
        {
            public void run() 
            {
            	if(textView != null && textView.get() != null)
            	{
	                textView.get().setText("WiFi Scanner");
	                
	                //Makes sure that wifi is enabled and it can scan for access points
	        		if(wifi.isWifiEnabled())
	        		{	                
		        		if(wifi.startScan())
		        		{
		        			//DEMO VARIABLE
		        			int count = 0;
		        			
		        			//Keep track of all available access points in a list
		        			List<WeightedScan> weightedScans = new LinkedList<WeightedScan>();
		        			
			        		// List available networks
			        		List<ScanResult> scans = wifi.getScanResults();
			        		
			        		if(scans != null && !scans.isEmpty())
			        		{
			        			//DEMO VARIABLE
			        			int number = 1;
			        			
			        			//Go through the list of scans, give them a value from 1-20 based on strength, then
			        			//create a weighted scan and save it in the list
				        		for (ScanResult scan : scans) 
				        		{
				        	        int level = WifiManager.calculateSignalLevel(scan.level, 30);
			        				
				        	        //DEMO CODE - for display
				        	        {
				        			//textView.get().append("\n" + number + ": " + scan.SSID + "\n" + scan.BSSID);
				        			//textView.get().append(" Strength " + level);
				        	        }
				        			
//				        	        if(scan.BSSID.contentEquals( "00:1a:70:d7:3b:bf")){
//				        	        WeightedScan wScan = wsFactory.Create(scan.BSSID);
//				        	        wScan.MAC = "00:1a:70:d7:3b:bf";
//				        	        wScan.posX = 23;
//				        	        wScan.posY = 26;
//				        	        wScan.level = level;
//				        	        count+=level;
//				        	        weightedScans.add(wScan);}
//				        	        else if(scan.BSSID.contentEquals("00:24:01:75:c8:b9")){
//					        	        WeightedScan wScan = wsFactory.Create(scan.BSSID);
//					        	        wScan.MAC = "00:24:01:75:c8:b9";
//					        	        wScan.posX = 23;
//					        	        wScan.posY = 0;
//					        	        wScan.level = level;
//					        	        count+=level;
//					        	        weightedScans.add(wScan);}
				        			WeightedScan wScan = wsFactory.Create(scan.BSSID);
				        			if(wScan != null)
				        			{
				        				wScan.SetLevel(level);
				        				
				        				//DEMO CODE
				        				{
				        				wScan.number = number;
				        				number++;
				        				count+=level;
				        				}
				        				
					        			weightedScans.add(wScan);
				        			}
				        		}
				        		if(!weightedScans.isEmpty())//DEMO CODE - for display
				        		{
					        		textView.get().append("\n\n");
					        		for (WeightedScan wscan : weightedScans)
					        		{
					        			textView.get().append(wscan.Name() + " " + wscan.Num() + "/" + count + 
					        					"\n" + wscan.GetPos().get(0) + " " + wscan.GetPos().get(1) + "\n");
					        		}
					        		
				        		}
				        		Map(weightedScans, count);
			        		}
			        		else
			        		{
			        			textView.get().append("\nScanning...");
			        		}
		        		}
	        		}
	        		else
	        		{
	        			textView.get().append("\nActivating Wi-Fi");
	        		}
	            }
            }
        });
    }
    
    /*
	 * Name : Map()
	 * Description : MOSTLY DEMO CODE - draws a map and shows locations of 4 access points
	 * 				 and an 'x' for the calculated location
	 * Parameters : scans - list of all the weighted scans found 
	 * 				count - the sum of all scan levels
	 * Returns : void
	*/
    private void Map(List<WeightedScan> scans, int count)
    {
    	double[] loc = {0,0};
    	if(scans.size() >= 0)
    	{
    		//Draw the "map"
    		char[][] map = new char[12][15];		
    		map[0]="______________\n".toCharArray();
    		for(int i=1;i<=10;i++)
    		{
    			map[i]="|------------|\n".toCharArray();
    		}
			map[11]="______________ ".toCharArray(); 
			
			//DEMO positions
			//int pos[] = {1,1,10,12,1,12,10,1};
			//int num = 0;
			
			//DEMO calculations of position
			for(WeightedScan scan : scans)
			{	
				//if(num < 8){
					loc[0] += scan.GetPos().get(0) * (double)scan.Num()/(double)count;
					loc[1] += scan.GetPos().get(1) * (double)scan.Num()/(double)count;
					//map[pos[num]][pos[num+1]] = Character.forDigit(scan.number, 10);
					//num += 2;
				//}
			}
			
			textView.get().append("\n\n( " + loc[0] + ",\n " + loc[1] + ")\n"); 
			
			if((int)loc[0] != 0 && (int)loc[1] != 0 && (int)loc[0] <= 10 && (int)loc[1] <= 12)
				map[(int)loc[0]][(int)loc[1]] = 'x';
			

//			String text = new String();
//			for(int i=0; i<12; i++)
//			{
//				for(int j=0; j<15; j++)
//					text += map[i][j];
//			}
			if(mapView != null && mapView.get() != null)
			{
				mapView.get().setText(/*text + */"\n\n(" + loc[0] +", "+loc[1]+")");
			}
    	}
    	else
    	{
			if(mapView != null && mapView.get() != null)
			{
				mapView.get().setText("Not enough access points");
			}
    	}
    }
}

