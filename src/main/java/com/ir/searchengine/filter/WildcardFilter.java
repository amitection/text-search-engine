package com.ir.searchengine.filter;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * Class that processes wildcards from the input document. Although this doesn't work because filters
 * are called at a later stage than processing wildcards in lucene.<br /><br />
 * Ref - https://www.toptal.com/database/full-text-search-of-dialogues-with-apache-lucene
 * @author amit
 */
public class WildcardFilter extends TokenFilter {

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	
	private final char QUESTION_WILDCARD = '?';
	private boolean hasWildcard = false;

	public WildcardFilter(TokenStream input) {
		super(input);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			char[] buffer = termAtt.buffer();
			for(int i = 0; i <buffer.length; i++) {
				if(buffer[i] == QUESTION_WILDCARD) {
					buffer[i] = ' ';
					hasWildcard = true;
				}
			}
			
			if(hasWildcard) {
				hasWildcard = false;
				termAtt.copyBuffer(buffer, 0, buffer.length);
			}
			
			return true;
		} else
			return false;
	}

}
