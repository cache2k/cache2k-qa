to run tests within IntelliJ add build step: 'datanucleus:enhance'

tutorial
========

Simple demonstration of using JDO with DataNucleus.
You can run this with any supported datastore by simply editing the following files

* <a href="https://github.com/datanucleus/samples-jdo/blob/master/tutorial/src/main/resources/META-INF/persistence.xml">src/main/resources/META-INF/persistence.xml</a>   **[Edit the datastore details here]**
* <a href="https://github.com/datanucleus/samples-jdo/blob/master/tutorial/pom.xml">pom.xml</a>   **[Edit the dependency jars for your datastore]**

__Choosing to NOT invoke the tutorial using the following methods means that you ignore our advice and any problem is yours__.

__Note that for some datastores, you will need to start them up before running the tutorial, and optionally create the schema defined in the persistence properties__.

Maven Instructions
==================
1. Run the command *mvn clean compile*. This builds everything and enhances the classes.

2. Run the command *mvn datanucleus:schema-create*. This creates the schema for the tutorial.

3. Run the command *mvn exec:java*. This runs the tutorial.

4. Run the command *mvn datanucleus:schema-delete*. This deletes the schema for the tutorial


