package com.ir.searchengine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.ir.searchengine.content.ContentLoader;

public class Application {

	private static Logger logger = Logger.getLogger(Application.class);
	
	public static void main(String[] args) {
		
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		logger.info("Current relative path is: " + s);
		
		ContentLoader cl = new ContentLoader(Constants.PATH_TO_CONTENT);
		cl.loadContent();
		List<Document> docs = cl.getDocuments();
		
		logger.debug("Successfully loaded ("+docs.size()+") documents.");
		
		Directory indexDir = new RAMDirectory();

		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		
		// creating the index
		try (IndexWriter indexWriter = new IndexWriter(indexDir, config)) {
			indexWriter.addDocuments(docs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		fireQuery(analyzer, indexDir, "destalling");
	}
	
	private static void fireQuery(StandardAnalyzer analyzer, Directory indexDir, String query) {
		try {
			// the "title" arg specifies the default field to use
			// when no field is explicitly specified in the query.

			Query q = new QueryParser("content", analyzer).parse(query);

			// 3. search
			int hitsPerPage = 10;
			IndexReader reader = DirectoryReader.open(indexDir);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs docs = searcher.search(q, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;

			// 4. display results
			System.out.println("Found " + hits.length + " hits.");
			System.err.print("\n\nRES_NO | DOCID | TITLE");
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				System.out.println((i + 1) + ". " + d.get("docId") + "\t" + d.get("title"));
			}

			// reader can only be closed when there
			// is no need to access the documents any more.
			reader.close();
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
