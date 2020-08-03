
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Thomas Otero H3R3T1C
 */
public class UpdateInfo {
    
    private final static String root = "update/";

    private final static String versionURL = "http://update.rossmann-software.de/version.html";

    public static String getLatestVersion() throws Exception {
        String data = getData(versionURL);
        return data.substring(data.indexOf("[version]") + 9, data.indexOf("[/version]"));
    }

    public static String getWhatsNew() throws Exception {
        String data = getData(versionURL);
        return data.substring(data.indexOf("[history]") + 9, data.indexOf("[/history]"));
    }

    private static String getData(String address) throws Exception {
        URL url = new URL(address);

        InputStream html = null;

        html = url.openStream();

        int c = 0;
        StringBuffer buffer = new StringBuffer("");

        while (c != -1) {
            c = html.read();

            buffer.append((char) c);
        }
        return buffer.toString();
    }

    public static void downloadFile() {
        try {
            String link = "http://update.rossmann-software.de/JABUpdate.jar";
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            long max = conn.getContentLength();
            // outText.setText(outText.getText()+"\n"+"Downloding file...\nUpdate Size(compressed): "+max+" Bytes");
            BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(new File("JABUpdate.jar")));
            byte[] buffer = new byte[32 * 1024];
            int bytesRead = 0;
            int in = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
                in += bytesRead;
                fOut.write(buffer, 0, bytesRead);
            }
            fOut.flush();
            fOut.close();
            is.close();
            //outText.setText(outText.getText()+"\nDownload Complete!");
           
        } catch (IOException ex) {
            Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getDownloadLinkFromHost() throws MalformedURLException, IOException {
        String path = "http://update.rossmann-software.de/version.html";
        URL url = new URL(path);

        InputStream html = null;

        html = url.openStream();

        int c = 0;
        StringBuilder buffer = new StringBuilder("");

        while (c != -1) {
            c = html.read();
            buffer.append((char) c);

        }
        return buffer.substring(buffer.indexOf("[url]") + 5, buffer.indexOf("[/url]"));
    }

    public static void launch()
    {
        String[] run = {"java","-jar","JABUpdate.jar"};
        try {
            Runtime.getRuntime().exec(run);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
    
    
    
    private static void cleanup()
    {
        //outText.setText(outText.getText()+"\nPreforming clean up...");
        File f = new File("update.zip");
        f.delete();
        remove(new File(root));
        new File(root).delete();
    }
    private static void remove(File f)
    {
        File[]files = f.listFiles();
        for(File ff:files)
        {
            if(ff.isDirectory())
            {
                remove(ff);
                ff.delete();
            }
            else
            {
                ff.delete();
            }
        }
    }
    private static void copyFiles(File f,String dir) throws IOException
    {
        File[]files = f.listFiles();
        for(File ff:files)
        {
            if(ff.isDirectory()){
                new File(dir+"/"+ff.getName()).mkdir();
                copyFiles(ff,dir+"/"+ff.getName());
            }
            else
            {
                copy(ff.getAbsolutePath(),dir+"/"+ff.getName());
            }

        }
    }
    public static void copy(String srFile, String dtFile) throws FileNotFoundException, IOException{

          File f1 = new File(srFile);
          File f2 = new File(dtFile);

          InputStream in = new FileInputStream(f1);

          OutputStream out = new FileOutputStream(f2);

          byte[] buf = new byte[1024];
          int len;
          while ((len = in.read(buf)) > 0){
            out.write(buf, 0, len);
          }
          in.close();
          out.close();
      }
    
    private static void unzip() throws IOException
    {
         int BUFFER = 2048;
         BufferedOutputStream dest = null;
         BufferedInputStream is = null;
         ZipEntry entry;
         ZipFile zipfile = new ZipFile("update.zip");
         Enumeration e = zipfile.entries();
         (new File(root)).mkdir();
         while(e.hasMoreElements()) {
            entry = (ZipEntry) e.nextElement();
           // outText.setText(outText.getText()+"\nExtracting: " +entry);
            if(entry.isDirectory())
                (new File(root+entry.getName())).mkdir();
            else{
                (new File(root+entry.getName())).createNewFile();
                is = new BufferedInputStream
                  (zipfile.getInputStream(entry));
                int count;
                byte data[] = new byte[BUFFER];
                FileOutputStream fos = new
                  FileOutputStream(root+entry.getName());
                dest = new
                  BufferedOutputStream(fos, BUFFER);
                while ((count = is.read(data, 0, BUFFER))
                  != -1) {
                   dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
                is.close();
            }
         }

    }
    
}
