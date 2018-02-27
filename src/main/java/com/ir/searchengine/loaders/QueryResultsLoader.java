package com.ir.searchengine.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class QueryResultsLoader {

	private static Logger logger = Logger.getLogger(QueryResultsLoader.class);

	Map<Integer, List<Integer[]>> queryResults;

	private String filename;

	public QueryResultsLoader(String filename) {
		this.filename = filename;
		this.queryResults = new HashMap<>();

		loadResults();
	}
	
	public Integer[] getRelevantDocsForQuery(int queryId, int relevanceBoundary) {
		List<Integer[]> docs = queryResults.get(queryId);
		List<Integer> relevantDocsList = new ArrayList<>();
		for(Integer[] doc : docs) {
			
			// If lies in the lower range of relevancy then doc is more relevant
			if(doc[1] <= relevanceBoundary && relevanceBoundary >= 0) {
				relevantDocsList.add(doc[0]);
			}
		}
		
		return relevantDocsList.toArray(new Integer[relevantDocsList.size()]);
	}

	/**
	 * Appends results in a HashMap where key = queryId and value is an
	 * Integer array. Integer[0] = docId and Integer[1] = relevancy
	 */
	private void loadResults() {
		
		File file = new File(this.filename);

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;

			while ((line = br.readLine()) != null) {

				String[] lineSplit = line.split(" ");
				Integer queryId = Integer.parseInt(lineSplit[0]);
				int docId = Integer.parseInt(lineSplit[1]);
				int relevancy = Integer.parseInt(lineSplit[2]);

				// if not present then create a new entry
				if (queryResults.get(queryId) == null) {
					List<Integer[]> temp = new ArrayList<>();
					temp.add(new Integer[] { docId, relevancy });
					queryResults.put(queryId, temp);
				} else {
					// else append to the existing entry
					queryResults.get(queryId).add(new Integer[] { docId, relevancy });
				}

			}
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException(e.getMessage());
		}

	}
}
