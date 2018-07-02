/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uniTartu.cep.interval2.events;

/**
 *
 * @author MKamel
 */
public class TemperatureWarning {

    private int rackID;
    private double averageTemperature;
    private double ftemp;
    private double stemp;

    public TemperatureWarning(int rackID,
            double averageTemperature, double ftemp, double stemp) {
        this.rackID = rackID;
        this.averageTemperature = averageTemperature;
        this.ftemp = ftemp;
        this.stemp = stemp;
      
    }

    public TemperatureWarning() {
        this(-1, -1, -1,-1);
    }

    public int getRackID() {
        return rackID;
    }

    public void setRackID(int rackID) {
        this.rackID = rackID;
    }     


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TemperatureWarning) {
            TemperatureWarning other = (TemperatureWarning) obj;

            return rackID == other.rackID && averageTemperature == other.averageTemperature;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return  Double.hashCode(averageTemperature);
    }


    @Override
    public String toString() {        
         
       return "TemperatureWarning(RackID =" + getRackID() +",  Avg Temp =" + averageTemperature + ",  First Temp =" + ftemp + ",  Second Temp =" + stemp + ")";
       
       
               }
    
}
              
    
