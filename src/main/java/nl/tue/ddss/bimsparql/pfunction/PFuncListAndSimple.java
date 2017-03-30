/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

