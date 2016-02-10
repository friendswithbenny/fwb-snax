package org.fwb.xml.dom;

import java.util.AbstractList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Preconditions;

/**
 * this class is an adapter between org.w3c.dom.NodeList and the Java Collections Framework
 */
public final class NodeListList<T extends Node> extends AbstractList<T> {
	final NodeList NL;
	
	/**
	 * must be a NodeList containing only nodes of type {@code <T>}
	 */
	public NodeListList(NodeList nl) {
		NL = nl;
	}
	
	@Override
	public int size() {
		return NL.getLength();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T get(int i) {
		Preconditions.checkElementIndex(i, size());
		return (T) NL.item(i);
	}
}