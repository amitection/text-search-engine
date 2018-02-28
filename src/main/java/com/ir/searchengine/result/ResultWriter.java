package com.ir.searchengine.result;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ResultWriter {

	private static final DateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
	
	List<Result> results;
	
	long currentTime;
	
	String filename;

	public ResultWriter(List<Result> results, String outputDir) throws IOException {
		File dir = new File(outputDir);
		createDirIfNotExists(dir);
		
		this.results = results;
		this.currentTime = System.currentTimeMillis();
		Date date = new Date();
		this.filename = outputDir + "/res_"+sdf.format(date);
		
		File file = new File(filename);
		file.createNewFile();
	}

	@SuppressWarnings({"unchecked","rawtypes"})
	public void writeResults() {
		
		try (Writer writer = Files
				.newBufferedWriter(Paths.get(filename))) {
			
			StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
					.withSeparator(' ')
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).build();

			beanToCsv.write(results);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (CsvDataTypeMismatchException e) {
			e.printStackTrace();
		} catch (CsvRequiredFieldEmptyException e) {
			e.printStackTrace();
		}
	}
	
	private void createDirIfNotExists(File dir) {
		if (!dir.exists()) {
		    System.out.println("Creating directory for Results file: " + dir.getName());
		    boolean result = false;

		    	dir.mkdir();
		    	result = true;

		    	if(result) {    
		        System.out.println("DIR created");  
		    }
		}
	}

}
