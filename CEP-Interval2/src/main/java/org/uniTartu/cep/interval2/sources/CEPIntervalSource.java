/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uniTartu.cep.interval2.sources;

/**
 *
 * @author MKamel
 */
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.uniTartu.cep.interval2.events.TemperatureEvent;
import java.util.Random;



public  class CEPIntervalSource extends RichParallelSourceFunction<TemperatureEvent> {

    private boolean running = true;

    private final int maxRackId;

    private final long pause;

    private final double temperatureStd;

    private final double temperatureMean;

    private Random random;

    private int shard;

    private int offset;

    public CEPIntervalSource(
            int maxRackId,
            long pause,
            double temperatureStd,
            double temperatureMean) {
        this.maxRackId = maxRackId;
        this.pause = pause;
        this.temperatureMean = temperatureMean;
        this.temperatureStd = temperatureStd;
    }

    @Override
    public void open(Configuration configuration) {
        int numberTasks = getRuntimeContext().getNumberOfParallelSubtasks();
        int index = getRuntimeContext().getIndexOfThisSubtask();
      
        offset = (int)((double)maxRackId / numberTasks * index);
        shard = (int)((double)maxRackId / numberTasks * (index + 1)) - offset;

        random = new Random();
    }

    @Override
    public void run(SourceContext<TemperatureEvent> sourceContext) throws Exception {
        while (running) {
            TemperatureEvent temperaturevent;

                int rackId          = random.nextInt(shard) + offset;
                double temperature  = random.nextGaussian() * temperatureStd + temperatureMean;
                temperaturevent     = new TemperatureEvent(rackId,temperature);
          
            sourceContext.collect(temperaturevent);

            Thread.sleep(pause);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
