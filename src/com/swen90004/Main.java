package com.swen90004;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class Main {

    public static void main(String[] args) {
        System.out.println("enter number of ticks: ");
        Scanner scanner = new Scanner(System.in);
        int ticks = Integer.parseInt(scanner.nextLine());
        Simulator simulator = new Simulator();
        simulator.init();

        try {
            PrintWriter writer = new PrintWriter(new File("output.csv"));
            StringBuffer csvHeader = new StringBuffer("");
            StringBuffer csvData = new StringBuffer("");
            csvHeader.append("ticks,Quiet,Jailed,Active\r\n");
            writer.write(csvHeader.toString());
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
            writer.write(csvData.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
