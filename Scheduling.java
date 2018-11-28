
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Scheduling {
		
	public static void main(String[] args) throws IOException {
		
		File file = new File("input1.txt"); // File variable
		FileWriter output = new FileWriter("output.txt", true);
		PrintWriter printWriter = new PrintWriter(output);
		Scanner input = new Scanner(file); //Setting up a scanner to be able to parse through the file.
		String type;
		int timeQuantum = 0;
		type = input.next();
		
		if(type.equals("RR")) { // If the type of scheduler is RR, we save the time quantum to a variable
			timeQuantum = input.nextInt();
		}
		input.nextLine();
		int processNum = input.nextInt();
		input.nextLine();
				
		
		int[][] processInfo = new int[processNum][4];
		for (int i = 0; i < processNum; i++)
		{
			// Get process number
			processInfo[i][0] = input.nextInt();
			// Get Arrival Time
			processInfo[i][1] = input.nextInt();
			// Get CPU burst
			processInfo[i][2] = input.nextInt();
			// Get priority
			processInfo[i][3] = input.nextInt();
			if (i!=processNum-1) input.nextLine();
		}
			//calling the functions based upon the type
			if(type.equals("RR")) {
				RR(output, printWriter, processInfo,timeQuantum);
			}
			else if(type.equals("SJF")) {
				SJF(output, printWriter, processInfo);
			}
			else if(type.equals("PR_noPREMP")) {
				PR_noPREMP(output, printWriter, processInfo);
			
			} else if(type.equals("PR_withPREMP")) {
				PR_withPREMP(output, printWriter, processInfo);
			}
			input.close();
		}		
	
	public static void RR(FileWriter output, PrintWriter printWriter, int[][] processInfo, int quantum) throws IOException{
		int[][] queue = new int [processInfo.length][4];
		int arrival = 0, timer = 0, numFinish = 0, toW = 0, timeIncrement, queueIterator = 0;
		System.out.println("RR " + quantum);
		printWriter.printf("RR " + quantum +"\n");
		
		//copy the burst array and arrival array so you dont affect the original array
		for(int i = arrival; i < processInfo.length && processInfo[i][1] <= timer; i++) {
			queue[queueIterator++] = processInfo[i];
			arrival++;
		}
		
		while(numFinish != processInfo.length) {
			timeIncrement = 0;
			//print the time and the process
			System.out.printf("%d %d\n", timer, queue[0][0]);
			printWriter.printf("%d %d\n", timer, queue[0][0]);
						
			//calculating the cpus burst
			
			if(queue[0][2] < quantum) {
				timeIncrement = queue[0][2];
			}
			else {
				timeIncrement = quantum;
			}
			//incrementing the timer by the amount of the burst
			timer += timeIncrement;
			
			//decrease the cpu burst by the time increment
			queue[0][2] -= timeIncrement;
			
			toW += timeIncrement*(queueIterator - 1);
			int[] temp = queue[0];
			queueIterator--;
			
			
			for(int i = 0; i < queueIterator; i++) {
				queue[i] = queue[i+1];
			}
			
			for(int i = arrival; i < processInfo.length && processInfo[i][1] <= timer; i++) {
				queue[queueIterator++] = processInfo[i];
				arrival++;
				toW += timer - processInfo[i][1];
			}
			
			if(temp[2] != 0) {
				queue[queueIterator++] = temp;
			} else {
				numFinish++;
			}
		}
		System.out.printf("AVG Waiting Time: %.2f\n", (double)toW/(processInfo.length));
		printWriter.printf("AVG Waiting Time: %.2f\n", (double)toW/(processInfo.length));
		printWriter.close();
	}
	
	//SJF ALGORITHM
	
	public static void SJF(FileWriter output, PrintWriter printWriter, int[][] processInfo) {
		int[][] queue  = new int[processInfo.length][4]; // creating new array
		int timer = 0;
		int arrival = 0;
		int queueIterator = 0;
		int finish = 0;
		int toW = 0;

		System.out.println("SJF");
		printWriter.printf("SJF\n");
				
		//waiting for proccess depending on the arrival time
		while(processInfo[0][1] != timer) {
			timer++; 
		}
		
		//adding the arriving process to an array
		for(int i = arrival; i < processInfo.length && processInfo[i][1] <= timer; i++) {
			queue[queueIterator++] = processInfo[i];
			arrival++;
		}
		
		do {
			int timeIncrement = 0;
			int currProcess = 0;
			
			for(int i = 1; i < queueIterator; i++) {
				//find process by shortest cpu burst, break ties with arrival times
				if(queue[currProcess][2] > queue[i][2] || queue[currProcess][2] == queue[i][2] && queue[currProcess][1] > queue[i][1]) {
					currProcess = i;
				}
			}
			System.out.printf("%d %d \n", timer, queue[currProcess][0]);
			printWriter.printf("%d %d\n", timer, queue[currProcess][0]);
			timeIncrement = queue[currProcess][2]; //setting the increment to the amount of the current processes cpu burst.
			queue[currProcess][2] = 0; //setting the current process burst to 0 after it has been completed
			finish++;
			timer += timeIncrement;
			//removing finished process from the ready queue
			queue[currProcess] = queue[queueIterator - 1];
			queueIterator--;
			
			//calculating the total wait time of the proccesses after going through the SJF algorithm
			toW += timeIncrement*queueIterator;
			
			//adding new proccess that came at later arrival time to ready queue 
			for(int i = arrival; i < processInfo.length && processInfo[i][1] <= timer; i++) {
				queue[queueIterator++] = processInfo[i];
				arrival++;
				toW += timer - processInfo[i][1];
			}
		} while (finish != processInfo.length);
		System.out.printf("AVG Waiting Time: %.2f\n", (double)toW/(processInfo.length));	
		printWriter.printf("AVG Waiting Time: %.2f\n", (double)toW/(processInfo.length));
		printWriter.close();
	}
	
	//PRIORITY SCHEDLUING WITHOUT PREEMPTION
	
	public static void PR_noPREMP(FileWriter output, PrintWriter printWriter, int[][] processInfo) {
		int[][] queue = new int[processInfo.length][4];
		System.out.println("PR_noPREMP");
		printWriter.printf("PR_noPREMP\n");
		
		int timer = 0;
		int arrival = 0;
		int queueIterator = 0;
		int finish = 0;
		int toW = 0;
		
		while(processInfo[0][1] != timer) timer++;
		
		//creating the secondary array so we are not modifiying the original
		for(int i = arrival; i < processInfo.length && processInfo[i][1] <= timer; i++) {
			queue[queueIterator++] = processInfo[i];
			arrival++;
		}
		
		do {
			int timeIncrement = 0;
			int currProcess = 0;
			for(int i = 1; i < queueIterator; i++) {
				//choosing the process with the highest process among the available processes
				if(queue[currProcess][3] > queue[i][3]) {
					currProcess = i;
				}
				
			}
			System.out.printf("%d %d \n", timer, queue[currProcess][0]);
			printWriter.printf("%d %d\n", timer, queue[currProcess][0]);
			timeIncrement = queue[currProcess][2]; //setting the increment to the amount of the current processes cpu burst.
			queue[currProcess][2] = 0; //setting the current process burst to 0 after it has been completed
			finish++;
			timer += timeIncrement;
			//removing finished process from the ready queue
			queue[currProcess] = queue[queueIterator - 1];
			queueIterator--;
			
			//calculating the total wait time of the proccesses after going through the SJF algorithm
			toW += timeIncrement*queueIterator;
			
			//adding new proccess that came at later arrival time to ready queue 
			for(int i = arrival; i < processInfo.length && processInfo[i][1] <= timer; i++) {
				queue[queueIterator++] = processInfo[i];
				arrival++;
				toW += timer - processInfo[i][1];
			}
		} while (finish != processInfo.length);
		System.out.printf("AVG Waiting Time: %.2f\n", (double)toW/(processInfo.length));	
		printWriter.printf("AVG Waiting Time: %.2f\n", (double)toW/(processInfo.length));
		printWriter.close();
		}
	
	
	//PRIORITY SCHEDULING WITH PREEMPTION
	
	public static void PR_withPREMP(FileWriter output, PrintWriter printWriter, int[][] processInfo) {
		int[][] queue = new int[processInfo.length][4];
		int timer = 0;
		int arrival = 0;
		int queueIterator = 0;
		boolean choice = true;
		int current = 0;
		int toW = 0;
		int finish = 0;
		
		
		System.out.println("PR_withPREMP");
		printWriter.printf("PR_withPREMP\n");
		
		while(processInfo[0][1] != timer) timer++;
		do {
			int timeIncrement = 0;
			
			//adding processes to the readyqueue
			for(int i = arrival; i < processInfo.length && processInfo[i][1] <= timer; i++) {
				queue[queueIterator++] = processInfo[i];
				if(current != -1 && processInfo[i][3] < queue[current][3]) {
					choice = true; //if scheduler needs to make a decision, we set this to true
				}
				arrival++;
			}
			
			//if scheduler is required to make a choice
			
			if(choice) {
				current = 0;
				for(int i = 1; i < queueIterator; i++) { //checking the ready queue to determine the process with the highest priority.
					if(queue[current][3] > queue[i][3]) {
						current = i;//setting the highest priority process to the variable current.
					}
					
				}
				choice = false;
				System.out.printf("%d %d\n", timer, queue[current][0]);
				printWriter.printf("%d %d\n", timer, queue[current][0]);
			}
			//if all processes have arrived, assign current away from the array so its not iterating through it.
			if(arrival==processInfo.length || (queue[current][2] + timer) <= processInfo[arrival][1]) {
				timeIncrement = queue[current][2];
				processInfo[current][2] = 0;
				finish++;
				queue[current] = queue[queueIterator - 1];//switch
				current = -1;
				queueIterator--;
				choice = true;
			}
			//next arrival occurs before the end of the current procceses cpu burst
			else {
				timeIncrement = processInfo[arrival][1] - timer;
				if(queueIterator != 0) {
					queue[current][2] -= timeIncrement;
					
				}
				
			}
			timer += timeIncrement;
			
			for(int i = 0; i <queueIterator; i++) {
				if(i != current) {
					toW += timeIncrement;
				}
			}
			
		}
		while(finish != processInfo.length);
		System.out.printf("AVG Waiting Time: %.2f\n", (double)toW/(processInfo.length));	
		printWriter.printf("AVG Waiting Time: %.2f\n", (double)toW/(processInfo.length));
		printWriter.close();
	}
		
	
	


}
