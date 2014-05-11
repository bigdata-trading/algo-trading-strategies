package ch.epfl.bigdata.ts.pattern.fitness;


import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.dataparser.Utils;
import ch.epfl.bigdata.ts.genalg.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

public class StockParameters {
    /* states of calculating the stock parameters */
    public static final String pathToTest = "/home/dorwi/Workspace/EPFL/bigdata/project/algo-trading-strategies/data/test_statistics/";
    public static final int FIRST = 0;
    public static final int FIRST_14 = 1;
    public static final int SECOND_14 = 2;
    public static final int ADX_CALCULATION = 3;


                /****************/
                /* constructors */
                /***************/

    public StockParameters(Boolean start){
        if (start){
            resetStockParameters();
        }
    }

    int state = FIRST;
    int count = 0;
    int count14 = 0;

    /* stock parameters */
    double previous_high;
    double previous_low;
    double previous_close;
    double current_high;
    double current_low;
    double TR; //true range
    double mDM=0; //minus directional movement
    double pDM=0; //plus directional movement
    double TR14=0; //sum of 14 intervals of true range
    double mDM14=0;
    double pDM14=0;
    double mDI14;//minus directional index: 100*(mDM14/TR14)
    double pDI14;
    double DIFF; //difference between the directional indexes
    double SUM; // sum of the directional indexes
    double DX; //directional movement indes: 100*(DIFF/SUM)
    double ADX=0; //average directional movement index
    double price;

    /* getters */
    public double getTrendStrength(){
        if (mDM > 0)
            return -ADX;
        return ADX;
    }

    public double getmDI14(){
        return mDI14;
    }

    public double getpDI14(){
        return  pDI14;
    }

    public boolean isADXcalculated(){
        return state == ADX_CALCULATION;
    }

    public double max3(double a,double b, double c){
        double p = a;
        if (b>p) p=b;
        if (c>p) p=c;
        return p;
    }

    public void resetStockParameters(){
        state = FIRST;
        count = 0;
        count14 = 0;
        mDM = 0;
        pDM = 0;
        TR14 = 0;
        mDM14 = 0;
        pDM14 = 0;
        ADX = 0;
    }

    public void low_high(long time,double price){
        if (count == 0){
            current_high = price;
            current_low = price;
        } else {
            if (price > current_high) current_high = price;
            if (price < current_low) current_low = price;
        }
    }

    public void current_to_previous(long time,double price){
        previous_high = current_high;
        previous_low = current_low;
        previous_close = price;
    }

    public void TR_pDM_mDM(){
        double upMove = current_high-previous_high;
        double downMove = -(current_low-previous_low);
        //System.out.println("" + upMove + "\t" + downMove);

        if ((upMove>downMove)&&(upMove>0))
            pDM=upMove;
        else
            pDM=0;

        if ((downMove>upMove)&&(downMove>0))
            mDM=downMove;
        else
            mDM=0;


        TR = max3(current_high-current_low,Math.abs(current_high-previous_close),Math.abs(current_low-previous_close));
        //if (current_high-previous_high>previous_low-current_low)
        //    pDM = Math.max(current_high-previous_high,0);
        //else pDM = 0;
        //if (previous_low-current_low>current_high-previous_high)
        //    mDM = Math.max(previous_low-current_low,0);
        //else mDM = 0;
    }

    public void pDI14_mDI14_DIFF_SUM_DX(){
        pDI14 = 100*(pDM14/TR14);
        mDI14 = 100*(mDM14/TR14);
        DIFF = Math.abs(mDI14-pDI14);
        SUM = mDI14+pDI14;
        DX = 100*(DIFF/SUM);
    }

    public void TR14_pDM14_mDM14(){
        pDM14=pDM14-(pDM14/14) + pDM;
        mDM14=mDM14-(mDM14/14) + mDM;
        TR14=TR14 - (TR14/14) + TR;
    }

    public void ADX_(){
        ADX = (13*ADX+DX)/14;
    }

