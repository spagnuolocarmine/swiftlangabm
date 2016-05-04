package it.isislab.swiftlang.abm.mason;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import sim.engine.SimState;
/**
@Author(
		   name = "Carmine Spagnuolo",
		   date = "05/02/2016"
		)
 */
public class MasonWrapper 
{

	SimState state;

	@Option(required = false, name="-simstate", usage = "SimState name optional, if not the first one is used (full qualified name)")
	private String simstatename="";
	
	@Option(name="-m",usage="mason model path")
	private String model_path;

	@Option(name="-outfile",usage="output to this file",metaVar="OUTPUT")
	private File out = new File(".");

	@Option(name="-trial",usage="number of runs")
	private Integer trial;

	@Option(name="-runid",usage="run identify")
	private String id;

	@Option(name="-s",usage="number of steps")
	private Integer steps;

	@Option(name="-i",usage="input list: var1,value1,var2,value2")
	private String input;

	@Option(name="-o",usage="output list: var1,value1,var2,value2")
	private String output;

	HashMap<String, Object> outputs=new HashMap<String, Object>();
	HashMap<String, String> parameter;

	private boolean toPrint=true;
	class PrintWait extends Thread{

		@Override
		public void run() {
			String[] phases = {"|", "/", "-", "\\"};

			while (toPrint)
			{
				for (String phase : phases)
				{
					System.out.print(("\r"+phase));
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			String[] arguments_output=output.split(",");
			for (int i = 0; i < arguments_output.length; i+=1) {

				Method m;
				try {

					m = state.getClass().getMethod("get"+arguments_output[i]);

					Object o= m.invoke(state);
					

					Object outvalue=outputs.get(arguments_output[i]);

					if(outvalue==null) outputs.put(arguments_output[i], o);
					else{
						try{

							Integer sout=(Integer)outvalue;
							sout+=(Integer)o;
							outputs.put(arguments_output[i], sout);


						}catch(Exception e1)
						{
							try{
								Double sout=(Double)outvalue;
								sout+=(Double)o;
								outputs.put(arguments_output[i], sout);
							}catch(Exception e2)
							{
								try{
									String sout=(String)outvalue;
									sout+="-"+o;
									outputs.put(arguments_output[i], sout);
								}catch(Exception e3)
								{
									e3.getStackTrace();
								}
							}

						}
					}


				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
	public static void main(String[] args) throws IOException {
		new MasonWrapper().doMain(args);
	}

	public void doMain(String[] args) throws IOException {

		CmdLineParser parser = new CmdLineParser(this);


		try {
			parser.parseArgument(args);
			if( input.isEmpty() )
				throw new CmdLineException(parser,"No model paramters in input is given");

		} catch( CmdLineException e ) {
			System.err.println(e.getMessage());
			System.err.println("java MASONWrapper [options...] arguments...");
			parser.printUsage(System.err);
			System.err.println();

			return;
		}

		System.out.println("MASON model: "+model_path);

		System.out.println("Output file: "+ out);

		Random r=new Random(System.currentTimeMillis());

		String[] arguments=input.split(",");
		parameter=new HashMap<String, String>();
		System.out.println("Model parameters:");
		for (int i = 0; i < arguments.length; i+=2) {
			try{
				System.out.println(arguments[i]+" "+arguments[i+1]);

				parameter.put(arguments[i], arguments[i+1]);
			}catch (Exception e) {
				System.out.println("java MASONWrapper [options...] arguments...");
				System.out.println("You must pass parameters setting as couple: var_name1 value1 var_name2 vaue2 ...");
				// print the list of available options
				parser.printUsage(System.err);
				System.exit(-1);
			}
		}
		System.out.println("Start simulation: ");

		try {


			for (int i = 0; i < trial; i++) {


				toPrint=true;
				int seed=r.nextInt();
				System.out.println("Run "+i+" with seed: "+seed);
				state = makeSimulation(model_path, seed);
				for(String variable_name: parameter.keySet())
				{
					Method m;

					try{

						Integer sout=Integer.parseInt(parameter.get(variable_name));
						m = state.getClass().getMethod("set"+variable_name, Integer.class);
						m.invoke(state, sout);

					}catch(Exception e1)
					{
						try{
							Double sout=Double.parseDouble(parameter.get(variable_name));
							m = state.getClass().getMethod("set"+variable_name, Double.class);
							m.invoke(state, sout);

						}catch(Exception e2)
						{
							try{

								String sout=(String)parameter.get(variable_name);
								m = state.getClass().getMethod("set"+variable_name, String.class);
								m.invoke(state, sout);
							}catch(Exception e3)
							{
								e3.getStackTrace();
							}
						}

					}
				}

				PrintWait waiter= new PrintWait();
				waiter.start();
				state.start();
				int istep=0;
				while(istep < steps)
				{
					state.schedule.step(state);
					istep++;
				}

				toPrint=false;
				waiter.join();
				System.out.println("End run "+i);

			}

			System.out.println("\nOutput parameters:");
			PrintWriter print_output;
			try {
				print_output = new PrintWriter(out);
				String[] arguments_output=output.split(",");
				print_output.print("\"run\",\"tick\"");
				for (int i = 0; i < arguments_output.length; i+=1) {
					print_output.print(",\""+arguments_output[i]+"\"");
				}
				print_output.print("\n");
				print_output.print(id+","+steps);
				for (int i = 0; i < arguments_output.length; i+=1) {

					if(outputs.get(arguments_output[i]) instanceof String)
					{
						print_output.print(","+outputs.get(arguments_output[i]));
					}
					else{
						if(outputs.get(arguments_output[i]) instanceof Integer)
						{
							System.out.println(arguments_output[i]+" "+((Integer)outputs.get(arguments_output[i])/trial));
							print_output.print(","+((Integer)outputs.get(arguments_output[i])/trial));
						}
						else{
							System.out.println(arguments_output[i]+" "+((Double)outputs.get(arguments_output[i])/trial));
							print_output.print(","+((Double)outputs.get(arguments_output[i])/trial));
						}

					}

				}
				print_output.print("\n");
				print_output.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

	}
	/**
	 * Create instance of DistributeState from jar
	 * @param params
	 * @param prefix
	 * @param pathJar
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "deprecation" })
	private SimState makeSimulation(String pathJar,long seed)
	{
		String path_jar_file=pathJar;
		try{
			@SuppressWarnings("resource")
			JarFile jar=new JarFile(new File(path_jar_file));
			Enumeration e=jar.entries();
			File file  = new File(path_jar_file);
			URL url = file.toURL(); 
			URL[] urls = new URL[]{url};
			@SuppressWarnings("resource")
			ClassLoader cl = new URLClassLoader(urls);
			
			
			Class distributedState=null;
			
			if(simstatename.equalsIgnoreCase(""))
			while(e.hasMoreElements()){

				JarEntry je=(JarEntry)e.nextElement();
				if(!je.getName().contains(".class")) continue;
				Class c;
				try{
					//System.out.println(je.getName());
					c=cl.loadClass(je.getName().replaceAll("/", ".").replaceAll(".class", ""));
					//TODO make sure that noclassfoundexception not be throw
					//SOMETIMES THIS NOT WORK! WE MUST FIX IT! 
					
				}catch(ClassNotFoundException e1)
				{
					continue;
				}
				if(c!=null && c.getSuperclass() !=null &&  c.getSuperclass().equals(SimState.class))
				{
					distributedState=c;
					break;
				}

			}
			else{
				distributedState=cl.loadClass(simstatename);
			}
			if(distributedState==null) return null;
			@SuppressWarnings("resource")
			//JarClassLoader cload = new JarClassLoader(new URL("jar:file://"+path_jar_file+"!/"));
			JarClassLoader cload = new JarClassLoader(new File(path_jar_file).toURI().toURL());
			cload.addToClassPath();
			return (SimState) cload.getInstanceWithSeed(distributedState.getName(),seed);
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;

	}

}