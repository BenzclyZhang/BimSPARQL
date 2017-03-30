package nl.tue.ddss.bimsparql.pfunction.pdt;


import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.pfunction.PFuncSimple;
import com.hp.hpl.jena.sparql.util.IterLib;

public class HasThicknessPF extends PFuncSimple{
	
	@Override
	public QueryIterator execEvaluated(Binding binding, Node subject,
			Node predicate, Node object, ExecutionContext execCxt) {
		Graph graph = execCxt.getActiveGraph();
		if (Var.isVar(subject))
			throw new ARQInternalErrorException(
					"hasThickness: Subject is a variable without binding");
		if (Var.isVar(object))
			return getHasVolumnPF(binding, graph, subject,
					Var.alloc(object), execCxt);
		else
			return verifyHasVolumnPF(binding, graph, subject, object,
					execCxt);
	}
	
	private QueryIterator getHasVolumnPF(Binding binding, Graph graph,
			Node subject, Var varIsExternal,
				ExecutionContext execCxt) {
	        double d=0;
			try {
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Node node=NodeFactory.createLiteral(Double.toString(d),null,XSDDatatype.XSDdouble);
			return IterLib.oneResult(binding, varIsExternal, node, execCxt);
		}

	private QueryIterator verifyHasVolumnPF(Binding binding, Graph graph,
			Node subject, Node length, ExecutionContext execCxt) {

		return IterLib.noResults(execCxt);
	}

}
