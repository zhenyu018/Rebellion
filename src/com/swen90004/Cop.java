package com.swen90004;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
/**
* Cop is a person and has arrest function
*/
public class Cop extends Person {

    public Cop(Patch patch){
        super(patch);
    }

    // arrest a random active agent in the vision and move to that patch
    public void arrest(){

        // find all active agents within vision
        int[] counts = getPosition().countNeighbours();
        ArrayList<Patch> tempPatches = new ArrayList<>();
        if(counts[1] > 0){
            for(Patch patch : getPosition().getVisionPatch()){
                for(Person person : patch.getPeople()){
                    if (person instanceof Agent && ((Agent) person).isActive()){
                        tempPatches.add(patch);
                    }
                }
            }

            // choose a random active agent from list
            int randPatch = ThreadLocalRandom.current().nextInt(0, tempPatches.size());
            int randPatchX = tempPatches.get(randPatch).getLocationX();
            int randPatchY = tempPatches.get(randPatch).getLocationY();
            // arrest the active agent
            Simulator.patches[randPatchX][randPatchY].getActiveAgent().beArrested();

            // move to that patch
            Simulator.patches[this.getPosition().getLocationX()]
                    [this.getPosition().getLocationY()].deletePerson(this);
            Simulator.patches[randPatchX][randPatchY].addPerson(this);
            setPosition(Simulator.patches[randPatchX][randPatchY]);
        }
    }
}
