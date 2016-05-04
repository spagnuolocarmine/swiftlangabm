package it.isislab.swiftlang.abm.mason.zombie;


import java.awt.Color;
import java.awt.Graphics2D;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.Bag;
import sim.util.Double2D;

/** A bouncing particle. */

public class Agent extends OvalPortrayal2D implements Steppable 
{
	private static final long serialVersionUID = 1;

	public boolean isInfected=false;

	public boolean randomize = false;

	Double2D pos;
	Double2D lastp;


	//Wandering
	public Double2D targetLocation = null;
	private Double2D desired_velocity = null;
	private Double2D steering = null;
	protected Color agentColor;
	static double MAX_VELOCITY;
	static double MAX_FORCE = 0.1;
	private Double2D tmp = null;
	private double mass = 1;
	private int next_decision = 0;
	private Double2D velocity = new Double2D(0,0); //2D field
	//End Wandering


	public Agent(boolean isInfected)
	{
		this.isInfected=isInfected;
	}

	public void step(SimState state)
	{
		JZombie tut = (JZombie)state;
		
		MAX_VELOCITY = isInfected?tut.zombie_step_size:tut.human_step_size;

		Double2D location = tut.particles.getObjectLocation(this);

		Bag neigh=tut.particles.getNeighborsWithinDistance(location, tut.n_size);

		for(Object o:neigh)
		{
			Agent a=(Agent)o;
			if(a.isInfected){
				isInfected=true;
				break;
			}
		}
		pos = tut.particles.getObjectLocation(this);
		lastp = pos;

		if(state.schedule.getSteps()==next_decision){
			targetLocation = new Double2D(tut.random.nextDouble() *tut.particles.getWidth(),tut.random.nextDouble()*tut.particles.getHeight());  
			next_decision = (int) state.schedule.getSteps() + (int) Math.floor((state.random.nextDouble()*10)+100);
		}

		tmp = seek(targetLocation,pos);

		steering = truncate(tmp,MAX_FORCE);
		steering = new Double2D(steering.getX()*(1/mass),steering.getY()*(1/mass));


		tmp = sumVector(velocity, steering);
		velocity = truncate (tmp , MAX_VELOCITY);

		pos = new Double2D(pos.x + velocity.x, pos.y +velocity.y);
		pos = truncate(pos, tut.particles.getWidth()-1,tut.particles.getHeight()-1);
		tut.particles.setObjectLocation(this,pos);


	}

	private Double2D seek(Double2D target, Double2D agent) {
		Double2D normVect;
		tmp= subVector(target,agent);
		normVect =normalize(tmp);
		desired_velocity = new Double2D(normVect.getX()* MAX_VELOCITY,normVect.getY()*MAX_VELOCITY);

		tmp = subVector(desired_velocity, velocity);
		return tmp;
	}

	//public void setColor(Color c){ this.agentColor = c; paint = agentColor;}



	private Double2D truncate(Double2D pos, double value){
		double x,y;
		x=(pos.x>value)?value:pos.x;
		y=(pos.y>value)?value:pos.y;
		return new Double2D(x,y);
	}

	private Double2D truncate(Double2D pos, double width, double height){
		double x = pos.x,y=pos.y;
		if(x<0) x=0;
		if(y<0) y=0;
		if(x>width) x=width;
		if(y>height) y=height;

		return new Double2D(x,y);
	}

	private Double2D sumVector(Double2D a, Double2D b)
	{ 
		double x,y;
		x=a.x+b.x;
		y=a.y+b.y;
		return new Double2D(x,y);
	}

	private Double2D subVector(Double2D a, Double2D b)
	{ 
		double x,y;
		x=a.x-b.x;
		y=a.y-b.y;
		return new Double2D(x,y);
	}

	private Double2D normalize(Double2D vector){
		double vector_lenght =Math.sqrt((Math.pow(vector.x, 2)+Math.pow(vector.y, 2)));

		return new Double2D(vector.x/vector_lenght,vector.y/vector_lenght);
	}
	public final void draw(Object object, Graphics2D graphics, DrawInfo2D info)
	{
		double diamx = info.draw.width*3;
		double diamy = info.draw.height*3;

		if (isInfected) graphics.setColor( Color.red );
		else  graphics.setColor( Color.green);
		graphics.fillOval(
				(int)(info.draw.x-diamx/2),(int)(info.draw.y-diamy/2),(int)(diamx),(int)(diamy));
	}
}
