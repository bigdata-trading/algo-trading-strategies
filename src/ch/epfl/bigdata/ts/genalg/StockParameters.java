package ch.epfl.bigdata.ts.genalg;


public class StockParameters {
    /* states of calculating the stock parameters */
    public static final int FIRST = 0;
    public static final int FIRST_14 = 1;
    public static final int SECOND_14 = 2;
    public static final int ADX_CALCULATION = 3;

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

    /* getters */
    public double getADX(){
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
        TR = max3(current_high-current_low,Math.abs(current_high-previous_close),Math.abs(current_low-previous_close));
        if (current_high-previous_high>previous_low-current_low)
            pDM = Math.max(current_high-previous_high,0);
        else pDM = 0;
        if (previous_low-current_low>current_high-previous_high)
            mDM = Math.max(previous_low-current_low,0);
        else mDM = 0;
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
                }
            }
            if (count14 == 14){
                ADX = ADX/14;
                state = ADX_CALCULATION;
            }
        } else if (state == ADX_CALCULATION){
            ++count;
            if (count == Constants.STOCK_INTERVAL_LENGTH){
                count = 0;
                TR_pDM_mDM();
                TR14_pDM14_mDM14();
                pDI14_mDI14_DIFF_SUM_DX();
                ADX_();
            }
        }
    }

    @Override
    public String toString(){
        return "ADX: "+ADX;
    }


}
