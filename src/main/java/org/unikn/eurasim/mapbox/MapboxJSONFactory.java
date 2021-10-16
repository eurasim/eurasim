package org.unikn.eurasim.mapbox;

import java.util.ArrayList;

import org.locationtech.jts.geom.Point;
import org.unikn.eurasim.algorithms.DangerZone;
import org.unikn.eurasim.algorithms.SafeArea;
import org.unikn.eurasim.model.StreetPath;
import org.unikn.eurasim.model.StreetNode;

public class MapboxJSONFactory {

	public static String generatePath() {
		String jsonResponse = "[{\"type\": \"geojson\",\"data\": {\"type\": \"Feature\",\"properties\": {";
		
		jsonResponse += "},\"geometry\": {\"type\": \"LineString\",\"coordinates\": [";
		jsonResponse += "[-122.48369693756104, 37.83381888486939],[-122.48348236083984, 37.83317489144141],[-122.48339653015138, 37.83270036637107],[-122.48356819152832, 37.832056363179625],[-122.48404026031496, 37.83114119107971],[-122.48404026031496, 37.83049717427869],[-122.48348236083984, 37.829920943955045],[-122.48356819152832, 37.82954808664175],[-122.48507022857666, 37.82944639795659],[-122.48610019683838, 37.82880236636284],[-122.48695850372314, 37.82931081282506],[-122.48700141906738, 37.83080223556934],[-122.48751640319824, 37.83168351665737],[-122.48803138732912, 37.832158048267786],[-122.48888969421387, 37.83297152392784],[-122.48987674713133, 37.83263257682617],[-122.49043464660643, 37.832937629287755],[-122.49125003814696, 37.832429207817725],[-122.49163627624512, 37.832564787218985],[-122.49223709106445, 37.83337825839438],[-122.49378204345702, 37.83368330777276]";
		jsonResponse += "]}}}]";
		
		return jsonResponse;
	}

	public static String generateSinglePath(StreetPath graphPath) {
		String jsonResponse = "[{\"type\": \"geojson\",\"data\": {\"type\": \"Feature\",\"properties\": {";
		
		jsonResponse += "},\"geometry\": {\"type\": \"LineString\",\"coordinates\": [";
		jsonResponse += "[-122.48369693756104, 37.83381888486939],[-122.48348236083984, 37.83317489144141],[-122.48339653015138, 37.83270036637107],[-122.48356819152832, 37.832056363179625],[-122.48404026031496, 37.83114119107971],[-122.48404026031496, 37.83049717427869],[-122.48348236083984, 37.829920943955045],[-122.48356819152832, 37.82954808664175],[-122.48507022857666, 37.82944639795659],[-122.48610019683838, 37.82880236636284],[-122.48695850372314, 37.82931081282506],[-122.48700141906738, 37.83080223556934],[-122.48751640319824, 37.83168351665737],[-122.48803138732912, 37.832158048267786],[-122.48888969421387, 37.83297152392784],[-122.48987674713133, 37.83263257682617],[-122.49043464660643, 37.832937629287755],[-122.49125003814696, 37.832429207817725],[-122.49163627624512, 37.832564787218985],[-122.49223709106445, 37.83337825839438],[-122.49378204345702, 37.83368330777276]";
		jsonResponse += "]}}}]";
		
		for(StreetNode n : graphPath.getPath()) {
			double lon = n.getLongitude();
			double lat = n.getLatitude();
			System.out.println("[POINT] log = " + lon+", "+lat);
		}
		
		return jsonResponse;
	}

