package com.kc.kafkaTwitter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Sample {

	// 3,5,10-->5,5,5,5
	// 10,10
	// 10,5,5
	// 3,3,3,3,3,5

	// 4,5,3,0,5,-4,9
	public static void main(String[] args) {
		int[] arr = new int[] { 3, 5, 10 };
		int num = 20;

		int[] arr1 = new int[] { -2, 1, -3, 4, -1, 2, 1, -5, 4 };
		int maxSum = 0;
		int currentSum = 0;
		int endIndex = 0;
		int begin = 0;
		for (int i = 0; i < arr1.length; i++) {
			currentSum = currentSum + arr1[i];

			if (currentSum < 0) {
				currentSum = 0;
				begin = i + 1;
			} else if (currentSum > maxSum) {
				maxSum = currentSum;
				// startIndex=begin; endIndex = i; }
				endIndex = i;

			}
		}
		System.out.println(maxSum);
		System.out.println("Start:" + begin + " End:" + endIndex);

		findCombination(arr, 3);
		System.out.println(count(arr, arr.length, num));
	}

	public static void findCombination(int[] arr, int num) {
		int[] tab = new int[num + 1];
		Arrays.fill(tab, 0);
		tab[0] = 1;

		for (int a : arr) {
			for (int b = a; b < num + 1; b++) {
				tab[b] += tab[b - a];
			}
		}

		System.out.println(Arrays.toString(tab));
		System.out.println(tab[num]);

	}

	static int count(int S[], int m, int n) {
		if (n == 0)
			return 1;

		if (n < 0)
			return 0;

		if (m < 1 && n > 0)
			return 0;

		return count(S, m - 1, n) + count(S, m, n - S[m - 1]);
	}

	public static void findPivotRotation(int[] arr) {

	}

}
