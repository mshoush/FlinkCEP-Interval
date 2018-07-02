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

public class TemperatureEvent  {
    
        private double temperature;
        private int rackID;        
    
    

    public TemperatureEvent(int rackID, double temperature) {
        this.temperature = temperature;
        this.rackID = rackID;
    }
     
    public int getRackID() {
        return rackID;
    }

    public void setRackID(int rackID) {
        this.rackID = rackID;
    }


    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TemperatureEvent) {
            TemperatureEvent other = (TemperatureEvent) obj;

            return other.canEquals(this) && super.equals(other) && temperature == other.temperature;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 41 * super.hashCode() + Double.hashCode(temperature);
    }

    public boolean canEquals(Object obj){
        return obj instanceof TemperatureEvent;
    }

    @Override
    public String toString() {
        return "TemperatureEvent(" + getRackID() + ", " + temperature + ")";
    }
}
