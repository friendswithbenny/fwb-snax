package org.fwb.xml.sax;

import java.util.AbstractList;
import org.xml.sax.Attributes;

/** wraps Attributes values as a List */
public class AttributesList extends AbstractList<String> {
	final Attributes ATTS;
	
	public AttributesList(Attributes atts) {
		ATTS = atts;
	}
	
	@Override
	public String get(int index) {
		return ATTS.getValue(index);
	}
	
	@Override
	public int size() {
		return ATTS.getLength();
	}
}
