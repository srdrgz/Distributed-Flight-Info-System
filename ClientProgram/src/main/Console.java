package main;
import java.util.*;
public class Console {

    private static Scanner scanner;
    public Console(Scanner s){
        this.scanner = s;
    }
    public static void print(String msg){
        System.out.println(msg);
    }
    public static int inputInt(String s) throws NumberFormatException{
        System.out.println(s);
        return Integer.parseInt(scanner.nextLine());
    }
    public static double inputDouble(String s) throws NumberFormatException{
        System.out.println(s);
        return Double.parseDouble(scanner.nextLine());
    }
    public static String inputString(String s){
        System.out.println(s);
        return scanner.nextLine();
    }
    public static String inputTime(String s){
        String result = "";
        boolean valid = false;
        while(!valid){
            int[] time = new int[4];
            System.out.println(s);
            String input = scanner.nextLine();
            char temp[] = input.toCharArray();
            if(temp.length != 5){
                System.out.println("Invalid format. Try again.");
                continue;
            }
            int colon = 2;
            for(int i = 0,k = 0 ; i < temp.length; i++){
                if(i!=colon){
                    time[k] = Integer.parseInt(String.valueOf(temp[i]));
                }
            }
            if(time[0] < 0 || time[0] > 2 ||time[1] < 0 ||time[2] < 0 ||time[3] < 0 || time[2] > 5) {
                System.out.println("Invalid format. Try again.");
            } else{
                if(time[0] == 2 && time[1] > 3){
                System.out.println("Invalid format. Try again.");
                }else{
                    result = input;
                    valid = true;
                }
            }

        }
        return result;
    }
}
