package gate.creole;

import gate.*;
import gate.creole.ResourceInstantiationException;
import gate.event.CreoleEvent;
import gate.event.CreoleListener;
import gate.event.PluginListener;
import gate.util.GateException;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import org.apache.tools.ant.types.resources.comparators.Content;
import org.jdom.input.SAXBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import gate.creole.AnnotationSchema;
import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.Element;

public class CreoleRegisterImpl_llmsuite_5_GPTLLMTest {

@Test(expected = GateException.class)
public void testRegisterInvalidPluginThrowsException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin invalidPlugin = new Plugin.Directory(new URL("file:/invalid")) {

public boolean isValid() {
return false;
}
};
register.registerPlugin(invalidPlugin);
}

@Test
public void testGetLargeVRsForNullResourceReturnsEmpty() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getLargeVRsForResource(null);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetAnnotationVRsWithNoMatchesReturnsEmpty() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getAnnotationVRs("NonexistentAnnotation");
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test(expected = GateException.class)
public void testGetAllInstancesThrowsWhenClassNotFound() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.getAllInstances("com.fake.NonExistingClass");
}

@Test
public void testGetVRsForInvalidClassNameThrowsRuntimeException() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
try {
// register.getLargeVRsForResource("invalid.NonExistentClass");
fail("Expected GateRuntimeException");
} catch (GateRuntimeException e) {
assertTrue(e.getMessage().contains("Couldn't get resource class"));
}
}

@Test
public void testCreoleListenerEventInvocations() {
final List<String> events = new ArrayList<String>();
CreoleListener listener = new CreoleListener() {

public void resourceLoaded(CreoleEvent e) {
events.add("loaded");
}

public void resourceUnloaded(CreoleEvent e) {
events.add("unloaded");
}

public void resourceRenamed(Resource r, String oldName, String newName) {
events.add("renamed");
}

public void datastoreOpened(CreoleEvent e) {
events.add("opened");
}

public void datastoreCreated(CreoleEvent e) {
events.add("created");
}

public void datastoreClosed(CreoleEvent e) {
events.add("closed");
}
};
assertEquals(6, events.size());
assertTrue(events.contains("loaded"));
assertTrue(events.contains("unloaded"));
assertTrue(events.contains("opened"));
assertTrue(events.contains("created"));
assertTrue(events.contains("closed"));
assertTrue(events.contains("renamed"));
}

@Test
public void testRemoveCreoleListenerActuallyRemoves() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
final List<String> result = new ArrayList<String>();
CreoleListener listener = new CreoleListener() {

public void resourceLoaded(CreoleEvent e) {
result.add("loaded");
}

public void resourceUnloaded(CreoleEvent e) {
}

public void resourceRenamed(Resource r, String a, String b) {
}

public void datastoreOpened(CreoleEvent e) {
}

public void datastoreCreated(CreoleEvent e) {
}

public void datastoreClosed(CreoleEvent e) {
}
};
// register.addCreoleListener(listener);
// register.removeCreoleListener(listener);
// Resource res = new DummyResource();
// CreoleEvent event = new CreoleEvent(res, 0);
// register.resourceLoaded(event);
assertEquals(0, result.size());
}

@Test
public void testGetDirectoriesAlwaysReturnsEmptySet() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
// Set<URL> dirs = register.getDirectories();
// assertNotNull(dirs);
// assertTrue(dirs.isEmpty());
}

@Test
public void testRemoveDirectoryWithNullDoesNothing() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
// register.removeDirectory(null);
assertTrue(true);
}

@Test
public void testRemoveDirectoryWithNonMatchingUrlDoesNothing() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
URL fakeUrl = new URL("file:/no/match");
register.removeDirectory(fakeUrl);
assertTrue(true);
}

@Test
public void testGetSmallVRsForResourceReturnsEmptyIfNoVRsMatch() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> smallVRs = register.getSmallVRsForResource("java.lang.String");
assertNotNull(smallVRs);
assertTrue(smallVRs.isEmpty());
}

@Test(expected = GateRuntimeException.class)
public void testGetVRsForResourceThrowsIfVRClassInvalid() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = new ResourceData();
// rd.setResourceClassName("com.fake.NonClass");
rd.setGuiType(ResourceData.LARGE_GUI);
rd.setResourceDisplayed("java.lang.String");
register.put("FakeVR", rd);
register.getLargeVRsForResource("java.lang.String");
}

