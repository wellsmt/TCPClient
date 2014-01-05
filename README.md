# TCPClient
Android TCP Client. Uses SCPI commands to send and receive data.

### Supported Android versions:
   * Target version 17
   * Min supported version 8

## Third Party Dependencies
   * Android SDK target version 17 (although we haven't really settled on this)
   * Android Plot

## Developer credits
   * Marc Bernardini
   * Michael Wells

## Icon Credits
   * Google Base Android Icon Set
   * VisualPharm http://www.visualpharm.com/
   * Marc Bernardini
   

## Acceptance Tests
### Devices Tab
   * Load the Wifi DAQ application using Eclipse
      * The application comes up on the Devices Tab, no devices are shown
   * Run the UdpResponder class from Eclipse. Note the UdpResponder class is in the io project.
   * Click the Refresh button on the Wifi DAQ application
      * A device named 'hello' of type 'AD7195W' shows up.
   * Click the Close All button
      * All devices are cleared from the list
   * Click the Android Home button
      * The application is put in the background, the Android home screen is displayed
   * Switch back to the Wifi DAQ application
      * The application regains focus, the previously displayed device is shown in the list
      
### Measurements Tab (unconnected)
   * Click on the Measurements tab
      * Tab comes up, no channels are shown, no data is shown on the plot
   * Click the Add Analog Channel button
      * Available Channels dialog is displayed with the message 'No Channels available. Try Connecting to a Wifi DAQ first.'
   * Click both the Ok and the Cancel buttons
      * Dialog should disappear
      
### Connecting
   * To be continued

      