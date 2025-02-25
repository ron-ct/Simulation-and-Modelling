import java.util.Random;

public class rollingDieSimulation{
    private int face;
    
    public rollingDieSimulation(){
        face = -1;
    }

    public void roll(){
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            
            float randomNumber = random.nextFloat(1);
            if(randomNumber < 1.0/6.0){
                face = 1;
            }else if(randomNumber < 2.0/6.0){
                face = 2;
            }else if(randomNumber < 3.0/6.0){
                face = 3;
            }else if(randomNumber < 4.0/6.0){
                face = 4;
            }else if(randomNumber < 5.0/6.0){
                face = 5;
            }else{
                face = 6;
            }
            System.err.println(face);
            
        }
    }

    public static void main(String[] args){
        rollingDieSimulation sim = new rollingDieSimulation();
        sim.roll();
    }


}