package com.driver.repository;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AirportRepository {
    TreeMap<String,Airport> airportMap;
    HashMap<Integer,Flight> flightMap;
    HashMap<Integer,Passenger> passengerMap;
    HashMap<Integer, HashSet<Integer>> flightPassengerLinkMap;

    public AirportRepository() {
        airportMap=new TreeMap<>();
        flightMap=new HashMap<>();
        passengerMap=new HashMap<>();
        flightPassengerLinkMap=new HashMap<>();
    }

    public void addAirport(Airport airport){
        airportMap.put(airport.getAirportName(),airport);
    }
    public String getLargestAirportName(){
        if(airportMap.isEmpty())
            return null;
        int terminals=0;
        String ans="";
        for(String name:airportMap.keySet()){
            if(airportMap.get(name).getNoOfTerminals()>terminals){
                ans=name;
                terminals=airportMap.get(name).getNoOfTerminals();
            }
        }
        return ans;
    }
    public double getShortestDurationOfPossibleBetweenTwoCities(City from, City to){
        double dr=Double.MAX_VALUE;
        for(Flight f:flightMap.values()){
            if(f.getFromCity().equals(from) && f.getToCity().equals(to)){
                dr=Math.min(dr,f.getDuration());
            }
        }
        return (dr==Double.MAX_VALUE)?-1:dr;
    }
    public int getNumberOfPeopleOn(Date date, String airportName){
        int count=0;
        City city=airportMap.get(airportName).getCity();
        for(Flight f:flightMap.values()){
            if(f.getFromCity().equals(city) || f.getToCity().equals(city)){
                if(f.getFlightDate()==date)
                    count+=f.getMaxCapacity();
            }
        }
        return count;
    }
    public int calculateFlightFare(Integer flightId){
        int ct=flightPassengerLinkMap.get(flightId).size();
        return 3000+ct*50;
    }
    public String bookATicket(Integer flightId,Integer passengerId){
        if(flightPassengerLinkMap.containsKey(flightId)){
            int ct=flightPassengerLinkMap.get(flightId).size();
            if(flightMap.get(flightId).getMaxCapacity()==ct)
                return "FAILURE";
            else if(flightPassengerLinkMap.get(flightId).contains(passengerId))
                return "FAILURE";
            else{
                HashSet<Integer> hs=flightPassengerLinkMap.get(flightId);
                hs.add(passengerId);
                flightPassengerLinkMap.put(flightId,hs);
            }
        }
        else{
            HashSet<Integer> hs=new HashSet<>();
            hs.add(passengerId);
            flightPassengerLinkMap.put(flightId,hs);
        }
        return "SUCCESS";
    }
    public String cancelATicket(Integer flightId,Integer passengerId){
        if(!flightPassengerLinkMap.containsKey(flightId))
            return "FAILURE";
        if(!flightPassengerLinkMap.get(flightId).contains(passengerId))
            return "FAILURE";
        flightPassengerLinkMap.get(flightId).remove(passengerId);
        return "SUCCESS";
    }
    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId){
        int ct=0;
        for (HashSet<Integer> hs:flightPassengerLinkMap.values()){
            if(hs.contains(passengerId))
                ct++;
        }
        return ct;
    }
    public String addFlight(Flight flight){
        flightMap.put(flight.getFlightId(),flight);
        return "SUCCESS";
    }
    public String getAirportNameFromFlightId(Integer flightId){
        if(flightMap.containsKey(flightId)){
            for(Airport airport:airportMap.values()){
                if(airport.getCity()==flightMap.get(flightId).getFromCity())
                    return airport.getAirportName();
            }
        }
        return null;
    }
    public int calculateRevenueOfAFlight(Integer flightId){
        int ct=flightPassengerLinkMap.get(flightId).size();
        int sum=0;
        for(int i=1;i<=ct;i++){
            sum=sum+3000+50*i;
        }
        return sum;
    }
    public String addPassenger(Passenger passenger){
        passengerMap.put(passenger.getPassengerId(),passenger);
        return "SUCCESS";
    }
}
