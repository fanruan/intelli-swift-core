package com.fr.swift.jdbc.org.antlr.v4.runtime.tree.xpath;

import com.fr.swift.jdbc.org.antlr.v4.runtime.tree.ParseTree;
import com.fr.swift.jdbc.org.antlr.v4.runtime.tree.Trees;

import java.util.Collection;

public class XPathTokenAnywhereElement extends XPathElement {
	protected int tokenType;
	public XPathTokenAnywhereElement(String tokenName, int tokenType) {
		super(tokenName);
		this.tokenType = tokenType;
	}

	@Override
	public Collection<ParseTree> evaluate(ParseTree t) {
		return Trees.findAllTokenNodes(t, tokenType);
	}
}
