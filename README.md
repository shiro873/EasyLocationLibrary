# EasyLocationLibrary

Utility wrapper library for easy location access including permissions.

# Installation

in root build.gradle add this line
    
    repositories {
			...
			maven { url 'https://jitpack.io' }
		}
    
In application level gradle file add:
  
  implementation 'com.github.shiro873:easylocationlibrary:0.1.1'
  
# Usage

  Initiate location object like this:
    EasyLocation location = new EasyLocation(this, this);
    
  For latitude and longitude of last known location:
    location.getLat() -- for latutude.
    location.getlon() --  for longitude
    
  Location address:
    location.getAddress(latitude, longitude)
    
  Distance between 2 places(in kilometers)
    location.getDistance(oldLatitude, oldLongitude, newLatitude, newLongitude)
    
  Location permission:
    location.checkPermissions()
    
  and on permissionResult method simply call
    location.permissionResult(requestCode, permissions[], grantResults)
  
  
