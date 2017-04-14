
package nl.tue.ddss.bimsparql.pfunction;

import java.util.List;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.engine.ExecutionContext ;
import com.hp.hpl.jena.sparql.engine.QueryIterator ;
import com.hp.hpl.jena.sparql.engine.binding.Binding ;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArgType;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval;

/** Common, simple case:
 *  <ul> 
 *  <li>subject arguments is a list</li>
 *  <li>object is not a list</li>
 *  <li>call the implementation with one binding at a time</li>
 *  </ul> */

public abstract
class PFuncListAndSimple extends PropertyFunctionEval
{
    protected PFuncListAndSimple()
    {
        super(PropFuncArgType.PF_ARG_LIST, PropFuncArgType.PF_ARG_SINGLE) ;
    }
    
    @Override
    public QueryIterator execEvaluated(Binding binding, PropFuncArg argSubject, Node predicate, PropFuncArg argObject, ExecutionContext execCxt)
    {
        return execEvaluated(binding, argSubject.getArgList(), predicate, argObject.getArg(), execCxt) ;
    }

    /** 
     * @param binding   Current solution from previous query stage 
     * @param subject   Node in subject slot, after substitution if a bound variable in this binding
     * @param predicate This predicate
     * @param object    List in object slot, after substitution if a bound variable in this binding
     * @param execCxt   Execution context
     * @return          QueryIterator
     */
    public abstract QueryIterator execEvaluated(Binding binding, 
                                                List<Node> subject, Node predicate, Node object,
                                                ExecutionContext execCxt) ;
    
}
