package multidimensional;

import data.TableData;
import perspectives.Viewer;
import perspectives.ViewerFactory;
import perspectives.ViewerFactory.RequiredData;

import multidimensional.PlanarProjectionViewer;


public class PlanarProjectionViewerFactory extends ViewerFactory {	

		public RequiredData requiredData() {
			
			RequiredData rd = new RequiredData("TableData","1");
			return rd;
		}

		@Override
		public String creatorType() {
			// TODO Auto-generated method stub
			return "Planaer Projection";
		}

		@Override
		public Viewer create(String name) {
			if (this.isAllDataPresent())
			{
				TableData t = (TableData)this.getData().get(0);
				return new PlanarProjectionViewer(name, t);
			}
			return null;
		}
		


}
