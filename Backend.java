package JavaBackEnd;
import java.util.*;
import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.json.JSONException;
import org.json.JSONML;

import java.io.IOException;
import java.nio.Buffer;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.time.LocalTime;
import org.json.simple.parser.JSONParser;
import org.json.JSONArray;
import java.io.FileReader;

@SuppressWarnings({ "serial", "unused" })
class DuplicateKey extends Exception {

}
@SuppressWarnings("serial")
class InvalidKey extends Exception {

}
@SuppressWarnings("serial")
class TimeExceeded extends Exception {

}
@SuppressWarnings("serial")
class KeySizeExceeded extends Exception{
	
}
@SuppressWarnings("serial")
class ValueSizeExceeded extends Exception{
	
}


public class Backend {
	//private static  Instrumentation instrument ;
	   private String FilePath; // Making FilePath immutable
	   
	     public Backend(String path) throws JSONException {
	        FilePath = path;
	        JSONObject fill = new JSONObject();
	        fill.put("","");      //Fill JSONobject with dummy value
	         try (FileWriter file = new FileWriter(FilePath,false))
	        		{
	        	      file.write(fill.toString());  //Write dummy JSONObject to JSONfile
	        	      file.close();
	        		}
	         catch (IOException E)
	         {
	        	System.out.println("Caught IOException"); 
	         }
	        
	        
	    }
	   
	     public Backend() throws JSONException {
	        FilePath = "E://FileEngine.JSON";
	        JSONObject fill = new JSONObject();
	        fill.put("","");  
	        try (FileWriter file = new FileWriter(FilePath,false))
	        		{
	        		  file.write(fill.toString());   //Write dummy JSONObject to JSONfile to avoid file exception
	        	      file.close();
	        		}
	         catch (IOException E)
	         {
	        	System.out.println("Caught IOException"); 
	         }
	        
	    }
	   
	    public void Create(String Key, JSONObject Val, int TimeToLive) throws Exception // Create method, if TimeToLive is provided
	    {    
	         try {if (Key.length()>32) // Check if Key is more than 32 Char
	        	 throw new KeySizeExceeded();
	         else if((Key.getBytes().length+ (String.valueOf(Val).getBytes().length))>16000) // Check if JSONObject is more than 16 KB
	        	 throw new ValueSizeExceeded();
	         }
	         catch (KeySizeExceeded e) {
	          	System.out.println(" Key size exceeds maximum size, Enter Valid Key");
	          } catch (ValueSizeExceeded e) {
	          	System.out.println(" Value size exceeds maximum size ");
	          }
	        
	         try (FileReader reader = new FileReader(FilePath)) {
	            //Read JSON file
	            //if(reader.size())
	        	JSONTokener tokener = new JSONTokener(reader);
	             JSONObject temp = new JSONObject(tokener);
	            if (temp.has(Key)) //Check if JSONObject has the given key value pair
	                throw new DuplicateKey();
	            JSONArray tempArray = new JSONArray();
	            tempArray.put(Val);     // Adding  JSONOBject provided by user as first element
	            temp.put(Key, tempArray);
	            try (FileWriter file = new FileWriter(FilePath,false)) // Clearing the file , by setting append to false
	            {

	                file.write(temp.toString());
	                file.close();

	            } catch (IOException e) {
	                System.out.println("Caught IO Exception");
	            }

	        } catch (DuplicateKey e) {
	            System.out.println("KEY already exists. Duplicate keys not allowed");
	        } catch (FileNotFoundException e) {
	            System.out.println("File Not Found");
	        } catch (IOException e) {
	            System.out.println("Caught IO Exception");
	        } 
	    }
	    
	    public void Create(String Key, JSONObject Val) throws Exception // Create method , if no TimeToLive is specified by user
	    {
	    	try {if (Key.length()>32) // Check if Key is more than 32 Char
	       	 throw new KeySizeExceeded();
	    	 else if((Key.getBytes().length+ (String.valueOf(Val).getBytes().length))>16000) // Check if JSONObject is more than 16 KB
	       	 throw new ValueSizeExceeded();
	        }
	        catch (KeySizeExceeded e) {
	         	System.out.println(" Key size exceeds maximum size, Enter Valid Key");
	         } catch (ValueSizeExceeded e) {
	         	System.out.println(" Value size exceeds maximum size ");
	         }
	        try (FileReader reader = new FileReader(FilePath)) {
	            //Read JSON file
	            
	        	JSONTokener tokener = new JSONTokener(reader);
	            JSONObject temp = new JSONObject(tokener);
	            if (temp.has(Key)) //Check if JSONObject has the given key value pair
	                throw new DuplicateKey();
	            JSONArray tempArray = new JSONArray();
	            tempArray.put(Val); // Adds JSONObject to array
	            temp.put(Key, tempArray);
	            try (FileWriter file = new FileWriter(FilePath,false)) //Writes updated JSON based store back to file
	            {
	                file.write(temp.toString());
	                file.close();
	            } catch (IOException e) {
	                System.out.println("Caught IO Exception");
	            }

	        } catch (DuplicateKey e) {
	            System.out.println("KEY already exists. Duplicate keys not allowed");
	        } catch (FileNotFoundException e) {
	            System.out.println("File Not Found");
	        } catch (IOException e) {
	            System.out.println("Caught IO Exception");
	        }
	    }

	    public JSONObject Read(String Key) throws Exception //Read method , which returns JSONObject
	    {
	        
	        try (FileReader reader = new FileReader(FilePath)) {
	            //Read JSON file
	            
	        	JSONTokener tokener = new JSONTokener(reader);
	            JSONObject temp = new JSONObject(tokener);
	            if (temp.has(Key)) //Check if JSONObject has the given key value pair
	            {
	                JSONArray tempArray = new JSONArray();
	                tempArray = temp.getJSONArray(Key);
                    return tempArray.getJSONObject(0);

	            } else
	                throw new InvalidKey();

	        }  catch (InvalidKey e) {
	            System.out.println("Invalid Key.Enter a valid key to continue");
	        } catch (FileNotFoundException e) {
	            System.out.println("File Not Found");
	        } catch (IOException e) {
	            System.out.println("Caught IO Exception");
	        }
			return null;
	   }
	    public void Delete(String Key) throws Exception // Delete method , for deleting a given < Key,JSONOBject > pair
	    {
	       
	        try (FileReader reader = new FileReader(FilePath)) //Read JSON file
	        {
	        	JSONTokener tokener = new JSONTokener(reader);
	            JSONObject temp = new JSONObject(tokener);
	            if (temp.has(Key)) //Check if JSONObject has the given key value pair
	            {
	                JSONArray tempArray = new JSONArray();
	                tempArray = temp.getJSONArray(Key);
	                LocalTime t = LocalTime.now();
	                int Curr = t.toSecondOfDay();
	                if ((Curr - tempArray.getInt(2)) < tempArray.getInt(1)) //Checking time to live condition , if satisfied removes < Key,Value > pair
	                    temp.remove(Key);
	                else
	                    throw new TimeExceeded();

	                try (FileWriter file = new FileWriter(FilePath,false)) //Writes back edited JSONObject back to the file
	                {

	                    file.write(temp.toString());
	                    file.close();
	                }
	            }
	                else
	                    throw new InvalidKey();

	            } 
	            catch (InvalidKey e) {
	                System.out.println("Invalid Key.Enter a valid key to continue");
	            } catch (IOException e) {
	                System.out.println("Caught IO Exception");
	            }

	         catch (TimeExceeded e) {
	            System.out.println("Key Exceeded Time To Live");
	        }
	    }

}
