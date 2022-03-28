# Activities

## Introduciton
  A common use case is a situation where we perform severl tasks at different frequencies. E.g. contacting friends and some friends are contacted more frequently that others, an excercise regime where some exercises are more frequent or practicing a music repetoire where some pieces of music are practiced more often than others.
  
  This program allow the user to specify how many times each activity needs to be done in a 12 week period and bubles the next task to the top. In the example below, this is used to track kid games / activities. An example screenshow is:
  
  ![image](https://user-images.githubusercontent.com/44659844/160315683-6be0eb79-43a7-4642-9b26-588707897820.png)

Notes:
* Overdue items are in red. Others are in green.
* *Done* tells the program that the topmost activity is complete. It will set the *Last* to current time the *Next* time to `now() + 12wks / Frequency`. And resort the list based on *Next*.
* *Bump* button is used to bump all *Next* times by a number of days specified in the edit box next to it. This is useful after a vacation etc.
* Whenever anything in the list changes, the old file is backup to Backup directory with a timestamp.
* _12 wks_ is currently hard coded, but easy to change.

## Technology

This UI is based on JavaFX and it is a bit tricky to use it now that JavaFX is not included in the jdk. I used [Bellsoft](https://bell-sw.com/) jdk which contain JavaFX. When building with IntelliJ, it will find JavaFX in the jdk only if "Settings -> Build, Execution, Deployment -> Compiler -> Java Comiler" "Use '--releae' option for cross compilation (Java 9 and later)" tickbox is unchecked.

Once built, `makeRun.sh` in the bin directory can be used to create a executable by embedding `stub.sh` at the front. Also `stub.sh` can be modified to load a different text file with a different set of activities.
