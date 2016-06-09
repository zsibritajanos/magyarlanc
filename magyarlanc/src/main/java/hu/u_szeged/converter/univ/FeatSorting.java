package hu.u_szeged.converter.univ;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FeatSorting {

	public static void main(String[] args) {
		File file = new File("./data/SZK2.51");
		listFilesForFolder(file);
	}

	public static void listFilesForFolder(final File folder) {

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().endsWith("udfeat")) {
					featSort(fileEntry.toString());
				}
			}
		}
	}

	private static void featSort(String fileEntry) {
		BufferedReader bufferedReader = null;
		String line = null;
		StringBuffer conversion = new StringBuffer();
		System.out.println(fileEntry);
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileEntry), "UTF-8"));

			while ((line = bufferedReader.readLine()) != null) {

				if (!line.equals("")) {
					//System.out.println(line);
					String[] lineSplit = line.split("\t");
					String[] features = lineSplit[3].split("\\|");
					List<String> featlist = new ArrayList<String>();
					for (String feature : features) {
						if (!feature.endsWith("None")) {
							featlist.add(feature);
						}					
					}
					Collections.sort(featlist);

					conversion.append(lineSplit[0] + "\t" + lineSplit[1] + "\t" + 
							lineSplit[2] + "\t");
					
					if(featlist.size() > 0) {
						for (int i = 0; i < featlist.size() - 1; i++) {
							conversion.append(featlist.get(i) + "|");
						}

						conversion.append(featlist.get(featlist.size() - 1) + "\n");
					} else {
						conversion.append("_" + "\t");
					}

				} else {		
					conversion.append("\n");
				}
			}
			bufferedReader.close();
			writeIntoFile(conversion.toString(), fileEntry);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeIntoFile(String text, String fileEntry) {

		BufferedWriter writer = null;
		String file = fileEntry.toString().replace("udfeat", "ud");

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

			writer.write(text);
			writer.flush();
			writer.close();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
