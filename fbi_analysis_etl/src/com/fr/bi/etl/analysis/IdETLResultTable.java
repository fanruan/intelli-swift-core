/**
 * 
 */
package com.fr.bi.etl.analysis;

import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * @author Daniel
 *
 */
public class IdETLResultTable implements ETLResultTable {
	
	private String id;

	/* (non-Javadoc)
	 * @see com.fr.stable.xml.XMLReadable#readXML(com.fr.stable.xml.XMLableReader)
	 */
	@Override
	public void readXML(XMLableReader reader) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.fr.stable.xml.XMLWriter#writeXML(com.fr.stable.xml.XMLPrintWriter)
	 */
	@Override
	public void writeXML(XMLPrintWriter writer) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.fr.bi.etl.analysis.ETLResultTable#getId()
	 */
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.fr.bi.etl.analysis.ETLResultTable#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.fr.bi.etl.analysis.ETLResultTable#getParentTable()
	 */
	@Override
	public ETLResultTable getParentTable() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.fr.bi.etl.analysis.ETLResultTable#isReady()
	 */
	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

}