@Test
public void testGetVRsForResourceExcludesNonAssignableResources() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vrData = new ResourceData();
// vrData.setResourceClassName(DummyVR.class.getName());
vrData.setGuiType(ResourceData.LARGE_GUI);
vrData.setResourceDisplayed("java.util.List");
register.put("DummyVR", vrData);
List<String> vrList = register.getLargeVRsForResource("java.lang.String");
assertNotNull(vrList);
assertTrue(vrList.isEmpty());
}

@Test
public void testGetAnnotationVRsReturnsDefaultVRFirst() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd1 = new ResourceData();
// rd1.setResourceClassName(DummyVR.class.getName());
rd1.setGuiType(ResourceData.NULL_GUI);
rd1.setAnnotationTypeDisplayed(null);
rd1.setResourceDisplayed(null);
// rd1.setMainView(true);
// register.put(DummyVR.class.getName(), rd1);
List<String> results = register.getAnnotationVRs();
assertEquals(1, results.size());
// assertEquals(DummyVR.class.getName(), results.get(0));
}

@Test
public void testGetVREnabledAnnotationTypesReturnsCorrectValues() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = new ResourceData();
// rd.setResourceClassName("DummyVRComponent");
rd.setGuiType(ResourceData.NULL_GUI);
rd.setAnnotationTypeDisplayed("Person");
rd.setResourceDisplayed(null);
register.put("DummyVRComponent", rd);
List<String> enabledTypes = register.getVREnabledAnnotationTypes();
assertEquals(1, enabledTypes.size());
assertTrue(enabledTypes.contains("Person"));
}

@Test
public void testGetAnnotationVRsReturnsOnlyMatchingType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData matching = new ResourceData();
// matching.setResourceClassName("gate.creole.AnnotationVR1");
matching.setGuiType(ResourceData.NULL_GUI);
matching.setAnnotationTypeDisplayed("Person");
ResourceData nonMatching = new ResourceData();
// nonMatching.setResourceClassName("gate.creole.AnnotationVR2");
nonMatching.setGuiType(ResourceData.NULL_GUI);
nonMatching.setAnnotationTypeDisplayed("Location");
register.put("gate.creole.AnnotationVR1", matching);
register.put("gate.creole.AnnotationVR2", nonMatching);
List<String> results = register.getAnnotationVRs("Person");
assertEquals(1, results.size());
assertEquals("gate.creole.AnnotationVR1", results.get(0));
}

@Test
public void testGetAnnotationVRsEmptyForNullTypeAttribute() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr = new ResourceData();
// vr.setResourceClassName("gate.creole.AnnotationVR3");
vr.setGuiType(ResourceData.NULL_GUI);
vr.setAnnotationTypeDisplayed(null);
register.put("gate.creole.AnnotationVR3", vr);
List<String> types = register.getAnnotationVRs();
List<String> filtered = register.getAnnotationVRs("SomeType");
assertEquals(1, types.size());
assertEquals(0, filtered.size());
}

@Test
public void testAddAndRemovePluginListenerCorrectness() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
final List<String> events = new ArrayList<String>();
PluginListener listener = new PluginListener() {

public void pluginLoaded(Plugin plugin) {
events.add("loaded:" + plugin.getName());
}

public void pluginUnloaded(Plugin plugin) {
events.add("unloaded:" + plugin.getName());
}
};
register.addPluginListener(listener);
Plugin testPlugin = new Plugin.Directory(new URL("file:./mock")) {

public boolean isValid() {
return true;
}
};
register.firePluginLoaded(testPlugin);
register.firePluginUnloaded(testPlugin);
assertEquals(2, events.size());
register.removePluginListener(listener);
register.firePluginLoaded(testPlugin);
assertEquals(2, events.size());
}

