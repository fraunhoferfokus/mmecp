package de.fhg.fokus.streetlife.mmecp;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin van Bernum (bke) on 08.09.2014.
 * Copyright (c) 2014 Fraunhofer FOKUS
 */

public class ConverterCommand {

	@Parameter(names = {"-s", "--schema"},
			   description = "File path to rule converter schema (*.rcs).",
			   required = true)
	public String schemaPath;

	@Parameter(names = {"-p", "--pmml"},
			   description = "PMML file to convert.\n       Usage: ... -p [file1] -p [file2] ...",
			   required = true)
	public List<String> pmmlPathList = new ArrayList<String>();

	@Parameter(names = {"-o", "--output"},
			   description = "File path for converted rules.",
			   required = true)
	public String outputPath;

}
