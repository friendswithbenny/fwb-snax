# the Simple, *Nice* API for XML.

SAX provides event-driven XML processing, which can be useful for performance/lifecycle reasons.

SAX provides an API correct for the most exhaustive possible usage of XML, which itself is very complex.
event-driven data modeling has its own inherent complexities,
even without the headache of namespaces and axes (element, attribute, etc.).

there are simplifications of Full XML that satisfy a great majority of use cases.

## SNAX presents an API layer for these simpler sub-sets of Full XML.

SNAX is implemented by delegating to any compliant (regular, non-SNAX) SAX implementation.
