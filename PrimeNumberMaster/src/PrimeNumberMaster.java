import ca.uqac.liara.PrimeNumberFinderProcessor;
import ca.uqac.liara.Server;
import ca.uqac.liara.WSReaderProcessor;
import ca.uqac.liara.WSWriterProcessor;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Source;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * Created by baptiste on 2/6/2017.
 */
public class PrimeNumberMaster {
    public static void main(String[] args) throws Exception {
        String nb = FileUtils.readFileToString(new File("number.txt"), "UTF-8");
        System.out.println("Number loaded");
        BigInteger primeNb1 = new BigInteger(nb);
        BigInteger primeNb2 = primeNb1;//new BigInteger("9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999499999");
        QueueSource q1 = new QueueSource();
        q1.addEvent(primeNb1);
        QueueSource q2 = new QueueSource();
        q2.addEvent(primeNb2);

        FileWriter fw = new FileWriter("results_2.txt");

        FunctionProcessor ones = new FunctionProcessor(new Constant(1));
        FunctionProcessor twos = new FunctionProcessor(new Constant(2));
        FunctionProcessor add = new FunctionProcessor(Addition.instance);
        CumulativeProcessor sum = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
        PrimeNumberFinderProcessor prime = new PrimeNumberFinderProcessor();
        PrimeNumberFinderProcessor prime2 = new PrimeNumberFinderProcessor();
        Server s = new Server(5050);
        WSWriterProcessor writer = new WSWriterProcessor(s);
        WSReaderProcessor reader = new WSReaderProcessor(s);
        Fork fork = new Fork(2);

        s.start();

        Connector.connect(ones,sum);
        Connector.connect(sum, fork);
        Connector.connect(fork, add,0,0);
        Connector.connect(twos, add, 0, 1);
        //Connector.connect(add, writer);
        //Connector.connect(add, prime2);
        Connector.connect(q1, writer);

        //Connector.connect(fork, prime, 1, 0);
        Connector.connect(q2, prime);

        //for(; ;) {
            long startTime = System.nanoTime();
            writer.send();
            for(;;){
                if(prime.getPullableOutput(0).hasNext()) break;
                Thread.sleep(1);
            }
            BigInteger nb1 = (BigInteger) prime.getPullableOutput(0).pull();
            System.out.println("Done computing nb1");
            /*for(;;){
                if(prime2.getPullableOutput(0).hasNext()) break;
                Thread.sleep(1);
            }*/
            //float nb2 = Float.parseFloat((String)prime2.getPullableOutput(0).pull());

            BigInteger nb2 = new BigInteger((String)reader.getPullableOutput(0).pull());
            long endTime = System.nanoTime();
            System.out.println("Done computing nb2");
            long duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
            if(!nb1.equals(BigInteger.valueOf(-1)) && !nb2.equals(BigInteger.valueOf(-1))){
                //System.out.println(nb1 + "," + nb2);
                fw.write(/*nb1 + "," + nb2 + " ---- "+*/duration+"\n");
                fw.flush();
            }

            System.out.println(/*nb1 + "," + nb2 + " ---- "+*/duration+"\n");
            Thread.sleep(1);
        //}
    }
}
