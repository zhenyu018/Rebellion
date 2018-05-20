package com.swen90004;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Person {


    private int pid;
    private boolean movement;
    private Patch position;

    public Person(int pid, Patch patch){
        this.position = patch;
        this.pid = pid;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            MyHandler handler = new MyHandler();
            parser.parse("src/com/swen90004/Parameter.xml", handler);
            movement = handler.getMovement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public Patch getPosition() {
        return position;
    }

    public void setPosition(Patch position) {
        this.position = position;
    }

    private Patch currentLocation;
    public void move(){
        ArrayList<Patch> tempPatches = new ArrayList<>();
        if(movement || this instanceof Cop){
            //iterate through the neighbors
            for(Patch patch : position.getVisionPatch()){
                if(patch.isOccupied()) tempPatches.add(patch);
            }

            int randPatch = ThreadLocalRandom.current().nextInt(0, tempPatches.size());
            int randPatchX = tempPatches.get(randPatch).getLocationX();
            int randPatchY = tempPatches.get(randPatch).getLocationY();

            //move the current person onto a random movable patch
            Simulator.patches[position.getLocationX()][position.getLocationY()].
                    deletePerson(this);
            Simulator.patches[randPatchX][randPatchY].addPerson(this);
            this.position = Simulator.patches[randPatchX][randPatchY];

        }
    }

}
