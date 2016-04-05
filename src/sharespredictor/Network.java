
package sharespredictor;

/**
 * Feed forward backpropagation Neural Network
 * 
 * @author M0336304
 */
public class Network {
   
    
    protected double globalError; //the networks global error for the training
    protected int inputCount; // the number of input neurons
    protected int outputCount; //the number of output neurons
    protected int hiddenCount; //the number of hidden neurons
    protected int neuronCount; // the total numbers of neurons in the network 
    protected int weightCount; // the number of weights in the network 
    protected double learningRate; //the learning rate
    protected double momentum; //the momentum for the training
    protected double fire[]; // the outputs from the various levels
    protected double matrix[]; // the weight matrix this, along with the threshold
                               //can be thought of as the memory of the neural net
    protected double thresholds[]; //the thresholds and the weight matrix , as mentioned abobe
                                  //can be thought of as memory of the neural net
    protected double error[]; // the errors from the last calculation
    protected double accMatrixDelta[]; //accumulates matrix delta's for training
    protected double accThresholdDelta[]; // the accumulation of the threshold Deltas
    protected double matrixDelta[]; //the changes that should be applied to the weight
    protected double thresholdDelta[] ; // the threshold deltas
    protected double errorDelta[]; //the changes in error
    /**
     * 
     * @param inCount the number of input neurons
     * @param hidCount the number of hidden neurons
     * @param outCount the number of output neurons
     * @param lRate the learning rate to be used when training
     * @param mom the momentum to be used when training
     */
    public Network(int inCount, int hidCount, int outCount, double lRate, double mom){
        this.learningRate = lRate;
        this.momentum = mom;
        this.inputCount = inCount;
        this.outputCount = outCount;
        this.learningRate = lRate;
        this.momentum = mom;
        
        neuronCount = inputCount + hiddenCount + outputCount;
        weightCount = (inputCount * hiddenCount) + (hiddenCount * outputCount);
        
        fire =new double[neuronCount];
        matrix = new double[weightCount];
        thresholds = new double[neuronCount];
        error = new double[neuronCount];
        matrixDelta = new double[weightCount];
        thresholdDelta = new double[neuronCount];   
        errorDelta = new double[neuronCount];
        accThresholdDelta = new double[neuronCount];
        accMatrixDelta = new double[weightCount];
        
        reset();              
        
               
        
    }
    
    /**
     * Reset the weight matrix and threshold
     */

    private void reset() {
      int i;
      
      for(i =0 ; i < neuronCount ; i ++){
          thresholds[i] =0.5 -(Math.random());
          thresholdDelta[i] = 0;
          accThresholdDelta[i] = 0;
      
      }
      for (i =0; i < matrix.length ; i ++){
          matrix[i] = 0.5 -(Math.random());
          matrixDelta[i]= 0;
          accMatrixDelta[i] =0;
      }
        
    }
    /**
     * 
     * @param len the length of  a complete training set
     * @return the current error for the neural network  
     */
    public double getError(int len){
        double err = Math.sqrt(globalError/ (len * outputCount));
        globalError = 0; //clear the accumulator
        return err ; 
        
    }
    /**
     * This method returns the activation value of an input using sigmoid Function
     * @param sum The activation from the neuron
     * @return The activation applied to the threshold method
    
   */
    
    public double threshold(double sum){
       return 1.0 / (1+ Math.exp(-1.0 *sum));
      //  return 1/(1+Math.pow(Math.E,-sum));
      //  return 1.0 / (1 + Math.exp(-1.0 * d));
    //    double a = Math.exp( sum );
   // double b = Math.exp( -sum );
  //  return ((a-b)/(a+b));
    }
    /**
     * The method to compute Outputs
     * 
     * @param input the input to be passed 
     * @return the final predicted output 
     */
    
    public double [] computeOutputs(double input[]){
        int i , j;
        final int hiddenIndex = inputCount; //if 30 input nodes, and 1 output nodes, 30
        final int outIndex = inputCount + hiddenCount; //31 
        
        for(i =0; i < input.length ; i ++){
            fire[i] = input[i];      
        }
        //first layer
        int inx =0;
        
        for(i = hiddenIndex ; i < outIndex; i++){
            double sum = thresholds[i];
            
            for (j =0; j < input.length ; j++){
                sum += fire[j] * matrix [inx++]; // Weighted sum, weight * inputs 
            }
            fire[i] = threshold(sum); //activation applied to the weighted sum 
        }
        //hidden layer      
        double result[] = new double[outputCount];
        for(i =outIndex ; i < neuronCount; i++ ){
            double sum = thresholds[i];
            
            for(j = hiddenIndex ; j< outIndex; j++){
                sum +=fire[j] * matrix[inx++];
            }
            fire[i] = threshold(sum);
            
            result[i-outIndex] = fire[i];
       //     System.out.println("result" + result[i-outIndex]);
            
        }
        return result;
    }
    /**
     * Calculate error 
     * 
     * @param ideal the desired output, i.e, the result that output neuron should have yielded
     */
    public void calcError(double ideal[]){
        int i, j;
        final int hiddenIndex = inputCount; //1
        final int outputIndex = inputCount + hiddenCount; //31
        
        //clear hidden layer errors
        for(i =inputCount; i< neuronCount; i ++){
        error[i]=0;
        }
        
        //layer errors and deltas for output layer
        for(i =outputIndex ; i < neuronCount; i++){
            error[i] = ideal[i-outputIndex] - fire[i]; // output - ideal 
            globalError += error[i] * error[i]; // error square
            errorDelta[i] =error[i] * fire[i] * (1 - fire[i]); // error * output * (1- outut) 
        }
        
        //hidden layer errors
        int winx = inputCount * hiddenCount; 
        
        for(i =outputIndex ; i < neuronCount; i ++){
            for(j =hiddenIndex; j<outputIndex; j++){
                accMatrixDelta[winx] += errorDelta[i] *fire[j]; // each errordelta * each output
                error[j] += matrix[winx] * errorDelta[i];  // each weight * each errorDelta
                winx++;
            }
        accThresholdDelta[i] += errorDelta[i];
        }
        
        //hidden layer deltas
        for(i = hiddenIndex ; i< outputIndex; i++){
            errorDelta[i] = error[i] * fire[i] * (1-fire[i]);   // error * output * (1 -output)
            
        }
        
        //input layer errors
        winx =0; //offser into weight array
        for(i =hiddenIndex; i<outputIndex; i++){
            for(j=0; j<hiddenIndex; j++){
                accMatrixDelta[winx] += errorDelta[i] *fire[j]; // errodeta * each output
                error[j] += matrix[winx] * errorDelta[i];        // weight * errordelta
                winx++;
            }
            accThresholdDelta[i] += errorDelta[i];              
        }
    }
    
    
    
  public void learn() {
  int i;

  // process the matrix
  for (i = 0; i < matrix.length; i++) {
   matrixDelta[i] = (learningRate * accMatrixDelta[i]) + (momentum * matrixDelta[i]);
   matrix[i] += matrixDelta[i];
   accMatrixDelta[i] = 0;
  }

  // process the thresholds
  for (i = inputCount; i < neuronCount; i++) {
   thresholdDelta[i] = learningRate * accThresholdDelta[i] + (momentum * thresholdDelta[i]);
   thresholds[i] += thresholdDelta[i];
   accThresholdDelta[i] = 0;
  }
 }
    
   
    
}