	public static String generatePath(ArrayList<StreetNode> gSources, ArrayList<StreetNode> gTargets) {
		String jsonResponse = "[";
		
		jsonResponse += "{\"type\": \"geojson\",\"data\": {\"type\": \"Feature\",\"properties\": {";
		jsonResponse += "},\"geometry\": {\"type\": \"LineString\",\"coordinates\": [";
		int count = 0;
		for(StreetNode s : gSources) {
			for(StreetNode t : gTargets) {
				jsonResponse += "["+s.getLongitude()+","+s.getLatitude()+"]";
				jsonResponse += ",";
				jsonResponse += "["+t.getLongitude()+","+t.getLatitude()+"]";
				
				if(count++ < gSources.size()*gTargets.size()-1) {
					jsonResponse += ",";
				}
			}
		}		
		jsonResponse += "]}}}";
		
		jsonResponse += ",";
		
		jsonResponse += "{\"type\": \"geojson\",\"data\": {\"type\": \"Feature\",\"properties\": {";
		jsonResponse += "},\"geometry\": {\"type\": \"LineString\",\"coordinates\": [";
		count = 0;
		for(StreetNode s : gSources) {
			for(StreetNode t : gTargets) {
				jsonResponse += "["+(s.getLongitude()+0.01)+","+s.getLatitude()+"]";
				jsonResponse += ",";
				jsonResponse += "["+(t.getLongitude()+0.01)+","+t.getLatitude()+"]";
				
				if(count++ < gSources.size()*gTargets.size()-1) {
					jsonResponse += ",";
				}
			}
		}		
		jsonResponse += "]}}}";
		
		jsonResponse += "]";
	
		return jsonResponse;
	}
	
	public static String generatePlan(ArrayList<StreetPath> plans, ArrayList<DangerZone> dangerZones,	ArrayList<SafeArea> safeAreas, int simulatedTime) {
		String jsonResponse = "[";

		jsonResponse += "{\"egress\": \""+simulatedTime+"\"}";
			
		for(int i=0;i<plans.size();i++) {
			
			StreetPath path = plans.get(i);
			
			//if(i > 0 )
				jsonResponse += ",";
			
			jsonResponse += "{\"type\": \"geojson\",\"data\": {\"type\": \"Feature\",\"properties\": { \"linetype\": \"route\", \"evacuees\": "+path.getNumberOfEvacuees()+", \"totalDistance\": "+path.getTotalDistance();
			jsonResponse += "},\"geometry\": {\"type\": \"LineString\",\"coordinates\": [";
		
			for(int j=0;j<path.getPath().size();j++) {
				if(j>0)
					jsonResponse += ",";
				jsonResponse += "["+path.getPath().get(j).getLongitude()+","+path.getPath().get(j).getLatitude()+"]";
			}
			// Coordinates Here
		
			jsonResponse += "]}}}";
		}
		
		for(int i=0;i<dangerZones.size();i++) {
			jsonResponse += ",";
			
			jsonResponse += "{\"type\": \"geojson\",\"data\": {\"type\": \"Feature\",\"properties\": { \"linetype\": \"connector\"";
			jsonResponse += "},\"geometry\": {\"type\": \"LineString\",\"coordinates\": [";
		
			jsonResponse += "["+dangerZones.get(i).getLocation().getX()+","+dangerZones.get(i).getLocation().getY()+"]";
			jsonResponse += ",";
			jsonResponse += "["+dangerZones.get(i).getLocation().getX()+","+dangerZones.get(i).getLocation().getY()+"]";
			//jsonResponse += "["+sourcesP.get(i).getX()+","+sourcesP.get(i).getY()+"]";
			//jsonResponse += ",";
			//jsonResponse += "["+sourcesT.get(i).getLongitude()+","+sourcesT.get(i).getLatitude()+"]";
			// Coordinates Here
		
			jsonResponse += "]}}}";
			
		}
		for(int i=0;i<safeAreas.size();i++) {
			jsonResponse += ",";
			
			jsonResponse += "{\"type\": \"geojson\",\"data\": {\"type\": \"Feature\",\"properties\": { \"linetype\": \"connector\"";
			jsonResponse += "},\"geometry\": {\"type\": \"LineString\",\"coordinates\": [";

			jsonResponse += "["+safeAreas.get(i).getLocation().getX()+","+safeAreas.get(i).getLocation().getY()+"]";
			jsonResponse += ",";
			jsonResponse += "["+safeAreas.get(i).getLocation().getX()+","+safeAreas.get(i).getLocation().getY()+"]";
			//jsonResponse += "["+targetsT.get(i).getLongitude()+","+targetsT.get(i).getLatitude()+"]";
			//jsonResponse += ",";
			//jsonResponse += "["+targetsP.get(i).getX()+","+targetsP.get(i).getY()+"]";

			// Coordinates Here
		
			jsonResponse += "]}}}";
			
		}
		
		jsonResponse += "]";
	
		return jsonResponse;
	}
}
