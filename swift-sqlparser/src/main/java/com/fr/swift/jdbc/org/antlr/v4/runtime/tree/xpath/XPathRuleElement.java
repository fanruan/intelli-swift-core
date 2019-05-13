package com.fr.swift.jdbc.org.antlr.v4.runtime.tree.xpath;

import com.fr.swift.jdbc.org.antlr.v4.runtime.ParserRuleContext;
import com.fr.swift.jdbc.org.antlr.v4.runtime.tree.ParseTree;
import com.fr.swift.jdbc.org.antlr.v4.runtime.tree.Tree;
import com.fr.swift.jdbc.org.antlr.v4.runtime.tree.Trees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XPathRuleElement extends XPathElement {
	protected int ruleIndex;
	public XPathRuleElement(String ruleName, int ruleIndex) {
		super(ruleName);
		this.ruleIndex = ruleIndex;
	}

	@Override
	public Collection<ParseTree> evaluate(ParseTree t) {
				// return all children of t that match nodeName
		List<ParseTree> nodes = new ArrayList<ParseTree>();
		for (Tree c : Trees.getChildren(t)) {
			if ( c instanceof ParserRuleContext ) {
				ParserRuleContext ctx = (ParserRuleContext)c;
				if ( (ctx.getRuleIndex() == ruleIndex && !invert) ||
					 (ctx.getRuleIndex() != ruleIndex && invert) )
				{
					nodes.add(ctx);
				}
			}
		}
		return nodes;
	}
}
