package org.icetests;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetTest {

	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(4242);
		while(true) {
			System.out.println("Accepting");
			Socket s = ss.accept();
			System.out.println("Accepted");
			OutputStream o = s.getOutputStream();
			System.out.println("Sending");
			o.write("192.168.91.4:4300".getBytes("US-ASCII"));
			System.out.println("Sent");
			o.flush();
			o.close();
			System.out.println("Closing");
		}
	}
}
