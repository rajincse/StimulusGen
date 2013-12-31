package util;

import perspectives.Viewer;
import perspectives.ViewerFactory;
import perspectives.ViewerFactory.RequiredData;

public class ImageTilerFactory extends ViewerFactory {

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String creatorType() {
		return "Image Tiler";
	}

	@Override
	public Viewer create(String name) {
		return new TileViewer(name);
	}
}
