package com.gosteev.atm;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Currency;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class ATMService {

	public static void print(Map<String, Map<Integer, Integer>> currenciesMap,
			PrintWriter out) {
		// Set<String> keySet = goodsMap.keySet();
		// for (String each : keySet) {
		// Integer count = goodsMap.get(each);
		// System.out.print(each + "=" + count + " ");
		// }
		Set<String> keySet = currenciesMap.keySet();
		for (String currency : keySet) {
			Map<Integer, Integer> nominalsMap = currenciesMap.get(currency);
			Set<Integer> keySet2 = nominalsMap.keySet();
			for (Integer nominal : keySet2) {
				int count = nominalsMap.get(nominal);
				if (count != 0) {
					out.println(currency + " " + nominal + " " + count);
				} 
			}
		}
	}

	public static boolean isAdd(String inputLine) {
		String[] arguments = inputLine.split(" ");

		if (arguments.length != 4) {
			return false;
		}

		String operation = arguments[0];
		if (!operation.equals("+")) {
			return false;
		}

		String currency = arguments[1];
		try {
			Currency.getInstance(currency);
		} catch (IllegalArgumentException e) {
			return false;
		}

		// 1 5 10 100 50 500
		String nominalText = arguments[2];
		try {
			int nominal = Integer.parseInt(nominalText);
			if (nominal != 1 && nominal != 5 && nominal != 10 && nominal != 50
					&& nominal != 100 && nominal != 500) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}

		String countText = arguments[3];
		try {
			int count = Integer.parseInt(countText);
			if (count <= 0) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	public static boolean isWithdraw(String inputLine,
			Map<String, Map<Integer, Integer>> currenciesMap) {
		String[] arguments = inputLine.split(" ");

		if (arguments.length != 3) {
			return false;
		}

		String operation = arguments[0];
		if (!operation.equals("-")) {
			return false;
		}

		String currency = arguments[1];
		try {
			Currency.getInstance(currency);
		} catch (IllegalArgumentException e) {
			return false;
		}

		String amountText = arguments[2];
		int amount = 0;
		try {
			amount = Integer.parseInt(amountText);
			if (amount <= 0) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}

		// мапа с текущими номиналами и количеством этих номиналов (купюр)
		Map<Integer, Integer> nominalsMap = currenciesMap.get(currency);

		Set<Integer> nominalsSet = nominalsMap.keySet();
		for (int nominal : nominalsSet) {
			int notesCountInATM = nominalsMap.get(nominal);
			if (notesCountInATM > 0) {
				int count = amount / nominal;
				if (count > notesCountInATM) {
					count = notesCountInATM;
				}
				amount = amount - count * nominal;
				if (amount == 0) {
					return true;
				}
			}
		}

		return false;
	}

	public static void add(String inputLine,
			Map<String, Map<Integer, Integer>> currenciesMap) {
		String[] arguments = inputLine.split(" ");
		String currency = arguments[1];
		int nominal = Integer.parseInt(arguments[2]);
		int count = Integer.parseInt(arguments[3]);

		if (!currenciesMap.containsKey(currency)) {
			SortedMap<Integer, Integer> nominalsMap = new TreeMap<>(Collections.reverseOrder());
			nominalsMap.put(500, 0);
			nominalsMap.put(100, 0);
			nominalsMap.put(50, 0);
			nominalsMap.put(10, 0);
			nominalsMap.put(5, 0);
			nominalsMap.put(1, 0);

			currenciesMap.put(currency, nominalsMap);
		}

		Map<Integer, Integer> nominalsMap = currenciesMap.get(currency);
		int previousAndCurrentCount = count + nominalsMap.get(nominal);
		nominalsMap.put(nominal, previousAndCurrentCount);
	}

	public static Map<String, Map<Integer, Integer>> withdraw(
			String inputLine, Map<String, Map<Integer, Integer>> currenciesMap) {
		String[] arguments = inputLine.split(" ");
		String currency = arguments[1];
		String amountText = arguments[2];
		int amount = Integer.parseInt(amountText);
		// мапа с текущими номиналами и количеством этих номиналов (купюр)
		Map<Integer, Integer> nominalsMap = currenciesMap.get(currency);

		Set<Integer> nominalsSet = nominalsMap.keySet();
		for (int nominal : nominalsSet) {
			int notesCountInATM = nominalsMap.get(nominal);
			if (notesCountInATM > 0) {
				int count = amount / nominal;
				if (count > notesCountInATM) {
					count = notesCountInATM;
				}
				amount = amount - count * nominal;
				int countToWithdraw = nominalsMap.get(nominal) - count;
				nominalsMap.put(nominal, countToWithdraw);
				if (amount == 0) {
					return currenciesMap;
				}
			}
		}

		return currenciesMap;
	}

}
