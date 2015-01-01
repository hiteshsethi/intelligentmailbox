/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import org.apache.commons.io.FileUtils;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author hitesh
 */
public class mailClassifier {
    
    static int first=0;
    static LibSVM svm;
    static FilteredClassifier fc;
    static int numCalls=0;
    static int TrainDone=0;
    public static void trainData(String messagecontent,String fname)
    {numCalls++;
       
        File file = new File("train.arff");
        int clas=0;
         if (file.exists())
        {
            first=1;
           System.out.println("lol");
        }
        
   //     file.close();
        try {
                 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("train.arff",true)));
            
            if(first==0)
          {
                 
                BufferedReader reader = new BufferedReader(new FileReader("classes.txt"));
                String line = null;
                String classL="";
                while ((line = reader.readLine()) != null) {
                    line=line.toUpperCase();
                    System.out.println(line);
                    classL=classL+line+",";
                    clas++;
                }
                reader.close();
               // classL = classL.substring(0, classL.length()-1);
                classL=classL+"INBOX";
                clas++;
                out.println("@relation dataemail");
                out.println("@attribute text string");
                out.println("@attribute @@class@@ {"+classL+"}");
                out.println("@data");
                first=1;
            }
            BufferedReader reader1 = new BufferedReader(new FileReader("classes.txt"));
                String line = null;
                String classL="";
                while ((line = reader1.readLine()) != null) {
                    line=line.toUpperCase();
                    System.out.println(line);
                    classL+=line+",";
                }
                reader1.close();
               // classL = classL.substring(0, classL.length()-1);
                classL=classL+"INBOX";
            List<String> lines = FileUtils.readLines(file);
            lines.set(2, "@attribute @@class@@ {"+classL+"}");
            FileUtils.writeLines(file, lines);
            out.println("'"+messagecontent+"',"+fname);
            //writer.close();
            out.close();
            
        } catch (IOException ex) {
           // Logger.getLogger(mailClassifier.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("mailclassifier error :"+ex);
        }
        //if(numCalls>=clas*10)
        {
            trainClassifier();
            //30 for 3 class's ke bad he work krna start kre
        }
    }
    public static void trainClassifier()
    {
        System.out.println("Training Done");
        TrainDone=1;
        try{
        BufferedReader breader = null;
        breader = new BufferedReader(new FileReader("train.arff"));
        Instances train = new Instances ( breader);
        StringToWordVector filter = new StringToWordVector();
    filter.setInputFormat(train);
    filter.setTFTransform(true);
    filter.setIDFTransform(true);
    File f=filter.getStopwords();
    filter.setStopwords(f);
   // dataRaw.deleteAttributeType(class);
   
        train.setClassIndex(train.numAttributes()-1);
        svm = new LibSVM();
            SelectedTag KERNELTYPE_LINEAR = new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE);
            svm.setKernelType(KERNELTYPE_LINEAR);
            System.out.println(svm.getKernelType());
            fc = new FilteredClassifier();
            fc.setFilter(filter);
            fc.setClassifier(svm);
           
            fc.buildClassifier(train);
        breader.close();
        }
        catch(Exception ex)
        { 
          System.out.println("the problem in svm :"+ex);  
        }
    }
    public static void main(String args[])
    {
     //   trainData("test","test");
        trainClassifier();
        testMessage("hitesh, heres what you missed from Google Students on Google+ A post that you might have missed");
      //  testMessage("Dominos: Buy 1 Pizza & Get 1 Free This Weeks Top New Coupons From CouponDunia Dominos Popular Coupons 1. Buy 1 Pizza & Get 1 Free You received this email because you signed up for coupon alerts from CouponDunia. Not interested anymore? Unsubscribe from Dominos coupon alerts.");
    }
    static int CountTestMessage=0;
    public static String testMessage(String content)
    {
        System.out.println("In test module");
        if(CountTestMessage==0)
        {
            trainClassifier();
        }
        else if(CountTestMessage>30)
        {
            CountTestMessage=0;
            trainClassifier();
        }
        CountTestMessage++;
        String ret=null;
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("test1.arff")));
            
            BufferedReader reader = new BufferedReader(new FileReader("classes.txt"));
                String line = null;
                String classL="";
                while ((line = reader.readLine()) != null) {
                    line=line.toUpperCase();
                    System.out.println(line);
                    classL=classL+line+",";
                }
                reader.close();
               // classL = classL.substring(0, classL.length()-1);
                classL=classL+"INBOX";
             
                out.println("@relation dataemail");
                out.println("@attribute text string");
                out.println("@attribute @@class@@ {"+classL+"}");
                out.println("@data");
                out.println("'"+content+"',"+"INBOX");
                out.close();
                 BufferedReader breader = null;
        breader = new BufferedReader(new FileReader("test1.arff"));
        Instances test = new Instances ( breader);
      test.setClassIndex(test.numAttributes()-1);
      
            try {
                double clsLabel = fc.classifyInstance(test.instance(0));
                System.out.println("clslabel  "+test.classAttribute().value((int) clsLabel));
                 ret=test.classAttribute().value((int) clsLabel);
            } catch (Exception ex) {
                System.out.println(ex);
              //  Logger.getLogger(mailClassifier.class.getName()).log(Level.SEVERE, null, ex);
            } 
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(mailClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        ret=ret.trim();
        return ret;
    }
}
