package com.tracktopell.util;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * com.tracktopell.util.MinimalDateRestServiceServer
 * @author alfredo estrada
 */
public class MinimalDateRestServiceServer extends Thread{

	private Socket socket;
	public MinimalDateRestServiceServer(Socket s) {
		this.socket = s;
	}
	
	
	public static void main(String[] args) {
        int port = 1080;
        if (args.length == 1){ 
        	port = Integer.parseInt(args[0]);
		}
 
        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            System.out.println(" --------------------- MinimalDateRestServiceServer Server is listening on port " + port+ " --------------------------");
            System.out.println("");
            System.out.println("               visit    http://localhost:" + port+ "/date");
            System.out.println("");
 
            while (true) {
                Socket socket = serverSocket.accept();

				MinimalDateRestServiceServer ss= new MinimalDateRestServiceServer(socket);
				ss.start();
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

	@Override
	public void run() {
		try {
			service();
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	private void service() throws IOException {
		OutputStream os = socket.getOutputStream();
		InputStream  is = socket.getInputStream();
		
		BufferedReader br= new BufferedReader(new InputStreamReader(is));
		String line = null;
		for(line = br.readLine() ; line !=null ; line = br.readLine()){
			System.out.println("<<< ["+line+"]");
			if(line.trim().length()==0){
				break;
			}
		}
		System.out.println(">>> AFTER READING REQUEST");
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		SimpleDateFormat sdfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date now = new Date();
		byte content[]= ("{\"ISO_8601_DATETIME\":\""+sdfISO.format(now)+"\"}").getBytes();
		
		PrintWriter writer = new PrintWriter(os, true);
		System.out.println(">>> WRITING HEADERS");
		writer.println("HTTP/1.1 200 OK");
		writer.println("Date: "+sdf.format(now));
		writer.println("Server: Tracktopell-MinimalDateRestServiceServer/1.0.0 "+System.getProperty("os.name")+"-"+System.getProperty("os.version")+"@"+System.getProperty("os.arch"));
		writer.println("Last-Modified: "+sdf.format(now));
		writer.println("Content-Length: "+content.length);
		writer.println("Content-Type: application/json");
		writer.println("Connection: Closed");
		writer.println("");
		System.out.println(">>> WRITING CONTENT");
		writer.write(new String(content));
		System.out.println(">>> END SERVICE.");
		writer.close();
	}
}
