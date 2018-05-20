package com.swen90004;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Person {

    private boolean movement;
    private Patch position;

    public Person(Patch patch){
        this.position = patch;
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


    public Patch getPosition() {
        return position;
    }

    public void setPosition(Patch position) {
        this.position = position;
    }

    public void move(){
        ArrayList<Patch> tempPatches = new ArrayList<>();
        if (movement){
            for(Patch patch : position.getVisionPatch()){
                if(!patch.isOccupied()){
                    tempPatches.add(patch);
                }
            }
            if (tempPatches.size() > 0){
                int randPatch = ThreadLocalRandom.current().nextInt(tempPatches.size());
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

}
