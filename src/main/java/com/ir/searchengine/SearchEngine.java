package com.ir.searchengine;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

public class SearchEngine {
	private static Logger logger = Logger.getLogger(SearchEngine.class);

	private Directory indexDir;

	public SearchEngine(Directory indexDir) {
		this.indexDir = indexDir;

	}

	public ScoreDoc[] fireQuery(Query query, Boolean printResults) {
		try {
			// Search
			int hitsPerPage = 10;
			IndexReader reader = DirectoryReader.open(indexDir);
			IndexSearcher searcher = new IndexSearcher(reader);

			// Scoring - Similarity
			searcher.setSimilarity(Application.getSimilarity());

			TopDocs docs = searcher.search(query, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;

			// display results
			if (printResults) {
				System.out.println("Found " + hits.length + " hits.");
				System.err.print("\nRES_NO | DOCID | TITLE | SCORE\n");
				for (int i = 0; i < hits.length; ++i) {
					int docId = hits[i].doc;
					double score = hits[i].score;
					Document d = searcher.doc(docId);
					System.out.println((i + 1) + ". " + d.get("docId") + "\t" + d.get("title") + "\t" + score);
				}
			}

			// reader can only be closed when there
			// is no need to access the documents any more.
			reader.close();

			return hits;
		} catch (IOException e) {
			logger.error("Failed to execute query '" + query + "'", e);
		}

		return null;
	}
}
