/*******************************************************************************
*                                                                              *
* This file is part of IfcOpenShell.                                           *
*                                                                              *
* IfcOpenShell is free software: you can redistribute it and/or modify         *
* it under the terms of the Lesser GNU General Public License as published by  *
* the Free Software Foundation, either version 3.0 of the License, or          *
* (at your option) any later version.                                          *
*                                                                              *
* IfcOpenShell is distributed in the hope that it will be useful,              *
* but WITHOUT ANY WARRANTY; without even the implied warranty of               *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                 *
* Lesser GNU General Public License for more details.                          *
*                                                                              *
* You should have received a copy of the Lesser GNU General Public License     *
* along with this program. If not, see <http://www.gnu.org/licenses/>.         *
*                                                                              *
********************************************************************************/

package org.ifcopenshell;

import org.bimserver.plugins.renderengine.RenderEngineException;
import org.bimserver.plugins.renderengine.RenderEngineGeometry;
import org.bimserver.plugins.renderengine.RenderEngineInstance;

public class IfcOpenShellEntityInstance implements RenderEngineInstance {
	private IfcGeomServerClientEntity entity;

	public IfcOpenShellEntityInstance(IfcGeomServerClientEntity entity) {
		this.entity = entity;
	}


	public double[] getTransformationMatrix() {
		return entity.getMatrix();
	}


	public RenderEngineGeometry generateGeometry() {
		if (entity == null) {
			return null;
		}
		return new RenderEngineGeometry(entity.getIndices(), entity.getPositions(), entity.getNormals(), entity.getColors(), entity.getMaterialIndices());
	}


	public double getArea() throws RenderEngineException {
		throw new UnsupportedOperationException();
	}
	

	public double getVolume() throws RenderEngineException {
		throw new UnsupportedOperationException();
	}
	
	public String getType(){
		return entity.getType();
	}
}
