/**
 * Simple terrain call. The terrain is a grid class to calculate data.
 * more info at www.plethora-project.com
 * requires toxiclibs and peasycam
 */

/* 
 * Copyright (c) 2011 Jose Sanchez
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * http://creativecommons.org/licenses/LGPL/2.1/
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

import processing.opengl.*;
import plethora.core.*;
import toxi.geom.*;
import peasy.*;

//using peasycam
PeasyCam cam;

//declare plethora Terrain
Ple_Terrain pTer;

//declare a float array to store the image info
float [][] heights;

int DIMX = 1000;
int DIMY = 1000;

void setup() {
  size(1200, 600, OPENGL);
  smooth();
  cam = new PeasyCam(this, 600);

 //declare a vector as the location
  Vec3D location = new Vec3D(-DIMX/2,-DIMY/2,0);
  //initialize the terrain, specifying columns and rows and cell Size
  pTer = new Ple_Terrain(this, location,  150,150, 7.5, 7.5);
  
  //build a float array buffer and then load as height (what I call a datamap)
  //this function matches the resolution of the image to the resulution of your terrain
  heights = pTer.loadImageToBuffer("datamap.jpg");
  
  //load that info into the height of the terrain
  pTer.loadBufferasHeight(heights, 0  , 40);
}

void draw() {
  background(235);
  
  stroke(0,90);
  strokeWeight(1);
  noFill();
  rect(-DIMX/2,-DIMY/2,    DIMX,DIMY);

  //call some of the functions of the terrain
  stroke(0,90);
  strokeWeight(2);
  pTer.display();
  
  //draw lines (horizontal, vertical or diagonal... or all of them!) 
  stroke(0,90);
  strokeWeight(1);
  pTer.drawLines(false,true,false);
  
   //display a float array (datamap) as grayScale  (polygons that can be exported)
   //might be a bit heavy depending on the resolution of your terrain:
   //inputs: datamap, with values that go from ---> to, displayed in colors from ---> to
  pTer.drawDataMap(heights, 0,255, 150,255);
}


