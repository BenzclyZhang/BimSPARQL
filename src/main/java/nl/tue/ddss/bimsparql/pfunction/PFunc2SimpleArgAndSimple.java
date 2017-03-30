package nl.tue.ddss.bimsparql.pfunction;

import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public abstract class PFunc2SimpleArgAndSimple extends PFuncListAndSimple{

	public PFunc2SimpleArgAndSimple(){
		super();
	}

	@Override
	public QueryIterator execEvaluated(Binding binding, List<Node> subject,
			Node predicate, Node object, ExecutionContext execCxt) {
		// TODO Auto-generated method stub
		if(subject.size()!=2){
			throw new QueryBuildException("Not 2 arguments (subject) to "+predicate.getURI());
		}
		return execEvaluated(binding, subject.get(0),subject.get(1),
				predicate, object, execCxt);
}
	protected abstract QueryIterator execEvaluated(Binding binding, Node subArg1,Node subArg2,
			Node predicate, Node object, ExecutionContext execCxt);
}
