package com.example.autocar;

public class Car {
    private String year;
    private String make;
    private String model;
    private CarSpecification specs;

    public String getYear(){
        return year;
    }
    public String getMake(){
        return make;
    }

    public String getModel(){
        return model;
    }
    public CarSpecification getSpecs(){
        return specs;
    }
}
