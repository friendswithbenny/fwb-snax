package org.fwb.xml.dom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

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
	
	/**
	 * @deprecated
	 * backed up this gross method from SNAX.
	 * not sure what to do with it now.
	 * it contains an old SAXON-workaround that's of obscure interest,
	 * but hopefully no longer needed
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	static final <T extends Node> List<T> wrap(Object o) {
		Preconditions.checkNotNull(o, "cannot wrap(null)");
		
		if (o instanceof NodeList)
			return new NodeListList<T>((NodeList) o);
		
		if (o instanceof List) {
			List<?> l = (List<?>) o;
			if (l.size() == 0)
				return (List<T>) o;
			Object e = l.get(0);
			
			if (e == null)
				throw new RuntimeException("Unable to wrap List containing null: " + l);
			
			if (e instanceof Node)
				return (List<T>) o;
			
			// damn you, saxon
			/*
			 * a nasty Saxon-workaround. I <3 everything Saxon, so I still can't believe this.
			 * Perhaps it's "fixed" by now, but it'd seemed deliberate.
			 * it's actually sensible that Saxon would return this actual-List construct,
			 * just really un-conventional/inconsistent/incompatible...
			 * 
			 * at any rate, i think this block actually "un-wrapped" that pretty thing somehow,
			 * back into the expected list of w3c Nodes.
			 */
			try {
				Class<?>
					c1 = Class.forName("net.sf.saxon.om.NodeInfo"),
					c2 = Class.forName("net.sf.saxon.dom.NodeOverNodeInfo");
				if (c1.isInstance(e)) {
					final Method M = c2.getMethod("wrap", c1);
					Function<Object, T> saxon = new Function<Object, T>() {
						public T apply(Object o) {
							try {
								return (T) M.invoke(null, o);
							} catch (InvocationTargetException e) {
								Throwable t = e.getCause();
								if (t instanceof RuntimeException)
									throw (RuntimeException) t;
								if (t instanceof Error)
									throw (Error) t;
								// never happens
								throw new RuntimeException("never happens, NONI.wrap should never throw checked Throwable", e);
							} catch (IllegalAccessException e) {
								throw new RuntimeException(e);
							}
						}
					};
					return Lists.transform((List<?>) o, saxon);
				}
			} catch (ClassNotFoundException ex) {
				
			} catch (NoSuchMethodException ex) {
				
			} catch (SecurityException ex) {
				
			}
			
			throw new RuntimeException("unrecognized type: List<" + e.getClass() + ">");
		}
		
		throw new RuntimeException("unrecognized type: " + o.getClass());
	}
}