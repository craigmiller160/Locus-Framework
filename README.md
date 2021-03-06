# **Locus Framework**
## **Version 1.4**

### MVC Abstraction Framework

Locus is an MVC abstraction framework, inspired by a wide range of innovative abstraction designs around the MVC pattern. The main goal is to provide a means by which Model, View, and Controller classes in a Java application can exist completely apart from one another, with no direct references whatsoever, yet still communicate with each other. It does this through the use of heavy reflection and an API that is designed to be 100% flexible. Any method, with any number of arguments, can be invoked through this system.

#### Changelog

**Version 1.4.4**
+ Upgraded CM160Utils to version 1.3.2, to enable support for null arguments in the reflective invocations.
+ Improved methods that provided generic return types so that they could support the use of primitives types.

**Version 1.4.3**
+ Minor logging improvements to LocusModel.

**Version 1.4.2**
+ Fixed NPEs in code that converted the return types of values retrieved through reflection.

**Version 1.4.1**
+ Removed the exception that was thrown if LocusView was automatically invoked for a property that doesn't currently exist in a view.

**Version 1.4**
+ Updated CM160Utils to Version 1.3, and updated project to adapt to API changes.
+ Changed LocusController.getController(...) with a callback object argument to LocusController.getControllerWithCallback(...).
+ Added ability to retrieve and instantiate controllers with constructor arguments to LocusController.
+ Removed unused controller singleton functionality, which caused conflicts and clashes with other features.

**Version 1.3.3**
+ Reversed some of the overly-aggressive access level restrictions with the Locus component classes, because the restrictions caused them to not be accessible at all outside of the framework.
+ Fixed JavaDoc creation errors

**Version 1.3.2**
+ Added isInitialized() method to Locus class so client code can determine if the framework is initialized.

**Version 1.3.1
+ Fixed NPE in LocusView caused by trying to get an instance of the UIThreadExecutor before initialization was finished.

**Version 1.3**
+ Improved locking on Locus initialization process to ensure that it cannot be run more than once at a time.
+ Added ability to use Java configuration to build LocusConfiguration and pass it to Locus initialization method.
+ Fixed bug where scanner exclusions weren't being checked when scanning for adder/remover methods.
+ Modified how controller callbacks are stored. Used the new SuperWeakHashMap class from CM160Utils 1.2 to ensure that no references are being unnecessarily preserved.
+ Series of improvements to LocusStorage after intensive testing.
+ Fixed bug in ViewObjectTracker that was causing NullPointerException.
+ Minor performance tweaks for initialization process.

**Version 1.2**
+ Changed model method scanning & storage. Now methods that have the same name but different params are considered unique, and multiple methods for the same property can be stored.
+ Modified how the configuration file is found. ConfigurationReader now accepts an InputStream, instead of a String. Locus initialization methods have been expanded to accept a stream instead of a String. This allows for better multi-platform (Java vs Android) flexibility.

**Version 1.1**
+ Expanded scanning to be either package based or class based. New class based scanning requires full, qualified class names in configuration.
+ Package and class based scanning is mutually exclusive. Only one or the other can be specified in the Locus XML configuration.
+ Expanded reflective method calls to include add/remove for collection based operations.
+ Spun off reflection logic into independent CM160-Utils project, and included that project as a dependency.
+ Added UIThreadExecutor logic, which provided a plugin mechanism for client code to ensure that all access to views only occurs on the appropriate UI Thread.
+ Code refactoring and improvements.

**Version 1.0**
+ Created main Locus components: View, Model, Controller, and Debug.
+ Ability to invoke any setter/getter reflectively in views and models.
+ Ability to reflectively retrieve Controller classes.
+ LocusControllerCallback allows the controller to retrieve values from the view without needing a reference to the view.
+ LocusDebug provides handy instant logging to the console, useful for debugging Locus issues and getting a snapshot of the current configuration.
+ Restrictions: Model setters/getters must have unique names. setFoo() cannot exist more than once, even if in separate classes or with different parameters. Views do not have this restriction.
+ XML based configuration, read into memory at framework initialization.
+ Ability to exclude/include classes & packages in the inheritence hierarchy. This allows superclass methods to be excluded, or default excluded superclasses to be included.
+ By default, all standard Java SDK classes are excluded from having their methods added to Locus storage.
+ Classpath package scanning to detect annotated classes, with package names specified in configuration.
