
package sharespredictor;

import java.text.NumberFormat;
import java.util.Date;

/**
 *
 * @author M0336304
 */
public class SharePredictor {

    /**
     * @param args the command line arguments
     */
   
    
    private SharesActualData actual;
    private double input[][];
    private double ideal[][];
    private final static int TRAINING_SIZE = 1565;
    public final static int INPUT_SIZE =5;
    public final static int OUTPUT_SIZE =1;
    public final static int HIDDEN_SIZE =2;
    public final static double LEARNING_RATE = 0.7 ;
    public final static double MOMENTUM = 0.9;
    		
    public final static Date PREDICT_FROM = CsvFileReader.parseDate("01/03/2004");
    public final static Date LEARN_FROM = CsvFileReader.parseDate("01/03/2000");
    private double error;
    private Network network; 
    public SharePredictor(){
        try {
           
            this.actual = new SharesActualData(INPUT_SIZE, OUTPUT_SIZE);
        this.actual.load("daimler.csv", "value.daimler.");
         //  this.actual.load("honda.csv", "Value.Honda.Motor.");
        //  this.actual.load("bmw.csv","Value.BMW." );
          //  this.actual.load("volkswagen.csv", "Value.Volkswagen.");
            System.out.println("Samples read: " + this.actual.size());
            

        }
        catch(final Exception e){
        e.printStackTrace();
        }
        
        generateTrainingData();
    }
    
    public void generateTrainingData(){
        this.input = new double[TRAINING_SIZE][INPUT_SIZE];
        this.ideal = new double[TRAINING_SIZE][OUTPUT_SIZE];
        
        int startIndex =0;
        for(final FinancialSample sample : this.actual.getSamples()){
            if(sample.getDate().after(PREDICT_FROM) ){
                break;
            }
            startIndex++;
        }
        
        final int eligibleSamples = TRAINING_SIZE - startIndex;
        if(eligibleSamples ==0){
            System.out.println
                    (" Need an earlier date for Learn _From or a smaller number for training size");
                    
            System.exit(0);
        }
        final int factor = eligibleSamples/TRAINING_SIZE;
        
        for(int i =0; i<TRAINING_SIZE; i++){
            this.actual.getInputData(startIndex + ( i * factor), this.input[i]);
            this.actual.getOutputData(startIndex + (i* factor), this.ideal[i]);
        }
        
        System.out.println("Learn: ");
        
        
        
        network = new Network (INPUT_SIZE, HIDDEN_SIZE, OUTPUT_SIZE, LEARNING_RATE, MOMENTUM);
        
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
		final StringBuilder str = new StringBuilder();
		percentFormat.setMinimumFractionDigits(4);
		
		//String error = percentFormat.format(network
		//     .getError(input.length));
		//double error1 =  Double.parseDouble(error);
        
        for(int i =0; i< input.length; i++){
            
            for(int j =0; j< input[0].length; j ++){
            	//while(error > 0.0001) {
                network.computeOutputs(input[i]);
                network.calcError(ideal[i]);
                network.learn();
            	//}
                
            }
            
            System.out.println("Trial " + i +  ", Error: " + percentFormat.format(network
                                .getError(input.length)));
            
        }
        /*
        
        for(int i =0; i< input.length; i++){
            
     
			 	//input[0].length =30 /input.length =1000 
		//	 	System.out.println(input.length);
			 for (int j=0;j<input[0].length;j++) {
			 System.out.print( " The input is: " + input[i][j] +" :"  );
				 
			 }
			 
			 double out[] = network.computeOutputs(input[i]);
			 
			 
			 System.out.println("The output ="+ out[0] );
		 }
		 
        
       
        
        */
        

        
       display(network);
        
    }

   
    
    
    private void display(Network network) {
        final NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(2);
        final double[] present = new double[INPUT_SIZE];
        double[] predict = new double[OUTPUT_SIZE];
        final double[] actualOutput = new double[OUTPUT_SIZE];

        int index = 0;
            for (final FinancialSample sample : this.actual.getSamples()) {
                 if (sample.getDate().after(PREDICT_FROM)) {
                final StringBuilder str = new StringBuilder();
                str.append(CsvFileReader.displayDate(sample.getDate()));
                str.append(":Share Price=");
                str.append(sample.getAmount());
                this.actual.getInputData(index - INPUT_SIZE, present);
                this.actual.getOutputData(index - INPUT_SIZE, actualOutput);
                
                predict = network.computeOutputs(present); 
               
                str.append(" input " +  present[0] + " "); 
                str.append(" Actual Output : ");
                str.append(percentFormat.format(actualOutput[0]));
                str.append(" Predicted Output : "); 
                str.append(percentFormat.format(predict[0]));
                System.out.println(str.toString());
                }
                 index++;
            }
        }
    
            public static void main(String[] args) {
        SharePredictor a = new SharePredictor();
      
    } 
}

