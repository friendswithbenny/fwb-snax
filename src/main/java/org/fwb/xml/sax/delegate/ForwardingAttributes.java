package org.fwb.xml.sax.delegate;

import org.xml.sax.Attributes;

/** this would extend ForwardingObject but prefers to avoid the dependency */
public abstract class ForwardingAttributes implements Attributes {
	public static class FixedForwardingAttributes extends ForwardingAttributes {
		private final Attributes delegate;
		public FixedForwardingAttributes(Attributes delegate) {
			this.delegate = delegate;
		}
		public Attributes delegate() {
			return delegate;
		}
	}
	
	public abstract Attributes delegate();
	
	@Override
	public int getIndex(String s, String s1) {
		return delegate().getIndex(s, s1);
	}
	@Override
	public int getIndex(String s) {
		return delegate().getIndex(s);
	}
	@Override
	public int getLength() {
		return delegate().getLength();
	}
	@Override
	public String getLocalName(int i) {
		return delegate().getLocalName(i);
	}
	@Override
	public String getQName(int i) {
		return delegate().getQName(i);
	}
	@Override
	public String getType(int i) {
		return delegate().getType(i);
	}
	@Override
	public String getType(String s, String s1) {
		return delegate().getType(s, s1);
	}
	@Override
	public String getType(String s) {
		return delegate().getType(s);
	}
	@Override
	public String getURI(int i) {
		return delegate().getURI(i);
	}
	@Override
	public String getValue(int i) {
		return delegate().getValue(i);
	}
	@Override
	public String getValue(String s, String s1) {
		return delegate().getValue(s, s1);
	}
	@Override
	public String getValue(String s) {
		return delegate().getValue(s);
	}
}
