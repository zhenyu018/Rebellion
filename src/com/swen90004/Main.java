package com.swen90004;

import java.io.*;
import java.util.Scanner;

/**
* This class is used to run the simulator with a number of ticks
* from user input. It is also used to write the result of experiment
* to a csv file called output.csv
*/
public class Main {

    public static void main(String[] args) {

        // Ask user to enter the number of ticks
        System.out.println("enter number of ticks: ");
        Scanner scanner = new Scanner(System.in);
        int ticks = Integer.parseInt(scanner.nextLine());

        // initialize simulator
        Simulator simulator = new Simulator();
        simulator.init();

        try {
            // create an output file, overwrite it if already existed
            PrintWriter writer = new PrintWriter(new File("output.csv"));

            // a buffer to store header and data
            StringBuffer csvHeader = new StringBuffer("");
            StringBuffer csvData = new StringBuffer("");
            csvHeader.append("Tick,Quiet,Jailed,Active\r\n");

            // write header to the output file
            writer.write(csvHeader.toString());

            // run the model for number of ticks
            for (int i = 1; i <= ticks; i++){
                simulator.go();
                simulator.agentStatistic();
                csvData.append(i);
                csvData.append(",");
                csvData.append(simulator.getQuietAgents());
                csvData.append(",");
                csvData.append(simulator.getPeopleInJail());
                csvData.append(",");
                csvData.append(simulator.getActiveAgents());
                csvData.append("\r\n");
                simulator.reset();
            }

            // write data to the output file
            writer.write(csvData.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
