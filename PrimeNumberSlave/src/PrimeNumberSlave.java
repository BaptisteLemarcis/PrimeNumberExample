import ca.uqac.liara.*;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.tmf.QueueSink;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by baptiste on 2/9/2017.
 */
public class PrimeNumberSlave {
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        PrimeNumberFinderProcessor prime = new PrimeNumberFinderProcessor();
        Client c = new Client(new URI("ws://localhost:5050"));
        c.connectBlocking();
        try{
            WSWriterProcessor writer = new WSWriterProcessor(c);
            WSReaderProcessor reader = new WSReaderProcessor(c);
            Connector.connect(reader, prime);
            Connector.connect(prime, writer);
            for(;;){
                writer.send();
                Thread.sleep(1);
            }
        } catch (Exception e) {

        }

    }
}
