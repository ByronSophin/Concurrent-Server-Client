import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
public class SuperMultiClient 
{
	public static void main(String[] args) throws InterruptedException {
		if (args.length < 2)
			return;
		
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		ArrayList<ClientHandle> temp = new ArrayList<ClientHandle>();
		try {

			int userInput = 0;
			while(true)
			{	
				Socket socket = new Socket(hostname, port);
				OutputStream output = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(output, true);
				Scanner scan = new Scanner(System.in);
				Scanner nums = new Scanner(System.in);
				System.out.println("Enter one of the following commands ");
				System.out.println("1- Host Current Date/Time");
				System.out.println("2- Host Uptime");
				System.out.println("3- Host Memory Use");
				System.out.println("4- Host Netstat");
				System.out.println("5- Host Current Users");
				System.out.println("6- Host Running Processes");
				System.out.println("7- Exit");
				System.out.println();
				userInput = scan.nextInt();

				if(userInput < 1 || userInput > 7)
				{
					System.out.println("Invalid Input");
				}
				else if(userInput == 7)
				{	
					writer.println(userInput);
					socket.close();
					scan.close();
					break;
				}
				else 
				{
					System.out.println("How many Client requests should be made?");
					int num = nums.nextInt();
					ClientHandle[] requests = new ClientHandle[num];
					for (int i = 0; i < num; i++) {
						requests[i] = new ClientHandle(hostname, port, userInput, i + 1);
						requests[i].start();
					}
					for (int i = 0; i < num; i++) {
						temp.add(requests[i]);
					}
					for(ClientHandle request : requests) {
					request.join();
					}
				}
				writer.close();
				socket.close();
				long totalTime = 0;
				for(int i = 0; i < temp.size(); i++)
				{
					totalTime = totalTime + temp.get(i).getTAT();
				}
				System.out.println("Total turn-around time: " + totalTime + "ms");
				System.out.println("Average turn-around time: " + (double)totalTime / (double)(temp.size()) + "ms");
				temp.clear();
				
			}
			

		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
		}
	}



	static class ClientHandle extends Thread {
		private String hostname;
		private int port;
		private int userInput;
		private long elapsedTime = 0;
		private int threadNumber;

		public ClientHandle(String hostname, int port, int userInput, int i) {
			this.hostname = hostname;
			this.port = port;
			this.userInput = userInput;
			this.threadNumber = i;
		}


		@Override
		public void run() {
			try (Socket socket = new Socket(hostname, port)) {
				OutputStream output = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(output, true);

				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				
					writer.println(userInput);
					long start = System.currentTimeMillis();
					String serverResponse;
					while(true)
					{
						if((serverResponse = reader.readLine()) == null)
						{
							break;
						}
						else
						{
							System.out.println(serverResponse + " from request " + threadNumber);
						}
					}
					long end = System.currentTimeMillis();
					elapsedTime = end - start;
					System.out.println("Turn-around time for request " + threadNumber + ": " + elapsedTime + " milliseconds ");
					reader.close();
					writer.close();
					socket.close();

			} catch (IOException ex) {
				System.out.println("I/O error: " + ex.getMessage());
			}
		}
		
		public long getTAT()
		{
			return elapsedTime;
		}
	}
}