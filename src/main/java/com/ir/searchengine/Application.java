package com.ir.searchengine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.ir.searchengine.analyzer.CustomAnalyzer;
import com.ir.searchengine.loaders.ContentLoader;
import com.ir.searchengine.loaders.QueryLoader;
import com.ir.searchengine.result.Result;
import com.ir.searchengine.result.ResultWriter;

/**
 * The main class that triggers the application.
 * @author amit
 */
public class Application {

	private static Logger logger = Logger.getLogger(Application.class);
	private static String scoring = "scoring";
	private static Similarity similarity = null;

	public static void main(String[] args) throws Exception {

		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		logger.info("Current relative path is: " + s);

		String indexdirType = "ramdir";
		String analyzerType = null;
		String sourceFolder = null;
		String outputFolder = null;
		Boolean printResults = new Boolean(false);
		int hitspp = 10;

		for (int i = 0; i < args.length; i++) {
			if ("-scoring".equals(args[i])) {
				scoring = args[i + 1];
				i++;
			} else if ("-indexdirType".equals(args[i])) {
				indexdirType = args[i + 1];
				i++;
			} else if ("-printResults".equals(args[i])) {
				printResults = Boolean.valueOf(args[i + 1]);
				i++;
			} else if ("-analyzer".equals(args[i])) {
				analyzerType = args[i + 1];
				i++;
			} else if ("-hitspp".equals(args[i])) {
				if (args[i + 1] != null || !args[i + 1].equals(""))
					hitspp = Integer.parseInt(args[i + 1]);
				// else default = 10
				
				i++;
			} else if ("-source".equals(args[i])) {
				if (args[i + 1] != null || !args[i + 1].equals(""))
					sourceFolder = args[i + 1];
				else
					throw new RuntimeException("Must specify source folder for CRAN.");
				i++;
			} else if ("-output".equals(args[i])) {
				if (args[i + 1] != null || !args[i + 1].equals(""))
					outputFolder = args[i + 1];
				else
					throw new RuntimeException("Output directory not present! Must specify as argument.");
				i++;
			}
		}

		// Load all the documents
		ContentLoader cl = new ContentLoader(sourceFolder);
		cl.loadContentFromFile();
		List<Document> docs = cl.getDocuments();

		Application app = new Application();
		Analyzer analyzer = app.getAnalyzer(analyzerType);
		IndexWriterConfig config = app.getIndexWriterConfig(analyzer, IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		Directory indexDir = app.getDirectory(indexdirType);

		// Load Indexes
		app.loadIndexes(indexDir, config, docs);
		logger.debug("Successfully loaded (" + docs.size() + ") documents.");

		// Instantiate the Search Engine
		SearchEngine searchEngine = new SearchEngine(indexDir, scoring);

		QueryLoader ql = new QueryLoader(sourceFolder, analyzer);
		ql.loadQueries();
		List<Query> queries = ql.getQueries();

		List<Result> results = new ArrayList<>();

		int queryCount = 1;
		for (Query query : queries) {
			ScoreDoc[] hits = searchEngine.fireQuery(query, printResults, hitspp);

			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				double score = hits[i].score;

				Result result = new Result(queryCount + "", "q0", (docId + 1) + "", (i + 1) + "", score + "", "exp");
				results.add(result);
			}

			queryCount++;
		}

		try {

			new ResultWriter(results, outputFolder).writeResults();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IndexWriterConfig getIndexWriterConfig(Analyzer analyzer, IndexWriterConfig.OpenMode openMode) throws Exception {
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(openMode);
		config.setSimilarity(Application.getSimilarity(scoring));
		return config;
	}

	public Analyzer getAnalyzer(String analyzerType) {
		Analyzer analyzer = null;
		if ("myAnalyzer".equals(analyzerType))
			analyzer = new CustomAnalyzer();
		else if ("englishAnalyzer".equals(analyzerType))
			analyzer = new EnglishAnalyzer();
		else if ("standardAnalyzer".equals(analyzerType))
			analyzer = new StandardAnalyzer();
		else {
			System.out.println("No analyzer provided. Defaulting to Standard Analyzer.");
			analyzer = new StandardAnalyzer();
		}

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

	public static Similarity getSimilarity(String scoring) throws Exception {

		if ("bm25".equals(scoring)) {
			// Checking for singleton
			if (similarity == null) {
				similarity = new BM25Similarity(1.2f, 0.75f);
				return similarity;
			} else {
				return similarity;
			}
		} else if ("tfidf".equals(scoring)) {

			if (similarity == null) {
				similarity = new ClassicSimilarity();
				return similarity;
			} else {
				return similarity;
			}
		} else {
			throw new Exception("Similarity cannot be null");
		}
	}

}
