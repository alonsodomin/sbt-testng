val testNG             = Build.root

// "testNGRuntimeJVM" is the "real" TestNG library on the JVM
val testNGRuntimeJS    = Build.testNGRuntimeJS

val testNGInterfaceJVM = Build.testNGInterfaceJVM
val testNGInterfaceJS  = Build.testNGInterfaceJS

// We don't need "testNGPluginJVM" because we can process annotations at runtime on the JVM
val testNGPluginJS     = Build.testNGPluginJS

val testNGTestsJVM     = Build.testNGTestsJVM
val testNGTestsJS      = Build.testNGTestsJS



val testNGPluginSBT     = Build.testNGPluginSBT
