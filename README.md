# the Simple, *Nice* API for XML.

SNAX is a layman-friendly extension of the universal [SAX](http://sax.sourceforge.net/) standard.

## SAX

the Simple API for XML provides event-driven XML processing, which can be useful for performance/lifecycle reasons.

SAX provides an API correct for the most exhaustive possible usage of XML, which itself is very complex.
event-driven data modeling has its own inherent complexities,
even without the headaches of advanced XML (namespaces, axes, etc.).

there are simplifications of Full XML that satisfy a great majority of XML's use cases.

## SNAX

SNAX presents an API layer for the most common usage of XML, a simpler sub-set of the Full XML standard. It is otherwise analogous to SAX's purely event-driven model. SNAX is implemented by delegating to any compliant SAX implementation.

* SimpleContentHandler provides simplified methods for null namespace, attributes, and content, interchangeably.
* SmartContentHandler provides further-simplified methods to add attributes and close tags,
at the expense of keeping stateful information such as a stack of open elements and their attributes.