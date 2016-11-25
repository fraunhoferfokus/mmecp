/**
 * Created by Kevin van Bernum (bke) on 04.08.2014.
 * Copyright (c) 2014 Fraunhofer FOKUS
 */
package de.fhg.fokus.streetlife.mmecp;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.PrintWriter;

public class ConverterMain {

	public static void main(String[] args) {
		ConverterCommand cc = new ConverterCommand();
		JCommander jcom = new JCommander(cc);
		jcom.setProgramName("java -jar rule_converter.jar");

		try {
			jcom.parse(args);
		} catch(ParameterException pe){
			System.out.println("Wrong Usage!");
			jcom.usage();
			System.exit(-1);
		}

		try {
			Converter myConverter = new Converter(cc.schemaPath);
			PrintWriter out = new PrintWriter(cc.outputPath);
			out.write(myConverter.convert(cc.pmmlPathList));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		} finally {
			System.out.println("Success!\n  Converted rules written to " + cc.outputPath + "!");
			System.exit(0);
		}
	}
}
