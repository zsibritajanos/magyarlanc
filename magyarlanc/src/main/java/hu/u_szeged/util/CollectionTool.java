package hu.u_szeged.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CollectionTool {

	public static <T> void incValueInMap(Map<T, Integer> map, T key, int value) {
		if (map.containsKey(key)) {
			map.put(key, map.get(key) + value);
		} else {
			map.put(key, value);
		}
	}

	public static <T> void incValueInMap(Map<T, Integer> map, T key) {
		incValueInMap(map, key, 1);
	}

	public static <T> void incValueInMapDouble(Map<T, Double> map, T key,
			double value) {
		if (map.containsKey(key)) {
			map.put(key, map.get(key) + value);
		} else {
			map.put(key, value);
		}
	}

	public static <T> void incValueInMapDouble(Map<T, Double> map, T key) {
		incValueInMapDouble(map, key, 1);
	}

	public static <K, V> V addKey(Map<K, V> map, K key, V defaulValue) {
		if (!map.containsKey(key)) {
			map.put(key, defaulValue);
		}
		return map.get(key);
	}

	public static <T> void normalizeMap(Map<T, Double> map) {
		double sum = 0.0;
		for (Entry<T, Double> ent : map.entrySet()) {
			sum += ent.getValue();
		}

		for (Entry<T, Double> ent : map.entrySet()) {
			map.put(ent.getKey(), ent.getValue() / sum);
		}
	}

	public static <K, V> void WriteMap(Map<K, V> map, OutputStream stream)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				stream));
		for (Entry<K, V> ent : map.entrySet()) {
			writer.write(ent.getKey() + "\t" + ent.getValue() + "\n");
		}
		writer.flush();
	}

	public static <T> List<List<T>> getPartitionsByNumOfPart(
			List<T> originalList, int numOfPart) {
		int[] partArr = new int[numOfPart];
		int baseSize = originalList.size() / numOfPart;
		int mod = originalList.size() % numOfPart;
		for (int i = 0; i < numOfPart; i++) {
			partArr[i] = baseSize;
			if (i < mod) {
				partArr[i]++;
			}
		}

		List<List<T>> partitions = new LinkedList<List<T>>();
		int sum = 0;
		for (int i = 0; i < numOfPart; i++) {
			partitions.add(originalList.subList(sum, sum + partArr[i]));
			sum += partArr[i];
		}
		return partitions;
	}

	public static <T> List<List<T>> getPartitionsByPartSize(
			List<T> originalList, int partitionSize) {
		List<List<T>> partitions = new LinkedList<List<T>>();
		for (int i = 0; i < originalList.size(); i += partitionSize) {
			partitions.add(originalList.subList(i,
					i + Math.min(partitionSize, originalList.size() - i)));
		}
		return partitions;
	}
}
