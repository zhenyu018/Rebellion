package com.swen90004;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Agent extends Person {


    private boolean isActive;
    private int remainJailTerm;
    private double riskAversion;
    private double perceivedHardship;
    private double governmentLegitimacy;
    private int maxJailTerm;
    private double k;
    private double threshold;

    public Agent(Patch patch, boolean isActive) {
        super(patch);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            MyHandler handler = new MyHandler();
            parser.parse("src/com/swen90004/Parameter.xml", handler);
            governmentLegitimacy = handler.getGovernmentLegitimacy();
            maxJailTerm = handler.getMaxJailTerm();
            k = handler.getK();
            threshold = handler.getThreshold();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        this.isActive = isActive;
        while(this.riskAversion == (double)0){
            this.riskAversion = ThreadLocalRandom.current().nextDouble(0,1);
        }
        while (this.perceivedHardship == (double)0){
            this.perceivedHardship = ThreadLocalRandom.current().nextDouble(0,1);
        }

    }

    public double calculateGrievance() {
        return this.perceivedHardship * (1 - governmentLegitimacy);
    }

    public double calculateArrestProbability() {
        int[] counts = getPosition().countNeighbours();
        return 1 - Math.exp(-k * Math.floor((double)counts[0] / (double) (1 + counts[1])));
    }

    public void judgeActive() {
        if (calculateGrievance() - (this.riskAversion * calculateArrestProbability())
                > threshold) {
            this.isActive = true;
        } else this.isActive = false;
    }

    private double getaverageGrievance() {
        double sum = 0;
        ArrayList<Patch> neighbours = this.getPosition().getVisionPatch();
        for (Patch patch : neighbours) {
            if (patch.getQuietAgent() != null){
                sum += patch.getActiveAgent().calculateGrievance();
            }
            if (patch.getActiveAgent() != null) {
                sum += patch.getActiveAgent().calculateGrievance();
            }
        }
        return sum / neighbours.size();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getRemainJailTerm(){
        return remainJailTerm;
    }

    public void manageJailTerm(int term){
        if (term > 0){
            remainJailTerm = ThreadLocalRandom.current().nextInt(1,maxJailTerm+1);
        }else {
            if (term < 0){
                remainJailTerm--;
            }
        }
    }

    public void  beArrested(){
        manageJailTerm(1);
        setActive(false);
    }

}
