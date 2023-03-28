package main;

import java.util.Scanner;

public class Console {
    private Scanner scanner;
    public Console(Scanner scanner) {
        this.scanner = scanner;
    }
    public static void print(String str) {
            System.out.println(str);
    }
    public int inputInt(String s) throws NumberFormatException{
        System.out.println(s);
        return Integer.parseInt(scanner.nextLine());
    }
    public int inputInt(int min, int max, String s){
        System.out.println(s);
        int choice = min;
        while(true){
            try{
                choice = Integer.parseInt(scanner.nextLine());
                if(choice >= min && choice <=max){
                    return choice;
                }
                else{
                    System.out.println("Invalid Choice");
                    System.out.println(s);
                }
            }catch(NumberFormatException ignored){}
        }
    }
    public String inputString(String s){
        System.out.println(s);
        return scanner.nextLine();
    }
    public double inputDouble(String s) throws NumberFormatException{
        System.out.println(s);
        return Double.parseDouble(scanner.nextLine());
    }

    public double inputDouble(double min, double max, String s){
        System.out.println(s);
        double choice = min;
        while(true){
            try{
                choice = Double.parseDouble(scanner.nextLine());
                if(choice >= min && choice <=max){
                    return choice;
                }
                else{
                    System.out.println("Invalid Choice");
                    System.out.println(s);
                }
            }catch(NumberFormatException ignored){}
        }
    }
}
