package hu.u_szeged.pos.mainpartofspeech;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainPartOfSpeech {
	static Pattern pattern = Pattern.compile("(.*@)(.*)");

	public static String readTrain(String file, String encoding, String out) {

		BufferedReader reader = null;
		Writer writer = null;
		String line = null;

		String[] splitted = null;
		Matcher matcher = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(
			    file), encoding));

			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
			    out), encoding));

			while ((line = reader.readLine()) != null) {
				splitted = line.split(" ");

				for (int i = 0; i < splitted.length; ++i) {

					matcher = pattern.matcher(splitted[i]);
					if (matcher.matches()) {
						splitted[i] = matcher.group(1) + matcher.group(2).charAt(0);
					} else {
						System.err.println(splitted[i]);
					}
				}
				
				for (String s : splitted) {
					writer.write(s + " ");
				}
				writer.write("\n");

			}
			reader.close();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		readTrain("d:/szeged.pos.train", "UTF-8", "d:/szeged.pos.train.main");
	}
}
