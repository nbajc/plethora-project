/**
 * Simple call for agent population with a flocking behavior based on Craig Reynolds
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

ArrayList <Ple_Agent> boids;

//using peasycam
PeasyCam cam;

//create a spline
Spline3D sp2;

float DIMX = 1000;
float DIMY = 1000;
float DIMZ = 1000;

int pop = 100;

void setup() {
  size(1200, 600, OPENGL);
  smooth();
  cam = new PeasyCam(this, 600);

  //initialize the arrayList
  boids = new ArrayList <Ple_Agent>();

  //build the spline from values
  sp2 = new Spline3D();
  for (int i = 0; i < 20; i ++) {
    Vec3D v = new Vec3D(-600 + (1200/20*i), random(-50, 50), random(-50, 50)+100);
    sp2.add(v);
  }
 

  for (int i = 0; i < pop; i++) {

    //set the initial location as 0,0,0
    Vec3D v = new Vec3D (0, 0, 200);
    //create the plethora agents!
    Ple_Agent pa = new Ple_Agent(this, v);

    //generate a random initial velocity
    Vec3D initialVelocity = new Vec3D (random(-1, 1), random(-1, 1), random(-1, 1));

    //set some initial values:
    //initial velocity
    pa.setVelocity(initialVelocity);
    //initialize the tail
    pa.initTail(5);

    //add the agents to the list
    boids.add(pa);
  }
}

void draw() {
  background(235);

  //draw the spline
  for (int i = 1; i < sp2.pointList.size(); i++) {
    Vec3D v1 = sp2.pointList.get(i);
    Vec3D v2 = sp2.pointList.get(i-1);
    stroke(0,90);
    line(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
  }
  
  //draw a rect as reference
  stroke(0, 90);
  strokeWeight(1);
  noFill();
  rect(-DIMX/2, -DIMY/2, DIMX, DIMY);

  //run all agents
  for (Ple_Agent pa : boids) {

    //call a separation function to keep distance between agents
    pa.separationCall(boids,20,2);
    
    //calculate future location at 50 units
    Vec3D fLoc = pa.futureLoc(30);
    stroke(255, 0, 0, 90);
    pa.vLine(fLoc, pa.loc);
    
    //calculate closest normal to spline //STILL IN PROGRESS
    //use the mouse to change the direction
    float m = map(mouseX, 0,width,-5,5);
    Vec3D cns = pa.closestNormalandDirectionToSpline(sp2, fLoc,m);
    stroke(255, 0, 0,10);
    pa.vLine(cns, fLoc);
    
    //follow point obtained from spline
    pa.seek(cns, 1);

    //define the boundries of the space as bounce
    pa.bounceSpace(DIMX/2, DIMY/2, DIMY/2);

    //update the tail info every frame (1)
    pa.updateTail(1);

    //display the tail interpolating 2 sets of values:
    //R,G,B,ALPHA,SIZE - R,G,B,ALPHA,SIZE
    pa.displayTailPoints(0, 0, 0, 0, 1, 0, 0, 0, 255, 1);

    //set the max speed of movement:
    pa.setMaxspeed(1);
    //pa.setMaxforce(0.05);

    //update agents location based on past calculations
    pa.update();
    
    //Display the location of the agent with a point
    strokeWeight(2);
    stroke(0);
    pa.displayPoint();

    //Display the direction of the agent with a line
    strokeWeight(1);
    stroke(100, 90);
    pa.displayDir(pa.vel.magnitude()*3);
  }
}

