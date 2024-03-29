/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* ---------------------
 * DirectedSubgraph.java
 * ---------------------
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh
 * Contributor(s):   Christian Hammer
 *
 * $Id: DirectedSubgraph.java 689 2009-07-04 06:40:29Z perfecthash $
 *
 * Changes
 * -------
 * 05-Aug-2003 : Initial revision (BN);
 * 11-Mar-2004 : Made generic (CH);
 * 28-May-2006 : Moved connectivity info from edge to graph (JVS);
 *
 */
package org.jgrapht.graph;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.util.*;


/**
 * A directed graph that is a subgraph on other graph.
 *
 * @see Subgraph
 */
public class DirectedSubgraph<V, E>
    extends Subgraph<V, E, DirectedGraph<V, E>>
    implements DirectedGraph<V, E>
{
    //~ Static fields/initializers ---------------------------------------------

    private static final long serialVersionUID = 3616445700507054133L;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new directed subgraph.
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     * @param vertexSubset vertices to include in the subgraph. If <code>
     * null</code> then all vertices are included.
     * @param edgeSubset edges to in include in the subgraph. If <code>
     * null</code> then all the edges whose vertices found in the graph are
     * included.
     */
    public DirectedSubgraph(
        DirectedGraph<V, E> base,
        Set<V> vertexSubset,
        Set<E> edgeSubset)
    {
        super(base, vertexSubset, edgeSubset);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * @see DirectedGraph#inDegreeOf(Object)
     */
    public int inDegreeOf(V vertex)
    {
        assertVertexExist(vertex);

        int degree = 0;

        for (E e : getBase().incomingEdgesOf(vertex)) {
            if (containsEdge(e)) {
                degree++;
            }
        }

        return degree;
    }

    /**
     * @see DirectedGraph#incomingEdgesOf(Object)
     */
    public Set<E> incomingEdgesOf(V vertex)
    {
        assertVertexExist(vertex);

        Set<E> edges = new ArrayUnenforcedSet<E>();

        for (E e : getBase().incomingEdgesOf(vertex)) {
            if (containsEdge(e)) {
                edges.add(e);
            }
        }

        return edges;
    }

    /**
     * @see DirectedGraph#outDegreeOf(Object)
     */
    public int outDegreeOf(V vertex)
    {
        assertVertexExist(vertex);

        int degree = 0;

        for (E e : getBase().outgoingEdgesOf(vertex)) {
            if (containsEdge(e)) {
                degree++;
            }
        }

        return degree;
    }

    /**
     * @see DirectedGraph#outgoingEdgesOf(Object)
     */
    public Set<E> outgoingEdgesOf(V vertex)
    {
        assertVertexExist(vertex);

        Set<E> edges = new ArrayUnenforcedSet<E>();

        for (E e : getBase().outgoingEdgesOf(vertex)) {
            if (containsEdge(e)) {
                edges.add(e);
            }
        }

        return edges;
    }
}

// End DirectedSubgraph.java
