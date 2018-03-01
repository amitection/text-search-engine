package com.ir.searchengine.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import com.ir.searchengine.Constants;

/** Helper class to load all the documents from the file to the main mem.
 * @author amit
 */
public class ContentLoader {

	private static Logger logger = Logger.getLogger(ContentLoader.class);

	private String filename;

	private List<Document> documents;

	private Integer corpusCount = 0;

	public ContentLoader(String filename) {
		this.filename = filename+"/cran.all.1400";
		this.documents = new ArrayList<>();
	}

	public String getFilename() {
		return filename;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public Integer getCorpusCount() {
		return corpusCount;
	}

	public void loadContentFromFile() {
		String docId = "";
		StringBuilder textAbstract = new StringBuilder();
		String authors = "";
		String bibliography = "";
		StringBuilder content = new StringBuilder();

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
					if(!".I".equals(newTag))
						line = br.readLine();
				}

				if (".I".equals(tag)) {

					// Extract the docId
					docId = line.split(" ")[1];

					corpusCount++;
					// Add the previous document to the set of documents
					if (corpusCount > 1) {
						Document doc = createDocument(docId, textAbstract.toString(), authors, bibliography, 
								content.toString());
						documents.add(doc);
					}

					// reset the previous variables
					docId = "";
					textAbstract = new StringBuilder();
					authors = "";
					bibliography = "";
					content = new StringBuilder();

				} else if (".T".equals(tag)) {
					textAbstract.append(line);
				} else if (".A".equals(tag)) {
					authors += line;
				} else if (".B".equals(tag)) {
					bibliography += line;
				} else if (".W".equals(tag)) {
					content.append(line);
				}
			}
			
			// Save the last document
			Document doc = createDocument(docId, textAbstract.toString(), authors, bibliography, 
					content.toString());
			documents.add(doc);
			
			logger.info("All Documents loaded successfully. Total documents: " + corpusCount);
		} catch (IOException e) {
			logger.error("Error loading documents. Corpus no: " + corpusCount, e);
			throw new RuntimeException(e.getMessage());
		}
	}

	private Document createDocument(String docId, String title, String authors, String bibliography,
			String content) throws IOException {

		Document doc = new Document();
		doc.add(new StringField("docId", docId, Field.Store.YES));
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new StringField("author", authors, Field.Store.YES));
		doc.add(new StringField("bibliography", bibliography, Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.YES));
		return doc;
	}

	/**
	 * Returns a tag or null if none present.
	 * 
	 * @param line
	 */
	private String getTagIfAny(String line) {

		for (String tag : Constants.CONTENT_SEPERATOR) {
			if(line.contains(tag))
				return tag;
		}
		return null;
	}

}
