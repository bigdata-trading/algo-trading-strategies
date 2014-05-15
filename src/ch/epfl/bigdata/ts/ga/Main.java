package ch.epfl.bigdata.ts.ga;

import ch.epfl.bigdata.ts.pattern.fitness.DoubleBottom;
import ch.epfl.bigdata.ts.pattern.fitness.DoubleTop;
import ch.epfl.bigdata.ts.pattern.fitness.HeadAndShoulders;
import ch.epfl.bigdata.ts.pattern.fitness.Rectangle;

import java.util.LinkedList;
import java.util.List;

public class Main {


    public static void main(String [] args){

        //Training
        int numOfDays = 30;
        int startMoney = 3000;
        int generationWindow = 5;
        int startData = 0;

        List<Training> strategies = new LinkedList<Training>();
        //strategies.add(new Training(DoubleBottom.getName(), DoubleBottom.getGeneRanges(), new DoubleBottom(numOfDays, startMoney, generationWindow, startData)));
        strategies.add(new Training(HeadAndShoulders.getGeneRanges(), new HeadAndShoulders(numOfDays, startMoney, generationWindow, startData), startData));
        //strategies.add(new Training(Rectangle.getGeneRanges(), new Rectangle(numOfDays, startMoney, generationWindow, startData), startData));
        //strategies.add(new Training(DoubleBottom.getGeneRanges(), new DoubleBottom(numOfDays, startMoney, generationWindow, startData), startData));
        //strategies.add(new Training(DoubleTop.getGeneRanges(), new DoubleTop(numOfDays, startMoney, generationWindow, startData), startData));

        //     strategies.add(new Training(DoubleBottom.getName(), DoubleBottom.getGeneRanges(), new Rectangle(numOfDays, startMoney, generationWindow, startData)));

        for (int i=0;i<strategies.size(); i++){
            strategies.get(i).start();
        }
        for (int i=0;i<strategies.size(); i++){
            try {
                strategies.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        //Evaluation

        int evalNumOfDays = 12;
        int evalStartMoney = 3000;
        int evalGenerationWindow = 12;
        int evalStartData = 30;


        List<Evaluation> evals = new LinkedList<Evaluation>();
        for (int i=0;i<strategies.size(); i++){
            Evaluation eval = new Evaluation(strategies.get(i).getStrategy().constructorWrapper(evalNumOfDays, evalStartMoney, evalGenerationWindow, evalStartData), strategies.get(i).getBestChromosomes());
            eval.start();
            evals.add(eval);
        }
        for (int i=0;i<evals.size(); i++){
            try {
                evals.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        //visaulization
        for (int i=0;i<evals.size(); i++){
            strategies.get(i).getStrategy().constructorWrapper(evalNumOfDays, evalStartMoney, evalGenerationWindow, evalStartData).calcFitness(evals.get(i).bestChromosome(), true);
        }

    }

}
