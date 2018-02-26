package com.ir.searchengine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.ir.searchengine.analyzer.CustomAnalyzer;
import com.ir.searchengine.loaders.ContentLoader;
import com.ir.searchengine.loaders.QueryLoader;

public class Application {

	private static Logger logger = Logger.getLogger(Application.class);
	private static String scoring = "scoring";
	private static BM25Similarity bm25 = null;

	public static void main(String[] args) throws ParseException {
		
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		logger.info("Current relative path is: " + s);

		String indexdirType = "indexdirType";
		Boolean printResults = new Boolean(false);

		for (int i = 0; i < args.length; i++) {
			if ("-scoring".equals(args[i])) {
				scoring = args[i + 1];
				i++;
			} 
			else if ("-indexdirType".equals(args[i])) {
				indexdirType = args[i + 1];
				i++;
			}
			else if ("-printResults".equals(args[i])) {
				printResults = Boolean.valueOf(args[i + 1]);
				i++;
			}
		}
		
		// Load all the documents
		ContentLoader cl = new ContentLoader(Constants.PATH_TO_CONTENT);
		cl.loadContentFromFile();
		List<Document> docs = cl.getDocuments();

		
		Application app = new Application();
		Analyzer analyzer = app.getAnalyzer();
		IndexWriterConfig config = app.getIndexWriterConfig(analyzer, IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		Directory indexDir = app.getDirectory(indexdirType);
		
		// Load Indexes
		app.loadIndexes(indexDir, config, docs);
		logger.debug("Successfully loaded (" + docs.size() + ") documents.");
		
		// Instantiate the Search Engine
		SearchEngine searchEngine = new SearchEngine(indexDir);
		
		QueryLoader ql = new QueryLoader(Constants.PATH_TO_QUERIES, analyzer);
		ql.loadQueries();
		List<Query> queries = ql.getQueries();
		
		int queryCount = 1;
		double[] averagePrecisions = new double[queries.size()];
		for(Query query : queries) {
			System.out.println("\n\nQuery No: "+queryCount);
			ScoreDoc[] hits = searchEngine.fireQuery(query, printResults);
			queryCount++;
		}
		
//		Query query = ql.createQuery("what (the ?transverse\n" + 
//				"curvature  effect) .");
//		searchEngine.fireQuery(query, printResults);
		
	}
	
	
	public IndexWriterConfig getIndexWriterConfig(Analyzer analyzer, IndexWriterConfig.OpenMode openMode) {
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(openMode);
		config.setSimilarity(Application.getSimilarity());
		return config;
	}
	
	public Analyzer getAnalyzer() {
		CustomAnalyzer analyzer = new CustomAnalyzer();
		return analyzer;
	}
	
	public Directory getDirectory(String directory) {
		Directory indexDir = null;
		if ("ramdir".equals(directory)) {
			indexDir = new RAMDirectory();
		}
		
		return indexDir;
	}
	
	public void loadIndexes(Directory indexDir, IndexWriterConfig config, List<Document> docs) {
		try (IndexWriter indexWriter = new IndexWriter(indexDir, config)) {
			indexWriter.addDocuments(docs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Similarity getSimilarity() {
		
		if ("bm25".equals(scoring)) {
			// Checking for singleton
			if(bm25 == null) {
				bm25 = new BM25Similarity(1.2f, 0.75f);
				return bm25;
			}
			else {
				return bm25;
			}
		} else if ("vectorspace".equals(scoring)) {
			//TODO
			return null;
		}
		return null;
	}
	
}
