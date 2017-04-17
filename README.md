# BimSPARQL
SPARQL query with extended functions for query ifcOWL. It is a prototype implementation as proof of concept.
All the defined functions are in the /Vocabularies folder. In each vocabulary(*.ttl file), mapping to ifcOWL (using SPIN www.spinrdf.org) or coding snippts are included.
It has adopted the Extended Well Known Text to represent IFC geometry data in RDF, and geometry-related functions are developed on top of it.
For using geometry related functions, it requires the /exe folder of IfcOpenShell Bimserver plugin in https://github.com/opensourceBIM/IfcOpenShell-BIMserver-plug
