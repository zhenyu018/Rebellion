package com.swen90004;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Cop extends Person {

    public Cop(Patch patch){
        super(patch);
    }

    public void arrest(){
        int[] counts = getPosition().countNeighbours();
        ArrayList<Patch> tempPatches = new ArrayList<>();
        if(counts[1]!=0){
            for(Patch patch : getPosition().getVisionPatch()){
                for(Person person : this.getPosition().getPeople()){
                    if (person instanceof Agent && ((Agent) person).isActive()){
                        tempPatches.add(patch);
                    }
                }
            }

            int randPatch = ThreadLocalRandom.current().nextInt(0, tempPatches.size());
            int randPatchX = tempPatches.get(randPatch).getLocationX();
            int randPatchY = tempPatches.get(randPatch).getLocationY();

            //send the suspect to jail on that patch
            Simulator.patches[randPatchX][randPatchY].getActiveAgent().beArrested();

            Simulator.patches[this.getPosition().getLocationX()][this.getPosition().getLocationY()].deletePerson(this);
            Simulator.patches[randPatchX][randPatchY].addPerson(this);
            setPosition(Simulator.patches[randPatchX][randPatchY]);

        }
    }
}