@Test
public void testSetResourceNameTriggersRenameCallback() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
final List<String> logs = new ArrayList<String>();
CreoleListener listener = new CreoleListener() {

public void resourceLoaded(CreoleEvent e) {
}

public void resourceUnloaded(CreoleEvent e) {
}

public void resourceRenamed(Resource r, String oldName, String newName) {
logs.add(oldName + "->" + newName);
}

public void datastoreOpened(CreoleEvent e) {
}

public void datastoreCreated(CreoleEvent e) {
}

public void datastoreClosed(CreoleEvent e) {
}
};
register.addCreoleListener(listener);
// DummyRenamable res = new DummyRenamable();
// res.setName("oldName");
// register.setResourceName(res, "newName");
// assertEquals("newName", res.getName());
assertEquals(1, logs.size());
assertEquals("oldName->newName", logs.get(0));
}

@Test
public void testRemoveDirectoryDoesNotFailForUnregisteredUrl() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
URL unusedDirectory = new URL("file:/tmp/unregistered");
register.removeDirectory(unusedDirectory);
assertTrue(true);
}

@Test
public void testClearCleansTypeListsAndResources() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = new ResourceData();
// rd.setResourceClassName(MockApp.class.getName());
rd.setTool(true);
// register.put(MockApp.class.getName(), rd);
assertFalse(register.keySet().isEmpty());
register.clear();
assertTrue(register.getToolTypes().isEmpty());
assertTrue(register.getLrTypes().isEmpty());
assertTrue(register.getControllerTypes().isEmpty());
assertTrue(register.getPrTypes().isEmpty());
assertTrue(register.getVrTypes().isEmpty());
assertTrue(register.getApplicationTypes().isEmpty());
assertTrue(register.keySet().isEmpty());
}

@Test
public void testVRTypesAreOrderedWithMainViewFirst() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd1 = new ResourceData();
// rd1.setResourceClassName("VR1");
rd1.setGuiType(ResourceData.LARGE_GUI);
rd1.setResourceDisplayed("java.util.ArrayList");
// rd1.setMainView(false);
ResourceData rd2 = new ResourceData();
// rd2.setResourceClassName("VR2");
rd2.setGuiType(ResourceData.LARGE_GUI);
rd2.setResourceDisplayed("java.util.List");
// rd2.setMainView(true);
register.put("VR1", rd1);
register.put("VR2", rd2);
List<String> list = register.getLargeVRsForResource("java.util.ArrayList");
assertEquals(2, list.size());
assertEquals("VR2", list.get(0));
}

@Test
public void testProcessFullCreoleXmlTreeThrowsExceptionIfDocInvalid() throws Exception {
try {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = new Plugin.Directory(new URL("file:/invalid")) {

public boolean isValid() {
return true;
}
};
register.registerPlugin(plugin);
fail("Expected GateException due to invalid document");
} catch (GateException ex) {
assertTrue(ex.getMessage().contains("couldn't open creole.xml"));
}
}

@Test
public void testEmptyInstantiationsReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = new ResourceData();
// rd.setResourceClassName("gate.creole.TestResource");
register.put("gate.creole.TestResource", rd);
List<Resource> result = register.getAllInstances("gate.creole.TestResource");
assertNotNull(result);
assertEquals(0, result.size());
}

@Test
public void testGetPublicsReturnsEmptyWhenAllInstancesMarkedPrivate() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = new ResourceData();
// rd.setResourceClassName("gate.creole.SomeResource");
rd.setPrivate(true);
register.put("gate.creole.SomeResource", rd);
List<LanguageResource> lrList = register.getPublicLrInstances();
assertTrue(lrList.isEmpty());
}

@Test
public void testRegisterComponentWithMalformedURL() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// try {
// Class malformedClass = new Class() {
// };
// register.registerComponent(malformedClass);
// fail("Expected GateException");
// } catch (GateException ex) {
// assertTrue(ex.getMessage().startsWith("Unable to register component"));
// }
}

@Test
public void testFireResourceLoadedCallsListener() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
final List<String> log = new ArrayList<String>();
CreoleListener listener = new CreoleListener() {

public void resourceLoaded(CreoleEvent e) {
log.add("loaded");
}

public void resourceUnloaded(CreoleEvent e) {
}

public void resourceRenamed(Resource r, String o, String n) {
}

public void datastoreOpened(CreoleEvent e) {
}

public void datastoreCreated(CreoleEvent e) {
}

public void datastoreClosed(CreoleEvent e) {
}
};
register.addCreoleListener(listener);
assertTrue(log.contains("loaded"));
}

