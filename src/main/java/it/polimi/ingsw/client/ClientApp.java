package it.polimi.ingsw.client;

import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("play in cli or gui?");
        boolean temp = true;
        while(temp){
            String mode = in.nextLine();
            mode = mode.toLowerCase();
            switch (mode){
                case"gui":
                    //temp=false;
                    System.out.println("no gui for now, go play with a stick or something");
                    break;
                case "cli":
                    temp=false;
                    new CLI().startCLI();
                    break;
                default:
                    System.out.println("try again");
                    System.out.println("play in cli or guy?");
            }
        }
    }
}