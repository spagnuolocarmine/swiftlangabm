# NetLogo Wrapper

This is a Java wrapper to execute [NetLogo](https://ccl.northwestern.edu/netlogo/) models from command line.

####Build the project

The following command generate a target folder, inside it there is the Java wrapper (netlogo-1.0-wrapper.jar):

``` mvn assembly:assembly ```

####Examples

In the folder ` resources/model` there is a NetLogo example model for an epidemic spread of a "Zombie Virus". Basically the ABM model is an instance of SIR model, where S = number susceptible, I =number infectious, and R =number recovered (immune), but without the last state I.

The model is characterized by:

  - num-people, the number of people involved in the simulation process;
  - num_infected, the intial number (at time 0 of the simulation) of infected people;
  - end_infected, the total number of infected after the end of the simulation.

######Run the experiment:

``` java -jar netlogo-1.0-wrapper.jar -m JZombiesLogo.nlogo -outfile test.out -s 3 -i num-people,800,num-infected,10 -o end_infected ```

  - ```-m```,  NetLogo model path;
  - ```-outfile```, output file for the outputs paramentes;
  - ```-s```, total number of steps;
  - ```-i```, list of input parameters (var1,value1,var2,value2 means var1=value1 and var2=value2);
  - ```-o```, list of output parameters.

######Example Output:
  ```
  NetLogo model: JZombiesLogo.nlogo
  Output file: test.out
  Model parameters:
  num-people 800
  num-infected 10
  Start simulation: 
  \
  Output parameters:
  end_infected 27.0
  ```
