# MASON Wrapper

This is a Java wrapper to execute [MASON](http://cs.gmu.edu/~eclab/projects/mason/) models from command line.

####Build the project

The following command generate a target folder, inside it there is the Java wrapper (mason-1.0-wrapper.jar):

``` mvn assembly:assembly ```

####Examples

In the folder ` resources/model` there is a NetLogo example model for an epidemic spread of a "Zombie Virus". Basically the ABM model is an instance of SIR model, where S = number susceptible, I =number infectious, and R =number recovered (immune), but without the last state I.

The model is characterized by:

  - num-people, the number of people involved in the simulation process;
  - num_infected, the intial number (at time 0 of the simulation) of infected people;
  - end_infected, the total number of infected after the end of the simulation.

######Run the experiment:

``` java -jar mason-1.0-wrapper.jar -m resources/models/JZombiesMason.jar -outfile test.csv -runid 1 -s 10 -trial 4 -i human_count,800,zombie_count,10,human_step_size,2.3,zombie_step_size,1.5 -o human_count```

  - ```-m```,  NetLogo model path;
  - ```-outfile```, output file for the outputs paramentes;
  - ```-runid```,  run identify;
  - ```-trial```,  number of trial to execute (for each one is changed the random seed randomly);
  - ```-s```, total number of steps;
  - ```-i```, list of input parameters (var1,value1,var2,value2 means var1=value1 and var2=value2);
  - ```-o```, list of output parameters.

######Example Output:
  ```
MASON model: ../resources/models/JZombieMason.jar
Output file: test.csv
Model parameters:
human_count 800
zombie_count 10
human_step_size 2.3
zombie_step_size 1.5
Start simulation: 
Run 0 with seed: -1282626260
\End run 0
Run 1 with seed: -2135988650
\End run 1
Run 2 with seed: -915412832
\End run 2
Run 3 with seed: 755883716
\End run 3

Output parameters:
human_count 90

  ```