    public void calculate(long time,double price){
        this.price = price;
        low_high(time, price);
        ++count;
        if (state == FIRST){
            if (count ==  Constants.STOCK_INTERVAL_LENGTH){
                current_to_previous(time, price);
                state = FIRST_14;
                count = 0;
                count14 = 0;
            }
        } else if (state == FIRST_14){
            if (count14 < 14){
                if (count ==  Constants.STOCK_INTERVAL_LENGTH){
                    count = 0;
                    ++count14;
                    TR_pDM_mDM();
                    pDM14+=pDM;
                    mDM14+=mDM;
                    TR14+=TR;
                    current_to_previous(time, price);
                }
            }
            if (count14 == 14){ // on the 14th period, we start calculating the TR14....
                state = SECOND_14;
                count14=0;
                count = 0;
                pDI14_mDI14_DIFF_SUM_DX();
                ADX+=DX;
            }
        } else if (state == SECOND_14){
            if (count14 < 14){
                if (count ==  Constants.STOCK_INTERVAL_LENGTH){
                    count = 0;
                    ++count14;
                    TR_pDM_mDM();
                    TR14_pDM14_mDM14();
                    pDI14_mDI14_DIFF_SUM_DX();
                    current_to_previous(time, price);

                }
            }
            if (count14 == 14){
                ADX = ADX/14;
                state = ADX_CALCULATION;
            }
        } else if (state == ADX_CALCULATION){
            //++count;
            if (count == Constants.STOCK_INTERVAL_LENGTH){
                count = 0;
                TR_pDM_mDM();
                TR14_pDM14_mDM14();
                pDI14_mDI14_DIFF_SUM_DX();
                ADX_();
                current_to_previous(time, price);

            }
        }
    }

    @Override
    public String toString(){
        //double TR14=0; //sum of 14 intervals of true range
        //double DIFF; //difference between the directional indexes
        //double SUM; // sum of the directional indexes
        //double DX; //directional movement indes: 100*(DIFF/SUM)
        //double ADX=0;
        //return "" + mDM + "\t" +pDM + "\t" +mDM14 + "\t" +pDM14 + "\t" +mDI14 + "\t" +pDI14;
        //return "ADX: "+ADX;

        DecimalFormat df = new DecimalFormat("0.00");
        String s = previous_high + "\t" + previous_low + "\t" + previous_close;
        //s += "\t" + df.format(TR);
        //s += "\t" + df.format(pDM);
        //s += "\t" + df.format(mDM);
        //s += "\t" + df.format(TR14);
        //s += "\t" + df.format(pDM14);
        //s += "\t" + df.format(mDM14);
        s += "\t" + df.format(pDI14);
        s += "\t" + df.format(mDI14);
        s += "\t" + df.format(ADX);

        return s;
    }

    public static void test() throws FileNotFoundException{
        StockParameters sp = new StockParameters(true);
        File file = new File(pathToTest + "stat.csv");
        Scanner scanner = new Scanner(file);

        /* read in the first two meaningless lines */
        scanner.nextLine();
        scanner.nextLine();

        /* for every line feed the data */
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            //System.out.println(line);
            String data[] = line.split(",");
            Double high = Double.parseDouble(data[2]);
            Double low = Double.parseDouble(data[3]);
            Double current = Double.parseDouble(data[4]);

            /* we need to send 14 values
              as a 14th, we must send the last price
             */

            sp.calculate(1,high);
            sp.calculate(1,low);
            for (int i=2; i<50; i++){
                sp.calculate(1,current);
            }

            System.out.println(sp);
        }

    }

    public static void main(String[] args) {
       /*
        try {
           test();
       } catch (FileNotFoundException e){

       }
       */

            try {
                StockParameters sp = new StockParameters(true);
                List<Tick> ticks = Utils.readCSV(Utils.dataFileNames[1]);

                for (Tick tick : ticks) {
                    sp.calculate(tick.getTimestamp(),tick.getPrice());
                }


            } catch (FileNotFoundException e) {
                System.out.println("File not found stacktrace: ");
               e.printStackTrace();

            }

    }

}
