package com.swen90004;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
* The simulator contains a map of all the patches
* and arrays of cops and agents
* It can also count the number of agents in different status
*/
public class Simulator {

    public static Patch[][] patches;
    private int numberOfPatches;
    private int numberOfCops;
    private int numberOfAgents;
    private int activeAgents;
    private int peopleInJail;
    private int quietAgents;
    public static Agent[] agents;
    public static Cop[] cops;

    public int getActiveAgents() {
        return activeAgents;
    }

    public int getPeopleInJail() {
        return peopleInJail;
    }

    public int getQuietAgents() {
        return quietAgents;
    }

    // initialize and read parameters
    public void init(){
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            MyHandler handler = new MyHandler();
            parser.parse("src/com/swen90004/Parameter.xml", handler);
            numberOfPatches = handler.getNumberOfPatches();
            numberOfAgents = handler.getNumberOfAgents();
            numberOfCops = handler.getNumberOfCops();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        //generate all the patches
        patches = new Patch[numberOfPatches][numberOfPatches];
        for(int i = 0; i < patches.length; i ++){
            for(int j = 0; j < patches[i].length; j ++){
                patches[i][j] = new Patch(i,j);
            }
        }

        //Generate agents and cops.
        agents = new Agent[numberOfAgents];
        for(int k = 0; k < numberOfAgents; k ++){
            agents[k] = generateAgent();
        }

        cops = new Cop[numberOfCops];
        for(int k = 0; k < numberOfCops; k ++){
            cops[k] = generateCop();
        }
    }

    // go one tick
    public void go() throws IOException {

            // M rule
            movePeople(agents);
            movePeople(cops);

            // A rule
            for (Agent agent : agents) {
                if (agent.getRemainJailTerm() == 0) {
                    agent.judgeActive();
                }
            }

            // C rule
            for (Cop cop : cops) {
                cop.arrest();
            }

            //Reduce the jail term of jailed agents.
            for (Agent agent : agents) {
                if (agent.getRemainJailTerm() > 0) agent.reduceJailTerm();
            }
    }

    // generate agent and put it to an available patch
    // and set agent to quiet
    public Agent generateAgent(){

        Patch availablePatch = findAvailablePatch();
        Agent agent= new Agent(availablePatch, false);
        availablePatch.addPerson(agent);
        return agent;
    }

    // generate cop and put it to an available patch
    public Cop generateCop(){

        Patch availablePatch = findAvailablePatch();
        Cop cop= new Cop(availablePatch);
        availablePatch.addPerson(cop);
        return cop;
    }

    // find an available patch for new agent or cop
    public Patch findAvailablePatch(){

        Patch availablePatch;
        // go through all the patch randomly until find an an available one
        while(true) {
            availablePatch = patches[ThreadLocalRandom.current().nextInt(0,numberOfPatches)]
                    [ThreadLocalRandom.current().nextInt(0,numberOfPatches)];
            if(availablePatch.getPeople().size() == 0) break;
        }

        return availablePatch;
    }


    private void movePeople(Person[] people){

        for (Person person: people){
            // cops can move freely
            if (person instanceof Cop){
                person.move();
            }
            // move only when agents are not in jail
            if (person instanceof Agent && ((Agent) person).getRemainJailTerm() == 0){
                person.move();
            }

        }
    }

    // count all the agents
    public void agentStatistic(){

        for(Agent agent : agents){
            if(agent.isActive()){
                // count active ones
                activeAgents ++;
            }
            if (agent.getRemainJailTerm() > 0){
                // count the ones in jail
                peopleInJail++;
            }
        }
        // calculate how many left, and they are quiet agents
        quietAgents = agents.length - activeAgents - peopleInJail;

    }

    // reset counters
    public void reset(){
        activeAgents = 0;
        peopleInJail = 0;
        quietAgents = 0;
    }

}
