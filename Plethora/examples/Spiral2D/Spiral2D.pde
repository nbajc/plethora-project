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

float DIMX = 600;
float DIMY = 600;
float DIMZ = 600;

int pop = 800;

void setup() {
  size(1200, 600, OPENGL);
  smooth();
  cam = new PeasyCam(this, 600);

  //initialize the arrayList
  boids = new ArrayList <Ple_Agent>();

  for (int i = 0; i < pop; i++) {

    //set the initial location as 0,0,0
    Vec3D v = new Vec3D (random(-DIMX/2, DIMX/2), random(-DIMY/2, DIMY/2), 0 );
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

  stroke(0, 90);
  strokeWeight(1);
  noFill();
  rect(-DIMX/2, -DIMY/2, DIMX, DIMY);

  for (Ple_Agent pa : boids) {

    //generate a rotation force - works like wander but u can specify the angle directly
    pa.spiral2D(100, 0, 0.01);

    //declare the space boundry behavior
    pa.wrapSpace(DIMX/2, DIMY/2, DIMY/2);

    //update the tail info every frame (1)
    pa.updateTail(1);

    //display the tail interpolating 2 sets of values:
    //R,G,B,ALPHA,SIZE - R,G,B,ALPHA,SIZE
    pa.displayTailPoints(0, 0, 0, 0, 1, 0, 0, 0, 255, 1);

    //set the max speed of movement:
    pa.setMaxspeed(3);
    //pa.setMaxforce(0.05);

    //update agents location based on past calculations
    pa.update();

    //make 2D in some Z plane
    pa.flatten(0);

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

