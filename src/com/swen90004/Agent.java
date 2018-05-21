package com.swen90004;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
* Agent is a person who can rebel
*/
public class Agent extends Person {

    private boolean isActive;
    private int remainJailTerm;
    private double riskAversion;
    private double perceivedHardship;
    private double governmentLegitimacy;
    private int maxJailTerm;
    private double k;
    private double threshold;
    private boolean extension;
    private double f;

    public Agent(Patch patch, boolean isActive) {

        super(patch);
        // initialize with parameters in XML file
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            MyHandler handler = new MyHandler();
            parser.parse("src/com/swen90004/Parameter.xml", handler);
            governmentLegitimacy = handler.getGovernmentLegitimacy();
            maxJailTerm = handler.getMaxJailTerm();
            k = handler.getK();
            threshold = handler.getThreshold();
            extension = handler.isExtension();
            f = handler.getF();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        this.isActive = isActive;
        // generate random double for later calculation
        this.riskAversion = ThreadLocalRandom.current().nextDouble(0,1);
        this.perceivedHardship = ThreadLocalRandom.current().nextDouble(0,1);
    }

    public double calculateGrievance() {

        double grievance = this.perceivedHardship * (1 - this.governmentLegitimacy);
        return grievance;
    }

    // the extension mode will increase grievance if it is lower than average one
    public double extensionGrievance(){

        double grievance = this.perceivedHardship * (1 - this.governmentLegitimacy);
        double averageGrievance = getAverageGrievance();
        if (grievance < averageGrievance && !isActive){
            grievance = k * (averageGrievance - grievance);
        }
        return grievance;
    }

    public double calculateArrestProbability() {
        int[] counts = getPosition().countNeighbours();
        return 1 - Math.exp(-k * Math.floor((double)counts[0] / (double) (1 + counts[1])));
    }

    public void judgeActive() {
        // if the extension switch is on
        if (extension){
            // in extension mode the grievance might be influenced by other agents
            if (extensionGrievance() - (this.riskAversion * calculateArrestProbability())
                    > threshold) {
                this.isActive = true;
            } else this.isActive = false;
            // without extension
        }else if (calculateGrievance() - (this.riskAversion * calculateArrestProbability())
                > threshold) {
            this.isActive = true;
        } else this.isActive = false;
    }

    // calculate average grievance of all the agents inside vision
    private double getAverageGrievance() {

        double sum = 0;
        ArrayList<Patch> neighbours = this.getPosition().getVisionPatch();
        for (Patch patch : neighbours) {
            for (Person person : patch.getPeople()){
                if (person instanceof Agent){
                    sum += ((Agent) person).calculateGrievance();
                }
            }
        }
        return sum / neighbours.size();
    }

    public boolean isActive() {
        return isActive;
    }


    public int getRemainJailTerm(){
        return remainJailTerm;
    }

    public void reduceJailTerm(){
        this.remainJailTerm--;
    }

    public void  beArrested(){
        this.isActive = false;
        this.remainJailTerm = ThreadLocalRandom.current().nextInt(1, maxJailTerm+1);

    }

}
