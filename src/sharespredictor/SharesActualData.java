package sharespredictor;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;


/**
 * Holds actual share data
 * @author M0336304
 */
public class SharesActualData {

    private final Set<FinancialSample>  samples= new TreeSet<FinancialSample>();
    private final int inputSize;
    private final int outputSize;

    public SharesActualData (final int inputSize, final int outputSize){
        this.inputSize = inputSize;
        this.outputSize = outputSize;

    }
    /**
     * This method divides the current input with the previous input and divides it 
     * by the previous input
     */
    public void calculatePercents(){
        double prev = -1;
        for(FinancialSample sample:this.samples){
            if(prev!=-1){
                final double movement = sample.getAmount() - prev;
                final double percent = movement /prev;
                sample.setPercent(percent);
            }
            prev = sample.getAmount();
        }
    }
    
    public void getInputData(final int offset, final double[] input){
        final Object[] samplesArray = this.samples.toArray();
        // get shares data
        for(int i =0; i< this.inputSize; i ++){
            final FinancialSample sample = (FinancialSample) samplesArray[offset + i];
            input[i] = sample.getPercent();        }
    }
    
    public void getOutputData(final int offset , final double[] output){
        final Object[] samplesArray = this.samples.toArray();

        for(int i =0; i<this.outputSize; i++){
            final FinancialSample sample =  (FinancialSample) samplesArray[offset
                    + this.inputSize +i];
            output[i] = sample.getPercent();
        }
    }
    
    public Set<FinancialSample> getSamples(){
        return this.samples;
    }
    
    public void load(final String filename, String sharesName) throws IOException, ParseException{
        loadShares(filename, sharesName);
        calculatePercents();
        
    }
    
    public void loadShares(final String filename, final String sharesName) throws IOException, ParseException{
        final CsvFileReader csv = new CsvFileReader(filename);
        while(csv.next()){
            final Date date = csv.getDate("day");
            
            final double amount = csv.getDouble(sharesName);
          //  final double amount = csv.getDouble("value.daimler.");
       //   final double amount = csv.getDouble("Value.BMW.");
    
       //  final double amount = csv.getDouble("Value.Honda.Motor.");
        //  final double amount = csv.getDouble("Value.Volkswagen.");    
            final FinancialSample sample = new FinancialSample();
            
            sample.setAmout(amount);
            sample.setDate(date);
            this.samples.add(sample);
        }
        csv.close();
    }
    
    public int size(){
        return this.samples.size();
    }
}

