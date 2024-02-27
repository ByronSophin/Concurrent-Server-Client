import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConcurrentS {

    public static void main(String[] args) {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            System.out.println("Press ctrl + c to close server: ");
            clientmayshutdown exit = new clientmayshutdown(serverSocket);
            Runtime r = Runtime.getRuntime();
            r.addShutdownHook(exit);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connection Successful with user: " + clientSocket.getInetAddress());

                    new ClientReqHandle(clientSocket).start();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
					break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    static class ClientReqHandle extends Thread {
        private Socket clientSocket;

        public ClientReqHandle(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
        	try {
            	InputStream input = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                String text;
                int userInput = 0;

                do {
                    text = reader.readLine();
                    if (text == null) {
                        break;
                    }
                    userInput = Integer.parseInt(text);
                    switch (userInput) {
                        case 1:
                            Date date = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/YYYY HH:mm");
                            String dateTime = format.format(date);
                            writer.println("Date and time for server: " + dateTime);
                            reader.close();
        					writer.close();
                            break;
                        case 2:
                        	String uptime = "";
                        	String line;
                        	Process proc = Runtime.getRuntime().exec("uptime");
                        	BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                        	line = stdInput.readLine();
                        	writer.println("Uptime: " + line);
                        	reader.close();
        					writer.close();
        					stdInput.close();
                            break;
                        case 3:
                        	Process proc3 = Runtime.getRuntime().exec("free --mega");
                        	BufferedReader stdInput3 = new BufferedReader(new InputStreamReader(proc3.getInputStream()));
                        	String line2 = stdInput3.readLine();
                        	String line3 = stdInput3.readLine();
                        	writer.println("(Megabytes)" + line2);
                        	writer.println("(Megabytes)" + line3);
                        	reader.close();
        					writer.close();
        					stdInput3.close();
                            break;
                        case 4:
                        	Process proc4 = Runtime.getRuntime().exec("netstat -a");
							BufferedReader stdInput4 = new BufferedReader(new InputStreamReader(proc4.getInputStream()));
							String line4;
							writer.println("Netstat: \n");
							while (true)
							{	
								line4 = stdInput4.readLine(); 
								if(line4 == null)
								{
									break;
								}
								else
								{
									writer.println(line4);
								}
							}
							reader.close();
							writer.close();
							stdInput4.close();
                            break;
                        case 5:
                        	Process proc5 = Runtime.getRuntime().exec("who");
							BufferedReader stdInput5 = new BufferedReader(new InputStreamReader(proc5.getInputStream()));
							String line5;
							writer.println("Users: ");
							while (true)
							{	
								line5 = stdInput5.readLine(); 
								if(line5 == null)
								{
									break;
								}
								else
								{
									writer.println(line5);
								}
							}
							reader.close();
							writer.close();
							stdInput5.close();
                            break;
                        case 6:
                        	Process proc6 = Runtime.getRuntime().exec("ps -aux");
							BufferedReader stdInput6 = new BufferedReader(new InputStreamReader(proc6.getInputStream()));
							String line6;
							writer.println("Users: \n");
							while (true)
							{	
								line6 = stdInput6.readLine(); 
								if(line6 == null)
								{
									break;
								}
								else
								{
									writer.println(line6);
								}
							}
							reader.close();
							writer.close();
							stdInput6.close();
                            break;
                        case 7:
                        	reader.close();
        					writer.close();
                            clientSocket.close();
                            System.out.println("Disconnected with Client");
                            break;
                        default:

                            break;
                    }

                } while (userInput != 7);
            } catch (IOException ex) {
            	System.out.println("Server instance is closing");
            } finally {
              try 
              	{
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e.getMessage());
                }
            }
        }
        }

    static class clientmayshutdown extends Thread {
        ServerSocket serverSocket;

        clientmayshutdown(ServerSocket server) {
            this.serverSocket = server;
        }

        public void run() {
            System.out.println("Server is closing...");
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
