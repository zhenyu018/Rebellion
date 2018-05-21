package com.swen90004;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
/**
* This class is an abstract class of a person who can move
* to a random patch.*/
public abstract class Person {

    private boolean movement;
    private Patch position;

    public Person( Patch patch){

        this.position = patch;
        // read the xml file to get parameters
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

    // this method is to move a person to an unoccupied random patch
    public void move(){

        // create a array list to store all the patches
        // which are unoccupied and within this person's vision
        ArrayList<Patch> randomPatches = new ArrayList<>();
        if(movement){
            // add all the available patches to randomPatches
            for(Patch patch : position.getVisionPatch()){
                if(!patch.isOccupied()) randomPatches.add(patch);
            }
            // find a random patch
            int randPatch = ThreadLocalRandom.current().nextInt(0, randomPatches.size());
            int randPatchX = randomPatches.get(randPatch).getLocationX();
            int randPatchY = randomPatches.get(randPatch).getLocationY();

            //move this person onto that random patch
            Simulator.patches[position.getLocationX()][position.getLocationY()].
                    deletePerson(this);
            Simulator.patches[randPatchX][randPatchY].addPerson(this);
            this.position = Simulator.patches[randPatchX][randPatchY];

        }
    }

}
