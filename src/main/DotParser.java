package main;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DotParser {
	
	/**
	 * Does the changes described in chapter 3 of the bachelor thesis.
	 * @param inputPath The path of the input file.
	 * @param outputPath The path of the output file.
	 */
	public static void adapt(String inputPath, String outputPath){
		String content = readFile(inputPath);
		content = parse(content);
		writeFile(content, outputPath);
	}
	
	/**
	 * Converts a file into a string in order to be parsed
	 * @param inputPath The path of the file to be converted
	 * @return A string representing the content of the file.
	 */
	private static String readFile(String inputPath){
		String result = null;
        DataInputStream in = null;

		try {
            File f = new File(inputPath);
            byte[] buffer = new byte[(int) f.length()];
            in = new DataInputStream(new FileInputStream(f));
            in.readFully(buffer);
            result = new String(buffer);
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
            try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return result;	}
	
	/**
	 * Writes into a file.
	 * @param content A string representing content to be written on the file.
	 * @param outputPath The path of the file to be overwritten. If there is not file, then a new file is created.
	 */
	private static void writeFile(String content, String outputPath){		
		try {	
			FileOutputStream fout;
			fout = new FileOutputStream (outputPath);
			new PrintStream(fout).println (content);		
			fout.close();		    
		} catch (IOException e) {
			System.err.println ("Error: could not write to file.");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	public static String parse (String content){
		String result = new String(content);
		result = parse1(result);
		result = parse2(result);
		return result;
	}
	
	/**
	 * Does the changes described in Section 3.1 of the bachelor thesis.
	 * @param content A string representing the content of the file to be adapted.
	 * @return A string with the first stage of the changes made.
	 */
	private static String parse1(String content){
		String result = new String(content);

		String newLine = System.getProperty("line.separator");
		
		result = result.replaceFirst(
				"\tgraph \\[" + newLine+
				"\t\tfontsize = \"14\"" + newLine +
				"\t\tfontname = \"Times-Roman\"" + newLine+
				"\t\tfontcolor = \"black\"" + newLine +
				"\t\tnodesep = \"1.5\"" + newLine +
				"\t\tranksep = \"1.5\"" + newLine +
				"\t\\]" + newLine+
				"\tnode \\[" + newLine+
				"\t\tfontsize = \"14\"" + newLine +
				"\t\tfontname = \"Times-Roman\"" + newLine +
				"\t\tfontcolor = \"black\"" + newLine +
				"\t\tshape = \"ellipse\"" + newLine +
				"\t\tstyle = \"solid\"" + newLine +
				"\t\\]" + newLine +
				"\tedge \\[" + newLine +
				"\t\tfontsize = \"14\"" + newLine +
				"\t\tfontname = \"Times-Roman\"" + newLine +
				"\t\tfontcolor = \"black\"" + newLine +
				"\t\tstyle = \"solid\"" + newLine +
				"\t\\]" + newLine, "");

		result = result.replace(
				"\t\tfontsize = \"12\"" + newLine +
				"\t\tfontname = \"Times-Roman\"" + newLine +
				"\t\tfontcolor = \"black\"" + newLine
				, "");
		
		result = result.replaceAll("\t\tcolor = \"#\\w+\"" + newLine, "");
		result = result.replaceAll("\t\tshape = \"\\w+\"" + newLine, "");
		
		result = result.replace("\t\tpenwidth = \"2\"" + newLine, "");
		result = result.replace("\t\tlabel = \"root\"" + newLine, "");
		result= result.replace("\t\tcolor = \"black\"" + newLine, "");		
		result = result.replace("\"root\"", "root");
		result = result.replaceFirst("style = \"solid\"", "style = \"invis\"");
		result = result.replace("\t\tstyle = \"solid\"" + newLine, "");
		result= result.replace("\t\tstyle = \"dotted\"" + newLine, "");
		
		Integer i = 0;
		while (result.contains("\"" + i + "\"")){
			result = result.replace("\"" + i + "\"", i.toString());
			i++;
		}
		
		result = result.replaceFirst("\"INITIALISATION.+\"", "\"\"");
		
		return result;
	}


	/**
	 * Does the changes described in Section 3.2 of the bachelor thesis.
	 * @param content A string representing the content of the file to be adapted.
	 * @return A string with the second stage of the changes made.
	 */
	private static String parse2(String content) {
		String result = new String(content);

		String newLine = System.getProperty("line.separator");
		
		Pattern pattern1 = Pattern.compile(
				"\t\\d+ -> \\d+ \\[" + newLine
				+ "\t\tlabel = \"[a-z_A-Z][\\w\\(\\)]+\"" + newLine
				+ "\t\\]");
		Matcher matcher1 = pattern1.matcher(result);
		
		while (matcher1.find()){
			String s1 = matcher1.group(0);

			Pattern pattern2 = Pattern.compile(
					"\t\\d+ -> \\d+ \\[" + newLine
					+ "\t\tlabel = \"");
			Matcher m2 = pattern2.matcher(s1);
			m2.find();
			String s2 = m2.group(0);

			Pattern p3 = Pattern.compile("\".+\"" + newLine + "\t\\]");			
			Matcher m3 = p3.matcher(s1);
			m3.find();
			String s3 = m3.group(0);
			s3 = s3.substring(1);
			
			String substitute = s2 + "?" + s3;
						
			result = result.replace(s1, substitute);
			
		}
		
		Integer k = 1;		
		pattern1 = Pattern.compile(
				"\t\\d+ -> \\d+ \\[" + newLine
				+ "\t\tlabel = \"[a-z_A-Z][\\w,\\(\\)]+-->[\\w]+\"" + newLine
				+ "\t\\]");
		matcher1 = pattern1.matcher(result);
		while(matcher1.find()){
			String s0 = matcher1.group(0);
						
			Pattern p2 = Pattern.compile("\\d+");
			Matcher m2 = p2.matcher(s0);
			
			m2.find();
			Integer origin = Integer.parseInt(m2.group(0));
			
			m2.find();
			Integer destiny = Integer.parseInt(m2.group(0));

			Pattern p3 = Pattern.compile("\".+-->");
			Matcher m3 = p3.matcher(s0);
			m3.find(); //the pattern found is the input of the system
			String input = m3.group(0);
			input = input.substring(1, input.length() -3);
			
			Pattern p4 = Pattern.compile("[\\w]+\"");
			Matcher m4 = p4.matcher(s0);
			m4.find(); //The pattern found is the output of the system
			String output = m4.group(0);
			output = output.substring(0, output.length() -1 );

			String substitute = 
					"\t" + origin + " -> i" + k + " [" + newLine+
					"\t\tlabel = \"?" + input + "\"" + newLine +
					"\t]" + newLine +
					"\ti" + k + " -> " + destiny + " [ " + newLine +
					"\t\tlabel = \"!" + output + "\"" + newLine+
					"\t]";
		
			result = result.replace(s0, substitute);
			k++;
		}
		
		return result;
	}
}