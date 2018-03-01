package com.ir.searchengine.analyzer;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import com.ir.searchengine.filter.WildcardFilter;

/** Analyzer developed taking the StandardAnalyzer as base.
 * @author amit
 */
public class CustomAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		
		final StandardTokenizer src = new StandardTokenizer();
		TokenStream tok = new StandardFilter(src);
		TokenFilter filter = new WildcardFilter(tok);
		filter = new LowerCaseFilter(filter);
		filter = new StopFilter(filter, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		return new TokenStreamComponents(src, filter);
	}

}
