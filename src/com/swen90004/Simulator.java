package com.swen90004;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Simulator {
    public static Patch[][] patches;
    private int numberOfPatches;
    private int numberOfCops;
    private int numberOfAgents;
    private int activeAgents;
    private int peopleInJail;
    private int quietAgents;
    private double governmentLegitymacy;
    private Agent[] agents;
    private Cop[] cops;
    private int pid;

    public int getActiveAgents() {
        return activeAgents;
    }

    public int getPeopleInJail() {
        return peopleInJail;
    }

    public int getQuietAgents() {
        return quietAgents;
    }

    public void init(){
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            MyHandler handler = new MyHandler();
            parser.parse("src/com/swen90004/Parameter.xml", handler);
            numberOfPatches = handler.getNumberOfPatches();
            numberOfAgents = handler.getNumberOfAgents();
            numberOfCops = handler.getNumberOfCops();
            pid = 0;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        patches = new Patch[numberOfPatches][numberOfPatches];
        for(int i = 0; i < patches.length; i ++){
            for(int j = 0; j < patches[i].length; j ++){
                patches[i][j] = new Patch(i,j);
                //logger.info("generated x = " + i + "y = " + j);
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
                if (agent.getRemainJailTerm() > 0) agent.manageJailTerm(-1);
            }





    }
    public Agent generateAgent(){

        Patch availablePatch = findAvailablePatch();
        Agent agent= new Agent(pid, availablePatch, false);
        pid++;
        availablePatch.addPerson(agent);
        return agent;
    }

    public Cop generateCop(){

        Patch availablePatch = findAvailablePatch();
        Cop cop= new Cop(pid, availablePatch);
        pid++;
        availablePatch.addPerson(cop);
        return cop;
    }

    public Patch findAvailablePatch(){

        Patch patch;

        while(true) {
            patch = patches[ThreadLocalRandom.current().nextInt(0,numberOfPatches)]
                    [ThreadLocalRandom.current().nextInt(0,numberOfPatches)];
            if(patch.getPeople().size() == 0) break;
        }

        return patch;
    }

    private void movePeople(Person[] people){
        for (Person person: people){
            if (person instanceof Cop){
                person.move();
            }
            if (person instanceof Agent && ((Agent) person).getRemainJailTerm() == 0){
                person.move();
            }

        }
    }

    public void agentStatistic(){
        for(Agent agent : agents){
            if(agent.isActive()){
                activeAgents ++;
            }
            if (agent.getRemainJailTerm() > 0){
                peopleInJail++;
            }
        }
        quietAgents = agents.length - activeAgents - peopleInJail;

    }

    /**
     * Reset the count of agents.
     */
    public void reset(){
        activeAgents = 0;
        peopleInJail = 0;
    }

    public void printPatch(){
        for (int i = 0; i < patches.length; i++){
            for (Patch patch : patches[i]){

            }
        }

    }

}
