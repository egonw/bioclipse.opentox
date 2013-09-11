/* Copyright (C) 2013  Egon Willighagen <egonw@users.sf.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.bioclipse.opentox.nm.api;

import java.util.List;

import net.bioclipse.core.domain.IMaterial;
import net.bioclipse.nm.business.NmManager;

import org.eclipse.core.runtime.IProgressMonitor;

public class Dataset {

	private static NmManager nm = new NmManager();

	public static String createNewDataset(String service,
		List<IMaterial> materials, IProgressMonitor monitor)
	throws Exception {
		String nmxContent = nm.asNMX(materials);
		return net.bioclipse.opentox.api.Dataset.createNewDataset(
			normalizeURI(service), nmxContent, monitor
		);
	}

	public static String normalizeURI(String datasetURI) {
		datasetURI = datasetURI.replaceAll("\\n", "");
		datasetURI = datasetURI.replaceAll("\\r", "");
		if (!datasetURI.endsWith("/")) datasetURI += "/";
		return datasetURI;
	}
}
