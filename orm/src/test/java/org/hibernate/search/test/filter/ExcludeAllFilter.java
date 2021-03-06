/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat, Inc. and/or its affiliates or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat, Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.search.test.filter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SegmentReader;
import org.hibernate.search.exception.SearchException;
import org.hibernate.search.filter.impl.AndDocIdSet;

/**
 * @author Emmanuel Bernard
 * @author Sanne Grinovero
 */
public class ExcludeAllFilter extends Filter implements Serializable {

	// ugly but useful for test purposes
	private static final Map<IndexReader,IndexReader> invokedOnReaders = new ConcurrentHashMap<IndexReader,IndexReader>();

	@Override
	public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
		AtomicReader reader = context.reader();
		verifyItsAReadOnlySegmentReader( reader );
		final IndexReader previousValue = invokedOnReaders.put( reader, reader );
		if ( previousValue != null ) {
			throw new IllegalStateException( "Called twice" );
		}
		return AndDocIdSet.EMPTY_DOCIDSET;
	}

	public static void verifyItsAReadOnlySegmentReader(IndexReader reader) {
		if ( ! ( reader instanceof SegmentReader ) ) {
			throw new SearchException( "test failed: we should receive subreaders" );
		}
	}

}
