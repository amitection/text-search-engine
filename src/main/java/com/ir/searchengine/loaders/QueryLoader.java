package com.ir.searchengine.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

import com.ir.searchengine.Constants;

public class QueryLoader {

	private static Logger logger = Logger.getLogger(QueryLoader.class);

	private String filename;

	private List<Query> queries;

	private Integer queryCount = 0;

	private Analyzer analyzer;
	
	private QueryParser qp;

	public QueryLoader(String filename, Analyzer analyzer) {
		this.qp = new QueryParser("content", analyzer);
		this.qp.setAllowLeadingWildcard(true);
		
		this.filename = filename;
		this.analyzer = analyzer;
		
		this.queries = new ArrayList<>();
	}

	public List<Query> getQueries() {
		return queries;
	}

	public Integer getQueryCount() {
		return queryCount;
	}

	public void loadQueries() {
		String queryId = "";
		StringBuilder queryStr = new StringBuilder();

		String tag = ".I";

		File file = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {

				// Returns a new tag if found else null
				String newTag = getTagIfAny(line);
				if (newTag != null) {
					tag = newTag;

					// progress to the next line where the actual content is
					if (!".I".equals(newTag))
						line = br.readLine();
				}

				if (".I".equals(tag)) {

					// Extract the docId
					queryId = line.split(" ")[1];

					queryCount++;
					// Add the previous document to the set of documents
					if (queryCount > 1) {
						Query query = createQuery(queryId, queryStr.toString());
						queries.add(query);
					}

					// reset the previous variables
					queryId = "";
					queryStr = new StringBuilder();

				} else if (".W".equals(tag)) {
//					if(line.contains("?")) {
//						line = line.replace('?', ' ');
//					}
					queryStr.append(line);
				}
			}

			// Save the last query
			Query query = createQuery(queryId, queryStr.toString());
			queries.add(query);

			logger.info("All Queries loaded successfully. Total queries: " + queryCount);
		} catch (IOException | ParseException e) {
			logger.error("Error loading queries. Query no: " + queryCount, e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public Query createQuery(String queryStr) throws ParseException {
		Query q = this.qp.parse(queryStr);
		return q;
	}

	private Query createQuery(String queryId, String queryStr) throws IOException, ParseException {
		// Query for each field and combine them into a boolean OR query
		Query q = this.qp.parse(queryStr);
		return q;
	}

	/**
	 * Returns a tag or null if none present.
	 * 
	 * @param line
	 */
	private String getTagIfAny(String line) {

		for (String tag : Constants.CONTENT_SEPERATOR) {
			if (line.contains(tag))
				return tag;
		}
		return null;
	}
	
}
