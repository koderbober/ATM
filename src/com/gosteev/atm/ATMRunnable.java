package com.gosteev.atm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ATMRunnable implements Runnable {

	private Socket clientSocket;
	private Map<String, Map<Integer, Integer>> currenciesMap;
	
	public ATMRunnable (Map<String, Map<Integer, Integer>> currenciesMap, Socket clientSocket) {
		this.currenciesMap = currenciesMap;
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {

		try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
				true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));) {

			out.println("WELCOME");
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if (ATMService.isAdd(inputLine)) {
					ATMService.add(inputLine, currenciesMap);
					out.println("OK");
				} else if (ATMService.isWithdraw(inputLine, currenciesMap)) {
					currenciesMap = ATMService
							.withdraw(inputLine, currenciesMap);
					out.println("OK");
				} else if (inputLine.equals("?")) {
					ATMService.print(currenciesMap, out);
					out.println("OK");
				} else if (inputLine.equals("EXIT")) {
					out.println("BYE");
					clientSocket.close();
					break;
				} else {
					out.println("ERROR");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
