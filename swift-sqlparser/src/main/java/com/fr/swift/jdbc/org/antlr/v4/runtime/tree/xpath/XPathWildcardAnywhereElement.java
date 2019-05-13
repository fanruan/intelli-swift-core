package com.fr.swift.jdbc.org.antlr.v4.runtime.tree.xpath;

import com.fr.swift.jdbc.org.antlr.v4.runtime.tree.ParseTree;
import com.fr.swift.jdbc.org.antlr.v4.runtime.tree.Trees;

import java.util.ArrayList;
import java.util.Collection;

public class XPathWildcardAnywhereElement extends XPathElement {
	public XPathWildcardAnywhereElement() {
		super(XPath.WILDCARD);
	}

	@Override
	public Collection<ParseTree> evaluate(ParseTree t) {
		if ( invert ) return new ArrayList<ParseTree>(); // !* is weird but valid (empty)
		return Trees.getDescendants(t);
	}
}
