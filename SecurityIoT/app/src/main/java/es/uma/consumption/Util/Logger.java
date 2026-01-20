package es.uma.consumption.Util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.FileHandler;


public class Logger {

    int logExContador;//,logTimeContador;
    public static FileHandler logger = null;

    File logFile;

    public Logger(String filename,boolean time) {

        logExContador   = 0;
       // logTimeContador = 0;

        String _filename;


        if (time)
            _filename=filename+"._time.log";
        else
            _filename=filename+"._excep.log";

        logFile = new File(Environment.getExternalStorageDirectory()+"/"+_filename);

        if (!logFile.exists())  {
            try  {
                Log.d("File created ", "File created "+_filename);
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addPhoneInfo(String info){
        addRecordToLog(info,true);

    }
    public void addHeaderToLog(String message) {
        addRecordToLog("********************************************************************",true);
        addRecordToLog(logExContador+" "+message,false);
        addRecordToLog("********************************************************************",true);
        logExContador++;
    }

    public void addRecordToLog(String message,boolean ret) {

          try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));

                buf.write(message + "\n");
                if (ret) buf.newLine();
                buf.flush();
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    private String getCustomStackTrace(Throwable aThrowable) {
        //add the class name and any message passed to constructor
        final StringBuilder result = new StringBuilder( "CRYPTO_CONSUMPTION: " );
        result.append(aThrowable.toString());
        final String NEW_LINE = System.getProperty("line.separator");
        result.append(NEW_LINE);

        //add each element of the stack trace
        for (StackTraceElement element : aThrowable.getStackTrace() ){
            result.append( element );
            result.append( NEW_LINE );
        }
        return result.toString();
    }

    public void addExceptionToLog(Throwable aThrowable){
        addRecordToLog(getCustomStackTrace(aThrowable),true);

    }

    public void addTime(String time){
        addRecordToLog(time,false);
        //logTimeContador++;
    }

    public void doFinal(){
       long space = logFile.length();

       if (space==0) logFile.delete();
    }

}
