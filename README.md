# Fitness Tracker – Android App

### Primary features:

- calculation and display of the total number of steps taken by the user since the device's last reboot

- calculation and display of the total number of steps taken by the user this morning

- calculation and display of the total number of steps taken by the user throughout the day


### Additional features:

- automatic request for access to the "ACTIVITY_RECOGNITION" function upon the initial launch of the application

- utilization of a customized FitnessTrackerTheme and Typography for enhanced visual presentation

- calculation and display of the total number of days elapsed since the device's last reboot

- systematic storage of the total number of steps taken this morning and throughout the day using SharedPreferences


### Known bugs:

- the total number of steps taken this morning and throughout the day is reset when the application is completely closed (e.g., swiped up from Recent Apps)

- the total number of steps taken this morning and throughout the day does not reset in the front-end when the next day starts, but does get reset in the back-end


### Areas of improvement:

- addressing known issues

- implementing a background service for continuous tracking

- employing a non-phone device (e.g., smartwatch)
