package plethora.core;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.*;
import toxi.geom.mesh.TriangleMesh;

/**
 * Terrain Class based on a grid condition. It contains a data buffer that allows to store different layers of data
 * obtained from images or other sources. This data can be red by Agents (Ple_Agents) or used for height or color.
 * The class is mainly a grid structure for calculations like vector fields (fluids) or path-finding.
 * 
 * Written my Jose Sanchez - 2011
 * for feedback please contact me at: jomasan@gmail.com
 * 
 * @author jomasan
 *
 */

public class Ple_Terrain {

	PApplet p5;

	public Vec3D [][] field;
	public Ple_Tile[][] tiles;
	public int COLS, ROWS;
	public float cellSizeX, cellSizeY; 

	public Vec3D origin;

	public ArrayList<Integer[][]> buffers;

	public boolean crop = false;
	public float minCropX = 0;
	public float minCropY = 0;
	public float minCropZ = 0;
	public float maxCropX = 0;
	public float maxCropY = 0;
	public float maxCropZ = 0;

	public TriangleMesh mesh;

	/**
	 * Constructor: This is the information you need to provide to build the class. Usually called in the 
	 * 'setup'. 
	 * 
	 * @param _p5
	 * @param _origin
	 * @param _COLS
	 * @param _ROWS
	 * @param _cellSizeX
	 * @param _cellSizeY
	 */

	public Ple_Terrain(PApplet _p5,Vec3D _origin, int _COLS, int _ROWS, float _cellSizeX, float _cellSizeY){
		p5 = _p5;

		origin = _origin;

		COLS = _COLS;
		ROWS = _ROWS;

		cellSizeX = _cellSizeX;
		cellSizeY = _cellSizeY;

		field = new Vec3D[COLS][ROWS];

		mesh = new TriangleMesh();

		init();
	}

