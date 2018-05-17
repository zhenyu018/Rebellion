package com.swen90004;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

public class Simulator {
    public static Patch[][] patches;
    private int numberOfPatches;
    private int numberOfCops;
    private int numberOfAgents;
    private int ticks;
    public int activeAgents;
    public int peopleInJail;
    public double governmentLegitymacy;
    public Agent[] agents;
    public Cop[] cops;

    public void init(){
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLParser handler = new XMLParser();
            parser.parse("Parameter.xml", handler);
            numberOfPatches = handler.getNumberOfPatches();
            numberOfAgents = handler.getNumberOfAgents();
            numberOfCops = handler.getNumberOfCops();
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

    public void go(int ticks) throws IOException {

        //create the excel file
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Rebellion");
        Map<Integer, Object[]> data = new HashMap<>();
        data.put(1, new Object[]{"Tick No.", "Quiet Agents",
                "Jailed Agents", "Active Agents"});

        //start running the world
        int it = 0;
        for (int i = ticks; i > 0; i--) {
            // print out the pic
            printPatch();

            //calculate the agents for the output excel.
            resetCount();
            countAgents();

            // M rule
            logger.info("agents are moving randomly");
            randMove(agents);

            logger.info("cops are moving randomly");
            randMove(cops);

            // special modes, reduce gov gradually
            if (it > 50 && this.graduallyChangeGov) {
                graduallyChange();
            }

            // special modes, reduce gov sharply
            if (it == 50 && this.sharplyChangGov) {
                sharplyChange();
            }

            //shuffle the agents and cops
            Collections.shuffle(Arrays.asList(agents));
            Collections.shuffle(Arrays.asList(cops));

            // A rule
            for (Agent agent : agents) {
                if (agent.getJailTerm() == 0) {
                    if (this.extension) agent.extensionBehavior();
                    else agent.determinBehavior();
                }
            }

            // C rule
            for (Cop cop : cops) {
                cop.enforce();
            }
            //Reduce the jail term of jailed agents.
            for (Agent agent : agents) {
                if (agent.getJailTerm() > 0) agent.reduceJailTerm();
            }

            it++;
            //logger.info("world running iteration it :" + it);

            //put the data into the row
            data.put(it + 1, new Object[]{it, quietAgent, jailedAgent
                    , activeAgent});

        }

        logger.info("finished running the world");


        //set up rows and cells and sort keys
        List<Integer> keys = new ArrayList<>(data.keySet());
        Collections.sort(keys);
        int rownum = 0;
        for (Integer key : keys) {
            Row row = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof Date)
                    cell.setCellValue((Date) obj);
                else if (obj instanceof Boolean)
                    cell.setCellValue((Boolean) obj);
                else if (obj instanceof String)
                    cell.setCellValue((String) obj);
                else if (obj instanceof Double)
                    cell.setCellValue((Double) obj);
                else if (obj instanceof Integer)
                    cell.setCellValue((int) obj);
            }
        }

        //write data to the excel file
        try {
            FileOutputStream out =
                    new FileOutputStream(new File(Paths.get(".")
                            .toAbsolutePath().normalize().toString(),
                            "Rebellion"), false);
            workbook.write(out);
            out.close();
            System.out.println("Excel written successfully..");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Agent generateAgent(){

        Patch currentPatch = randPatch();

        Agent agent= new Agent(currentPatch, false);

        currentPatch.setPerson(agent);

        return agent;
    }

    public Cop generateCop(){

        Patch currentPatch = randPatch();

        Cop cop= new Cop(currentPatch);

        currentPatch.setPerson(cop);

        return cop;
    }

    public Patch findAvailablePatch(){

        logger.info("randPatch method");
        Patch patch;
        logger.info("start randomly finding patch");

        while(true) {
            patch = patches[randInt(0,numOfPathes-1)]
                    [randInt(0,numOfPathes-1)];
            if(patch.getPerson().size() == 0) break;
        }
        logger.info("finished randomly finding patch");

        return patch;
    }

    private void movePeople(Person[] people){
        logger.info("randMoving starts");
        ArrayList<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            remaining.add(i);
        }
        int step = 0;
        boolean isAgentArray = false;
        if(array[0] instanceof Agent) isAgentArray = true;

        //select from remaining index when remaining is not empty
        while (step<array.length) {
            int index = randInt(0,remaining.size()-1);
            if(remaining.get(index)>=0){
                //move agents and cops here
                if(isAgentArray){
                    if(agents[index].getJailTerm() == 0)
                        agents[index].move();
                }
                else cops[index].move();
                remaining.set(index,-1);
                step++;
            }
        }
        logger.info("randmove finished after " + step + "iterations.");
    }

    private void agentStatistic(){
        for(Agent agent : agents){
            if(agent.isActive()) activeAgent ++;
            else if(agent.getJailTerm() > 0) jailedAgent ++;
            else quietAgent ++;
        }
    }

    /**
     * Reset the count of agents.
     */
    private void reset(){
        activeAgent = 0;
        jailedAgent = 0;
        quietAgent = 0;
    }

}
