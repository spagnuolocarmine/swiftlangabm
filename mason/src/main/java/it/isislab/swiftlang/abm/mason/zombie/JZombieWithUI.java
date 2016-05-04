package it.isislab.swiftlang.abm.mason.zombie;


import java.awt.Color;

import javax.swing.JFrame;

import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.simple.AdjustablePortrayal2D;
import sim.portrayal.simple.MovablePortrayal2D;
import sim.portrayal.simple.OrientedPortrayal2D;
import sim.portrayal.simple.TrailedPortrayal2D;

public class JZombieWithUI extends GUIState
    {
    private static final long serialVersionUID = 1;

    public Display2D display;
    public JFrame displayFrame;

    ContinuousPortrayal2D particlesPortrayal = new ContinuousPortrayal2D();
   
    public Object getSimulationInspectedObject() { return state; }  // non-volatile
    public static void main(String[] args)
        {
        new JZombieWithUI().createController();
        }
    
    public JZombieWithUI() { super(new JZombie(System.currentTimeMillis())); }
    
    public JZombieWithUI(SimState state) { super(state); }
    
    public static String getName() { return "JZombie"; }
    
// We comment this out of the example, which will cause MASON to look
// for a file called "index.html" in the same directory -- which we've
// included for consistency with the other applications in the demo 
// apps directory.

/*
  public static Object getInfoByClass(Class theClass)
  {
  return "<H2>Tutorial3</H2><p>An odd little particle-interaction example.";
  }
*/
    
    public void quit()
        {
        super.quit();
        
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;  // let gc
        display = null;       // let gc
        }

    public void start()
        {
        super.start();
        // set up our portrayals
        setupPortrayals();
        }
    
    public void load(SimState state)
        {
        super.load(state);
        // we now have new grids.  Set up the portrayals to reflect that
        setupPortrayals();
        }
        
    // This is called by start() and by load() because they both had this code
    // so I didn't have to type it twice :-)
    public void setupPortrayals()
        {
     
        particlesPortrayal.setField(((JZombie)state).particles);
       
        
        // reschedule the displayer
        display.reset();
                
        // redraw the display
        display.repaint();
        }
    
    public void init(Controller c)
        {
        super.init(c);
        
        // Make the Display2D.  We'll have it display stuff later.
        display = new Display2D(600,600,this); // at 400x400, we've got 4x4 per array position
        displayFrame = display.createFrame();
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);

        // specify the backdrop color  -- what gets painted behind the displays
        display.setBackdrop(Color.black);

        // attach the portrayals
       
        display.attach(particlesPortrayal,"Particles");
        }
    }
    
    
    
    
    
