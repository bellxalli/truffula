# Truffula Notes
As part of Wave 0, please fill out notes for each of the below files. They are in the order I recommend you go through them. A few bullet points for each file is enough. You don't need to have a perfect understanding of everything, but you should work to gain an idea of how the project is structured and what you'll need to implement. Note that there are programming techniques used here that we have not covered in class! You will need to do some light research around things like enums and and `java.io.File`.

PLEASE MAKE FREQUENT COMMITS AS YOU FILL OUT THIS FILE.

## App.java

In the App.java, you will be able to choose from a variety of different options, such as whether to show hidden files, whether you will use your colored output that you will make in ColorPrinter (in wave 1), and it will help you choose the root directory that will be the starting point for the tree you will print.

## ConsoleColor.java
The consoleColor.java is an enum which holds unchangable values that can be used in other methods and functions throughout the program. This emun holds colors as variables and methods that will be used in colorPrinter.java
## ColorPrinter.java / ColorPrinterTest.java

## TruffulaOptions.java / TruffulaOptionsTest.java
In truffulaOptions.java you will be created a method that creates a truffulaOptions object by taking in an array of strings holding the given data for the fields to be set to in the truffulaOptions class. These fields are private and final meaning they cannot be changed or accessed outside of the class. This file also holds various other methods that will be used to access the private fields outside of the class. The test file holds one test that can and will be used as an example for the new tests to be written.
## TruffulaPrinter.java / TruffulaPrinterTest.java

## AlphabeticalFileSorter.java