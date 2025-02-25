import java.util.Random;
import java.util.Scanner;

public class rollDiceSimulation{
    private int face;

    public rollDiceSimulation(){
        this.face = -1;
    }

    public void rollDice(int N){
        Random random = new Random();
        int[] frequency = new int[6]; // to store the count for faces 1 to 6

        //Roll the die N times
        for (int i = 0; i < N; i++) {
            float randomNumber = random.nextFloat(1);

            if(randomNumber < 1.0/6.0){
                face = 1;
            }
            else if(randomNumber < 2.0/6.0){
                face = 2;
            }
            else if(randomNumber < 3.0 / 6.0){
                face = 3;
            } else if (randomNumber < 4.0 / 6.0) {
                face = 4;
            } else if (randomNumber < 5.0 / 6.0) {
                face = 5;
            }
            else{face = 6;}

            // we have to increment the number in the corresponding position in the frequency array
            frequency[face - 1]++;


        }

        //display results
        System.out.println("\nResults:");
        //- for left-align
        //10 to mean that the placeholder should accept 10 characters
        //% to mean a placeholder
        System.out.printf("%-10s%-10s%-10s", "FACE", "FREQUENCY", "PERCENTAGE");
        System.out.println("\n--------------------------------------------------");

        for (int i = 0; i < 6; i++) {
            double percentage = (frequency[i] * 100.0) / N; //percentage = frequency value/total x 100
            System.out.printf("%-10d%-10d%-10.1f%%%n", (i + 1), frequency[i], percentage);

        }
    }


    public static void main(String[] args){
        rollDiceSimulation aDice = new rollDiceSimulation();
        int N = 1000; //default value
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many times should the dice roll? ");
        if(scanner.hasNextInt()){
            N = scanner.nextInt();
        }

        aDice.rollDice(N);
    }
}