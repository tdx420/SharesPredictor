
package sharespredictor;

import java.text.NumberFormat;
import java.util.Date;


/**
 * Holds a sample of financial share data at the specified date. 
 * @author M0336304
 */
public class FinancialSample implements Comparable <FinancialSample>{

    private double amount;
    private Date date;
    private double percent;
    
    @Override
    public int compareTo(final FinancialSample other) {
        return getDate().compareTo(other.getDate());
        
    }
    /**
     * 
     * @return the date 
     */
    public Date getDate() {
        return this.date;
    }
    
    /**
     * @return the amount
     */
    public double getAmount(){
        return this.amount;
    }
    
    /**
     * 
     * @return the percent 
     */
    public double getPercent(){
        return this.percent;
    }
    
    
    public void setAmout(final double amount){
        this.amount = amount;
    }
    
    public void setDate(final Date date){
        this.date = date;
    }
    
    public void setPercent(final double percent){
        this.percent = percent;
    }
    
    public String toString(){
        final NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        final StringBuilder result = new StringBuilder();
        result.append(CsvFileReader.displayDate(this.date));
        result.append(", Amount: ");
        result.append(this.amount);
        
        return result.toString();
    }
    
}
