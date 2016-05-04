package it.isislab.swiftlang.abm.mason.zombie;


import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.util.*;


public class JZombie extends SimState
{
	private static final long serialVersionUID = 1;


	public Continuous2D particles;

	public int gridWidth = 200;
	public int gridHeight = 200;
	public int human_count = 140;
	public int zombie_count = 10;
	public double zombie_step_size=0.1;
	public double human_step_size=3;
	public int n_size=3;

	public int gethuman_count() {
		return human_count;
	}

	public void sethuman_count(int human_count) {
		this.human_count = human_count;
	}

	public int getzombie_count() {
		return zombie_count;
	}

	public void setzombie_count(int zombie_count) {
		this.zombie_count = zombie_count;
	}

	public double getzombie_step_size() {
		return zombie_step_size;
	}

	public void setzombie_step_size(double zombie_step_size) {
		this.zombie_step_size = zombie_step_size;
	}

	public double gethuman_step_size() {
		return human_step_size;
	}

	public void sethuman_step_size(double human_step_size) {
		this.human_step_size = human_step_size;
	}

	public JZombie(long seed)
	{
		super(seed);
	}


	public void start()
	{
		super.start();

		particles = new Continuous2D(1.0, gridWidth, gridHeight);

		Agent p;

		for(int i=0 ; i< human_count + zombie_count ; i++)
		{
			boolean zombie=(i < zombie_count);
			p = new Agent(zombie);  // random direction
			schedule.scheduleRepeating(p);
			particles.setObjectLocation(p,
					new Double2D(random.nextInt(gridWidth),random.nextInt(gridHeight)));  // random location
		}
		
		schedule.scheduleRepeating(Schedule.EPOCH + 1 , new Steppable() {
			
			public void step(SimState arg0) {
				// TODO Auto-generated method stub
				zombie_count=0;
				human_count=0;
				for(Object o:particles.allObjects)
				{
					Agent a=(Agent)o;
					if(a.isInfected)
						zombie_count++;
					else 
						human_count++;
				}
				//System.out.println(human_count);
			}
		});

	}

	public static void main(String[] args)
	{
		doLoop(JZombie.class, args);
		System.exit(0);
	}    
}
