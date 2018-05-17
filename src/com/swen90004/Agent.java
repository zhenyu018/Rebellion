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

    public Agent(int pid, Patch patch, boolean isActive) {
        super(pid, patch);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLParser handler = new XMLParser();
            parser.parse("Parameter.xml", handler);
            governmentLegitimacy = handler.getGovernmentLegitimacy();
            maxJailTerm = handler.getMaxJailTerm();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        this.isActive = isActive;
        this.riskAversion = ThreadLocalRandom.current().nextDouble();
        this.perceivedHardship = ThreadLocalRandom.current().nextDouble();
    }

    public double calculateGrievance() {
        return this.perceivedHardship * (1 - governmentLegitimacy);
    }

    public double calculateArrestProbability() {
        int[] counts = getPosition().countNeighbours();
        return 1 - Math.exp(-Configuration.ARREST_FACTOR *
                (counts[0] / (1 + counts[1])));
    }

    public boolean judgeActive() {
        if (calculateGrievance() - (this.riskAversion * calculateArrestProbability())
                > Configuration.REBEL_THRESHOLD) {
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
        if (term > maxJailTerm){
            remainJailTerm = ThreadLocalRandom.current().nextInt(0,maxJailTerm+1);
        }else {
            remainJailTerm = term;
            if (term < 0){
                remainJailTerm--;
            }
        }
    }

    public void  beArrested(){
        manageJailTerm(ThreadLocalRandom.current().nextInt(0,maxJailTerm+1));
    }

}
