import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SingleServerQueuing {
    // Constants
    static final int Q_LIMIT = 100;  // Limit on queue length
    static final int BUSY = 1;       // Server busy indicator
    static final int IDLE = 0;       // Server idle indicator

    // Simulation state variables
    static int next_event_type, num_custs_delayed, num_delays_required, num_events, num_in_q, server_status;
    static double area_num_in_q, area_server_status, mean_interarrival, mean_service;
    static double sim_time, time_last_event, total_of_delays;
    // Note: Arrays are allocated with one extra element; index 0 is unused to match the C code.
    static double[] time_arrival = new double[Q_LIMIT + 1];
    // Only two events are used (arrival and departure); we leave index 0 unused.
    static double[] time_next_event = new double[3];

    static PrintWriter outfile;

    public static void main(String[] args) {
        Scanner infile = null;
        try {
            infile = new Scanner(new File("mm1.in"));
            outfile = new PrintWriter("mm1.out");
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            System.exit(1);
        }

        num_events = 2;

        // Read input parameters: mean interarrival time, mean service time, and number of delays required.
        if (infile.hasNextDouble()) {
            mean_interarrival = infile.nextDouble();
        }
        if (infile.hasNextDouble()) {
            mean_service = infile.nextDouble();
        }
        if (infile.hasNextInt()) {
            num_delays_required = infile.nextInt();
        }

        // Write report heading and input parameters.
        outfile.println("Single-server queueing system\n");
        outfile.printf("Mean interarrival time%11.3f minutes\n\n", mean_interarrival);
        outfile.printf("Mean service time%16.3f minutes\n\n", mean_service);
        outfile.printf("Number of customers%14d\n\n", num_delays_required);

        // Initialize the simulation.
        initialize();

        // Run the simulation while more delays are still needed.
        while (num_custs_delayed < num_delays_required) {
            timing();
            update_time_avg_stats();
            switch (next_event_type) {
                case 1:
                    arrive();
                    break;
                case 2:
                    depart();
                    break;
                default:
                    // Should never reach here.
                    break;
            }
        }

        // Generate report and close files.
        report();
        infile.close();
        outfile.close();
    }

    // Initialization function.
    static void initialize() {
        sim_time = 0.0;
        server_status = IDLE;
        num_in_q = 0;
        time_last_event = 0.0;
        num_custs_delayed = 0;
        total_of_delays = 0.0;
        area_num_in_q = 0.0;
        area_server_status = 0.0;
        // Schedule the first arrival and set departure event far in the future.
        time_next_event[1] = sim_time + expon(mean_interarrival);
        time_next_event[2] = 1.0e30;
    }

    // Timing function.
    static void timing() {
        double min_time_next_event = 1.0e29;
        next_event_type = 0;
        // Determine the next event type by finding the minimum event time.
        for (int i = 1; i <= num_events; i++) {
            if (time_next_event[i] < min_time_next_event) {
                min_time_next_event = time_next_event[i];
                next_event_type = i;
            }
        }
        // If no event is found, exit the simulation.
        if (next_event_type == 0) {
            outfile.printf("\nEvent list empty at time %f", sim_time);
            outfile.flush();
            System.exit(1);
        }
        sim_time = min_time_next_event;
    }

    // Arrival event function.
    static void arrive() {
        double delay;
        // Schedule the next arrival.
        time_next_event[1] = sim_time + expon(mean_interarrival);
        if (server_status == BUSY) {
            num_in_q++;
            if (num_in_q > Q_LIMIT) {
                outfile.printf("\nOverflow of the array time_arrival at time %f", sim_time);
                outfile.flush();
                System.exit(2);
            }
            // Record the time of arrival for the customer in the queue.
            time_arrival[num_in_q] = sim_time;
        } else {
            // If server is idle, customer experiences zero delay.
            delay = 0.0;
            total_of_delays += delay;
            num_custs_delayed++;
            server_status = BUSY;
            // Schedule a departure event.
            time_next_event[2] = sim_time + expon(mean_service);
        }
    }

    // Departure event function.
    static void depart() {
        double delay;
        if (num_in_q == 0) {
            // If no customers are waiting, mark the server as idle.
            server_status = IDLE;
            time_next_event[2] = 1.0e30;
        } else {
            // The customer at the front of the queue begins service.
            num_in_q--;
            delay = sim_time - time_arrival[1];
            total_of_delays += delay;
            num_custs_delayed++;
            time_next_event[2] = sim_time + expon(mean_service);
            // Shift each remaining customer up in the queue.
            for (int i = 1; i <= num_in_q; i++) {
                time_arrival[i] = time_arrival[i + 1];
            }
        }
    }

    // Report generator function.
    static void report() {
        outfile.printf("\n\nAverage delay in queue%11.3f minutes\n\n", total_of_delays / num_custs_delayed);
        outfile.printf("Average number in queue%10.3f\n\n", area_num_in_q / sim_time);
        outfile.printf("Server utilization%15.3f\n\n", area_server_status / sim_time);
        outfile.printf("Time simulation ended%12.3f minutes", sim_time);
    }

    // Update area accumulators for time-average statistics.
    static void update_time_avg_stats() {
        double time_since_last_event = sim_time - time_last_event;
        time_last_event = sim_time;
        area_num_in_q += num_in_q * time_since_last_event;
        area_server_status += server_status * time_since_last_event;
    }

    // Exponential variate generation function.
    static double expon(double mean) {
        return -mean * Math.log(Math.random());
    }
}
