package com.swen90004;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {

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

    public int getNumberOfAgents() {
        return numberOfAgents;
    }

    public int getNumberOfCops() {
        return numberOfCops;
    }

    private int numberOfAgents;
    private int numberOfCops;
    private int numberOfPatches;
    private double governmentLegitimacy;
    private int maxJailTerm;
    private boolean movement;
    private int vision;


    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        switch (qName){
            case "numberofPatches":
                numberOfPatches = Integer.parseInt(attributes.getValue("value"));
            case "governmentLegitimacy":
                governmentLegitimacy = Double.parseDouble(attributes.getValue("value"));
            case "maxJailTerm":
                maxJailTerm = Integer.parseInt(attributes.getValue("value"));
            case "movement":
                movement = Boolean.valueOf(attributes.getValue("value"));
            case "vision":
                vision = Integer.parseInt(attributes.getValue("value"));
            case "numberOfAgents":
                numberOfAgents = Integer.parseInt(attributes.getValue("value"));
            case "numberOfCops":
                numberOfCops = Integer.parseInt(attributes.getValue("value"));
            default:
                break;
        }
    }



}
