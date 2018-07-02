/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uniTartu.cep.interval2;

/**
 *
 * @author MKamel
 */



import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.IngestionTimeExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import org.uniTartu.cep.interval2.events.TemperatureEvent;
import org.uniTartu.cep.interval2.events.TemperatureWarning;
import org.uniTartu.cep.interval2.sources.CEPIntervalSource;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction.Context;
import org.apache.flink.streaming.api.functions.ProcessFunction.OnTimerContext;

import java.util.List;
import java.util.Map;


public class CEPInterval {
    
    private static final double TEMPERATURE_THRESHOLD = 100;
    private static final int MAX_RACK_ID = 10;
    private static final long PAUSE = 100;
    private static final double TEMP_STD = 20;
    private static final double TEMP_MEAN = 80;
    
    public static double temp; 
  


    public static void main(String[] args) throws Exception {
        
        
        //Step no 1, Create an Execution Environment 
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Use ingestion time => TimeCharacteristic == EventTime + IngestionTimeExtractor
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        //Define Data Source (Random Data) as an inputeventStream
        
        DataStream<TemperatureEvent> inputEventStream = env
                .addSource(new CEPIntervalSource(
                        MAX_RACK_ID,
                        PAUSE,
                        TEMP_STD,
                        TEMP_MEAN))
                .assignTimestampsAndWatermarks(new IngestionTimeExtractor<>());

        // Warning pattern: Two consecutive temperature events whose temperature is higher than the given threshold
        // appearing within a time interval of 10 seconds
        Pattern<TemperatureEvent, ?> warningPattern = Pattern.<TemperatureEvent>begin("first")
                .subtype(TemperatureEvent.class)
                .where(new IterativeCondition<TemperatureEvent>() {
                    private static final long serialVersionUID = -6301755149429716724L;

                    @Override
                    public boolean filter(TemperatureEvent value, IterativeCondition.Context<TemperatureEvent> ctx) throws Exception {
                        temp = value.getTemperature();
                         return temp >= TEMPERATURE_THRESHOLD;
                    }
                })
                .next("second")
                .subtype(TemperatureEvent.class)
                .where(new IterativeCondition<TemperatureEvent>() {
                    private static final long serialVersionUID = 2392863109523984059L;

                    @Override
                    public boolean filter(TemperatureEvent value, IterativeCondition.Context<TemperatureEvent> ctx) throws Exception {
                    return value.getTemperature() >= (1.1 * temp);
                    }
                })
                .within(Time.seconds(10));

       // Creat Interval for two consecutive reading of temp
        //PatternStream<MonitoringEvent> tempIntervalStream = CEP.pattern(inputEventStream.keyBy("rackID"), warningPattern);
       
       

       // Create a pattern stream from our warning pattern
       // the output stream from flink CEP is the required interval Stream which is the match from 
       //two consecutive reading of temperature
       //t1 > threshold
       //t2 > t1 * 1.1
        PatternStream<TemperatureEvent> intervalstream = CEP.pattern(
                inputEventStream,
                warningPattern);
                
        DataStream<TemperatureWarning> warnings = intervalstream.select(
            (Map<String, List<TemperatureEvent>> pattern) -> {
                TemperatureEvent first = (TemperatureEvent) pattern.get("first").get(0);
                TemperatureEvent second = (TemperatureEvent) pattern.get("second").get(0);

                return new TemperatureWarning(
                        first.getRackID(), 
                        (first.getTemperature() + second.getTemperature()) / 2,
                        first.getTemperature(),
                        second.getTemperature());
            }
        );     

      
       //Where to put the output >> Step no 4 >> in Stdout flink print()
        warnings.print();

      

        
        //Step No 5 
        //Trigger the programme execution by calling execute(), mode of execution (local or cluster). 
        env.execute("CEP Interval job");
    }
}