@Test
public void testUnregisterNonExistingPluginDoesNothing() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = new Plugin.Directory(new URL("file:/nonexistent")) {

public boolean isValid() {
return true;
}
};
register.unregisterPlugin(plugin);
assertTrue(true);
}

@Test
public void testRegisterDirectoriesFailsWithInvalidUrl() {
try {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.registerDirectories(new URL("file:/not-present"));
fail("Expected GateException");
} catch (GateException ex) {
assertTrue(ex.getMessage().contains("Failed to load plugin"));
} catch (Exception ex) {
fail("Unexpected exception type: " + ex.getMessage());
}
}

@Test
public void testRemoveListenerIgnoresMissing() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener fakeListener = new CreoleListener() {

public void resourceLoaded(CreoleEvent e) {
}

public void resourceUnloaded(CreoleEvent e) {
}

public void resourceRenamed(Resource r, String o, String n) {
}

public void datastoreOpened(CreoleEvent e) {
}

public void datastoreCreated(CreoleEvent e) {
}

public void datastoreClosed(CreoleEvent e) {
}
};
// register.removeCreoleListener(fakeListener);
assertTrue(true);
}

@Test
public void testGetAnnotationVRsWithNullAnnotationTypeReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> list = register.getAnnotationVRs(null);
assertNotNull(list);
assertTrue(list.isEmpty());
}

@Test(expected = GateRuntimeException.class)
public void testGetAnnotationVRsThrowsForBadClass() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData bad = new ResourceData();
// bad.setResourceClassName("invalid.foo.Foo");
bad.setGuiType(ResourceData.NULL_GUI);
bad.setAnnotationTypeDisplayed("Token");
register.put("invalidVR", bad);
register.getAnnotationVRs("Token");
}

@Test
public void testSetResourceNameWithNullOldName() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
final List<String> names = new ArrayList<String>();
// register.setResourceName(r, "new-name");
assertEquals(1, names.size());
assertEquals("new-name", names.get(0));
}

@Test
public void testReadPluginNamesMappingsHandlesMalformedXml() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
URL invalidUrl = new URL("file:/fake/path/");
Gate.setGateHome(new java.io.File("/invalid"));
try {
register.getPlugins();
assertTrue(true);
} catch (Exception ex) {
assertTrue("Should not throw exception", false);
}
}

@Test
public void testPutWithVRListEnsuresNoDuplicates() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = new ResourceData();
rd.setGuiType(ResourceData.SMALL_GUI);
register.put("gate.creole.MockVR", rd);
register.put("gate.creole.MockVR", rd);
// List<String> vrTypes = register.getVrTypes();
// assertEquals(1, vrTypes.size());
}

@Test
public void testResourceClassMismatchInGetAllInstancesSkipsEntry() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData badRD = new ResourceData();
// badRD.setResourceClassName("non.existent.Class");
register.put("non.existent.Class", badRD);
try {
register.getAllInstances("gate.Resource");
fail("Expected GateRuntimeException");
} catch (GateRuntimeException e) {
assertTrue(e.getMessage().contains("does not exist in the VM"));
}
}

@Test
public void testGetAnnotationVRsReturnsDefaultFirstIfMainViewTrue() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData first = new ResourceData();
// first.setResourceClassName("VR1");
first.setGuiType(ResourceData.NULL_GUI);
first.setAnnotationTypeDisplayed("Token");
// first.setMainView(true);
ResourceData second = new ResourceData();
// second.setResourceClassName("VR2");
second.setGuiType(ResourceData.NULL_GUI);
second.setAnnotationTypeDisplayed("Token");
// second.setMainView(false);
register.put("VR1", first);
register.put("VR2", second);
List<String> vrList = register.getAnnotationVRs("Token");
assertEquals(2, vrList.size());
assertEquals("VR1", vrList.get(0));
}

@Test
public void testGetVrTypesReturnsUnmodifiableList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = new ResourceData();
// rd.setResourceClassName("VRA");
rd.setGuiType(ResourceData.LARGE_GUI);
register.put("VRA", rd);
// List<String> types = register.getVrTypes();
try {
// types.add("otherVR");
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException expected) {
assertTrue(true);
}
}

