package com.gosteev.atm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ATMServer {

	public static void main(String[] args) throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(5000);) {
			Map<String, Map<Integer, Integer>> currenciesMap = new HashMap<>();

			while (true) {
				Socket clientSocket = serverSocket.accept();
				Thread thread = new Thread(new ATMRunnable(currenciesMap,
						clientSocket));
				thread.start();
			}
		}
	}

}
