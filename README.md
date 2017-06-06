# CypherOS System Framework

Our framework provides extended API's that function properly across Android releases. We provide plugin modules and services to imporve core functionality system wide. The framework is split into
different packages that have different objectives.

##  Luna Hardware Abstraction Framework (HAF)

This framework provides support for custom third party features and access to hardware controllers. Everything is placed here, and if a device supports it, we can extend a remote variable to it's
device tree with a custom class that overrides the targeted boolean.

### Here's an example

We have a feature called KeyDisabler. It's an API that can allow a hardware key based device to disable it's hardware key's. This feature is defined in our HardwareManager as a supported feature.
We then create a KeyDisabler class with the configurations set to false by default. To set this to true for your device add the required build variable

```BOARD_HARDWARE_CLASS += $(PLATFORM_PATH)/aoscphw```

The variable being BOARD_HARDWARE_CLASS with the remaining being the path. In that path add a duplicate KeyDisabler class but instead of defaulting to false, set the neccessary flags to direct to
the hardware configurations, indicating a "true" flag. 

## Piracy Protection

This is a service dedicated to eliminating piracy on our software. This service broadcast a series of persistent notification events, notifying the user when a piracy related application is installed.
If the user chooses not to remove the application, this service can disable custom features and/or applications. If the user chooses to associate him/herself with piracy, then our support will no longer
be extended to them, forcing the user to either remove the application or take the piracy onto another platform.


Copyright 2017 - CypherOS