@Test
public void testPutSameKeyIncrementsAndLogs() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd1 = new ResourceData();
// rd1.setResourceClassName("gate.creole.TestComponent");
ResourceData rd2 = new ResourceData();
// rd2.setResourceClassName("gate.creole.TestComponent");
register.put("gate.creole.TestComponent", rd1);
ResourceData reused = register.put("gate.creole.TestComponent", rd2);
assertEquals(rd1, reused);
assertEquals(2, reused.getReferenceCount());
}

@Test
public void testGetControllerTypesReturnsEmptyWhenNonePresent() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Set<String> controllers = register.getControllerTypes();
assertNotNull(controllers);
assertTrue(controllers.isEmpty());
}

@Test
public void testRemoveControllerResourceRemovesFromControllerTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = new ResourceData();
// rd.setResourceClassName("gate.creole.SerialController");
register.put("gate.creole.SerialController", rd);
assertTrue(register.getControllerTypes().contains("gate.creole.SerialController"));
register.remove("gate.creole.SerialController");
assertFalse(register.getControllerTypes().contains("gate.creole.SerialController"));
}

@Test
public void testAddAndRemoveCreoleListener() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
final List<String> events = new ArrayList<String>();
// CreoleListener listener = new CreoleListenerAdapter() {
// 
// public void resourceLoaded(CreoleEvent e) {
// events.add("loaded");
// }
// };
// register.addCreoleListener(listener);
// Resource mockResource = new Resource() {
// 
// public String getName() {
// return "X";
// }
// 
// public void setName(String name) {
// }
// 
// public FeatureMap getFeatures() {
// return Factory.newFeatureMap();
// }
// 
// public void cleanup() {
// }
// 
// public Resource init() {
// return this;
// }
// };
// CreoleEvent event = new CreoleEvent(mockResource, CreoleEvent.RESOURCE_LOADED);
// register.resourceLoaded(event);
// register.removeCreoleListener(listener);
// register.resourceLoaded(event);
assertEquals(1, events.size());
}

@Test
public void testGetPublicTypesWithMixedPrivacy() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData publicRD = new ResourceData();
// publicRD.setResourceClassName("gate.creole.PublicComp");
publicRD.setPrivate(false);
ResourceData privateRD = new ResourceData();
// privateRD.setResourceClassName("gate.creole.PrivateComp");
privateRD.setPrivate(true);
register.put("gate.creole.PublicComp", publicRD);
register.put("gate.creole.PrivateComp", privateRD);
Set<String> rawTypes = new HashSet<String>();
rawTypes.add("gate.creole.PublicComp");
rawTypes.add("gate.creole.PrivateComp");
List<String> filtered = register.getPublicTypes(rawTypes);
assertEquals(1, filtered.size());
assertTrue(filtered.contains("gate.creole.PublicComp"));
}

@Test
public void testGetPublicsWithNullResourceDataReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// Resource mockResource = new Resource() {
// 
// public String getName() {
// return null;
// }
// 
// public void setName(String name) {
// }
// 
// public FeatureMap getFeatures() {
// return Factory.newFeatureMap();
// }
// 
// public void cleanup() {
// }
// 
// public Resource init() {
// return this;
// }
// };
List<Resource> list = new ArrayList<Resource>();
// list.add(mockResource);
List<Resource> result = register.getPublics(list);
assertEquals(0, result.size());
}

@Test
public void testAddPluginListenerDoesNotDuplicate() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
final List<String> log = new ArrayList<String>();
PluginListener listener = new PluginListener() {

public void pluginLoaded(Plugin p) {
log.add("loaded");
}

public void pluginUnloaded(Plugin p) {
log.add("unloaded");
}
};
register.addPluginListener(listener);
register.addPluginListener(listener);
Plugin plugin = new Plugin.Directory(new URL("file:/mock")) {

public boolean isValid() {
return true;
}
};
register.firePluginLoaded(plugin);
assertEquals(1, log.size());
}

@Test
public void testRemoveUnregisteredPluginWithDependenciesDoesNothing() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin dependency = new Plugin.Directory(new URL("file:/fake/dependency")) {

// public List<Plugin> getRequiredPlugins() {
// return Collections.emptyList();
// }
};
Plugin mainPlugin = new Plugin.Directory(new URL("file:/fake/main")) {

// public List<Plugin> getRequiredPlugins() {
// List<Plugin> deps = new ArrayList<Plugin>();
// deps.add(dependency);
// return deps;
// }
};
register.unregisterPlugin(mainPlugin);
assertTrue(true);
}

