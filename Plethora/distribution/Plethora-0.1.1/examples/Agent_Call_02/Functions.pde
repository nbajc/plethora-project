//FUNCTIONS

void runAgents() {
  for (Ple_Agent pa : boids) {
    
    stroke(255,0,169,90);
    strokeWeight(1);
    pa.flock(boids, 50, 80*80, 50 *50, 2, 0.5, 1.2);
    
    pa.update();
    pa.wrapSpace(600, 150, 150);
    
    
    pa.showCoh = true;
    //pa.updateTail(8);
    
    stroke(0,255,169);
    strokeWeight(2);
    //pa.displayTailPoints(255,0,0,255,0,255,100,100,1,10);
    
    pa.setMaxspeed(8);
    //pa.setMaxforce(0.05);
  
    strokeWeight(3);
    stroke(255, 0, 0);
    pa.displayPoint();
    
    strokeWeight(1);
    stroke(255);
    pa.displayDir(pa.vel.magnitude()*5);
    
  }
}

void buildAgents(int pop) {
  for (int i = 0; i < pop; i++) {
    Vec3D v = new Vec3D (random(-DIMX/2, DIMX/2), random(-DIMY/2, DIMY/2), random(-DIMZ/2, DIMZ/2));
    Ple_Agent pa = new Ple_Agent(this, v);

    Vec3D initialVelocity = new Vec3D (random(-1, 1), random(-1, 1), random(-1, 1));
   // Vec3D initialVelocity = new Vec3D (0,0,0);
    pa.setVelocity(initialVelocity);
    
    pa.initTail(30);
    
    boids.add(pa);
    
    
  }
}

void buildBox(float x,float y, float z) {
  noFill();
  stroke(100);
  strokeWeight(1);
  pushMatrix();
  scale(x,y,z);
  box(1);
  popMatrix();
}

