import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.File;

public class SingleServerQueuing{
    // define constants 
    // use static so that I can use these variables without objects

    static final int Q_Limit = 100;
    static final int Busy = 1;
    static final int Idle = 0;

    //some variables declaration
    static int nextEventType;
    static int numberCustomersDelayed;
    static int numberDelaysRequired;
    static int numberEvents;
    static int numberInQueue;
    static int serverStatus;

    static double areaNumberInQ;
    static double areaServerStatus;
    static double meanInterarrival;
    static double meanService;
    static double simulationTime;
    static double timeLastEvent;
    static double totalOfDelays;

    //create timeArrival array with Q_limit+1 elements (because of ignore index 0)
    static double[] timeArrival = new double[Q_Limit+1];

    //create timeNextEvent array of size 3, indices 1 and 2 are used
    static double[] timeNextEvent = new double[3];

    static PrintWriter outfile;

    //initialization function
    static void initialize(){
        // initialize simulation clock
        simulationTime = 0.0;

        //initialize state variables
        serverStatus = Idle;
        numberInQueue = 0;
        timeLastEvent = 0.0;

        //initialize statistical counters
        numberCustomersDelayed = 0;
        totalOfDelays = 0.0;
        areaNumberInQ = 0.0;
        areaServerStatus = 0.0;

        /**
         * Initialize event list
         * Since no customers are present, the departure(service completion) event is eliminated from consideration
         */

        timeNextEvent[1] = simulationTime + expon(meanInterarrival);

        /**
         * The use of a very large number is to represent a very distant future
         * Currently there are no customers in the system so the departure is in essence not possible at this moment
         * I also want to mitigate premature event execution by the simulation engine.
         * 
         */
        timeNextEvent[2] =  1.0e+30;

    }

    static void timing(){
        int i;
        double minTimeNextEvent = 1.0e+29;
        nextEventType = 0;

        //determine the event type of the next event to occur
        // this is done by finding the minimum event time
        for(i = 1; i <= numberEvents; ++i){
            if(timeNextEvent[i] < minTimeNextEvent){
                minTimeNextEvent = timeNextEvent[i];
                nextEventType = i;
            }
        }

        //if no event is available, exit simulation
        if(nextEventType == 0){
            outfile.printf("\n Event list empty at time %f", simulationTime);
            outfile.flush();
            System.exit(1);
        }
        simulationTime = minTimeNextEvent;

    }



    /**
     * Arrival event function
     */
    static void arrive(){
        double delay;

        //schedule next arrival
        timeNextEvent[1] = simulationTime + expon(meanInterarrival);

        //check to see whether server is busy
        if(serverStatus == Busy){
            //server is busy, so increment no of customers in queue
            numberInQueue++;

            //check to see whether overflow condition exists
            if(numberInQueue > Q_Limit){
                // the queue has overflowed so stop the simulation
                outfile.printf("\n Overflow of the array timeArrival at time %f", simulationTime);
                outfile.flush();
                System.exit(2);

            }
            // there is still room in the queue, so store the time of arrival of the 
            // arriving customer at the (new) end of timeArrival
            timeArrival[numberInQueue] = simulationTime;
        }
        else{
            // Server is idle, so arriving customer has a delay of zero.
            delay = 0.0;
            totalOfDelays += delay;

            //increment the number of customers delayed, and make server busy
            numberCustomersDelayed++;
            serverStatus = Busy;

            //schedule a departure (service completion)
            timeNextEvent[2] = simulationTime + expon(meanService);

        }

    }

    static void depart(){
        int i;
        double delay = 0.0;

        //check to see whether queue is empty
        if(numberInQueue == 0){
            // queue is empty, so make server idle and eliminate tje departure event from consideration
            serverStatus = Idle;
            timeNextEvent[2] = 1.0e+30;

        }
        else{
            //queue is not empty, so decrease the number of customers in queue
            numberInQueue--;

            //compute delay of customer who is beginning service and update the total delay accumulator.
            delay = simulationTime - timeArrival[1];
            totalOfDelays += delay;

            // increment the number of customers delayed, and schedule departure
            numberCustomersDelayed++;
            timeNextEvent[2] = simulationTime + expon(meanService);

            //move each customer in queue if any, by one place
            for(i = 1; i <= numberInQueue; ++i){
                timeArrival[i] = timeArrival[i+1];

            }
        }

    }

    static void report(){
        //report generator function
        outfile.printf("\n\n Average delay in queue %11.3f minutes \n\n", totalOfDelays / numberCustomersDelayed);
        outfile.printf("Average number in queue%10.3f\n\n", areaNumberInQ / simulationTime);
        outfile.printf("Server utilization%15.3f\n\n", areaServerStatus / simulationTime);
        outfile.printf("Time simulation ended%12.3f minutes", simulationTime);
    }

    static void updateTimeAverageStats(){
        //update area accumulators for time average statistics

        double timeSinceLastEvent;
        
        //compute time since last event, and update last-event-time marker
        timeSinceLastEvent = simulationTime - timeLastEvent;
        timeLastEvent = simulationTime;

        //update area under numberInQueue function
        areaNumberInQ += numberInQueue * timeSinceLastEvent;

        //update area under server-busy indicator function
        areaServerStatus += serverStatus * timeSinceLastEvent;
        
    }

    /**
     * Exponential variate generation function
     */
    static double expon(double mean){
        return -mean * Math.log(Math.random());

    }


    
    
    public static void main(String[] args){
        //use scanner to read input
        Scanner infile = null;

        try{
            infile = new Scanner(new File("mm1.in"));
            outfile = new PrintWriter("mm1.out");

        } catch(FileNotFoundException e){
            System.out.println("File not found: " + e.getMessage());
            System.exit(1);

        }

        numberEvents = 2;

        //read input parameters such as mean interarrival time, mean service time and number of delays required
        if(infile.hasNextDouble()){
            meanInterarrival = infile.nextDouble();

        }

        if(infile.hasNextDouble()){
            meanService = infile.nextDouble();

        }

        if(infile.hasNextInt()){
            numberDelaysRequired = infile.nextInt();
        }

        //test code to display whether variables are appropriately assigned
        System.out.printf("Debug: meanInterarrival = %.3f, meanService = %.3f, numberDelaysRequired = %d\n",
                  meanInterarrival, meanService, numberDelaysRequired);


        // write heading and input parameters to the output
        outfile.println("Single-Server Queuing System \n");
        outfile.printf("Mean Interarrival time %11.3f minutes \n\n", meanInterarrival);
        outfile.printf("Mean Service time %16.3f minutes \n\n", meanService);
        outfile.printf("Number of customers%14d\n\n", numberDelaysRequired);

        //initialize simulation
        initialize();

        //there is need to run simulation while more delays are still needed
        while(numberCustomersDelayed < numberDelaysRequired){
            //determine the next event
            timing();

            //update time average statistical accumulators
            updateTimeAverageStats();

            if(nextEventType == 1){
                arrive();
            } else if(nextEventType == 2){
                depart();
            }
        }

        //invoke the report generator and close files
        report();
        infile.close();
        outfile.close();
    

    }

}