	/**
	 * initialize the class
	 */
	public void init(){
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				field[i][j] = new Vec3D(i*cellSizeX+origin.x, j *cellSizeY+origin.y,origin.z);
			}
		}
	}

	/**
	 * initialize a collection of tiles (Ple_Tiles)
	 */
	public void initTiles(){

		tiles = new Ple_Tile[COLS][ROWS];

		for (int i = 0; i < COLS-1; i++) {
			for (int j = 0; j < ROWS-1; j++) {

				Vec3D p1 = field[i][j];
				Vec3D p2 = field[i+1][j];
				Vec3D p3 = field[i+1][j+1];
				Vec3D p4 = field[i][j+1];

				tiles[i][j] = new Ple_Tile(p5, p1,p2,p3,p4);

			}
		}
	}


	/**
	 * update Tiles locations based on terrain Locations
	 */
	public void updateTiles(){
		for (int i = 0; i < COLS-1; i++) {
			for (int j = 0; j < ROWS-1; j++) {

				Vec3D p1 = field[i][j];
				Vec3D p2 = field[i+1][j];
				Vec3D p3 = field[i+1][j+1];
				Vec3D p4 = field[i][j+1];

				tiles[i][j].a = p1;
				tiles[i][j].b = p2;
				tiles[i][j].c = p3;
				tiles[i][j].d = p4;	
			}
		}
	}

	/**
	 * display tiles polygons
	 */
	public void displayTiles(){
		for (int i = 0; i < COLS-1; i++) {
			for (int j = 0; j < ROWS-1; j++) {
				tiles[i][j].drawPoly();	
			}
		}
	}

	/**
	 * define cropping thresholds
	 * @param cX
	 * @param cY
	 * @param cZ
	 * @param cX2
	 * @param cY2
	 * @param cZ2
	 */
	public void crop(float cX, float cY, float cZ, float cX2, float cY2, float cZ2){
		minCropX = cX;
		minCropY = cY;
		minCropZ = cZ;

		maxCropX = cX2;
		maxCropY = cY2;
		maxCropZ = cZ2;
	}


	/**
	 * set the height of a point to a value
	 * @param col
	 * @param row
	 * @param z
	 */
	public void setPointZ(int col, int row, float z){
		if(col < COLS && row < ROWS && col > -1 && row > -1){
			field[col][row].z = z;
		}
	}

	/**
	 * add some height to a point
	 * @param col
	 * @param row
	 * @param z
	 */
	public void addPointZ(int col, int row, float z){
		if(col < COLS && row < ROWS && col > -1 && row > -1){
			field[col][row].z += z;
		}
	}

	/**
	 * activate / de-activate cropping 
	 * @param c
	 */
	public void setCropActive(boolean c){
		crop = c;
	}

	/**
	 * set the location of each point of the field based on a vector array
	 * @param pts
	 * @param numCols
	 * @param numRows
	 */
	public void setLocFromData(Vec3D [] pts){
		int countX = 0;
		int countY = 0;
		for (int i = 0; i < pts.length; i +=1) {
			//float c = map (pts[i].z, 10, 50, 0, 255);
			//stroke(c, 90);
			//strokeWeight(0.2);
			//if(pts[i].x < 300 && y > 100){
			//point(pts[i].x, pts[i].y, pts[i].z); 

			field[countX][countY] = pts [i].copy();
			countX ++;
			if(countX == COLS){
				countY++;
				countX = 0;
			}
		}
	}

	/**
	 * display points of the field
	 */
	public void display(){
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				if(checkCrop(field[i][j])){
					vPt(field[i][j]);
				}
			}
		}
	}

	/**
	 * calculates if point is in cropping thresholds
	 * @param v
	 * @return
	 */
	public boolean checkCrop (Vec3D v){
		boolean result = true;
		if(v.x < minCropX || v.y < minCropY || v.z < minCropZ || v.x > maxCropX || v.y > maxCropY || v.z > maxCropZ ){
			if(crop){
				result = false;
			}
		}else{
			result = true;
		}
		return result;
	}

	/**
	 * add a noise value to the heights
	 * @param minHeight
	 * @param maxHeight
	 */
	public void noiseHeight(float minHeight, float maxHeight){
		p5.noiseSeed((int)p5.random(10000));
		float xoff = 0;
		for (int i = 0; i < COLS; i++) {
			float yoff = 0;
			for (int j = 0; j < ROWS; j++) {
				float height = PApplet.map(p5.noise(xoff,yoff),0,1,minHeight,maxHeight);
				field[i][j].z += height;
				yoff += 0.1;
			}
			xoff += 0.1;
		}
	}

	/**
	 * draw lines between points (vertical, horizontal or diagonal)
	 * @param vertical
	 * @param horizontal
	 * @param diagonal
	 */
	public void drawLines(boolean vertical, boolean horizontal, boolean diagonal){
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {

				int nX = 1;
				int nY = 1;
				if(i == COLS-1)nX = 0;
				if(j == ROWS-1)nY = 0;

				Vec3D p1 = field[i][j];
				Vec3D p2 = field[i+nX][j];
				Vec3D p3 = field[i+nX][j+nY];
				Vec3D p4 = field[i][j+nY];

				if(checkCrop(field[i][j])){

					if(horizontal)vLine(p1,p2);

					if(vertical)vLine(p1,p4);

					if(diagonal)vLine(p1,p3);

				}		
			}
		}


	}

	/**
	 * draw polygons with the data of a data-map (2 dimentional array)
	 * @param data
	 * @param from1
	 * @param to1
	 * @param from2
	 * @param to2
	 */
	public void drawDataMap(float [][] data, float from1, float to1, float from2, float to2){
		for (int i = 0; i < COLS-1; i++) {
			for (int j = 0; j < ROWS-1; j++) {
				Vec3D p1 = field[i][j];
				Vec3D p2 = field[i+1][j];
				Vec3D p3 = field[i+1][j+1];
				Vec3D p4 = field[i][j+1];

				int c = (int) PApplet.map(data[i][j], from1, to1, from2, to2);
				//int c = (int)data[i][j];		
				//PApplet.constrain(c, 10, 255);
				if(data[i][j] < 0) data[i][j] = 0;
				if(data[i][j] > 255) data[i][j] = 255;

				p5.noStroke();
				p5.fill(c);
				p5.beginShape();
				vex(p1);
				vex(p2);
				vex(p3);
				//vex(p4);	
				p5.endShape();	

				p5.beginShape();
				vex(p1);
				//vex(p2);
				vex(p3);
				vex(p4);	
				p5.endShape();	
			}
		}
	}

	/**
	 * increase or decrease the values of a data-map gradually. (good for stigmergy)
	 * @param data
	 * @param value
	 * @param upperThreshold
	 * @param lowerThreshold
	 * @return
	 */
	public float [][] fadeDataMap(float [][] data, float value, float upperThreshold, float lowerThreshold){
		for (int i = 0; i < COLS-1; i++) {
			for (int j = 0; j < ROWS-1; j++) {

				data[i][j] += value;

				if(data[i][j] >= upperThreshold){
					data[i][j] = upperThreshold;
				}
				if(data[i][j] <= lowerThreshold){
					data[i][j] = lowerThreshold;
				}
			}
		}

		return data;
	}


	/**
	 * display each node (Ple_Node)
	 * @param index
	 */
	public void displayIndexArrayList(ArrayList index){
		for(int i = 0; i < index.size(); i++){
			Ple_Node node = (Ple_Node) index.get(i);
			vPt(node.loc);

		}
	}


	/**
	 * exports the mesh of the terrain
	 * @param name
	 * @param obj
	 * @param stl
	 */
	public void exportMesh(String name, boolean obj, boolean stl){
		for (int i = 0; i < COLS-2; i++) {
			for (int j = 0; j < ROWS-2; j++) {
				Vec3D p1 = field[i][j];
				Vec3D p2 = field[i+1][j];
				Vec3D p3 = field[i+1][j+1];
				Vec3D p4 = field[i][j+1];

				mesh.addFace(p1, p2, p3);
				mesh.addFace(p1, p3, p4);
			}
		}
		if(stl){
			mesh.saveAsSTL(p5.sketchPath(mesh.name + name +".stl"));
		}
		if(obj){
			mesh.saveAsOBJ(p5.sketchPath(mesh.name + name + ".obj"));
		}
		mesh.clear();
	}

	/**
	 * vertex from a vector
	 * @param v
	 */
	public void vex(Vec3D v){
		p5.vertex(v.x,v.y,v.z);
	}

	/**
	 * create a vector field from sin/cosine values - not yet implemented
	 * @param data
	 * @param from
	 * @param to
	 * @return
	 */
	public Vec3D [][] vectorField2D(float [][] data, float from, float to){

		Vec3D [][]vecField = new Vec3D[COLS-1][ROWS-1];

		for (int i = 0; i < COLS-1; i++) {
			for (int j = 0; j < ROWS-1; j++) {
				float value = PApplet.map(data[i][j], from, to, 0, PApplet.TWO_PI);
				vecField[i][j] =  new Vec3D(PApplet.cos(value),PApplet.sin(value),0);
			}
		}
		return vecField;
	}


	/**
	 * generate a data-map from an image (black and white preferably)
	 * @param image
	 */
	public float[][] loadImageToBuffer(String image){
		float [][] info = new float[COLS][ROWS];
		PImage im = p5.loadImage(image);

		int w = im.width;
		int h = im.height;

		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {

				int xLoc = (int) PApplet.map(i, 0, COLS-1, 0, w-1);
				int yLoc = (int) PApplet.map(j, 0, ROWS-1, 0, h-1);

				float c =  p5.red(im.get(xLoc,yLoc));
				info[i][j] = c;
			}
		}

		//buffers.add(info);
		return info;
	}

	/**
	 * create heights from data-map
	 * @param bufferId
	 * @param min
	 * @param max
	 */
	public void loadBufferasHeight(float [][] data, float min, float max){

		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				//float col = p5.red(data[i][j]);
				float height = PApplet.map(data[i][j], 0, 255, min, max);
				field[i][j].z = height;
			}
		}
	}

	/**
	 * draw a vector field
	 * missing * (add arrows)
	 * @param vecField
	 * @param len
	 */
	public void drawVectorField(Vec3D [][] vecField, float len){

		for (int i = 0; i < COLS-1; i++) {
			for (int j = 0; j < ROWS-1; j++) {

				Vec3D v = vecField[i][j].copy();
				v.normalize();
				v.scaleSelf(len);
				Vec3D vPlusLoc = field[i][j].add(v);

				vLine(field[i][j],vPlusLoc);				
			}
		}

	}

	/**
	 * generates a data-map of the angle of inclination
	 * @param from
	 * @param to
	 * @return
	 */
	public float[][] calcSteepnessMap(){

		float [][] fieldAngles = new float[COLS-1][ROWS-1];

		for (int i = 0; i < COLS-1; i++) {
			for (int j = 0; j < ROWS-1; j++) {
				Vec3D p1 = field[i][j];
				//Vec3D p2 = field[i+1][j];
				Vec3D p3 = field[i+1][j+1];
				//Vec3D p4 = field[i][j+1];

				fieldAngles[i][j] = calcSteepnessAngle(p1,p3);

			}
		}
		return fieldAngles;
	}

	/**
	 * draw a line from 2 vectors
	 * @param v1
	 * @param v2
	 */
	public void vLine(Vec3D v1, Vec3D v2){
		p5.line(v1.x,v1.y,v1.z, v2.x,v2.y,v2.z);
	}

	/**
	 * calculates angles of inclination
	 * @param a
	 * @param b
	 * @return
	 */
	public float calcSteepnessAngle(Vec3D a, Vec3D b){
		float ang = 0;

		Vec3D cflat = new Vec3D(b.x,b.y,a.z);

		Vec3D v1 = b.sub(a);
		v1.normalize();
		Vec3D v2 = cflat.sub(a);
		v2.normalize();

		//p5.stroke(255,0,0);
		//vLine(a,c);

		ang = v1.angleBetween(v2);
		return ang;
	}

	/**
	 * returns the closest terrain node from a given vector
	 * @param v
	 * @return
	 */
	public Vec3D closestNode(Vec3D v){

		float cloDist = 1000000;
		int cloIdc = 0;
		int cloIdr = 0;

		for (int i = 0; i < COLS; i ++){
			for (int j = 0; j < ROWS; j ++){

				float d = field[i][j].distanceTo(v);
				if(d < cloDist && d > 0){
					cloDist = d;
					cloIdc = i;
					cloIdr = j;
				}
			}
		}
		return field[cloIdc][cloIdr].copy();
	}

	/**
	 * returns the location on the grid (projected down to the terrain on average location)
	 * @param v
	 * @return
	 */
	public Vec3D getLocInGrid(Vec3D v) {
		int i = xLocInGrid(v);
		int j = yLocInGrid(v);
		return field[i][j].copy();
	}

	/**
	 * get the location in the grid in X
	 * @param v
	 * @return
	 */
	public int xLocInGrid (Vec3D v){
		int i = (int) PApplet.constrain((v.x-origin.x)/cellSizeX,0,COLS-1);
		return i;
	}

	/**
	 * get the location on  the grid in Y
	 * @param v
	 * @return
	 */
	public int yLocInGrid (Vec3D v){
		int j = (int) PApplet.constrain((v.y-origin.y)/cellSizeY,0,ROWS-1);
		return j;
	}

	/**
	 * draw point from a vector
	 * @param v
	 */
	public void vPt(Vec3D v){
		p5.point(v.x,v.y,v.z);
	}



}
