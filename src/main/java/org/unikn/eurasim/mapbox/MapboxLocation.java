package org.unikn.eurasim.mapbox;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

public class MapboxLocation extends DirectPosition2D {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1675802187026528569L;
	
	private MapboxLocation(double longitude, double latitude) throws NoSuchAuthorityCodeException, FactoryException {
		super(CRS.decode("EPSG:4326"), longitude, latitude);
	}

	public static MapboxLocation generateCoordinate(double longitude, double latitude) {
		try {
			return new MapboxLocation(longitude,latitude);
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "["+this.x+","+this.y+"]";
	}
}
