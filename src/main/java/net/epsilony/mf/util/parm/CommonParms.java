/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.epsilony.mf.util.parm;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class CommonParms {
    public static final String SPATIAL_DIMENSION         = "spatialDimension";
    public static final String VALUE_DIMENSION           = "valueDimension";
    public static final String SPACE_NODES               = "spaceNodes";
    public static final String BOUNDARY_NODES            = "boundaryNodes";
    public static final String LAGRANGLE_DIRICHLET_NODES = "lagrangleDirichletNodes";
    public static final String EXTRA_LAGRANGLE_NODES     = "extraLagrangleNodes";
    public static final String NODES                     = "nodes";
    public static final String LOAD_MAP                  = "loadMap";
    public static final String BOUNDARIES                = "boundaries";
    public static final String DIRICHLET_BOUNDARIES      = "dirichletBoundaries";
    public static final String MODEL_INPUTED             = "modelInputed";
    public static final String CONSTITUTIVE_LAW          = "constitutiveLaw";
    //  ['SPATIAL_DIMENSION', 'VALUE_DIMENSION', 'SPACE_NODES', 'BOUNDARY_NODES', 'LAGRANGLE_DIRICHLET_NODES', 'EXTRA_LAGRANGLE_NODES', 'NODES', 'LOAD_MAP', 'BOUNDARIES', 'DIRICHLET_BOUNDARIES', 'MODEL_INPUTED', 'CONSTITUTIVE_LAW'] 

    /*
     
    def to_p(s):
        res=re.findall(r'^[a-z]+',s)[0].upper()
        all_segs=re.findall(r'[A-Z][a-z]*',s)
        for seg in all_segs:
            res+="_"+seg.upper()
        return res
     
    def to_pf(s):                                                                                  
        res='public static final String '+to_p(s)+' = '+'"'+s+'";';
        return res

    consts=[
        "spatialDimension",
        "valueDimension",
        "spaceNodes",
        "boundaryNodes",
        "lagrangleDirichletNodes",
        "extraLagrangleNodes",
        "nodes",
        "loadMap",
        "boundaries", 
        "dirichletBoundaries",
        "modelInputed",
        "constitutiveLaw"
    ]

    for cst in consts:
        print(to_pf(cst))
    
    print(r'/* ',str([to_p(cst) for cst in consts]),r' /*')
    */
}
