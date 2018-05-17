package com.swen90004;

import com.sun.org.apache.xml.internal.resolver.readers.SAXParserHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;

public class Patch {
    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    private int locationX;
    private int locationY;
    private boolean isOccupied;
    private ArrayList<Person> people;
    private int numberOfPatches;
    private int vision;
    public Patch(int x, int y){
        this.locationX = x;
        this.locationY = y;
        this.isOccupied = false;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLParser handler = new XMLParser();
            parser.parse("Parameter.xml", handler);
            numberOfPatches = handler.getNumberOfPatches();
            vision = handler.getVision();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    public Patch GetPatch(int x, int y) {
        return Simulator.patches[ (x + numberOfPatches) % numberOfPatches]
                    [(y + numberOfPatches) % numberOfPatches];
    }

    public boolean isOccupied(){
        //if there is no one in this patch, return true
        if(people == null) return true;

        //if the only person in this patch is jailed, then this patch is not occupied
        for(Person person : this.people){
            if(person instanceof Cop || person instanceof Agent &&
                    ((Agent)person).getRemainJailTerm()==0) return false;
        }
        return true;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public void addPerson(Person person){
        this.people.add(person);
    }

    public void deletePerson(Person person){
        this.people.remove(person);
    }

    public ArrayList<Patch> getVisionPatch(){
        ArrayList<Patch> visionPatches = new ArrayList<>();
        for (int y = 1; y <= vision; y++) {
            visionPatches.add(GetPatch(locationX, locationY + y));
            visionPatches.add(GetPatch(locationX, locationY - y));
        }
        for (int x = 1; x <=vision; x++) {
            int sq = (int) Math.sqrt(vision * vision - x *
                    x);
            visionPatches.add(GetPatch(locationX + x, locationY));
            visionPatches.add(GetPatch(locationX - x, locationY));

            for (int y = 1; y <= sq; y++) {
                visionPatches.add(GetPatch(locationX + x, locationY + y));
                visionPatches.add(GetPatch(locationX - x, locationY + y));
                visionPatches.add(GetPatch(locationX + x, locationY - y));
                visionPatches.add(GetPatch(locationX - x, locationY - y));
            }
        }
        visionPatches.add(this);

        return visionPatches;
    }

    public int[] countNeighbours(){
        int cops = 0;
        int activeAgents= 0;
        for(Patch patch : getVisionPatch()){
            for (Person person: this.people){
                if (person instanceof Cop){
                    cops++;
                }
                if (person instanceof Agent && ((Agent) person).isActive()){
                    activeAgents++;
                }
            }
        }
        return new int[]{cops,activeAgents};
    }

    public Agent getQuietAgent(){
        for(Person person : people){
            if(person instanceof Agent && !((Agent) person).isActive())
                return (Agent)person;
        }
        return null;
    }

    public Agent getActiveAgent(){
        for(Person person : people){
            if(person instanceof Agent && ((Agent) person).isActive())
                return (Agent)person;
        }
        return null;
    }



}
