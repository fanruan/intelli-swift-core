/**
 * 
 */
package com.fr.bi.etl.analysis;

import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLWriter;

/**
 * @author Daniel
 *
 */
public interface ETLResultTable extends XMLReadable, XMLWriter {
	
	public String getId();
	
	public String getName();
	
	public ETLResultTable getParentTable();
	
	public boolean isReady();
	

}