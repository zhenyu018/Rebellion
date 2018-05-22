package com.swen90004;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
* The MyHandler is used to read parameters from Parameter.xml file
*/
public class MyHandler extends DefaultHandler {

    public int getNumberOfPatches() {
        return numberOfPatches;
    }

    public double getGovernmentLegitimacy() {
        return governmentLegitimacy;
    }

    public int getMaxJailTerm() {
        return maxJailTerm;
    }

    public boolean getMovement() {
        return movement;
    }

    public int getVision() {
        return vision;
    }

    public double getK() {
        return k;
    }

    public double getThreshold() {
        return threshold;
    }

    public double getF() {
        return f;
    }

    public boolean isExtension() {
        return extension;
    }

    public double getPercentageOfAgents() {
        return percentageOfAgents;
    }

    public double getPercentageOfCops() {
        return percentageOfCops;
    }

    private double percentageOfAgents;
    private double percentageOfCops;
    private int numberOfPatches;
    private double governmentLegitimacy;
    private int maxJailTerm;
    private boolean movement;
    private int vision;
    private double k;
    private double threshold;
    private double f;
    private boolean extension;


    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        // assign values read from Prameter.xml to the corresponding variables.
        switch (qName){
            case "numberofPatches":
                numberOfPatches = Integer.parseInt(attributes.getValue("value"));
                break;
            case "governmentLegitimacy":
                governmentLegitimacy = Double.parseDouble(attributes.getValue("value"));
                break;
            case "maxJailTerm":
                maxJailTerm = Integer.parseInt(attributes.getValue("value"));
                break;
            case "movement":
                movement = Boolean.valueOf(attributes.getValue("value"));
                break;
            case "vision":
                vision = Integer.parseInt(attributes.getValue("value"));
                break;
            case "percentageOfAgents":
                percentageOfAgents = Double.parseDouble(attributes.getValue("value"));
                break;
            case "percentageOfCops":
                percentageOfCops = Double.parseDouble(attributes.getValue("value"));
                break;
            case "k":
                k = Double.parseDouble(attributes.getValue("value"));
                break;
            case "threshold":
                threshold = Double.parseDouble(attributes.getValue("value"));
                break;
            case "f":
                f = Double.parseDouble(attributes.getValue("value"));
                break;
            case "extension":
                extension = Boolean.valueOf(attributes.getValue("value"));
                break;
            default:
                break;
        }
    }
    @Override
    public void endDocument(){

    }



}