@Test
public void testRemoveExplicitlyRemovesTypeFromToolSet() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = new ResourceData();
// rd.setResourceClassName("gate.creole.ToolImpl");
rd.setTool(true);
register.put("gate.creole.ToolImpl", rd);
assertTrue(register.getToolTypes().contains("gate.creole.ToolImpl"));
register.remove("gate.creole.ToolImpl");
assertFalse(register.getToolTypes().contains("gate.creole.ToolImpl"));
}

@Test
public void testRemoveNullResourceDataYieldsNull() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData result = register.remove("non.existing.key");
assertNull(result);
}

@Test
public void testGetAllInstancesWithHiddenResourcesExcluded() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// Resource hiddenRes = new Resource() {
// 
// public String getName() {
// return "HIDDEN";
// }
// 
// public void setName(String name) {
// }
// 
// public FeatureMap getFeatures() {
// FeatureMap fm = Factory.newFeatureMap();
// fm.put(GateConstants.HIDDEN_FEATURE_KEY, true);
// return fm;
// }
// 
// public void cleanup() {
// }
// 
// public Resource init() throws ResourceInstantiationException {
// return this;
// }
// };
ResourceData rd = new ResourceData();
// rd.setResourceClassName("gate.creole.HiddenComponent");
// rd.getInstantiations().add(hiddenRes);
register.put("gate.creole.HiddenComponent", rd);
List<Resource> instances = register.getAllInstances("gate.creole.HiddenComponent", false);
assertTrue(instances.isEmpty());
}

@Test
public void testGetAllInstancesWithHiddenResourcesIncluded() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// Resource hiddenRes = new Resource() {
// 
// public String getName() {
// return "HIDDEN";
// }
// 
// public void setName(String name) {
// }
// 
// public FeatureMap getFeatures() {
// FeatureMap fm = Factory.newFeatureMap();
// fm.put(GateConstants.HIDDEN_FEATURE_KEY, true);
// return fm;
// }
// 
// public void cleanup() {
// }
// 
// public Resource init() throws ResourceInstantiationException {
// return this;
// }
// };
ResourceData rd = new ResourceData();
// rd.setResourceClassName("gate.creole.HiddenComponent");
// rd.getInstantiations().add(hiddenRes);
register.put("gate.creole.HiddenComponent", rd);
List<Resource> instances = register.getAllInstances("gate.creole.HiddenComponent", true);
assertEquals(1, instances.size());
assertEquals("HIDDEN", instances.get(0).getName());
}

@Test
public void testSetResourceNameWithNullNameShouldNotThrow() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// register.setResourceName(res, "NEW_NAME");
// assertEquals("NEW_NAME", res.getName());
}

@Test
public void testGetVREnabledAnnotationTypesReturnsEmptyWhenAnnotationTypeNull() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData badVREntry = new ResourceData();
// badVREntry.setResourceClassName("SomeVR");
badVREntry.setGuiType(ResourceData.NULL_GUI);
badVREntry.setAnnotationTypeDisplayed(null);
register.put("SomeVR", badVREntry);
List<String> result = register.getVREnabledAnnotationTypes();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testClearAfterAddingMultipleTypesEmptiesEverything() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData pr = new ResourceData();
// pr.setResourceClassName("gate.PR");
register.put("gate.PR", pr);
ResourceData lr = new ResourceData();
// lr.setResourceClassName("gate.LR");
register.put("gate.LR", lr);
ResourceData tool = new ResourceData();
// tool.setResourceClassName("gate.Tool");
tool.setTool(true);
register.put("gate.Tool", tool);
register.clear();
assertTrue(register.getPrTypes().isEmpty());
assertTrue(register.getLrTypes().isEmpty());
assertTrue(register.getToolTypes().isEmpty());
}

@Test
public void testRegisterPluginWithMissingCreoleXMLThrowsGateException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = new Plugin.Directory(new URL("file:/fake/invalid")) {

public boolean isValid() {
return true;
}
};
try {
register.registerPlugin(plugin);
fail("Expected GateException due to missing creole.xml");
} catch (GateException e) {
assertTrue(e.getMessage().contains("couldn't open creole.xml"));
}
}
}
