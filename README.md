------------------------------------------------------------------------
SBsoftware - Regina Logistics WebApp
Revision History
------------------------------------------------------------------------
Ver 1.2.0 - 25/06/2012 [Production Install]
------------------------------------------------------------------------
Feedback from users
- Need Legend
- Dissabled beds from DB need to dissappear from GUI
- Delete 3 letter name/lastname initials from info-dialog
- Doctors need to have fixed colors. The implemented palette
  is more of a burden. Fixed list of doctors/colors was furnished. I
  Propose hardcoding (via properties file w/no CRUD) the  list of docs
  and present the list as pulldown menu when creating new doctor 
  polygons.
- TV feature info placed outside the window.
- Propose a way of printing all the layers with doctor info. The webapp
  needs to take the place of the hand-crafted excel file that records
  doctor info.
  
@Home post install
- Tried to access the site from different computers to see if changing
  the doctor info would be shared between sessions. Confirmed that the 
  singleton model used in servlet init works.
- Tried hot-deploy of app just by replacing the 'regina_webapp.war' file.

------------------------------------------------------------------------
Ver 1.2.1_RC - 26/06/2012 [Bugfixing RC]
------------------------------------------------------------------------
Implemented
- Fixed bug on non-existent dates. Eg. February 30th would cause a DB 
  error that propagates to GUI. Fixed problem by checking date (using 
  javascript Date object) on dateChange() method call.
- Fixed potential bug new doctor creation. If for the room number the
  user inputs the characters ',' or ';' it would save this info in the 
  properties file key.
- RolledBack on 3 letter name/lastname initials as per users requests.

TO_DO and Brainstorms
- For the dissabled bed issue need to query the CBADATI schema for the
  info. Unfortunately the occInfo is located in CBAREGINA schema. To 
  avoid the double query, propose to implement a new attribute in the 
  already implemented singleton object to capture a Set of keys with the
  dissabled bed info. This object would be updated everytime the user
  changes the floor (buildingId) by invoking the selectorsChanged()
  method.  
  
------------------------------------------------------------------------
Ver 1.2.2_Final - 24/07/2012 [Final GA]
------------------------------------------------------------------------
Released
- Fixed bug on non-existent dates. Eg. February 30th would cause a DB 
  error that propagates to GUI. Fixed problem by checking date (using 
  javascript Date object) on dateChange() method call.
- Fixed potential bug new doctor creation. If for the room number the
  user inputs the characters ',' or ';' it would save this info in the 
  properties file key.
- RolledBack on 3 letter name/lastname initials as per users requests. 

------------------------------------------------------------------------
Ver 1.2.3_Final - 03/08/2012 [Final GA]
------------------------------------------------------------------------
Released
- Implemented legend
- Implemented timer for canvasMgr init
- Performance improvements (delete occArray on widgetChange)

------------------------------------------------------------------------
Ver 1.2.8_Final - 08/02/2012 [Final GA]
------------------------------------------------------------------------
Released
- Implemente 'Confirmed Dismission'
- Added new icons
- Added new icons to legends

------------------------------------------------------------------------
Ver 1.3.0_Final - 18/03/2013 [Final GA]
------------------------------------------------------------------------
Released
- Doctor info captured from DB
- Introduction of H2dB for inMemory persistance
- Rewrote prop file helper classes
- Rewrote DB helper classes
- Rewrote maintenance mechanism for capturing polyshapes
- Split maintenance js files for rooms and beds

------------------------------------------------------------------------
Ver 1.3.1_Final - 25/03/2013 [Final GA]
------------------------------------------------------------------------
Released
- Legend via canvas layer to match colors ofDoctors
- JavaScript code cleanup
- Fix Room Polygons for final floor (configuration)

------------------------------------------------------------------------
Ver 1.3.2_Final - 18/04/2013 [Final GA]
------------------------------------------------------------------------
Released
- [Bug sb002] - problem with doctors list on query.
- Upgraded 'jaybird-full-2.1.6.jar' to version 'jaybird-full-2.2.2.jar'

------------------------------------------------------------------------
Ver 1.3.3_Final - 20/04/2013 [Final GA]
------------------------------------------------------------------------
Released
- Doctors legend taylored to actual doctors on floor 
- RolledBack 'jaybird-full-2.2.2.jar' to version 'jaybird-full-2.1.6.jar'

------------------------------------------------------------------------
Ver 2.0.0_Final - 13/04/2015 [Final GA]
------------------------------------------------------------------------
Released [Too many changes to list!!]
- Revamped object model
- Reworked all javascript objects
- Reworked all java servlet objects
- introduction of Cleaning Layer
- indroduction of Excel file creation (Cleaning Schedule)

------------------------------------------------------------------------
Ver 2.0.1_GA - 25/04/2015 [Final GA]
------------------------------------------------------------------------
Excel file creation mod.
- New dialog for excel file selection.
- Logic to modulate DatePicker calendar with dates on excel file name.
- Modification of Excel file for many dates not only one.
Introduction of Connection Pool
- CP usage for FireBird connection.