package com.ir.searchengine.result;

import com.ir.searchengine.Constants;
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

	public ResultWriter(List<Result> results) throws IOException {
		this.results = results;
		this.currentTime = System.currentTimeMillis();
		Date date = new Date();
		this.filename = Constants.PATH_TO_WRITE_RESULTS + "res_"+sdf.format(date);
		
		File file = new File(filename);
		file.createNewFile();
	}

	@SuppressWarnings({"unchecked","rawtypes"})
	public void writeResults() {
		
		try (Writer writer = Files
				.newBufferedWriter(Paths.get(filename))) {
			
			StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).withSeparator(' ')
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

}
